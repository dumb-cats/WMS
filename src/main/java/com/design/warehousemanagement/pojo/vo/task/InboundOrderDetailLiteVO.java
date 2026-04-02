package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

/**
 * 入库单明细轻量信息（供任务生成使用）。
 */
@Data
public class InboundOrderDetailLiteVO {
    private Long id;
    private Long modelId;
    private Long motorcycleId;
    private String vin;
    private Long targetBinId;
    private Integer detailStatus;
}
