package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InboundOrderDetailInsertVO {
    private Long orderId;
    private Long modelId;
    private String modelCode;
    private String modelName;
    private String batchNo;
    private String suggestedBinCode;
    private Integer plannedQuantity;
}
