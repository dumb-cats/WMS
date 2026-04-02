package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

/**
 * 库存快照轻量对象。
 */
@Data
public class InventoryLiteVO {
    private Long id;
    private Integer quantity;
    private Integer availableQuantity;
}
