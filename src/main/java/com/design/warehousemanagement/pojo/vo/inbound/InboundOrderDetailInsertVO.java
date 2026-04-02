package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InboundOrderDetailInsertVO {
    /** 所属入库单ID */
    private Long orderId;
    /** 车型ID */
    private Long modelId;
    /** 摩托车实例ID（按单车追踪时可用） */
    private Long motorcycleId;
    /** 车架号VIN */
    private String vin;
    /** 目标货位ID */
    private Long targetBinId;
    /** 计划入库数量 */
    private Integer plannedQuantity;
    /** 备注 */
    private String remark;
    /** 创建人 */
    private String createBy;
    /** 更新人 */
    private String updateBy;
}
