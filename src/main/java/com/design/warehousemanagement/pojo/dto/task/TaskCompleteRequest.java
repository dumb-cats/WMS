package com.design.warehousemanagement.pojo.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务完工请求。
 */
@Data
public class TaskCompleteRequest {

    @NotNull(message = "operatorId不能为空")
    private Long operatorId;

    private String operatorName;

    @Min(value = 1, message = "quantity必须大于0")
    private Integer quantity = 1;

    private String remark;
}
