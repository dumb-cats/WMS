package com.design.warehousemanagement.pojo.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务开工请求。
 */
@Data
public class TaskStartRequest {
    @NotNull(message = "operatorId不能为空")
    private Long operatorId;
}
