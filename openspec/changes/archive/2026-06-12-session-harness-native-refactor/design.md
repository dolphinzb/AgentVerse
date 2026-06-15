# Design — HarnessAgent 接管会话权威

## 架构目标

```
┌─────────────────────────────────────────────────────────────┐
│                  修正后数据流                                │
└─────────────────────────────────────────────────────────────┘

   ChatController / WebSocket                                    
       │                                                         
       ▼                                                         
   ChatService                                                  
       │                                                         
       ├──→ SessionIndex.registerSession(agentId, userId)  ← 仅元数据
       │                                                         
       └──→ harnessAgent.call(                                  
              msgs, RuntimeContext.builder()                   
                  .sessionId(sessionId)                        
                  .userId(userId)                              
                  .build())                                   
              │                                                
              │  (内部)                                         
              ▼                                                
       ┌─────────────────────────────────┐                     
       │    HarnessAgent                  │                     
       │    ├── session memory (Msg[])   │ ← Source of Truth  
       │    ├── SessionSpec backend       │ ← Redis/MySQL      
       │    ├── Compaction                │                     
       │    ├── ToolResultEviction        │                     
       │    ├── LongTermMemory            │                     
       │    └── MemoryFlushHook → MEMORY.md│                    
       └─────────────────────────────────┘                     
              │                                                 
              │ (返回最后一条 Msg)                               
              ▼                                                 
       ChatService.recordTokenUsage(Msg.metadata)              
       ChatService.toResponse(Msg) → MessageResponse            
              │                                                 
              ▼                                                 
       HTTP / SSE response                                     
```

## 关键设计决策

### 决策 1：SessionIndex 用 Redis Hash + InMemory 回退
**理由**：元数据查询（`getAgentId` / `listSessions`）是 O(1) 点查 + 偶尔列表扫描，Redis Hash + ZSet（按 createdAt 排序）最契合。前端 Dashboard 列表按用户过滤 + 按时间倒序，与现有 Redis 数据布局一致。

**数据布局**：
```
agentverse:session:index:{sessionId}     Hash { agentId, userId, createdAt, status }
agentverse:session:index:user:{userId}   Set  { sessionId1, sessionId2, ... }
agentverse:session:index:list             ZSet { sessionId → createdAt_epoch }
```

### 决策 2：SessionSpec 复用 agentscope 的后端抽象
**理由**：`io.agentscope:extensions-session-redis` 的 `SessionSpec.builder().backend(Backend.REDIS).connection(...)` 已经是 Spring-friendly 的 Builder，**不需要自造轮子**。`enable_session_persistence` 字段通过 `AgentConfigAssembler` 透传给 `builder.session(spec)` 的有无。

**风险**：1.1.0-RC1 阶段 API 可能变 — 由 `AgentConfigAssembler` 隔离，调用方不直接接触 agentscope 类型。

### 决策 3：MessageResponse 改为 blocks-only，前后端同步升级
**结论**：去掉 `content` 兼容垫片。`blocks: List<BlockDto>` 是唯一字段，前后端一起升级。

**为什么不保留 `content` 兼容**：

伪兼容是劣化。ReAct 一轮的 blocks 实际序列是：

```
[Reasoning("我先查天气..."),
 ToolUse("get_weather", {city:"Beijing"}),
 ToolResult({temp:25}),
 Text("北京今天 25 度")]
```

若 `content` 从 blocks 挑首 Text 块填进去，老前端拿到 `"北京今天 25 度"` 看似正常，**实际丢失**思考、工具调用、工具结果、错误。等于把 agent 的核心可观测性偷偷阉割，比直接报字段缺失还危险。

**配套动作**：

1. **后端 MessageResponse 重构** — `content` 字段删除，保留 `blocks` 单一来源
2. **API 升 v2** — `GET /api/v2/chat/sessions/{id}/messages`；v1 路由**保留只读不写**，给前端 1 个迭代窗口切流量后下线
3. **前端 ChatView 同步重写** — 按 `blocks` 列表循环渲染（`MessageBlock.vue` 类组件直接接 `BlockDto` 的 sealed 子类型）
4. **破坏性变更可见** — 旧客户端调 v2 拿到 null content 会 NPE，比拿到"看似正常但残缺"的数据强一百倍

**块类型**：
```java
public sealed interface BlockDto {
    record Text(String text) implements BlockDto {}
    record ToolUse(String toolName, Map<String,Object> args) implements BlockDto {}
    record ToolResult(String toolName, Object result, boolean isError) implements BlockDto {}
    record Reasoning(String text) implements BlockDto {}
    record Image(String url, String mimeType) implements BlockDto {}
}
```

