# harness-agent-engine（修改）

## 变更说明

`HarnessAgentEngine` 不再手动过滤 `TOOL_CALL` / `TOOL_RESULT` 事件类型——由 agentscope HookEvent 系统全量投递。`runningAgents` 升级为 `InterruptBus`，支持跨实例中断。

## 事件过滤

### 旧逻辑（删除）

```java
private boolean isValidEventType(String type) {
    return !type.equals("TOOL_CALL") && !type.equals("TOOL_RESULT");
}
```

**问题**：手动过滤丢掉了工具调用和工具结果块，导致前后端看不到 tool_use / tool_result 块。

### 新逻辑（删除过滤）

不再过滤。`HarnessAgent.stream()` / `call()` 内部走 HookEvent 系统：

| 事件 | 用途 |
|------|------|
| `PreCallEvent` | agent 调用前 |
| `PostCallEvent` | **TokenUsageHook 提取 tokens** |
| `PreReasoningEvent` / `PostReasoningEvent` | 推理前后 |
| `ReasoningChunkEvent` | 推理流式块 |
| `PreActingEvent` / `PostActingEvent` | 工具调用前后 |
| `ActingChunkEvent` | 工具流式块 |
| `PreSummaryEvent` / `PostSummaryEvent` | 摘要前后 |
| `SummaryChunkEvent` | 摘要流式块 |
| `ErrorEvent` | 错误 |

**业务消费方**通过 `MessageProjector` 投影 `Msg` → `MessageResponse(blocks)`，SSE 推送全结构化块给前端。

## 中断机制

### 旧逻辑

```java
private final ConcurrentHashMap<String, HarnessAgent> runningAgents = new ConcurrentHashMap<>();

public void interrupt(String sessionId) {
    HarnessAgent agent = runningAgents.get(sessionId);
    if (agent != null) {
        agent.interrupt();
    }
}
```

**问题**：只能中断**当前实例**的 Agent。

### 新逻辑

```java
@Service
public class InterruptBus {
    private final RedisTemplate<String, String> redis;
    private final ConcurrentHashMap<String, HarnessAgent> localAgents = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void subscribe() {
        // SUBSCRIBE agentverse:interrupt:*
        redis.execute((RedisCallback<Void>) conn -> {
            conn.subscribe(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    String channel = new String(message.getChannel());
                    String sessionId = channel.substring("agentverse:interrupt:".length());
                    HarnessAgent local = localAgents.get(sessionId);
                    if (local != null) {
                        local.interrupt();
                        localAgents.remove(sessionId);
                    }
                }
            }, "agentverse:interrupt:*".getBytes());
            return null;
        });
    }
    
    public void publish(String sessionId) {
        HarnessAgent local = localAgents.get(sessionId);
        if (local != null) {
            local.interrupt();
            localAgents.remove(sessionId);
            return;
        }
        redis.convertAndSend("agentverse:interrupt:" + sessionId, "1");
    }
}
```

`HarnessAgentEngine.interrupt(sessionId)` 改为调 `InterruptBus.publish(sessionId)`。

详见 [distributed-interrupt spec](./distributed-interrupt/spec.md)。

## API 变更

无 API 变更。`POST /v1/chat/sessions/{id}/interrupt` 行为从本地升级为跨实例。

## 错误码

无新增错误码。

## RBAC 权限

无新增权限。
