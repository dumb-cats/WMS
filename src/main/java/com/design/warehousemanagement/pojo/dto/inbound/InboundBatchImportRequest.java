package com.design.warehousemanagement.pojo.dto.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 入库单批量导入请求。
 */
@Data
public class InboundBatchImportRequest {

    /** 批量导入的入库单集合。 */
    @Valid
    @NotEmpty(message = "orders不能为空")
    private List<InboundOrderCreateRequest> orders;
}
