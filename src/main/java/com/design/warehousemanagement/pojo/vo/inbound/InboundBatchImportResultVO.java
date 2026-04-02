package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 入库单批量导入执行结果。
 */
@Data
@AllArgsConstructor
public class InboundBatchImportResultVO {
    private Integer successCount;
    private Integer failCount;
    private List<InboundCreateResultVO> successOrders;
    private List<String> errors;
}
