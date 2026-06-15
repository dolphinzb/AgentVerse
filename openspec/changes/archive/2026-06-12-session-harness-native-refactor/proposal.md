# Session 存储让位给 HarnessAgent：消除影子库与保真度损失

## Why

当前 [ChatService](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/service/ChatService.java) 在调用 HarnessAgent 的同时，**自建**了一套 `SessionStore`（`InMemorySessionStore` + `RedisSessionStore`）来持久化"user/assistant 纯文本"消息。这套并行实现存在三个根本问题：

1. **影子库 ≠ Source of Truth**：HarnessAgent 内部已经维护了完整的 `Msg` 内存（含 `ToolUseBlock` / `ToolResultBlock` / `ReasoningBlock` / 多模态块），通过 `RuntimeContext.sessionId` 隔离会话。我们自建的 `Message` POJO 只有 `role + content + timestamp`，**写入的仅是 user/assistant 两条最终文本**。Compaction / 工具结果驱逐 / 长期记忆 / MemoryFlush 全部作用于 HarnessAgent 内部 memory，而 `getSessionHistory` 返回的却是 SessionStore 的未压缩纯文本——**LLM 实际看到的上下文 ≠ 用户看到的历史**。
2. **能力被绕开 80%**：AgentScope 提供的 `SessionSpec`（`extensions-session-redis` / `extensions-session-mysql`）原生支持 session 持久化、resume、跨进程恢复、消息分支、tool_call 历史持久化——这些都没用到。
3. **双写必然漂移**：任何一处异常（Redis 写入失败、`recordTokenUsage` 抛错、NPE）都会导致 SessionStore 与 HarnessAgent 内部状态不一致，且无补偿机制。

本次变更把会话存储权威让位给 HarnessAgent，SessionStore 降级为只读索引，并补齐中断分发、Token 提取、Legacy 迁移。

## What Changes

- **将 `SessionStore` 拆为 `SessionIndex` + 委派给 HarnessAgent**：
  - 新建 `SessionIndex` 接口，仅负责元数据（`agentId` / `userId` / `createdAt` / `status`）和会话列表查询，**不存消息**。
  - 删除 `Message` POJO；`MessageResponse` 改为 blocks-only（`text` / `tool_use` / `tool_result` / `reasoning`），**无 content 兼容字段**，前后端同步升级。
  - `addMessage` / `getSession` 全部从业务路径移除——HarnessAgent 内部通过 `RuntimeContext.sessionId` 自动管理。
