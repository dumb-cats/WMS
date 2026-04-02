package com.design.warehousemanagement.pojo.vo.task;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 任务批量分配执行结果。
 */
@Data
@Builder
public class TaskDispatchResultVO {
    private Integer requestedCount;
    private Integer assignedCount;
    private Integer skippedCount;
    private List<String> assignedTaskNos;
    private List<String> warnings;
}
