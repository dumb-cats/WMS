package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

@Data
public class InboundOrderDetailLiteVO {
    private Long id;
    private Long modelId;
    private Long motorcycleId;
    private String vin;
    private Long targetBinId;
    private Integer detailStatus;
}
