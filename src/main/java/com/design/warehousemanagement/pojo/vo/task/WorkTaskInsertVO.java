package com.design.warehousemanagement.pojo.vo.task;

import lombok.Builder;
import lombok.Data;

/**
 * 工作任务落库对象。
 */
@Data
@Builder
public class WorkTaskInsertVO {
    /** 任务编号 */
    private String taskNo;
    /** 任务类型：1入库 2出库 3移库 */
    private Integer taskType;
    /** 所属仓库ID */
    private Long warehouseId;
    /** 关联入库单ID */
    private Long inboundOrderId;
    /** 车型ID */
    private Long modelId;
    /** 单车ID */
    private Long motorcycleId;
    /** 车架号 */
    private String vin;
    /** 目标货位ID */
    private Long targetBinId;
    /** 任务状态 */
    private Integer taskStatus;
    /** 优先级 */
    private Integer priority;
    /** 创建人 */
    private String createBy;
    /** 更新人 */
    private String updateBy;
}
