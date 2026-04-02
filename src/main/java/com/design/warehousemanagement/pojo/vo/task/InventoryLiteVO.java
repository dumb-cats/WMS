package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

@Data
public class InventoryLiteVO {
    private Long id;
    private Integer quantity;
    private Integer availableQuantity;
}