### 决策 4：InterruptBus 用 Redis Pub/Sub
**理由**：现有 Redis 已是基础设施依赖，不引入新中间件。Pub/Sub 的"最多一次"语义可接受——中断失败最多导致 agent 多跑一轮，**无正确性风险**。

**协议**：
- 发送：`PUBLISH agentverse:interrupt:{sessionId} 1`
- 订阅：每个 runtime 实例启动时 `SUBSCRIBE agentverse:interrupt:*`，回调从 channel 提取 sessionId → 调本地 `HarnessAgent.interrupt()`

### 决策 5：用 `HookEvent` 完整捕获 agent 生命周期 + 提取 Token

**事件订阅**（替代之前的手动 `isValidEventType` 过滤）：

| 我们关注的能力 | 订阅事件 | 用途 |
|----------------|----------|------|
| 推理过程（reasoning 块持久化） | `ReasoningChunkEvent` | 落 SessionSpec 内部；SSE 推给前端 |
| 工具调用请求（tool_use 块持久化） | `PreActingEvent` / `PostActingEvent` | `PostActingEvent` 拿 tool_use + tool_result |
| 工具流式进度 | `ActingChunkEvent` | SSE 推给前端"工具执行中..." |
| 摘要流式（SSE 主回复） | `SummaryChunkEvent` | SSE 推给前端"主回答" |
| 错误 | `ErrorEvent` | 写 `chat_usage` 错误标记 + SSE 错误事件 |
| **Token 用量（逐轮累加）** | **`PostReasoningEvent`** | **`getReasoningMessage().getChatUsage()` → 累加到 Hook 实例字段** |
| **Token 用量（最终落库）** | **`PostCallEvent`** | **取 `getFinalMessage().getChatUsage()` → `chatUsageService.saveUsage(...)`** |

#### 5.1 提取 Token 的 AgentScope 原生方式

AgentScope 把 `ChatUsage` 嵌入到 `Msg` 的 metadata 里（key 为 `MessageMetadataKeys.CHAT_USAGE = "_chat_usage"`），通过 `Msg.getChatUsage()` 反序列化拿到 `ChatUsage{inputTokens, outputTokens, time}`。

