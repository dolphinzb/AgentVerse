# chat-service（修改）

## 变更说明

ChatService 不再通过 SessionStore 持久化"user/assistant 纯文本"消息。消息由 HarnessAgent 内部 SessionSpec 接管；ChatService 仅做业务编排和读路径投影。**写路径/读路径同时切换**（见 [tasks.md Phase 1](../tasks.md)），避免拆接口独立发布后历史查询返回空。

## 依赖变更

**移除依赖**：
- `chat.SessionStore`（接口）
- `chat.InMemorySessionStore`
- `chat.RedisSessionStore`
- `chat.Message`（POJO）
- `chat.RedisConfig`（仅 SessionStore 用）

**新增依赖**：
- `chat.SessionIndex`（接口）
- `chat.RedisSessionIndex`
- `chat.InMemorySessionIndex`
- `chat.MessageProjector`
- `chat.LegacySessionStoreReader`（降级读路径，受 `agent.session.legacy-read-enabled` 开关控制）
- `engine.HarnessAgentEngine`（已有，仅调 API）
- `engine.TokenUsageHook`（仅 recordTokenUsage 用）

## 业务路径变更

### sendMessage（同步对话）

**旧流程**：
```
1. 校验 session 存在
2. SessionStore.addMessage(sessionId, userMessage)
3. HarnessAgent.call(messages, RuntimeContext)
4. SessionStore.addMessage(sessionId, assistantMessage)
5. recordTokenUsage(sessionId, modelConfigId, 0, 0)  ← 写死 0,0
6. 返回 assistantMessage
```

**新流程**：
```
1. 校验 session 存在（SessionIndex）
2. HarnessAgent.call(userMsg, RuntimeContext.sessionId) ← sessionId 注入 RuntimeContext
   → HarnessAgent 内部：
     a. 加载历史 Msgs
     b. 执行 ReAct 循环
     c. 通过 SessionSpec 持久化
     d. 触发 PostCallEvent → TokenUsageHook 写 chat_usage
3. 返回 HarnessAgent.lastResponse（MessageProjector 投影成 MessageResponse）
```

**注意**：`recordTokenUsage(sessionId, 0, 0)` 删除。主路径由 `TokenUsageHook` 写入；仅在 Hook 未注册时保留兜底（从 `Msg.metadata` 提取 agentscope 提供的 tokens）。

### streamChat（SSE 流式对话）

**变更**：
- 旧：`agent.executeChat()` 边调 `agent.stream()` 边 `SessionStore.addMessage`
- 新：`agent.stream()` → HookEvent 自动落库（`SummaryChunkEvent` 持久化 text 块到 SessionSpec）
- 旧：手动过滤 `isValidEventType` 黑名单
- 新：移除 `isValidEventType` + 改造 `extractContent` 处理 `TOOL_CALL/TOOL_RESULT` 事件；全结构化块推送

### listMessages（查询历史）

**旧**：`SessionStore.getMessages(sessionId)` 返回 `List<Message>`（POJO）

**新（主路径）**：`HarnessAgent.getMemory(sessionId)` → `MessageProjector.project(msgs)` → `List<MessageResponse>`（带 blocks）

**新（降级路径）**：当 `agent.session.legacy-read-enabled=true` 时，`getSessionHistory()` 改走 `LegacySessionStoreReader` 读 `SessionStore.getSession()`，仅返回老纯文本（**丢失 tool_call / reasoning 块**）。默认 false；P1 异常时临时切 true 兜底。老 session 主路径返回 404 + "会话不存在，请重新创建"。

## 字段变更

### MessageResponse 改为 blocks-only（无 content 兼容字段）

`content` 字段**彻底删除**，不留兼容垫片。详见 [harness-native-session spec](./harness-native-session/spec.md)。

### BlockDto sealed 接口 Jackson 配置

`BlockDto` 是 `sealed interface`，必须显式标注 `@JsonTypeInfo(use = Id.NAME, property = "type")` + `@JsonSubTypes({...})`，否则 `MessageProjector` 反序列化 `Msg.blocks` 时抛 `MismatchedInputException`。

## 错误码

- `SESSION_NOT_FOUND` 扩展：老 session 历史查询返回 404 + 业务码 1704（"会话不存在或已归档，请重新创建"），前端引导用户创建新会话。

## RBAC 权限

无变更。
