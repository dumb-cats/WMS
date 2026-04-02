package com.design.warehousemanagement.service.impl.wms;

import com.design.warehousemanagement.mapper.wms.WorkTaskMapper;
import com.design.warehousemanagement.pojo.dto.task.*;
import com.design.warehousemanagement.pojo.vo.task.*;
import com.design.warehousemanagement.service.wms.WorkTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class WorkTaskServiceImpl implements WorkTaskService {

    private static final DateTimeFormatter TASK_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final WorkTaskMapper workTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundTaskGenerateResultVO generateInboundTasks(Long inboundOrderId, InboundTaskGenerateRequest request) {
        // 1) 先校验单据是否存在以及是否允许继续生成任务。
        InboundOrderLiteVO order = workTaskMapper.findInboundOrderById(inboundOrderId);
        if (order == null) {
            throw new IllegalArgumentException("入库单不存在: " + inboundOrderId);
        }
        if (order.getOrderStatus() != null && (order.getOrderStatus() == 3 || order.getOrderStatus() == 4)) {
            throw new IllegalArgumentException("入库单已完成或已取消，不能继续生成任务");
        }

        // 2) 拉取所有入库明细，按明细生成任务（当前策略：一条明细一条任务）。
        List<InboundOrderDetailLiteVO> details = workTaskMapper.findInboundDetailsByOrderId(inboundOrderId);
        if (details.isEmpty()) {
            throw new IllegalArgumentException("入库单没有明细，不能生成任务");
        }

        List<String> taskNos = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int skipped = 0;

        for (InboundOrderDetailLiteVO detail : details) {
            // detail_status=2 表示已入库，直接跳过。
            if (detail.getDetailStatus() != null && detail.getDetailStatus() == 2) {
                skipped++;
                warnings.add("明细" + detail.getId() + "已入库，跳过生成");
                continue;
            }

            if (Boolean.TRUE.equals(request.getKeepExistingTasks())) {
                // 幂等保护：若已存在待执行任务，则不重复下发。
                int activeCount = workTaskMapper.countActiveInboundTask(inboundOrderId, detail.getModelId(), detail.getTargetBinId());
                if (activeCount > 0) {
                    skipped++;
                    warnings.add("明细" + detail.getId() + "存在待执行任务，已跳过");
                    continue;
                }
            }

            String taskNo = generateTaskNo();
            // 任务关键字段来源：
            // - 仓库、单据来自主单
            // - 车型、VIN、目标货位来自明细
            WorkTaskInsertVO task = WorkTaskInsertVO.builder()
                    .taskNo(taskNo)
                    .taskType(1)
                    .warehouseId(order.getWarehouseId())
                    .inboundOrderId(order.getId())
                    .modelId(detail.getModelId())
                    .motorcycleId(detail.getMotorcycleId())
                    .vin(detail.getVin())
                    .targetBinId(detail.getTargetBinId())
                    .taskStatus(0)
                    .priority(request.getPriority())
                    .createBy(request.getOperator())
                    .updateBy(request.getOperator())
                    .build();
            workTaskMapper.insertWorkTask(task);
            taskNos.add(taskNo);
        }

        workTaskMapper.updateInboundOrderStatusToPending(inboundOrderId, request.getOperator());

        return InboundTaskGenerateResultVO.builder()
                .inboundOrderId(order.getId())
                .inboundOrderNo(order.getOrderNo())
                .generatedCount(taskNos.size())
                .skippedCount(skipped)
                .generatedTaskNos(taskNos)
                .warnings(warnings)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskActionResultVO startTask(Long taskId, TaskStartRequest request) {
        // 开工只允许 0/1 -> 2，具体限制由 SQL 条件保证。
        WorkTaskLiteVO task = mustGetTask(taskId);
        int updated = workTaskMapper.startTask(taskId, request.getOperatorId());
        if (updated == 0) {
            throw new IllegalArgumentException("任务状态不允许开工，当前状态=" + task.getTaskStatus());
        }
        return TaskActionResultVO.builder()
                .taskId(task.getId())
                .taskNo(task.getTaskNo())
                .taskStatus(2)
                .message("任务已开始执行")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskActionResultVO checkpointTask(Long taskId, TaskCheckpointRequest request) {
        WorkTaskLiteVO task = mustGetTask(taskId);
        Integer verifyResult = 1;
        // expectedContent 为空时默认通过；不为空时按“期望值=扫描值”判定。
        if (request.getExpectedContent() != null && !request.getExpectedContent().isBlank()) {
            verifyResult = request.getExpectedContent().equals(request.getScanContent()) ? 1 : 0;
        }

        workTaskMapper.insertCheckpoint(
                taskId,
                request.getCheckpointType(),
                request.getScanContent(),
                request.getExpectedContent(),
                verifyResult,
                request.getOperatorId(),
                request.getDeviceId(),
                request.getLocationInfo(),
                request.getRemark(),
                LocalDateTime.now()
        );

        return TaskActionResultVO.builder()
                .taskId(task.getId())
                .taskNo(task.getTaskNo())
                .taskStatus(task.getTaskStatus())
                .message(verifyResult == 1 ? "检查点校验通过" : "检查点校验失败")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskActionResultVO completeTask(Long taskId, TaskCompleteRequest request) {
        WorkTaskLiteVO task = mustGetTask(taskId);
        // 完成任务必须在“执行中”状态，避免重复完工或跨状态提交。
        if (task.getTaskStatus() == null || task.getTaskStatus() != 2) {
            throw new IllegalArgumentException("仅执行中的任务可以完成，当前状态=" + task.getTaskStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        // 1) 查询当前库存快照，计算变更前后数量。
        InventoryLiteVO inventory = workTaskMapper.findInventory(
                task.getWarehouseId(), task.getModelId(), task.getTargetBinId(), task.getMotorcycleId());
        int beforeQty = inventory == null || inventory.getQuantity() == null ? 0 : inventory.getQuantity();
        int delta = request.getQuantity();
        int afterQty = beforeQty + delta;

        // 2) 落库存快照：存在则累加，不存在则新建。
        if (inventory == null) {
            workTaskMapper.insertInventory(task.getWarehouseId(), task.getModelId(), task.getTargetBinId(),
                    task.getMotorcycleId(), delta, safeOperatorName(request.getOperatorName()), now);
        } else {
            workTaskMapper.updateInventory(inventory.getId(), delta, safeOperatorName(request.getOperatorName()), now);
        }

        // 3) 写库存流水：记录任务、单据、操作人、前后数量，便于审计追溯。
        InboundOrderLiteVO order = task.getInboundOrderId() == null ? null : workTaskMapper.findInboundOrderById(task.getInboundOrderId());
        workTaskMapper.insertMovement(task.getWarehouseId(), task.getModelId(), task.getMotorcycleId(), task.getVin(),
                beforeQty, afterQty, task.getTargetBinId(), taskId,
                order == null ? null : order.getOrderNo(), request.getOperatorId(), safeOperatorName(request.getOperatorName()), now,
                request.getRemark());

        // 4) 更新任务状态为已完成。
        int updated = workTaskMapper.completeTask(taskId, request.getOperatorId());
        if (updated == 0) {
            throw new IllegalStateException("任务完成失败，请刷新后重试");
        }

        // 5) 回写入库单实际数量，并按累计数量刷新单据状态（部分/完成）。
        if (task.getInboundOrderId() != null) {
            workTaskMapper.incrementInboundOrderActualQuantity(task.getInboundOrderId(), delta, safeOperatorName(request.getOperatorName()), now);
            workTaskMapper.refreshInboundOrderStatus(task.getInboundOrderId(), safeOperatorName(request.getOperatorName()));
        }

        return TaskActionResultVO.builder()
                .taskId(task.getId())
                .taskNo(task.getTaskNo())
                .taskStatus(3)
                .message("任务已完成并完成库存落账")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskActionResultVO abandonTask(Long taskId, TaskAbandonRequest request) {
        // 放弃支持 0/1/2 -> 4，必须写入放弃原因。
        WorkTaskLiteVO task = mustGetTask(taskId);
        int updated = workTaskMapper.abandonTask(taskId, request.getOperatorId(), request.getReason(), LocalDateTime.now());
        if (updated == 0) {
            throw new IllegalArgumentException("当前状态不允许放弃，当前状态=" + task.getTaskStatus());
        }

        return TaskActionResultVO.builder()
                .taskId(task.getId())
                .taskNo(task.getTaskNo())
                .taskStatus(4)
                .message("任务已放弃")
                .build();
    }

    private WorkTaskLiteVO mustGetTask(Long taskId) {
        WorkTaskLiteVO task = workTaskMapper.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        return task;
    }

    private String safeOperatorName(String operatorName) {
        return operatorName == null || operatorName.isBlank() ? "SYSTEM" : operatorName;
    }

    private String generateTaskNo() {
        // 任务号生成规则：TK + 时间戳 + 4位随机，最多重试8次避免冲突。
        for (int retry = 0; retry < 8; retry++) {
            String suffix = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
            String taskNo = "TK" + LocalDateTime.now().format(TASK_NO_TIME_FORMATTER) + suffix;
            if (workTaskMapper.existsTaskNo(taskNo) == 0) {
                return taskNo;
            }
        }
        throw new IllegalStateException("任务编号生成失败，请重试");
    }
}
