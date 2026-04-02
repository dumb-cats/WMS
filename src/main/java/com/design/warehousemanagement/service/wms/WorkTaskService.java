package com.design.warehousemanagement.service.wms;

import com.design.warehousemanagement.pojo.dto.task.*;
import com.design.warehousemanagement.pojo.vo.task.InboundTaskGenerateResultVO;
import com.design.warehousemanagement.pojo.vo.task.TaskActionResultVO;

public interface WorkTaskService {

    InboundTaskGenerateResultVO generateInboundTasks(Long inboundOrderId, InboundTaskGenerateRequest request);

    TaskActionResultVO startTask(Long taskId, TaskStartRequest request);

    TaskActionResultVO checkpointTask(Long taskId, TaskCheckpointRequest request);

    TaskActionResultVO completeTask(Long taskId, TaskCompleteRequest request);

    TaskActionResultVO abandonTask(Long taskId, TaskAbandonRequest request);
}
