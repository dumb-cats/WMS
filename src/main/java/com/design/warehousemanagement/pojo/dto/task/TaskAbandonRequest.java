package com.design.warehousemanagement.pojo.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskAbandonRequest {

    @NotNull(message = "operatorId不能为空")
    private Long operatorId;

    @NotBlank(message = "reason不能为空")
    private String reason;
}
