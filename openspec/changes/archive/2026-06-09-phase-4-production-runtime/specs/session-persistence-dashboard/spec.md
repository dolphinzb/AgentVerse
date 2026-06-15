# session-persistence-dashboard

## 概述

SessionStore 接口化（InMemory/Redis 双实现）、Agent 实例追踪、仪表盘统计 API + 前端页面。

## 数据模型

### agent_instance 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) PK | 实例 ID |
| agent_id | VARCHAR(64) NOT NULL | 关联 agent_definition.id |
| session_id | VARCHAR(64) | 当前会话 ID |
| status | VARCHAR(16) DEFAULT 'active' | active / stopped |
| started_at | TIMESTAMP NOT NULL | 启动时间 |
| last_active_at | TIMESTAMP NOT NULL | 最后活跃时间 |

索引：`idx_agent_instance_agent_id`、`idx_agent_instance_status`

## SessionStore 接口

```java
public interface SessionStore {
    String createSession(String agentId);
    List<Message> getMessages(String sessionId);
    void addMessage(String sessionId, Message message);
    String getAgentId(String sessionId);
    LocalDateTime getCreatedAt(String sessionId);
    void deleteSession(String sessionId);
    boolean exists(String sessionId);
}
```

### InMemorySessionStore

- `@Component @Primary`，作为 Redis 不可用时的回退
- ConcurrentHashMap 存储

### RedisSessionStore

- `@Component @ConditionalOnBean(RedisTemplate.class)`
- Key 前缀 `agentverse:session:`
  - `agentverse:session:{id}:messages` → List<Message>（JSON 序列化）
  - `agentverse:session:{id}:meta` → Hash（agentId, createdAt）
- TTL：24h（可配置）

### RedisConfig

- `@ConditionalOnProperty(name = "spring.data.redis.host")`
- 配置 JSON 序列化器、JavaTimeModule

## 仪表盘 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/v1/dashboard/stats | 仪表盘统计数据 | dashboard:read |

响应结构：

```json
{
  "agentCount": { "draft": 2, "active": 5, "archived": 1 },
  "activeSessions": 3,
  "todayChatCount": 42,
  "tokenUsage": { "inputTokens": 15000, "outputTokens": 8000 }
}
```

## 前端

- `DashboardView.vue`：4 个概览卡片（Agent 数量、活跃会话、今日对话、Token 用量）
- 路由：`/dashboard`
- 侧边栏菜单项
