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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundOrderServiceImpl implements InboundOrderService {

    private static final String SYSTEM_OPERATOR = "SYSTEM";
    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

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
        List<InboundCreateResultVO> successOrders = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < request.getOrders().size(); i++) {
            InboundOrderCreateRequest order = request.getOrders().get(i);
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
        if (orderType < 1 || orderType > 4) {
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
