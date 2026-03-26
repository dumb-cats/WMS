package com.design.warehousemanagement.pojo.dto.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class InboundBatchImportRequest {

    @Valid
    @NotEmpty(message = "orders不能为空")
    private List<InboundOrderCreateRequest> orders;
}
