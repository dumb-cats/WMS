package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 库存移动日志表
 * @TableName inventory_movements
 */
@TableName(value ="inventory_movements")
@Data
public class InventoryMovements {
    /**
     * 流水ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的任务ID
     */
    private Integer workTaskId;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 变更数量
     */
    private Integer changeQuantity;

    /**
     * 源库位ID
     */
    private Integer fromLocationId;

    /**
     * 目标库位ID
     */
    private Integer toLocationId;

    /**
     * 移动类型：IN, OUT, ADJ
     */
    private String movementType;

    /**
     * 创建时间
     */
    private Date createdAt;
}
