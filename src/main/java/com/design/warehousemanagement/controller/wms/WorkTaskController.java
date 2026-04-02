package com.design.warehousemanagement.controller.wms;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.dto.task.*;
import com.design.warehousemanagement.service.wms.WorkTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "任务管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wms/tasks")
public class WorkTaskController {

    private final WorkTaskService workTaskService;

    @Operation(summary = "按入库单生成入库任务")
    @PostMapping("/inbound-orders/{inboundOrderId}/generate")
    public Result generateInboundTasks(@PathVariable Long inboundOrderId,
                                       @Valid @RequestBody(required = false) InboundTaskGenerateRequest request) {
        InboundTaskGenerateRequest safeRequest = request == null ? new InboundTaskGenerateRequest() : request;
        return Result.success(workTaskService.generateInboundTasks(inboundOrderId, safeRequest));
    }

    @Operation(summary = "开始执行任务")
    @PostMapping("/{taskId}/start")
    public Result startTask(@PathVariable Long taskId, @Valid @RequestBody TaskStartRequest request) {
        return Result.success(workTaskService.startTask(taskId, request));
    }

    @Operation(summary = "记录任务检查点")
    @PostMapping("/{taskId}/checkpoint")
    public Result checkpointTask(@PathVariable Long taskId, @Valid @RequestBody TaskCheckpointRequest request) {
        return Result.success(workTaskService.checkpointTask(taskId, request));
    }

    @Operation(summary = "完成任务并落库存")
    @PostMapping("/{taskId}/complete")
    public Result completeTask(@PathVariable Long taskId, @Valid @RequestBody TaskCompleteRequest request) {
        return Result.success(workTaskService.completeTask(taskId, request));
    }

    @Operation(summary = "放弃任务")
    @PostMapping("/{taskId}/abandon")
    public Result abandonTask(@PathVariable Long taskId, @Valid @RequestBody TaskAbandonRequest request) {
        return Result.success(workTaskService.abandonTask(taskId, request));
    }

    @Operation(summary = "批量分配待处理任务到工人")
    @PostMapping("/dispatch")
    public Result dispatchTasks(@Valid @RequestBody TaskDispatchRequest request) {
        return Result.success(workTaskService.dispatchTasks(request));
    }

    @Operation(summary = "查询工人待办任务列表")
    @GetMapping("/workers/{workerId}/todo")
    public Result listWorkerTodoTasks(@PathVariable Long workerId,
                                      @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(workTaskService.listWorkerTodoTasks(workerId, limit));
    }
}
