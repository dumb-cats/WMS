# 入库管理模块设计文档（基于当前已实现接口）

## 1. 文档目的
本设计文档用于补充当前系统中“已实现”的入库管理接口说明，统一业务语义、接口契约、状态流转和后续演进计划，方便前后端联调与后续版本迭代。

---

## 2. 模块边界

### 2.1 当前已覆盖能力
1. 入库单手工创建。
2. 入库单批量导入（JSON）。
3. 入库单批量导入（文件：CSV/XLS/XLSX，EasyExcel解析）。
4. 按入库单生成入库任务（任务驱动第一阶段）。

### 2.2 当前未覆盖能力
1. 入库任务执行（开始/完成/放弃）。
2. 扫码检查点（库位码、VIN码）校验。
3. 入库完成后库存快照、库存流水写入。

---

## 3. 领域对象与表关系

## 3.1 主体表
- `wms_inbound_order`：入库主单。
- `wms_inbound_order_detail`：入库明细。
- `wms_work_task`：工作任务（入库任务类型 `task_type=1`）。

## 3.2 关键关系
- 一个入库单（order）包含多条入库明细（detail）。
- 任务生成阶段按明细生成任务（当前策略）。
- 任务与单据通过 `inbound_order_id` 关联。

---

## 4. 接口设计（当前已实现）

## 4.1 手工创建入库单
- **URL**: `POST /wms/inbound-orders/manual`
- **用途**: 人工录入单据与明细。
- **核心校验**:
  - `orderType` 范围校验（1~4）
  - 明细车型编码存在性校验
- **输出**:
  - `orderId`
  - `orderNo`
  - `totalQuantity`

## 4.2 JSON 批量导入
- **URL**: `POST /wms/inbound-orders/batch-import`
- **用途**: 外部系统按 JSON 批量导入。
- **策略**:
  - 逐条处理
  - 单条失败不影响其余条目
- **输出**:
  - 成功数量、失败数量
  - 成功单列表
  - 错误信息列表

## 4.3 文件批量导入（EasyExcel）
- **URL**: `POST /wms/inbound-orders/batch-import/file`
- **Content-Type**: `multipart/form-data`
- **文件字段**: `file`
- **支持格式**: `csv/xls/xlsx`
- **表头规范**:
  - `sourceNo,orderType,supplierName,contactPerson,contactPhone,planTime,remark,modelCode,batchNo,preferredBinCode,plannedQuantity`
- **聚合规则**:
  - 同一 `sourceNo + orderType + supplierName` 聚合为一张入库单，多行转多明细。

## 4.4 按入库单生成任务
- **URL**: `POST /wms/tasks/inbound-orders/{inboundOrderId}/generate`
- **用途**: 将“入库单意图”转换为“待执行任务”。
- **请求参数（可选）**:
  - `keepExistingTasks`：是否跳过已存在待执行任务（默认 `true`）
  - `priority`：任务优先级（1~10，默认 `5`）
  - `operator`：操作人（默认 `SYSTEM`）
- **当前策略**:
  - 每条明细生成一条入库任务（`task_type=1`）
  - 对已入库明细（`detail_status=2`）跳过
  - 若存在待执行任务（状态0/1/2）可跳过（幂等）

---

## 5. 状态流转设计

## 5.1 入库单状态（主单）
- 0 草稿
- 1 待入库
- 2 部分入库
- 3 已完成
- 4 已取消

当前实现中：生成任务后会将草稿推进为待入库。

## 5.2 入库明细状态
- 0 待处理
- 1 已分配库位
- 2 已入库
- 3 异常

当前实现中：已入库明细不会重复生成任务。

## 5.3 任务状态（工作任务）
- 0 待分配
- 1 已分配
- 2 执行中
- 3 已完成
- 4 已放弃
- 5 异常

当前实现中：任务生成后初始状态为“待分配”。

---

## 6. 幂等与一致性策略

## 6.1 入库单号幂等
- 单号规则：`IN + yyyyMMddHHmmss + 4位随机数`
- 生成时查重，避免重复单号。

## 6.2 任务号幂等
- 任务号规则：`TK + yyyyMMddHHmmss + 4位随机数`
- 生成时查重，避免重复任务号。

## 6.3 任务重复下发防护
- 在 `keepExistingTasks=true` 时，若同入库单+车型+目标货位已存在状态0/1/2任务，则跳过。

## 6.4 事务边界
- 入库单创建：主表+明细同事务。
- 任务生成：任务插入+单据状态推进同事务。

---

## 7. 错误码与提示建议

建议下一版本将当前 `IllegalArgumentException` 收敛为统一业务异常编码，例如：
- `INBOUND_ORDER_NOT_FOUND`
- `INBOUND_ORDER_STATUS_INVALID`
- `INBOUND_DETAIL_EMPTY`
- `TASK_NO_GENERATE_FAILED`
- `MODEL_CODE_INVALID`

---

## 8. 下一阶段开发计划（建议）

## 8.1 目标
完成“任务执行闭环”：`任务执行 -> 检查点 -> 库存快照 -> 库存流水`。

## 8.2 计划接口
1. `POST /wms/tasks/{taskId}/start`：任务开工。
2. `POST /wms/tasks/{taskId}/checkpoint`：扫码检查点。
3. `POST /wms/tasks/{taskId}/complete`：任务完成并更新库存。
4. `POST /wms/tasks/{taskId}/abandon`：任务放弃并记录原因。

## 8.3 技术方案
- 使用事务确保“任务完成 + 库存更新 + 流水写入 + 单据回写”原子性。
- 引入乐观锁/条件更新防止并发超扣。
- 增加审计日志（操作人、设备、时间、来源接口）。

---

## 9. 联调建议
1. 先完成“手工建单 -> 生成任务”联调闭环。
2. 再接入“批量导入 -> 任务生成”。
3. 最后上线“任务执行与库存闭环”并做压测。

---

## 10. 验收标准（本期）
1. 能创建入库单（手工/JSON/文件）并返回单号。
2. 能按入库单生成任务并返回任务号列表。
3. 重复调用任务生成接口时不会无脑重复下发。
4. 已入库明细不会重复下发任务。
