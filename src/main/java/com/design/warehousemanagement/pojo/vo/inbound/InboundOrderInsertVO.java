package com.design.warehousemanagement.pojo.vo.inbound;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InboundOrderInsertVO {
    private Long id;
    private String orderNo;
    private String sourceNo;
    private Integer orderType;
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private Integer totalQuantity;
    private Integer actualQuantity;
    private Integer orderStatus;
    private LocalDateTime planTime;
    private String remark;
    private String createBy;
    private String updateBy;
}
