# session-persistence-dashboard（修改）

## 变更说明

SessionStore 拆为 SessionIndex（仅元数据）+ SessionSpec（HarnessAgent 内部，由 agentscope 接管）。删除 SessionStore 接口和两个实现；新增 SessionIndex 双实现。

## 旧 SessionStore 接口（删除）

```java
public interface SessionStore {
    String createSession(String agentId);
    List<Message> getMessages(String sessionId);  // ← 删除
    void addMessage(String sessionId, Message message);  // ← 删除
    String getAgentId(String sessionId);
    LocalDateTime getCreatedAt(String sessionId);
    void deleteSession(String sessionId);
    boolean exists(String sessionId);
}
```

## 新 SessionIndex 接口

```java
public interface SessionIndex {
    String create(String agentId, String userId);
    Optional<SessionMeta> get(String sessionId);
    boolean exists(String sessionId);
    List<SessionMeta> listByAgent(String agentId, String userId);
    void updateStatus(String sessionId, SessionStatus status);
    void delete(String sessionId);

    record SessionMeta(
        String sessionId,
        String agentId,
        String userId,
        LocalDateTime createdAt,
        SessionStatus status
    ) {}

    enum SessionStatus { ACTIVE, STOPPED, DELETED }
}
```

**关键变化**：`getMessages` 和 `addMessage` **不存在**。读消息走 `HarnessAgent.getMemory(sessionId)`；写消息由 HarnessAgent 内部管理。

## Redis Key 变更

| 旧 key | 新 key | 状态 |
|--------|--------|------|
| `agentverse:session:{id}:messages` | （由 SessionSpec 接管） | **保留只读**（降级用） |
| `agentverse:session:{id}:meta` | `agentverse:session:{id}:meta` | 保留（字段扩展） |
| — | `agentverse:agent:{id}:sessions` | 新增（List，用于 listByAgent） |

**老 key 不删除**：`agentverse:session:{id}:messages` 保留只读状态，由 `LegacySessionStoreReader` 在 `agent.session.legacy-read-enabled=true` 时提供降级读（默认 false；P1 异常时临时切回，丢失 tool_call / reasoning 块）。运维可选择 `redis-cli DEL` 清理。

### meta Hash 字段扩展

| 字段 | 类型 | 说明 |
|------|------|------|
| agentId | String | 关联 agent_definition.id |
| userId | String | 用户 ID |
| createdAt | ISO8601 | 创建时间 |
| status | String | ACTIVE / STOPPED / DELETED |

## 仪表盘 API 变更

### GET /api/v1/dashboard/stats

**响应不变**。`activeSessions` 改从 `SessionIndex.listByAgent` 聚合（而非 SessionStore.scanAll）。

### 新增 GET /api/v1/dashboard/sessions

按 agent 查询 session 列表（仅元数据，**不返回消息**）。

```json
{
  "sessions": [
    {
      "sessionId": "sess-001",
      "agentId": "agent-001",
      "userId": "user-001",
      "status": "ACTIVE",
      "createdAt": "2026-06-10T10:00:00Z"
    }
  ]
}
```

## LegacySessionStoreReader（降级读路径）

封装 `SessionStore.getSession(sessionId)` 反序列化为 `List<Message>`（旧 POJO，纯文本） → 转 `List<MessageResponse>(Text(text))`。

**约束**：
- 仅在 `agent.session.legacy-read-enabled=true` 时启用（默认 false）
- 仅读取 `agentverse:session:{id}:messages`，**不写回新后端**
- 老数据已丢失 tool_call / reasoning / image 块，降级返回仅含 `Text` 块
- 主路径失败或 P1 异常时临时切 true 兜底
- Phase 3 完成后**删除**（连同开关配置）

**不做 Legacy 迁移**：老 session 工具调用记录本就丢失，强行迁移保留双份不一致数据。重构后老 session 历史查询返回 404 + "会话不存在，请重新创建"提示，前端引导用户创建新会话。

## 文件结构

### 删除
- `chat/SessionStore.java`（Phase 3）
- `chat/InMemorySessionStore.java`（Phase 3）
- `chat/RedisSessionStore.java`（Phase 3）
- `chat/Message.java`（Phase 1）
- `chat/RedisConfig.java`（仅 SessionStore 用）（Phase 3）

### 新增
- `chat/SessionIndex.java`（接口，Phase 1）
- `chat/RedisSessionIndex.java`（Phase 1）
- `chat/InMemorySessionIndex.java`（Phase 1）
- `chat/LegacySessionStoreReader.java`（降级读，Phase 1，Phase 3 删）
- `chat/MessageProjector.java`（Phase 1）
- `config/SessionSpecConfig.java`（Phase 2）

## 错误码

- 老 session 历史查询返回 404 + 业务码 1704（"会话不存在或已归档，请重新创建"），前端引导用户创建新会话。

## RBAC 权限

| 权限代码 | 名称 | admin | developer | operator |
|----------|------|-------|-----------|----------|
| dashboard:read | 查看仪表盘 | Y | Y | Y |
| dashboard:read-sessions | 查看 session 列表 | Y | Y | - |

（dashboard:read-sessions 为新增，operator 不需要看 session 列表）
