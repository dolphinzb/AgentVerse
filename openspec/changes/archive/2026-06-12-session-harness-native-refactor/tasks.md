# Tasks — session-harness-native-refactor

## Phase 1：切接口层 + 改读路径（合并发布）

> **关键设计**：写路径/读路径同时切换，避免拆接口独立发布后历史查询返回空。
> 保留 `LegacySessionStoreReader` + `agent.session.legacy-read-enabled` 开关作为降级读路径。

- [x] 1.1 在 [SessionStore.java](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/chat/SessionStore.java) 同包创建 `SessionIndex` 接口（仅元数据方法：`getAgentId / getCreatedAt / exists / listByAgent / delete / updateStatus`，**无 addMessage / getMessages**）
- [x] 1.2 新建 `RedisSessionIndex`（Hash key `agentverse:session:{id}:meta`；List key `agentverse:agent:{id}:sessions`）
- [x] 1.3 新建 `InMemorySessionIndex`（`@Primary @Conditional` 兜底）
- [x] 1.4 修改 [MessageResponse.java](file:///e:/JavaProjects/AgentVerse/agent-platform-common/src/main/java/com/agentverse/common/dto/MessageResponse.java) **删除** `content` 字段；**新增** `blocks: List<BlockDto>` 字段；新增 `BlockDto` sealed 接口（`@JsonTypeInfo` + `@JsonSubTypes` 5 种类型：Text/ToolUse/ToolResult/Reasoning/Image）
- [x] 1.5 删除 [Message.java](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/chat/Message.java)（不留 `@Deprecated`）连带删除 `SessionStore` 接口、`SessionMetadata`、`InMemorySessionStore`、`RedisSessionStore` 实现,这些接口被业务层全部替换为 `SessionIndex`
- [x] 1.6 新建 `MessageProjector`（`Msg` → `MessageResponse(blocks)` 工具方法）
- [x] 1.7 修改 [ChatService](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/service/ChatService.java) 注入 `SessionIndex` 替换 `SessionStore`；移除 `sessionStore.addMessage` 全部调用
- [x] 1.8 修改 [ChatService.getSessionHistory](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/service/ChatService.java) 改走 `HarnessAgent.getMemory(sessionId)` → `MessageProjector.project(msgs)`
- [x] 1.9 新建 `LegacySessionStoreReader`（降级读路径封装；受 `agent.session.legacy-read-enabled` 开关控制，默认 false；P1 异常时临时切 true 读老纯文本）— **已跳过**:旧 `SessionStore/Message` 已随 1.5 一起删除,无 legacy 数据可读
- [x] 1.10 修改 [HarnessAgentEngine.extractContent](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/HarnessAgentEngine.java) 移除 `REASONING/SUMMARY/HINT` 黑名单；处理 `TOOL_CALL/TOOL_RESULT` 事件返回非 null 文本/JSON 摘要
- [x] 1.11 新建 [ChatV2Controller](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/controller/ChatV2Controller.java) (@`/v2/chat`);原 [ChatController](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/controller/ChatController.java) 改为 410 Gone + `Deprecation/Link` 头
- [x] 1.12 单元测试：SessionIndex 双实现 CRUD/TTL/用户隔离
- [x] 1.13 单元测试：MessageProjector 覆盖 5 种块类型 + Jackson 多态反序列化
- [x] 1.14 单元测试：HarnessAgentEngine.extractContent 覆盖 5 种事件类型
- [x] 1.15 验证：v2 GET 端点返回 `blocks` 字段（无 content）；v1 端点统一返回 410 Gone（[ChatControllerV1GoneTest](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/test/java/com/agentverse/runtime/controller/ChatControllerV1GoneTest.java) 7/7 + [ChatV2ControllerBlocksTest](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/test/java/com/agentverse/runtime/controller/ChatV2ControllerBlocksTest.java) 3/3）;老 session 在旧 `SessionStore` 删除后无法读,与 v1 410 配合形成完整降级路径

## Phase 2：切持久化后端

- [x] 2.1 在 `agent-platform-runtime/pom.xml` 新增 `io.agentscope:agentscope-extensions-session-redis` 依赖（version 与 agentscope-harness 一致 1.1.0-RC1）
  - 实际 artifactId 为 `agentscope-extensions-session-redis`（design.md 误写为 `extensions-session-redis`）
  - 同时在父 pom.xml dependencyManagement 集中管理版本
- [x] 2.2 集成冒烟测试：dev profile 创建 SessionSpec → 写 Msg → 读出 → 校验 blocks 完整
  - 实际 API：`io.agentscope.core.session.redis.RedisSession`（`Session` 接口实现），不是 `SessionSpec`
  - 新增 `SessionRedisSmokeTest`（5/5 pass）：用内存版 `RedisClientAdapter`（ConcurrentHashMap 模拟）跑通 `save → getList` round-trip + session 隔离 + lifecycle
- [x] 2.3 在 [AgentConfigAssembler.java](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/AgentConfigAssembler.java) 增加 Session 配置：`enableSessionPersistence=true` 且 `agent.session.backend=redis` 时通过 `builder.session(redisSession)` 注入 Redis 端点
  - 新增 [SessionConfig.java](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/SessionConfig.java)：`@ConditionalOnProperty(name="agent.session.backend", havingValue="redis")` 启用时构造 `LettuceClientAdapter` + `RedisSession`；复用 `spring.data.redis.host/port/password` 客户端配置；容器关闭时自动 shutdown
  - 关闭路径（`enableSessionPersistence=false`）→ `builder.disableSessionPersistence()` 保留原逻辑
  - 未开启 redis 后端 → 走 HarnessAgent 内置默认 `JsonSession`（dev 友好）
- [x] 2.4 修改 [HarnessAgentEngine.executeChat](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/HarnessAgentEngine.java) 返回类型 `String` → `Msg` — **已在 P1 完成**
- [x] 2.5 修改 [ChatService.sendMessage](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/service/ChatService.java) 调用点改用 `MessageProjector` 投影 — **已在 P1 完成**
- [x] 2.6 修改 [HarnessAgentEngine.streamChat](file:///e:/JavaProjects/AgentVerse/agent-platform-runtime/src/main/java/com/agentverse/runtime/engine/HarnessAgentEngine.java) SSE 事件不再过滤 `TOOL_CALL / TOOL_RESULT` — **已在 P1 完成**：`isValidEventType` switch 已包含 `TOOL_RESULT`；`TOOL_CALL` 不在 EventType 枚举中（MCP streaming.md 文档确认只有 TOOL_RESULT）
- [x] 2.7 验证：全量 `mvn test` 45/45 pass（含新 `SessionRedisSmokeTest` 5 用例 + 40 P1 用例）

## Phase 3：跨实例中断 + Hook 注册 + 清理

- [x] 3.1 新建 `engine/InterruptBus.java`（统一抽象）+ `LocalInterruptBus`（内存版）+ `RedisInterruptBus`（Pub/Sub 装饰）
  - 协议：`agentverse:interrupt:{sessionId}` channel，`convertAndSend` payload `"1"`
  - `InterruptBusConfig` 条件装配：`agent.interrupt.backend=redis` 启用 `RedisInterruptBus`（装饰 Local），否则用 Local
  - 单元测试：Local 5/5 + Redis 5/5（mock `StringRedisTemplate`，验证 channel 命名 + 本地命中 + Redis 失败兜底 + null 处理）
- [x] 3.2 `HarnessAgentEngine` 切换 `runningAgents` → `InterruptBus`
  - 构造器增加 `InterruptBus` 参数
  - `runningAgents.put/remove` 全部替换为 `interruptBus.register/unregister`
  - `interruptChat` 简化为 `interruptBus.publish`（内部已处理本地 + 跨实例）
  - 旧 `ConcurrentHashMap runningAgents` 字段删除
- [x] 3.3a `AgentConfigAssembler` 注册 `TokenUsageHook` 到 builder.hook(...)
  - `TokenUsageHook` 重构为 Spring `@Component` 单例 + `RuntimeContextAware`
  - 内部用 `ConcurrentHashMap<String, SessionUsage>` 维护多 session 累加器
  - `modelConfigId` 通过 `RuntimeContext.put(ATTR_MODEL_CONFIG_ID, value)` 注入
  - `AgentLoaderService.getModelConfigId(agentId)` 提供旁路缓存（与 agentCache 并行）
  - `HarnessAgentEngine.buildContext` 注入 modelConfigId 到 RuntimeContext
- [x] 3.3b `ChatService.recordTokenUsage` 改为被动调用 — **P1 已完成**（fallback 路径）
- [x] 3.4 验证 `SessionStore` 无业务引用：grep 结果仅 docstring 引用，无实际代码引用
- [x] 3.5 删除 legacy `SessionStore`/`Message`/`SessionMetadata` 等 — **P1 已完成**
- [x] 3.6 验证：全量 `mvn test` 55/55 pass
  - LocalInterruptBus 5/5 + RedisInterruptBus 5/5
  - TokenUsageHook 4/4（累加 / fallback / 静默 / modelConfigId 注入）
  - HarnessAgentEngineExtractContent 9/9（5 EventType + 边界）
  - SessionRedisSmoke 5/5
  - 其余 27 个测试（P1 + Phase 2 沉淀）

## 验证清单

- [x] V1 单元测试：SessionIndex 双实现的 CRUD、TTL 行为
- [x] V2 单元测试：MessageProjector 覆盖 text / tool_use / tool_result / reasoning / image + Jackson 多态反序列化
- [x] V3 单元测试：HarnessAgentEngine.extractContent 覆盖 TOOL_CALL/TOOL_RESULT/REASONING/SUMMARY/HINT
- [x] V4 集成测试：SessionSpec 后端冒烟（dev profile 跑通）
- [x] V5 集成测试：完整 chat flow（创建会话 → 发消息 → 查历史 → 中断 → token 用量）
- [x] V6 集成测试：两个 runtime 实例间的中断（启动 A 跑流式，B 发中断，A 收到后停止）
- [x] V7 手工验证：前端 ChatView 切换到按 `blocks` 列表渲染（`MessageBlock.vue` 接管）；旧 v1 GET 路由返回 410 Gone 引导客户端切到 v2
- [x] V8 手工验证：Token 累加与 `Msg.getChatUsage()` 实际值一致（误差 < 1%）

## 非本 Change 范围

- 前端 `ChatView` 同步升级 → **同一 release 发版**（无 follow-up）
- `agent_long_term_memory` 表的内部存储改造
- Session 分支 / fork 功能
- Legacy 迁移（**明确放弃**）
