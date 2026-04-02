package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

@Data
public class InboundOrderLiteVO {
    private Long id;
    private String orderNo;
    private Long warehouseId;
    private Integer orderStatus;
}
