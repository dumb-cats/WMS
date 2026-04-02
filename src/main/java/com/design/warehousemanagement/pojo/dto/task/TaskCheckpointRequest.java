package com.design.warehousemanagement.pojo.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCheckpointRequest {

    @NotNull(message = "checkpointType不能为空")
    private Integer checkpointType;

    @NotBlank(message = "scanContent不能为空")
    private String scanContent;

    private String expectedContent;

    @NotNull(message = "operatorId不能为空")
    private Long operatorId;

    private String deviceId;

    private String locationInfo;

    private String remark;
}
