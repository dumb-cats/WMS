package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

/**
 * 入库单轻量信息（供任务模块使用）。
 */
@Data
public class InboundOrderLiteVO {
    private Long id;
    private String orderNo;
    private Long warehouseId;
    private Integer orderStatus;
}
