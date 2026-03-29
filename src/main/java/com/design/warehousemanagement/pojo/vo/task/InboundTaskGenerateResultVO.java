package com.design.warehousemanagement.pojo.vo.task;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InboundTaskGenerateResultVO {

    private Long inboundOrderId;
    private String inboundOrderNo;
    private Integer generatedCount;
    private Integer skippedCount;
    private List<String> generatedTaskNos;
    private List<String> warnings;
}
