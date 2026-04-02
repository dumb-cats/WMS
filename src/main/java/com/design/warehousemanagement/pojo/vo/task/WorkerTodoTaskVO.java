package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

/**
 * 工人待办任务列表项。
 */
@Data
public class WorkerTodoTaskVO {
    private Long taskId;
    private String taskNo;
    private Integer taskType;
    private Integer taskStatus;
    private Integer priority;
    private Long inboundOrderId;
    private Long modelId;
    private String vin;
    private Long targetBinId;
}
