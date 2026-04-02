package com.design.warehousemanagement.pojo.vo.task;

import lombok.Builder;
import lombok.Data;

/**
 * 任务动作执行结果。
 */
@Data
@Builder
public class TaskActionResultVO {
    private Long taskId;
    private String taskNo;
    private Integer taskStatus;
    private String message;
}
