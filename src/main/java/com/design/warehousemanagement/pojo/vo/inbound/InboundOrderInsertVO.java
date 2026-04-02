package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入库单落库对象。
 */
@Data
public class InboundOrderInsertVO {
    /** 主键ID */
    private Long id;
    /** 入库单号 */
    private String orderNo;
    /** 目标仓库ID */
    private Long warehouseId;
    /** 来源单号（ERP/外部系统） */
    private String sourceNo;
    /** 入库类型：1采购 2退货 3调拨 4其他 */
    private Integer orderType;
    /** 供应商名称 */
    private String supplierName;
    /** 计划总数量 */
    private Integer totalQuantity;
    /** 实际总数量 */
    private Integer actualQuantity;
    /** 单据状态：0草稿 1待入库 2部分入库 3已完成 4已取消 */
    private Integer orderStatus;
    /** 计划入库时间 */
    private LocalDateTime planTime;
    /** 备注 */
    private String remark;
    /** 创建人 */
    private String createBy;
    /** 更新人 */
    private String updateBy;
}
