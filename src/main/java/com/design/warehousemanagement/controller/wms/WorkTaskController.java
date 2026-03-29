package com.design.warehousemanagement.controller.wms;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.dto.task.InboundTaskGenerateRequest;
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
}
