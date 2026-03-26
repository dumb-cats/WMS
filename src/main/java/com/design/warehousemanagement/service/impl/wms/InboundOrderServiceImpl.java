package com.design.warehousemanagement.service.impl.wms;

import com.design.warehousemanagement.mapper.wms.InboundOrderMapper;
import com.design.warehousemanagement.pojo.dto.inbound.InboundBatchImportRequest;
import com.design.warehousemanagement.pojo.dto.inbound.InboundOrderCreateRequest;
import com.design.warehousemanagement.pojo.dto.inbound.InboundOrderDetailCreateRequest;
import com.design.warehousemanagement.pojo.vo.inbound.*;
import com.design.warehousemanagement.service.wms.InboundOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundOrderServiceImpl implements InboundOrderService {

    private static final String SYSTEM_OPERATOR = "SYSTEM";
    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter CSV_PLAN_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InboundOrderMapper inboundOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundCreateResultVO createOrder(InboundOrderCreateRequest request) {
        validateOrderType(request.getOrderType());

        InboundOrderInsertVO order = new InboundOrderInsertVO();
        order.setOrderNo(generateOrderNo());
        order.setSourceNo(request.getSourceNo());
        order.setOrderType(request.getOrderType());
        order.setSupplierName(request.getSupplierName());
        order.setContactPerson(request.getContactPerson());
        order.setContactPhone(request.getContactPhone());
        order.setTotalQuantity(sumPlannedQuantity(request.getDetails()));
        order.setActualQuantity(0);
        order.setOrderStatus(0);
        order.setPlanTime(request.getPlanTime());
        order.setRemark(request.getRemark());
        order.setCreateBy(SYSTEM_OPERATOR);
        order.setUpdateBy(SYSTEM_OPERATOR);
        inboundOrderMapper.insertOrder(order);

        List<InboundOrderDetailInsertVO> detailRows = buildDetailRows(order.getId(), request.getDetails());
        inboundOrderMapper.batchInsertDetails(detailRows);

        return new InboundCreateResultVO(order.getId(), order.getOrderNo(), order.getTotalQuantity());
    }

    @Override
    public InboundBatchImportResultVO batchImport(InboundBatchImportRequest request) {
        return doBatchImport(request.getOrders());
    }

    @Override
    public InboundBatchImportResultVO batchImportFromCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("导入文件不能为空");
        }
        List<InboundOrderCreateRequest> orders = parseCsv(file);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("CSV未解析到任何有效数据");
        }
        return doBatchImport(orders);
    }

    private InboundBatchImportResultVO doBatchImport(List<InboundOrderCreateRequest> orders) {
        List<InboundCreateResultVO> successOrders = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            InboundOrderCreateRequest order = orders.get(i);
            int rowNumber = i + 1;
            try {
                InboundCreateResultVO result = createOrder(order);
                successOrders.add(result);
            } catch (Exception e) {
                String msg = String.format("第%d条导入失败: %s", rowNumber, e.getMessage());
                log.warn(msg, e);
                errors.add(msg);
            }
        }

        return new InboundBatchImportResultVO(successOrders.size(), errors.size(), successOrders, errors);
    }

    private List<InboundOrderCreateRequest> parseCsv(MultipartFile file) {
        Map<String, InboundOrderCreateRequest> orderMap = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null) {
                return new ArrayList<>();
            }
            validateCsvHeader(header);

            String line;
            int row = 1;
            while ((line = reader.readLine()) != null) {
                row++;
                if (line.isBlank()) {
                    continue;
                }
                String[] cols = splitCsvLine(line);
                if (cols.length < 11) {
                    throw new IllegalArgumentException("CSV第" + row + "行字段不足，至少11列");
                }

                final int currentRow = row;
                String sourceNo = trim(cols[0]);
                Integer orderType = parseInteger(cols[1], "orderType", currentRow);
                String supplierName = trim(cols[2]);
                if (supplierName == null || supplierName.isEmpty()) {
                    throw new IllegalArgumentException("CSV第" + currentRow + "行supplierName不能为空");
                }

                InboundOrderCreateRequest order = orderMap.computeIfAbsent(buildOrderGroupKey(sourceNo, orderType, supplierName),
                        k -> buildOrderFromCsv(cols, sourceNo, orderType, supplierName, currentRow));

                InboundOrderDetailCreateRequest detail = new InboundOrderDetailCreateRequest();
                detail.setModelCode(require(cols[7], "modelCode", currentRow));
                detail.setBatchNo(trim(cols[8]));
                detail.setPreferredBinCode(trim(cols[9]));
                detail.setPlannedQuantity(parseInteger(cols[10], "plannedQuantity", currentRow));
                if (detail.getPlannedQuantity() <= 0) {
                    throw new IllegalArgumentException("CSV第" + currentRow + "行plannedQuantity必须大于0");
                }
                order.getDetails().add(detail);
            }
        } catch (IOException e) {
            throw new IllegalStateException("读取CSV失败", e);
        }

        return new ArrayList<>(orderMap.values());
    }

    private InboundOrderCreateRequest buildOrderFromCsv(String[] cols, String sourceNo, Integer orderType,
                                                        String supplierName, int row) {
        InboundOrderCreateRequest order = new InboundOrderCreateRequest();
        order.setSourceNo(sourceNo);
        order.setOrderType(orderType);
        order.setSupplierName(supplierName);
        order.setContactPerson(trim(cols[3]));
        order.setContactPhone(trim(cols[4]));
        String planTime = trim(cols[5]);
        if (planTime != null && !planTime.isEmpty()) {
            try {
                order.setPlanTime(LocalDateTime.parse(planTime, CSV_PLAN_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("CSV第" + row + "行planTime格式错误，应为yyyy-MM-dd HH:mm:ss");
            }
        }
        order.setRemark(trim(cols[6]));
        order.setDetails(new ArrayList<>());
        return order;
    }

    private void validateCsvHeader(String header) {
        String expected = "sourceNo,orderType,supplierName,contactPerson,contactPhone,planTime,remark,modelCode,batchNo,preferredBinCode,plannedQuantity";
        if (!expected.equalsIgnoreCase(header.trim())) {
            throw new IllegalArgumentException("CSV表头不正确，必须为: " + expected);
        }
    }

    private String[] splitCsvLine(String line) {
        return line.split(",", -1);
    }

    private String buildOrderGroupKey(String sourceNo, Integer orderType, String supplierName) {
        String source = sourceNo == null || sourceNo.isEmpty() ? "NO_SOURCE" : sourceNo;
        return source + "|" + orderType + "|" + supplierName;
    }

    private String require(String value, String fieldName, int row) {
        String cleaned = trim(value);
        if (cleaned == null || cleaned.isEmpty()) {
            throw new IllegalArgumentException("CSV第" + row + "行" + fieldName + "不能为空");
        }
        return cleaned;
    }

    private Integer parseInteger(String value, String fieldName, int row) {
        try {
            return Integer.parseInt(require(value, fieldName, row));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("CSV第" + row + "行" + fieldName + "必须为数字");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private List<InboundOrderDetailInsertVO> buildDetailRows(Long orderId, List<InboundOrderDetailCreateRequest> details) {
        List<InboundOrderDetailInsertVO> rows = new ArrayList<>(details.size());
        for (InboundOrderDetailCreateRequest detail : details) {
            MotorcycleModelLiteVO model = inboundOrderMapper.findModelByCode(detail.getModelCode());
            if (model == null) {
                throw new IllegalArgumentException("车型编码不存在或不可用: " + detail.getModelCode());
            }
            rows.add(InboundOrderDetailInsertVO.builder()
                    .orderId(orderId)
                    .modelId(model.getId())
                    .modelCode(model.getModelCode())
                    .modelName(model.getModelName())
                    .batchNo(detail.getBatchNo())
                    .suggestedBinCode(detail.getPreferredBinCode())
                    .plannedQuantity(detail.getPlannedQuantity())
                    .build());
        }
        return rows;
    }

    private int sumPlannedQuantity(List<InboundOrderDetailCreateRequest> details) {
        return details.stream().mapToInt(InboundOrderDetailCreateRequest::getPlannedQuantity).sum();
    }

    private void validateOrderType(Integer orderType) {
        if (orderType == null || orderType < 1 || orderType > 4) {
            throw new IllegalArgumentException("orderType仅支持1~4");
        }
    }

    private String generateOrderNo() {
        for (int retry = 0; retry < 8; retry++) {
            String suffix = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
            String orderNo = "IN" + LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER) + suffix;
            if (inboundOrderMapper.existsOrderNo(orderNo) == 0) {
                return orderNo;
            }
        }
        throw new IllegalStateException("入库单号生成失败，请重试");
    }
}
