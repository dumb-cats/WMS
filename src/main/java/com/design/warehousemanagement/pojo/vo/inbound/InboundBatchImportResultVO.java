package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InboundBatchImportResultVO {
    private Integer successCount;
    private Integer failCount;
    private List<InboundCreateResultVO> successOrders;
    private List<String> errors;
}
