# 入库任务生成模块设计文档（第一阶段）

## 1. 目标与范围

### 1.1 目标
- 在“入库单创建完成”后，提供标准化的任务生成入口，落地任务驱动架构。
- 保障任务生成幂等（避免重复下发待执行任务）。
- 为下一阶段“任务执行 -> 库存快照 -> 库存流水”闭环打基础。

### 1.2 本期范围（已实现）
- 新增接口：`POST /wms/tasks/inbound-orders/{inboundOrderId}/generate`
- 任务生成规则：按入库单明细逐条生成入库任务（task_type=1）
- 幂等策略：同入库单+车型+目标货位，若已有待执行任务（状态0/1/2）则跳过
- 单据联动：生成任务后，将入库单从草稿推进到“待入库”

### 1.3 非目标（本期不做）
- 任务执行（开工、完工、放弃）接口
- 任务检查点扫码
- 库存与库存流水写入

---

## 2. 业务流程

1. 客户端调用生成接口，传入入库单ID。
2. 系统校验入库单存在且非完成/取消。
3. 查询入库单明细。
4. 对每条明细执行：
   - 若明细已入库（detail_status=2）则跳过；
   - 若 keepExistingTasks=true 且已有待执行任务则跳过；
   - 否则生成任务号并落库到 `wms_work_task`。
5. 更新入库单状态：草稿->待入库。
6. 返回生成结果：生成数、跳过数、任务编号、告警列表。

---

## 3. API 设计

## 3.1 生成入库任务
- **URL**: `/wms/tasks/inbound-orders/{inboundOrderId}/generate`
- **Method**: `POST`
- **RequestBody**:
```json
{
  "keepExistingTasks": true,
  "priority": 5,
  "operator": "SYSTEM"
}
```

- **Response（示例）**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "inboundOrderId": 1001,
    "inboundOrderNo": "IN202603291122339001",
    "generatedCount": 3,
    "skippedCount": 1,
    "generatedTaskNos": ["TK202603291123100001"],
    "warnings": ["明细2002存在待执行任务，已跳过"]
  }
}
```

---

## 4. 数据设计

### 4.1 读数据
- `wms_inbound_order`: 入库单基础信息、状态
- `wms_inbound_order_detail`: 明细模型、目标货位、明细状态

### 4.2 写数据
- `wms_work_task`: 写入任务记录
- `wms_inbound_order`: 状态草稿->待入库

### 4.3 任务号规则
- 格式：`TK + yyyyMMddHHmmss + 4位随机数`
- 落库前查重（`existsTaskNo`）

---

## 5. 幂等与一致性策略

### 5.1 幂等策略
- 对每条明细，查询是否已有待执行任务（状态0/1/2）：
  - 条件：`task_type=1 + inbound_order_id + model_id + target_bin_id`
  - 存在则跳过，防止重复下发。

### 5.2 事务边界
- 生成流程使用单事务：
  - 插入任务
  - 更新入库单状态
- 任一步失败整体回滚，避免“生成部分任务但状态未推进”的脏数据。

---

## 6. 异常策略

- 入库单不存在：抛业务异常
- 入库单状态为已完成/已取消：禁止生成
- 明细为空：禁止生成
- 任务号重试失败：抛业务异常

---

## 7. 代码结构（本次新增）

- Controller：`WorkTaskController`
- Service：`WorkTaskService` / `WorkTaskServiceImpl`
- Mapper：`WorkTaskMapper` + `WorkTaskMapper.xml`
- DTO：`InboundTaskGenerateRequest`
- VO：`InboundTaskGenerateResultVO`、`InboundOrderLiteVO`、`InboundOrderDetailLiteVO`、`WorkTaskInsertVO`

---

## 8. 下一次开发计划（第二阶段）

## 8.1 目标
实现“任务执行闭环”：`任务执行 -> 检查点 -> 库存快照 -> 库存流水`。

## 8.2 功能清单
1. `POST /wms/tasks/{taskId}/start`（开始执行）
2. `POST /wms/tasks/{taskId}/checkpoint`（扫码检查点）
3. `POST /wms/tasks/{taskId}/complete`（完成任务并落库存）
4. `POST /wms/tasks/{taskId}/abandon`（放弃任务）

## 8.3 关键设计
- 状态机约束：0->1->2->3，异常分支到4/5
- 完成任务时统一事务：
  - 更新 `wms_inventory`
  - 追加 `wms_inventory_movement`
  - 回写单据/明细状态
- 乐观锁与防重：库存更新使用版本号或条件更新

## 8.4 交付物
- API 文档 + 状态机图
- 集成测试（任务->库存->流水）
- 失败补偿策略（异常任务重派）
