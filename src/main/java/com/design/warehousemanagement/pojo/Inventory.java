package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.util.Date;

/**
 * 实时库存表
 * @TableName inventory
 */
@TableName(value ="inventory")
@Data
public class Inventory {
    /**
     * 库存ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 库位ID
     */
    private Integer locationId;

    /**
     * 现有数量
     */
    private Integer quantity;

    /**
     * 锁定数量（被任务占用）
     */
    private Integer lockedQuantity;

    /**
     * 更新时间
     */
    private Date lastUpdatedAt;
}
