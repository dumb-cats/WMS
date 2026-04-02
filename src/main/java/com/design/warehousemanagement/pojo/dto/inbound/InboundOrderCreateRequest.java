package com.design.warehousemanagement.pojo.dto.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InboundOrderCreateRequest {

    private String sourceNo;

    @NotNull(message = "orderType不能为空")
    private Integer orderType;

    @NotBlank(message = "supplierName不能为空")
    private String supplierName;

    private String contactPerson;

    private String contactPhone;

    private LocalDateTime planTime;

    private String remark;

    @Valid
    @NotEmpty(message = "details不能为空")
    private List<InboundOrderDetailCreateRequest> details;
}
