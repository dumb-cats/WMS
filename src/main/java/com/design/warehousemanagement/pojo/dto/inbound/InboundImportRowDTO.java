package com.design.warehousemanagement.pojo.dto.inbound;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * EasyExcel 导入行模型（支持 CSV/XLSX 同结构）。
 */
@Data
public class InboundImportRowDTO {

    @ExcelProperty(value = "sourceNo", index = 0)
    private String sourceNo;

    @ExcelProperty(value = "orderType", index = 1)
    private Integer orderType;

    @ExcelProperty(value = "supplierName", index = 2)
    private String supplierName;

    @ExcelProperty(value = "contactPerson", index = 3)
    private String contactPerson;

    @ExcelProperty(value = "contactPhone", index = 4)
    private String contactPhone;

    @ExcelProperty(value = "planTime", index = 5)
    private String planTime;

    @ExcelProperty(value = "remark", index = 6)
    private String remark;

    @ExcelProperty(value = "modelCode", index = 7)
    private String modelCode;

    @ExcelProperty(value = "batchNo", index = 8)
    private String batchNo;

    @ExcelProperty(value = "preferredBinCode", index = 9)
    private String preferredBinCode;

    @ExcelProperty(value = "plannedQuantity", index = 10)
    private Integer plannedQuantity;
}
