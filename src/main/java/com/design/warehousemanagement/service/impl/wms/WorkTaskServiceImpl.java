package com.design.warehousemanagement.service.impl.wms;

import com.design.warehousemanagement.mapper.wms.WorkTaskMapper;
import com.design.warehousemanagement.pojo.dto.task.InboundTaskGenerateRequest;
import com.design.warehousemanagement.pojo.vo.task.InboundOrderDetailLiteVO;
import com.design.warehousemanagement.pojo.vo.task.InboundOrderLiteVO;
import com.design.warehousemanagement.pojo.vo.task.InboundTaskGenerateResultVO;
import com.design.warehousemanagement.pojo.vo.task.WorkTaskInsertVO;
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
        InboundOrderLiteVO order = workTaskMapper.findInboundOrderById(inboundOrderId);
        if (order == null) {
            throw new IllegalArgumentException("入库单不存在: " + inboundOrderId);
        }
        if (order.getOrderStatus() != null && (order.getOrderStatus() == 3 || order.getOrderStatus() == 4)) {
            throw new IllegalArgumentException("入库单已完成或已取消，不能继续生成任务");
        }

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
                int activeCount = workTaskMapper.countActiveInboundTask(inboundOrderId, detail.getModelId(), detail.getTargetBinId());
                if (activeCount > 0) {
                    skipped++;
                    warnings.add("明细" + detail.getId() + "存在待执行任务，已跳过");
                    continue;
                }
            }

            String taskNo = generateTaskNo();
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

    private String generateTaskNo() {
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
