# AI 对话模块详细设计

## 1. 模块概述
本模块提供 AI 对话能力，支持同步问答、流式问答、历史查询与历史清理。

## 2. 功能详细描述
1. 同步对话：返回一次完整回答。
2. 流式对话：SSE 按 token 推送。
3. 获取历史：按 userId 返回历史消息。
4. 清空历史：按 userId 清除会话上下文。

## 3. 接口设计
- `GET /ai/chat`
- `GET /ai/stream`
- `GET /ai/history`
- `GET /ai/clear`

## 4. 数据结构设计
- Controller：`ChatController`
- 会话组件：`ChatSession`
- AI 模型接口：`ChatModel`
- 返回结构：`Result` 或 `SseEmitter`

## 5. 业务流程设计
1. 接收 `userId + message`。
2. 由 ChatSession 拼装 Prompt。
3. 调用 ChatModel 同步/流式获取响应。
4. 持续维护用户会话历史。

## 6. 异常处理设计
- AI 调用异常返回统一错误信息。
- 流式发送异常时主动结束 emitter。

## 7. 安全设计
1. 建议增加用户级限流与配额。
2. 建议增加内容安全过滤（输入/输出）。
3. 建议增加会话过期策略与敏感信息脱敏。
