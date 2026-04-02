package com.design.warehousemanagement.pojo.vo.task;

import lombok.Data;

@Data
public class WorkTaskLiteVO {
    private Long id;
    private String taskNo;
    private Integer taskType;
    private Long warehouseId;
    private Long inboundOrderId;
    private Long modelId;
    private Long motorcycleId;
    private String vin;
    private Long sourceBinId;
    private Long targetBinId;
    private Integer taskStatus;
}