**官方实现范例**：[`agentscope.core.agent.StructuredOutputHook`](file:///E:/JavaProjects/agentscope-java/agentscope-core/src/main/java/io/agentscope/core/agent/StructuredOutputHook.java) 在 `PostReasoningEvent` 里逐轮累加：

```java
// StructuredOutputHook.java:228-237
ChatUsage usage = msg.getChatUsage();
if (usage != null) {
    hasUsage = true;
    totalInput  += usage.getInputTokens();
    totalOutput += usage.getOutputTokens();
    totalTime   += usage.getTime();
}
```

这是 AgentScope 官方采用的模式，**完全规避了我们之前担心的"如何从模型响应提取 usage"的问题**。

#### 5.2 TokenUsageHook 实现（基于 AgentScope 原生 API + Reactive）

```java
package com.agentverse.agent.hook;

import io.agentscope.core.hook.Hook;
import io.agentscope.core.hook.HookEvent;
import io.agentscope.core.hook.PostCallEvent;
import io.agentscope.core.hook.PostReasoningEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Token 用量统计 Hook。
 *
 * <p>基于 AgentScope 的 {@link Hook} 事件机制，在 {@link PostReasoningEvent}
 * 中逐轮累加 ChatUsage，在 {@link PostCallEvent} 中把聚合结果写入计费表。
 *
 * <p>设计要点：
 * <ul>
 *   <li>实例字段保存 sessionId / modelConfigId / 累加器；Hook 是有状态对象，
 *       每个 session 创建一次，session 结束随 agent 销毁</li>
 *   <li>数据库写操作用 {@code Mono.fromCallable} + {@code boundedElastic}
 *       调度，<b>不阻塞</b> agent 响应链</li>
 *   <li>异常用 {@code onErrorResume} 兜底，<b>绝不</b>让 Hook 失败导致 agent
 *       整轮响应回滚</li>
 *   <li>参考 AgentScope 官方 {@code StructuredOutputHook} 的 ChatUsage
 *       累加模式</li>
 * </ul>
 */
public class TokenUsageHook implements Hook {

    private static final Logger log = LoggerFactory.getLogger(TokenUsageHook.class);

    /** 当前 session id（由 ChatService 在创建 Hook 时注入） */
    private final String sessionId;
    /** 当前 session 使用的模型配置 id（计费归属，类型与 {@code ModelConfig.id} 对齐：String） */
    private final String modelConfigId;
    /** 持久化接口（解耦 DB 层，对接 {@code ChatUsageService.saveUsage}） */
    private final ChatUsageService chatUsageService;

    /** 逐轮累加器 */
    private long totalInputTokens = 0L;
    private long totalOutputTokens = 0L;
    private double totalTime = 0.0;
    private boolean hasUsage = false;

    public TokenUsageHook(String sessionId, String modelConfigId,
                          ChatUsageService chatUsageService) {
        this.sessionId = sessionId;
        this.modelConfigId = modelConfigId;
        this.chatUsageService = chatUsageService;
    }

    @Override
    public <T extends HookEvent> Mono<T> onEvent(T event) {
        // 1) 逐轮累加 — PostReasoningEvent 每轮 reasoning 完成后触发
        if (event instanceof PostReasoningEvent e) {
            Msg reasoningMsg = e.getReasoningMessage();
            if (reasoningMsg != null) {
                ChatUsage usage = reasoningMsg.getChatUsage();
                if (usage != null) {
                    hasUsage = true;
                    totalInputTokens  += usage.getInputTokens();
                    totalOutputTokens += usage.getOutputTokens();
                    totalTime         += usage.getTime();
                }
            }
            return Mono.just(event);
        }

        // 2) 整轮结束 — PostCallEvent 拿到最终响应后异步落库
        if (event instanceof PostCallEvent e) {
            // 优先用累加值；如未累到则尝试从 finalMessage 拿
            long inTokens = totalInputTokens;
            long outTokens = totalOutputTokens;

            if (!hasUsage) {
                Msg finalMsg = e.getFinalMessage();
                if (finalMsg != null) {
                    ChatUsage usage = finalMsg.getChatUsage();
                    if (usage != null) {
                        inTokens  = usage.getInputTokens();
                        outTokens = usage.getOutputTokens();
                        hasUsage  = true;
                    }
                }
            }

            if (!hasUsage) {
                log.warn("No ChatUsage available for session={}, skip persist", sessionId);
                return Mono.just(event);
            }

            // 落库走 IO 线程，Hook 链不阻塞
            return Mono.fromCallable(() -> {
                chatUsageService.saveUsage(
                        sessionId, modelConfigId, inTokens, outTokens);
                return event;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(ex -> {
                // 计费写失败不阻塞 agent 主流程
                log.error("Failed to save chat usage for session={}", sessionId, ex);
                return Mono.just(event);
            });
        }

        return Mono.just(event);
    }
}
```

#### 5.3 关键设计取舍

| 取舍 | 决策 | 理由 |
|------|------|------|
| 累加位置 | `PostReasoningEvent` | 每个 reasoning 轮次后立即拿到 usage，避免 agent 异常退出时漏计；与 AgentScope 官方 `StructuredOutputHook` 同款模式 |
| 最终落库位置 | `PostCallEvent` | 整轮结束，agent 即将返回，hook 链外没有更晚的点 |
| 阻塞处理 | `Mono.fromCallable + boundedElastic` | `chatUsageService.saveUsage` 是 JPA/Redis IO，Hook 协议是 reactive，**必须**切到 IO 线程 |
| 异常处理 | `onErrorResume` 返回 `event` | 计费失败不能影响业务主链路；只 WARN/ERROR 日志 |
| 降级 | 拿不到 `ChatUsage` → 跳过 + WARN | 不做 length/4 估算，估算误差会污染计费；缺失就缺失，让上游 `model_providers` 补全 usage 即可 |
| Hook 状态隔离 | 每个 session 创建一个 `TokenUsageHook` 实例 | Hook 是有状态的，session 结束随 agent 销毁；并发 session 互不干扰 |

**关键事件 → 块类型映射**（让 `MessageProjector` 直接消费）：

```
ReasoningChunkEvent  →  Reasoning(text)         BlockDto
PreActingEvent       →  ToolUse(name, args)     BlockDto
PostActingEvent      →  ToolResult(name, result) BlockDto
SummaryChunkEvent    →  Text(text)              BlockDto
ErrorEvent           →  metadata.error = true
```

### 决策 6：**不**做 Legacy 迁移，老 session 历史查询返回 404
**理由**：老数据本身已经丢失 tool_call / multimodal 块，强行兼容反而会保留两份不一致的数据。重构后：
1. 老 session 调 `GET /messages` → 返回 404 + "会话不存在，请重新创建"提示
2. 前端在响应 404 时引导用户创建新会话
3. 老 Redis key（`agentverse:session:*:messages`）保留**只读**状态，由 `LegacySessionStoreReader` 在 `agent.session.legacy-read-enabled=true` 时提供降级纯文本读（默认 false；P1 异常时临时切回，丢失 tool_call 块）
4. 不删除老 key，给运维留兜底

## 数据流对比

| 操作 | 修正前 | 修正后 |
|------|--------|--------|
| 发送消息 | 写 SessionStore → 调 agent → 写 SessionStore | 调 agent（HarnessAgent 内部全权管理） |
| 查询历史 | SessionStore.getSession() | agent.getMemory(sessionId) 投影到 MessageResponse |
| 列出活跃会话 | SessionStore.listSessions() | SessionIndex.listSessions(userId) |
| 中断流式响应 | 本地 ConcurrentHashMap 查 agent → interrupt | InterruptBus：本地命中 → interrupt；否则 Redis Pub/Sub 广播 |
| Token 计费 | 写死 0/0 | Hook 在 `PostReasoningEvent` 逐轮累加 + `PostCallEvent` 落库，提取自 `Msg.getChatUsage()` |
| 长期记忆 | agentscope LTM + 自建 Service 写库 | 同上（无变化） |

## 实施分阶段

```
Phase 1 ─ 切接口层 + 改读路径（合并发布）
   • 拆 SessionStore → SessionIndex（仅元数据）
   • 删 Message POJO
   • MessageResponse 改 blocks-only（删 content 字段；新增 BlockDto sealed 接口）
   • ChatService 不再 addMessage（数据流已断）
   • ChatService.getSessionHistory 改走 HarnessAgent.getMemory() + MessageProjector
   • HarnessAgentEngine.extractContent 改造处理 TOOL_CALL/TOOL_RESULT
   • 保留 LegacySessionStoreReader + legacy-read-enabled 开关作为降级读路径
   • 验证：v2 GET 端点返回 blocks 字段；降级开关可切

Phase 2 ─ 切持久化后端
   • 引入 extensions-session-redis
   • AgentConfigAssembler 配置 SessionSpec
   • HarnessAgentEngine.executeChat 返回类型 String → Msg（级联修改）
   • 验证：新 session 走新后端；Token 累加正确

Phase 3 ─ 切辅助子系统
   • InterruptBus 实现 + 替换 runningAgents
   • Token 用量从 Msg.metadata 提取
   • 删除 InMemorySessionStore / RedisSessionStore / SessionStore 接口
   • 删除 LegacySessionStoreReader 与 legacy-read-enabled 配置
   • 验证：跨实例中断、Token 用量统计
```

每个 Phase 独立可回滚。Phase 1 写路径/读路径同时切换是关键设计（避免拆接口独立发布后历史查询返回空）；Phase 2 引入新依赖是最大风险点。

## 风险登记

| 风险 | 严重度 | 缓解 |
|------|--------|------|
| `extensions-session-redis` 1.1.0-RC1 API 不稳定 | 高 | `AgentConfigAssembler` 隔离；dev profile 集成冒烟测试先验 |
| `BlockDto` Jackson 多态反序列化失败 | 高 | sealed interface 显式标注 `@JsonTypeInfo` + `@JsonSubTypes`；单元测试覆盖 5 种类型 |
| `enableSessionPersistence=false` 时消息丢失 | 高 | 同步禁用 MessageProjector 读路径并返回 503 |
| `extractContent` 残留黑名单导致 tool_call 数据丢失 | 中 | Phase 1 同步改造 + 单元测试覆盖 5 种事件类型 |
| `executeChat` 返回 `Msg` 引起调用链级联修改 | 中 | 一次性列出所有调用方改造；PR review 强制要求 |
| 老 session 工具调用记录丢失 | 中 | 接受；老 session 历史查询返回 404 + 引导创建新会话 |
| 前端 blocks 字段不适配 | 中 | **前后端同步升级**（同一 release），v1 路由 410 Gone 引导；不留 content 兼容垫片 |
| Phase 1 写/读路径同时切换风险 | 中 | 保留 `LegacySessionStoreReader` + `legacy-read-enabled` 降级开关 |
| Pub/Sub 不可达导致中断失败 | 低 | 写 WARN 日志；客户端超时兜底 |
| Token 估算精度低 | 低 | `ChatUsage` 缺失时跳过落库（WARN），不做 length/4 估算 |
