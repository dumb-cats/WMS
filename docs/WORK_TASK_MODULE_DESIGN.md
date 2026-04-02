# 工作任务模块详细设计

## 1. 模块概述
本模块负责入库任务生成与任务执行生命周期管理，覆盖任务生成、开工、检查点记录、任务完成、任务放弃，并在任务完成时完成库存落账。

## 2. 功能详细描述
1. 按入库单生成任务（支持幂等跳过）。
2. 任务开工（状态 0/1 -> 2）。
3. 检查点记录（扫码+校验）。
4. 任务完成（状态 2 -> 3）。
5. 任务放弃（状态 0/1/2 -> 4）。
6. 完成任务时更新库存快照与库存流水，并回写入库单状态。

## 3. 接口设计
- `POST /wms/tasks/inbound-orders/{inboundOrderId}/generate`
- `POST /wms/tasks/{taskId}/start`
- `POST /wms/tasks/{taskId}/checkpoint`
- `POST /wms/tasks/{taskId}/complete`
- `POST /wms/tasks/{taskId}/abandon`

## 4. 数据结构设计
- 任务表：`wms_work_task`
- 检查点表：`wms_task_checkpoint`
- 库存快照：`wms_inventory`
- 库存流水：`wms_inventory_movement`
- DTO：`InboundTaskGenerateRequest`、`TaskStartRequest`、`TaskCheckpointRequest`、`TaskCompleteRequest`、`TaskAbandonRequest`
- VO：`InboundTaskGenerateResultVO`、`TaskActionResultVO`、`WorkTaskInsertVO`、`WorkTaskLiteVO`、`InventoryLiteVO`

## 5. 业务流程设计
1. 任务生成：校验单据 -> 遍历明细 -> 幂等判断 -> 生成任务。
2. 开工：状态检查后进入执行中。
3. 检查点：记录扫码内容与校验结果。
4. 完成：更新库存快照 -> 写库存流水 -> 更新任务状态 -> 回写单据。
5. 放弃：写放弃原因并结束任务。

## 6. 异常处理设计
- 任务不存在：返回业务异常。
- 状态非法流转：拒绝执行。
- 完成任务时状态非执行中：拒绝。
- 库存更新失败：事务回滚。

## 7. 安全设计
1. 全流程建议接入 JWT + RBAC（当前预留）。
2. 关键动作（complete/abandon）要求 operatorId。
3. 检查点保留审计信息（device/location/time）。
4. 建议后续补充接口幂等键与防重放策略。