- **引入 AgentScope `SessionSpec` 后端**：新增 `extensions-session-redis` 依赖，`AgentConfigAssembler` 通过 `builder.session(SessionSpec)` 接管持久化。`enable_session_persistence` 字段继续保留，控制 Builder 行为。
- **捕获所有事件类型**：[HarnessAgentEngine.isValidEventType](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/HarnessAgentEngine.java#L120-L122) 不再过滤 `TOOL_CALL` / `TOOL_RESULT`。改用 AgentScope `HookEvent` 系统（`PostCallEvent` / `PreActingEvent` / `PostActingEvent` / `ReasoningChunkEvent` / `SummaryChunkEvent`）订阅完整生命周期。
- **分布式中断**：[HarnessAgentEngine.runningAgents](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/HarnessAgentEngine.java#L23) 由 `ConcurrentHashMap` 升级为 `InterruptBus`（Redis Pub/Sub），支持跨实例中断。
- **Token 用量提取**：[ChatService.recordTokenUsage](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/service/ChatService.java) 不再写死 `0, 0`，改为从 `Msg.metadata` 提取 agentscope 提供的 input/output tokens。
- **不做 Legacy 迁移**：老 session 工具调用记录本就丢失，强行迁移保留双份不一致数据。重构后老 session 历史查询返回 404 + "会话不存在"提示，前端引导用户创建新会话。

## Capabilities

### New Capabilities

- `harness-native-session`：定义 HarnessAgent 内部 session 作为唯一权威，业务层只读投影。
- `distributed-interrupt`：定义跨实例的中断分发机制（Redis Pub/Sub）。

### Modified Capabilities

- `chat-service`：消息存储由 SessionStore 改为 HarnessAgent；删除 `addMessage` / `getSession` 业务路径。
- `session-persistence-dashboard`：`SessionStore` 拆为 `SessionIndex`（仅元数据）+ 委派消息给 agentscope；`MessageResponse` 支持结构化块。
- `loop-enhancements`：Builder 中 session 持久化配置改由 `SessionSpec` 控制；`enable_session_persistence` 字段语义保持。
- `harness-agent-engine`：`HarnessAgentEngine` 事件过滤不再丢 `TOOL_CALL` / `TOOL_RESULT`；`runningAgents` 替换为 `InterruptBus`。

## Impact

**受影响的代码（≈ 12 个文件）**：
- `agent-platform-runtime/.../chat/SessionStore.java` → 拆为 `SessionIndex` + 删除
- `agent-platform-runtime/.../chat/InMemorySessionStore.java` → 改为 `SessionIndex` 的 InMemory 实现
- `agent-platform-runtime/.../chat/RedisSessionStore.java` → 改为 `SessionIndex` 的 Redis 实现
- `agent-platform-runtime/.../chat/Message.java` → 删除
- `agent-platform-runtime/.../chat/SessionMetadata.java` → 保留
- `agent-platform-runtime/.../engine/AgentConfigAssembler.java` → 增加 `SessionSpec` 配置
- `agent-platform-runtime/.../engine/HarnessAgentEngine.java` → 事件过滤调整 + 改用 `InterruptBus`
- `agent-platform-runtime/.../engine/AgentLoaderService.java` → 配合 SessionSpec
- `agent-platform-runtime/.../engine/InterruptBus.java` → 新建
- `agent-platform-runtime/.../engine/LegacySessionMigrator.java` → 新建
- `agent-platform-runtime/.../service/ChatService.java` → 删除 `addMessage` 调用，改造 `recordTokenUsage`
- `agent-platform-common/.../dto/MessageResponse.java` → 删除 `content` 字段，保留 `blocks` 单一来源
- `agent-platform-runtime/pom.xml` → 新增 `extensions-session-redis` 依赖

**API 影响**：
- `GET /api/v2/chat/sessions/{sessionId}/messages` 返回结构由 `[{role, content, timestamp}]` 升级为 `[{id, role, blocks, metadata, timestamp}]`，**破坏性变更**。v1 路由保留只读不写（不再写入新数据），给前端 1 个迭代窗口切流量后下线。
- `POST /api/v1/chat/sessions/{sessionId}/interrupt` 由本地 Map 升级为 Redis Pub/Sub 跨实例中断。

**数据库影响**：
- 无新表 / 无 schema 变更
- `agent_instance` 表保留语义不变，仍用于活跃实例追踪
- `agent_long_term_memory` 表保留（与 agentscope LTM 互补，业务可显式写入）

**依赖影响**：
- 新增 `io.agentscope:extensions-session-redis`（1.1.0-RC1 同步版本）
- `agentscope-harness` 版本不变

**风险**：
- agentscope `extensions-session-redis` 1.1.0-RC1 稳定性未充分验证 → `AgentConfigAssembler` 隔离；dev profile 集成冒烟测试先验
- 老 session 数据的 tool_call 历史无法恢复（旧数据本就没存）→ 接受；老 session 历史查询返回 404
- 前端 MessageResponse 字段变化 → **同步升级，不留兼容垫片**（伪兼容反而会偷走 thinking / tool_call 块）
- `BlockDto` Jackson 多态反序列化 → sealed interface 必须显式标注 `@JsonTypeInfo` + `@JsonSubTypes`
- `HarnessAgentEngine.extractContent` 残留黑名单 → 同步改造并补单元测试
- `HarnessAgentEngine.executeChat` 返回类型由 `String` 改 `Msg` → 调用链级联修改；PR review 列出所有调用方
- `enableSessionPersistence=false` 时消息丢失 → 同步禁用 MessageProjector 读路径并返回 503

**非目标**：
- 前端 `ChatView` 同步升级（同一 release，一起发版，不留 follow-up）
- `agent_long_term_memory` 表的内部存储改造
- Session 分支 / fork 功能（agentscope 原生支持，但前端未设计）
- 模型路由 / Provider 优化
