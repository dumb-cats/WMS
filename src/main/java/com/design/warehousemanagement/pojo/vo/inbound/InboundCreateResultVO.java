package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InboundCreateResultVO {
    private Long orderId;
    private String orderNo;
    private Integer totalQuantity;
}
