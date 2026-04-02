package com.design.warehousemanagement.pojo.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 任务批量分配请求。
 */
@Data
public class TaskDispatchRequest {

    @NotNull(message = "warehouseId不能为空")
    private Long warehouseId;

    @NotEmpty(message = "workerIds不能为空")
    private List<Long> workerIds;

    /**
     * 分配上限（本次最多分配多少条任务）。
     */
    @Min(value = 1, message = "dispatchLimit不能小于1")
    @Max(value = 500, message = "dispatchLimit不能大于500")
    private Integer dispatchLimit = 50;

    /**
     * 任务优先级过滤上限（值越小优先级越高）。
     */
    @Min(value = 1, message = "maxPriority不能小于1")
    @Max(value = 10, message = "maxPriority不能大于10")
    private Integer maxPriority = 10;

    @NotNull(message = "operatorId不能为空")
    private Long operatorId;
}
