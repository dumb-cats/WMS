# 入库单管理模块详细设计

## 1. 模块概述
本模块负责入库单创建与批量导入，支持手工录入、JSON 批量导入、文件批量导入（csv/xls/xlsx），并为任务生成提供数据来源。

## 2. 功能详细描述
1. 手工创建入库单。
2. JSON 批量导入入库单。
3. 文件批量导入（EasyExcel）。
4. 单号生成与防重。
5. 明细车型主数据校验。

## 3. 接口设计
### 3.1 手工创建
- `POST /wms/inbound-orders/manual`

### 3.2 JSON批量导入
- `POST /wms/inbound-orders/batch-import`

### 3.3 文件批量导入
- `POST /wms/inbound-orders/batch-import/file`
- Content-Type: `multipart/form-data`
- 文件字段：`file`

## 4. 数据结构设计
- 主单表：`wms_inbound_order`
- 明细表：`wms_inbound_order_detail`
- DTO：`InboundOrderCreateRequest`、`InboundOrderDetailCreateRequest`、`InboundBatchImportRequest`、`InboundImportRowDTO`
- VO：`InboundOrderInsertVO`、`InboundOrderDetailInsertVO`、`InboundCreateResultVO`、`InboundBatchImportResultVO`

## 5. 业务流程设计
1. 接收请求并校验参数。
2. 生成入库单号并写入主单。
3. 校验车型编码后批量写入明细。
4. 批量导入逐条执行并汇总失败原因。
5. 文件导入按 `sourceNo+orderType+supplierName` 聚合成单。

## 6. 异常处理设计
- 文件为空或格式不支持直接拒绝。
- 车型编码不存在直接报错。
- 单号冲突重试，重试失败抛异常。
- 批量导入失败项返回行级错误信息。

## 7. 安全设计
1. 入参使用校验注解。
2. 导入流程限制文件类型（csv/xls/xlsx）。
3. 单号防重保证业务唯一性。
4. 建议后续补充导入文件大小与行数限制。
