package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("assignment_rules")
public class AssignmentRule {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String ruleName;

    private String productCategory;

    private Integer targetZoneId;

    private Integer priority;

    private Boolean isActive;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
