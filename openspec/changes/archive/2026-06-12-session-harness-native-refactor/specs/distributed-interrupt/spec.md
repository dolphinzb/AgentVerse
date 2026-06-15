# distributed-interrupt

## 概述

把 `HarnessAgentEngine.runningAgents`（本地 `ConcurrentHashMap<sessionId, HarnessAgent>`）升级为 `InterruptBus`（Redis Pub/Sub），支持**跨实例**中断。客户端在任何 runtime 实例调中断 API，**正在流式输出**的实例都能收到并停止。

## 数据流

```
Client A ──POST /v1/chat/sessions/{id}/interrupt──→ Instance A
                                                      │
                                                      ├─ 本地命中？→ Agent.interrupt()
                                                      │
                                                      └─ 跨实例？→ InterruptBus.publish(id)
                                                                       │
                                                                       └─ Redis Pub/Sub
                                                                              channel:
                                                                              agentverse:interrupt:{sessionId}
                                                                                     │
                                                                                     └─ Instance B（正在流式）收到
                                                                                            └─ Agent.interrupt() → SSE 停
```

## 协议

- **发布**：`PUBLISH agentverse:interrupt:{sessionId} 1`
- **订阅**：每个 runtime 实例启动时 `SUBSCRIBE agentverse:interrupt:*`，回调从 channel 名提取 sessionId
- **本地命中**：publish 端先查本地 `runningAgents`，命中直接 `Agent.interrupt()`，避免绕一圈
- **序列化**：payload 固定为 `1`（仅作 trigger，复杂控制后续可升级为 JSON）

## 启动时序

1. Application 启动 → `InterruptBus.subscribe()` 启动后台线程
2. 收到消息 → 反解 sessionId → `localAgents.get(sessionId).interrupt()`
3. 中断后 → `runningAgents.remove(sessionId)`
4. 启动失败 → 整体降级到本地 `ConcurrentHashMap`（日志 WARN）

## 配置

```yaml
agent:
  interrupt:
    channel-prefix: agentverse:interrupt:
    pubsub-pool-size: 4            # Lettuce 连接池
    enabled: true                  # false 时降级为本地 Map
    local-first-timeout-ms: 100   # 本地命中超时（快速失败）
```

## 失败模式

| 场景 | 行为 |
|------|------|
| Redis 不可用 | 启动期 `subscribe()` 失败 → 启动降级到本地 Map；运行时 publish 失败 → WARN 日志 + 客户端超时 |
| Agent 已结束 | `runningAgents` 不存在 sessionId → 忽略 |
| 实例宕机 | Redis Pub/Sub fan-out 自动跳过；其他实例的本地 Agent 不受影响 |
| 重复中断 | `Agent.interrupt()` 幂等（agentscope 保证） |

## API 变更

无 API 变更。`POST /v1/chat/sessions/{id}/interrupt` 行为从"本地中断"升级为"跨实例中断"。

## 错误码

无新增错误码。原有 `1701 SESSION_NOT_FOUND` 继续适用。

## RBAC 权限

无新增权限。
