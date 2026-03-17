package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 物料表
 * @TableName products
 */
@TableName(value ="products")
@Data
public class Products {
    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * SKU编码
     */
    private String sku;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品类别
     */
    private String category;

    /**
     * 单位
     */
    private String unit;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 当前库存
     */
    private Integer stock;

    /**
     * 最小库存（预警）
     */
    private Integer minStock;

    /**
     * 最大库存（预警）
     */
    private Integer maxStock;

    /**
     * ABC分类 (A/B/C)
     */
    private String abcClass;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
