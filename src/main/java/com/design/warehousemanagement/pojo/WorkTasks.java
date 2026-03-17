package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.util.Date;

/**
 * 工作任务表
 * @TableName work_tasks
 */
@TableName(value ="work_tasks")
@Data
public class WorkTasks {
    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 任务类型：INBOUND, OUTBOUND, TRANSFER
     */
    private String taskType;

    /**
     * 任务状态：PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, ABORTED
     */
    private String status;

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 目标数量
     */
    private Integer targetQuantity;

    /**
     * 实际完成数量
     */
    private Integer actualQuantity;

    /**
     * 源库位ID（移库/出库用）
     */
    private Integer fromLocationId;

    /**
     * 目标库位ID（入库/移库用）
     */
    private Integer toLocationId;

    /**
     * 分配工人ID
     */
    private String assignedUserId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 放弃原因
     */
    private String abortReason;

    /**
     * 版本号（用于乐观锁）
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
