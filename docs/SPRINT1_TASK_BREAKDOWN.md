# Sprint 1 详细开发任务拆分（任务执行闭环）

## Sprint 目标
在已完成“入库单创建 + 任务生成”的基础上，交付任务执行闭环最小可用版本：
`任务开工 -> 检查点校验 -> 完成任务(库存快照+流水) -> 任务放弃`。

---

## 任务拆分清单

## T1. 任务执行 API 设计与实现
- `POST /wms/tasks/{taskId}/start`
- `POST /wms/tasks/{taskId}/checkpoint`
- `POST /wms/tasks/{taskId}/complete`
- `POST /wms/tasks/{taskId}/abandon`

### 验收
- 4 个接口可用，入参校验完整。
- 状态非法流转时返回明确错误信息。

## T2. 任务状态机落地
- 允许：`0/1 -> 2`（开工）
- 允许：`2 -> 3`（完成）
- 允许：`0/1/2 -> 4`（放弃）
- 异常操作直接拒绝。

### 验收
- 任意非法状态转换被拦截。

## T3. 检查点记录能力
- 保存 `checkpoint_type/scan_content/expected_content/verify_result/operator_id/device_id/location_info/scan_time`。
- 校验结果自动计算（expected 为空默认成功）。

### 验收
- 每次调用 checkpoint 都有审计记录。

## T4. 完成任务时库存落账
- 更新/新增 `wms_inventory`（数量、可用量、最近入库时间）。
- 追加 `wms_inventory_movement`（before/after、taskId、orderNo、operator）。

### 验收
- 任务完成后库存和流水一致。

## T5. 单据回写（轻量）
- 任务完成后累计入库单 `actual_quantity`。
- 按实际数量刷新单据状态：待入库/部分入库/已完成。

### 验收
- 入库单状态可随任务执行自动推进。

## T6. 文档与联调
- 更新接口说明和调用顺序。
- 提供前端调用示例与错误码建议。

---

## 交付物
1. 代码：Controller/Service/Mapper/DTO/VO。
2. 文档：本拆分文档 + 代码注释。
3. 联调顺序：start -> checkpoint -> complete / abandon。
