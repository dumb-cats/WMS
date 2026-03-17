package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 任务步骤存证表（扫码验证记录）
 * @TableName task_checkpoints
 */
@TableName(value ="task_checkpoints")
@Data
public class TaskCheckpoints {
    /**
     * 存证ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的任务ID
     */
    private Integer taskId;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 操作类型：SCAN_SKU, SCAN_LOCATION
     */
    private String operationType;

    /**
     * 扫码获得的值
     */
    private String scannedCode;

    /**
     * 验证结果
     */
    private Boolean isCorrect;

    /**
     * 记录时间
     */
    private Date createdAt;
}
