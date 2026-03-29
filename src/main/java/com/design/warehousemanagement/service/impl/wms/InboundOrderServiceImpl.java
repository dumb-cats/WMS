package com.design.warehousemanagement.service.impl.wms;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.design.warehousemanagement.mapper.wms.InboundOrderMapper;
import com.design.warehousemanagement.pojo.dto.inbound.InboundBatchImportRequest;
import com.design.warehousemanagement.pojo.dto.inbound.InboundImportRowDTO;
import com.design.warehousemanagement.pojo.dto.inbound.InboundOrderCreateRequest;
import com.design.warehousemanagement.pojo.dto.inbound.InboundOrderDetailCreateRequest;
import com.design.warehousemanagement.pojo.vo.inbound.*;
import com.design.warehousemanagement.service.wms.InboundOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public InboundBatchImportResultVO batchImportFromFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("导入文件不能为空");
        }
        List<InboundOrderCreateRequest> orders = parseByEasyExcel(file);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("导入文件未解析到任何有效数据");
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

    private List<InboundOrderCreateRequest> parseByEasyExcel(MultipartFile file) {
        List<InboundImportRowDTO> rows = readRows(file);
        Map<String, InboundOrderCreateRequest> orderMap = new LinkedHashMap<>();

        for (int i = 0; i < rows.size(); i++) {
            int row = i + 2;
            InboundImportRowDTO rowData = rows.get(i);
            if (isBlank(rowData.getSupplierName()) && isBlank(rowData.getModelCode())) {
                continue;
            }

            Integer orderType = rowData.getOrderType();
            if (orderType == null) {
                throw new IllegalArgumentException("第" + row + "行 orderType 不能为空");
            }
            String supplierName = require(rowData.getSupplierName(), "supplierName", row);

            InboundOrderCreateRequest order = orderMap.computeIfAbsent(
                    buildOrderGroupKey(rowData.getSourceNo(), orderType, supplierName),
                    key -> buildOrderFromRow(rowData, row, supplierName, orderType)
            );

            InboundOrderDetailCreateRequest detail = new InboundOrderDetailCreateRequest();
            detail.setModelCode(require(rowData.getModelCode(), "modelCode", row));
            detail.setBatchNo(trim(rowData.getBatchNo()));
            detail.setPreferredBinCode(trim(rowData.getPreferredBinCode()));
            if (rowData.getPlannedQuantity() == null || rowData.getPlannedQuantity() <= 0) {
                throw new IllegalArgumentException("第" + row + "行 plannedQuantity 必须大于0");
            }
            detail.setPlannedQuantity(rowData.getPlannedQuantity());
            order.getDetails().add(detail);
        }

        return new ArrayList<>(orderMap.values());
    }

    private List<InboundImportRowDTO> readRows(MultipartFile file) {
        String filename = file.getOriginalFilename();
        ExcelTypeEnum excelType = resolveExcelType(filename);
        try {
            return EasyExcel.read(file.getInputStream())
                    .head(InboundImportRowDTO.class)
                    .excelType(excelType)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            throw new IllegalStateException("读取导入文件失败", e);
        }
    }

    private ExcelTypeEnum resolveExcelType(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("文件名为空，无法识别格式");
        }
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return ExcelTypeEnum.CSV;
        }
        if (lowerName.endsWith(".xlsx")) {
            return ExcelTypeEnum.XLSX;
        }
        if (lowerName.endsWith(".xls")) {
            return ExcelTypeEnum.XLS;
        }
        throw new IllegalArgumentException("仅支持 .csv/.xls/.xlsx 文件");
    }

    private InboundOrderCreateRequest buildOrderFromRow(InboundImportRowDTO rowData, int row,
                                                        String supplierName, Integer orderType) {
        InboundOrderCreateRequest order = new InboundOrderCreateRequest();
        order.setSourceNo(trim(rowData.getSourceNo()));
        order.setOrderType(orderType);
        order.setSupplierName(supplierName);
        order.setContactPerson(trim(rowData.getContactPerson()));
        order.setContactPhone(trim(rowData.getContactPhone()));
        String planTime = trim(rowData.getPlanTime());
        if (!isBlank(planTime)) {
            try {
                order.setPlanTime(LocalDateTime.parse(planTime, CSV_PLAN_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("第" + row + "行 planTime 格式错误，应为 yyyy-MM-dd HH:mm:ss");
            }
        }
        order.setRemark(trim(rowData.getRemark()));
        order.setDetails(new ArrayList<>());
        return order;
    }

    private String buildOrderGroupKey(String sourceNo, Integer orderType, String supplierName) {
        String source = isBlank(sourceNo) ? "NO_SOURCE" : sourceNo.trim();
        return source + "|" + orderType + "|" + supplierName;
    }

    private String require(String value, String fieldName, int row) {
        String cleaned = trim(value);
        if (isBlank(cleaned)) {
            throw new IllegalArgumentException("第" + row + "行 " + fieldName + " 不能为空");
        }
        return cleaned;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
