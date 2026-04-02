package com.design.warehousemanagement.service.wms;

import com.design.warehousemanagement.pojo.dto.task.*;
import com.design.warehousemanagement.pojo.vo.task.InboundTaskGenerateResultVO;
import com.design.warehousemanagement.pojo.vo.task.TaskActionResultVO;
import com.design.warehousemanagement.pojo.vo.task.TaskDispatchResultVO;
import com.design.warehousemanagement.pojo.vo.task.WorkerTodoTaskVO;

import java.util.List;

public interface WorkTaskService {

    InboundTaskGenerateResultVO generateInboundTasks(Long inboundOrderId, InboundTaskGenerateRequest request);

    TaskActionResultVO startTask(Long taskId, TaskStartRequest request);

    TaskActionResultVO checkpointTask(Long taskId, TaskCheckpointRequest request);

    TaskActionResultVO completeTask(Long taskId, TaskCompleteRequest request);

    TaskActionResultVO abandonTask(Long taskId, TaskAbandonRequest request);

    TaskDispatchResultVO dispatchTasks(TaskDispatchRequest request);

    List<WorkerTodoTaskVO> listWorkerTodoTasks(Long workerId, Integer limit);
}
