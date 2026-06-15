# harness-native-session

## 概述

HarnessAgent 接管 session 存储权威。ChatService 不再通过 SessionStore 写入消息；HarnessAgent 内部通过 SessionSpec + RuntimeContext.sessionId 自动管理。SessionIndex 仅存元数据（agentId / userId / createdAt / status），**不存消息**。

## 数据模型

### MessageResponse（blocks-only）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) | 消息 ID（agentscope 生成） |
| role | VARCHAR(16) | user / assistant / system / tool |
| **blocks** | **JSON ARRAY** | **唯一内容字段**：结构化块列表 |
| createdAt | TIMESTAMP | 创建时间 |

> **破坏性变更**：旧版 `content` 字段**彻底删除**，不留兼容垫片。原因：伪兼容会丢失 thinking / tool_use / tool_result 等结构化块，比字段缺失更危险。

### Block 类型

```json
[
  { "type": "text",       "text": "你好" },
  { "type": "reasoning",  "text": "用户问天气，我需要查询北京" },
  { "type": "tool_use",   "name": "get_weather", "args": { "city": "北京" } },
  { "type": "tool_result", "name": "get_weather", "result": "晴，25℃" }
]
```

### SessionIndex 接口

```java
public interface SessionIndex {
    String create(String agentId, String userId);
    Optional<SessionMeta> get(String sessionId);
    boolean exists(String sessionId);
    List<SessionMeta> listByAgent(String agentId, String userId);
    void updateStatus(String sessionId, SessionStatus status);
    void delete(String sessionId);
}

public record SessionMeta(
    String sessionId,
    String agentId,
    String userId,
    LocalDateTime createdAt,
    SessionStatus status  // ACTIVE / STOPPED / DELETED
) {}
```

**明确不在 SessionIndex**：消息内容、消息列表、token 用量。

## API 变更

### GET /api/v2/chat/sessions/{sessionId}/messages

**变更**：**破坏性变更**——`content` 字段**彻底删除**，`blocks` 为唯一内容字段。v1 路由保留只读不写（不再写入新数据），前端有 1 个迭代窗口切流量后下线。

**响应示例**：

```json
{
  "messages": [
    {
      "id": "msg-001",
      "role": "user",
      "blocks": [{ "type": "text", "text": "北京今天天气？" }],
      "createdAt": "2026-06-10T10:00:00Z"
    },
    {
      "id": "msg-002",
      "role": "assistant",
      "blocks": [
        { "type": "reasoning",  "text": "查询北京天气" },
        { "type": "tool_use",   "name": "get_weather", "args": { "city": "北京" } },
        { "type": "tool_result", "name": "get_weather", "result": "{temp:25}" },
        { "type": "text",       "text": "北京今天晴，25℃。" }
      ],
      "createdAt": "2026-06-10T10:00:05Z"
    }
  ]
}
```

## 业务规则

- 业务路径（`ChatService.sendMessage` / `ChatService.streamChat`）**不再调用** `sessionStore.addMessage`；消息由 HarnessAgent 内部管理。
- `getMessages` 改为 `HarnessAgent.getMemory(sessionId)` → `MessageProjector.project(msgs)`。
- 业务实例 ID 由 HarnessAgent 返回的 `Msg.id` 决定。
- `RuntimeContext.sessionId` 是会话唯一标识，必须非空。

## 错误码

无新增错误码。

## RBAC 权限

无新增权限。
