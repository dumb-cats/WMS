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
- `POST /wms/tasks/dispatch`
- `GET /wms/tasks/workers/{workerId}/todo`

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
6. 分配：按优先级拉取待分配任务，按工人列表轮询分配。
7. 工人待办：按状态（已分配/执行中）查询个人任务列表。

## 6. 工人待办任务列表怎么分配
1. 先筛选候选任务：
   - 仅分配 `task_status = 0` 且 `assigned_user_id IS NULL` 的任务；
   - 按 `priority ASC, id ASC` 排序，取前 `dispatchLimit` 条；
   - 可通过 `maxPriority` 控制本轮是否只分配高优任务。
2. 再执行分配算法：
   - 使用 Round-Robin（轮询）在 `workerIds` 中均匀发放；
   - 每条任务落库时二次校验状态仍为待分配，避免并发重复领取。
3. 最终落状态：
   - 任务状态改为 `1`（已分配），写入 `assigned_user_id`、`assign_time`、`last_operator_id`；
   - 分配结果返回成功条数、跳过条数、任务号列表、告警信息。
4. 工人任务列表：
   - 接口 `GET /wms/tasks/workers/{workerId}/todo?limit=20`；
   - 默认返回该工人 `task_status in (1,2)` 的任务，且执行中优先展示。

## 7. 异常处理设计
- 任务不存在：返回业务异常。
- 状态非法流转：拒绝执行。
- 完成任务时状态非执行中：拒绝。
- 库存更新失败：事务回滚。

## 8. 安全设计
1. 全流程建议接入 JWT + RBAC（当前预留）。
2. 关键动作（complete/abandon）要求 operatorId。
3. 检查点保留审计信息（device/location/time）。
4. 建议后续补充接口幂等键与防重放策略。
