package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 库位表
 * @TableName locations
 */
@TableName(value ="locations")
@Data
public class Locations {
    /**
     * 库位ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 库位编码，如 A-01-01-01
     */
    private String code;

    /**
     * 库位类型：ZONE, BIN
     */
    private String type;

    /**
     * 父级库位ID，用于建立层级关系
     */
    private Integer parentId;

    /**
     * 库位描述
     */
    private String description;

    /**
     * 物理坐标X（用于路径规划）
     */
    private BigDecimal coordinateX;

    /**
     * 物理坐标Y（用于路径规划）
     */
    private BigDecimal coordinateY;

    /**
     * 最大承重
     */
    private BigDecimal maxWeight;

    /**
     * 是否被占用
     */
    private Boolean isOccupied;

    /**
     * 当前停放的商品ID
     */
    private Integer currentProductId;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
