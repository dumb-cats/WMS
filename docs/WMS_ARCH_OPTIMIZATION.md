# WMS 架构优化建议（基于 ai_warehouse_ddl.sql）

## 1. 建议采用的领域分层

1. **系统域（IAM & 审计）**：`sys_user`、`sys_role`、`sys_menu`、`sys_operation_log`。
2. **主数据域（Master Data）**：`wms_warehouse`、`wms_zone`、`wms_rack`、`wms_bin`、`wms_motorcycle_model`。
3. **业务单据域（Order）**：`wms_inbound_order`、`wms_inbound_order_detail`、`wms_outbound_order`、`wms_outbound_order_detail`。
4. **任务执行域（Task）**：`wms_work_task`、`wms_task_checkpoint`。
5. **库存域（Inventory）**：`wms_inventory`、`wms_inventory_movement`、`wms_stocktake`、`wms_stocktake_detail`。
6. **IoT 与规则域（Extension）**：`wms_device`、`wms_assignment_rule`、`wms_alert_event`。

## 2. 企业级落地建议

- **先单体分层，后模块化拆分**：先在同一 Spring Boot 项目中做 `api/application/domain/infrastructure` 分包，稳定后按域拆成微服务。
- **单据与任务解耦**：入库单只记录业务意图；实际执行通过 `wms_work_task` 触发，避免单据状态与执行状态耦合。
- **库存强审计**：库存快照只读写 `wms_inventory`，任何变化必须追加 `wms_inventory_movement`，禁止直接覆盖历史。
- **幂等与防重**：外部系统导入建议新增 `biz_idempotent_key`（唯一索引），避免重复入库。
- **多仓隔离与租户化预留**：核心业务表统一增加 `warehouse_id` 过滤策略，并预留 `tenant_id` 字段。
- **可观测性**：统一 traceId、业务单号（orderNo/taskNo）穿透日志。

## 3. 入库接口与表关系（当前实现）

- 手工创建与批量导入均落到：
  - 主表：`wms_inbound_order`
  - 明细表：`wms_inbound_order_detail`
- 明细合法性校验：通过 `wms_motorcycle_model.model_code` 做车型主数据校验。
- 入库单号：`IN + yyyyMMddHHmmss + 4位随机数`，并做唯一性检查。

## 4. 下一阶段建议

1. 增加“确认收货”接口：生成 `wms_work_task`（入库任务）。
2. 增加“上架完成”接口：更新 `wms_bin` 占用、写入 `wms_inventory` 与 `wms_inventory_movement`。
3. 增加失败补偿：批量导入按行事务（REQUIRES_NEW）+ 失败回执下载。
4. 增加 OpenAPI 与契约测试：保障前后端/外部系统联调质量。

## 5. 批量导入方式（已实现）

- JSON 导入：`POST /wms/inbound-orders/batch-import`。
- 文件导入：`POST /wms/inbound-orders/batch-import/file`（`multipart/form-data`，字段名 `file`）。
- 文件导入使用 **EasyExcel** 统一解析，支持 `csv/xls/xlsx`。
- 表头固定为：
  `sourceNo,orderType,supplierName,contactPerson,contactPhone,planTime,remark,modelCode,batchNo,preferredBinCode,plannedQuantity`
- 规则：同一 `sourceNo + orderType + supplierName` 会聚合为一张入库单，多行会形成多条明细。
- `planTime` 格式：`yyyy-MM-dd HH:mm:ss`。
