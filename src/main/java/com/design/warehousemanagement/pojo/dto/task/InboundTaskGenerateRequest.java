package com.design.warehousemanagement.pojo.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 入库任务生成请求。
 * keepExistingTasks=true 时跳过已存在的待执行任务，避免重复下发。
 */
@Data
public class InboundTaskGenerateRequest {

    private Boolean keepExistingTasks = true;

    /**
     * 默认优先级：1最高，10最低。
     */
    @Min(value = 1, message = "priority不能小于1")
    @Max(value = 10, message = "priority不能大于10")
    private Integer priority = 5;

    /**
     * 操作人（审计字段）。
     */
    private String operator = "SYSTEM";
}
