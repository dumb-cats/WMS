package com.design.warehousemanagement.pojo.vo.task;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkTaskInsertVO {
    private String taskNo;
    private Integer taskType;
    private Long warehouseId;
    private Long inboundOrderId;
    private Long modelId;
    private Long motorcycleId;
    private String vin;
    private Long targetBinId;
    private Integer taskStatus;
    private Integer priority;
    private String createBy;
    private String updateBy;
}
