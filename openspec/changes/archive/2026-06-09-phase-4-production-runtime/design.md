## Context

AgentVerse 阶段一至三已完成：Agent CRUD + 版本管理、用户认证 + RBAC 权限、模型管理 + Token 用量追踪。当前运行时引擎处于 POC 状态：

**当前状态：**
- SessionStore：纯内存 ConcurrentHashMap，重启即丢失
- AgentLoaderService：仅构建基础 HarnessAgent（name/description/sysPrompt/maxIters/model），无 Hook 配置
- ChatService：Token 用量硬编码为 0，无 Redis 集成
- AgentDefinition：有 workspaceMode/status 字段但无实际逻辑
- 无工作区目录管理、无记忆管理、无上下文压缩、无长期记忆

**关键约束：**
- 所有高级能力（记忆、压缩、驱逐、持久化、计划执行）均来自 HarnessAgent Builder API
- Agent 配置存储在数据库规范化表中，运行时从数据库读取 → 调用 Builder 方法
- Redis 用于会话持久化，需新增依赖
- 工作区目录结构：workspace/user/{userId}/agent/{agentId}
- Agent 生命周期状态：draft → active → archived

## Goals / Non-Goals

**Goals:**
- Agent 拥有独立工作区，具备记忆刷盘和维护能力
- 会话持久化切换到 Redis，服务重启后会话不丢失
- Token 用量从占位实现改为真实提取
- 长对话自动压缩上下文，大工具结果自动驱逐
- 跨会话长期记忆
- 任务规划能力（PlanNotebook）
- Agent 生命周期状态机（draft/active/archived）
- 仪表盘首页统计

**Non-Goals:**
- 分布式工作区（远程共享/沙箱文件系统）— 仅实现 local 模式
- SubAgent 编排 — 阶段五
- 工具系统（自定义工具注册/执行）— 阶段五
- RAG 知识库集成 — 阶段六
- 模型负载均衡/故障转移
- Token 限流/配额管理
- Agent 批量评测

## Decisions

### Decision 1: 工作区隔离方案

**选择：基于文件系统的用户级隔离**

```
workspace/
  user/{userId}/
    agent/{agentId}/
      MEMORY.md          ← 记忆刷盘文件
      AGENTS.md          ← Agent 上下文文件
      ...
```

- WorkspaceManager 根据 userId + agentId 创建隔离目录
- FilesystemFactory 当前仅支持 LocalFilesystemSpec（单实例模式）
- 后续可扩展 RemoteFilesystemSpec（多实例共享）和 SandboxFilesystemSpec（沙箱隔离）

### Decision 2: 会话持久化方案

**选择：Redis 后端（extensions-session-redis）**

| 方案 | 优点 | 缺点 |
|------|------|------|
| Redis | 高性能、原生 TTL、分布式友好 | 新增基础设施依赖 |
| MySQL | 无新依赖、事务保证 | 性能差、无 TTL |
| 文件系统 | 无新依赖 | 并发问题、无查询能力 |

**结论**：使用 HarnessAgent 的 extensions-session-redis 扩展，通过 SessionPersistenceHook 自动持久化。新增 spring-data-redis 依赖。

### Decision 3: Token 用量提取方案

**选择：从 HarnessAgent 事件流中提取**

当前 ChatService 硬编码 Token 用量为 0，因为 agentscope 库的 Msg/Event 未暴露 token usage 数据。阶段四需要：
1. 检查 agentscope 1.1.0-RC1 是否在 Event 中暴露 usage 数据
2. 如果暴露：从流式/同步响应中提取 input_tokens/output_tokens
3. 如果未暴露：保持占位实现，记录到 chat_usage 表的 message_record 中，待库支持后提取

### Decision 4: Agent 配置字段扩展

**选择：在 agent_definition 主表新增配置字段**

新增字段（均为运行时配置，影响 HarnessAgent Builder 调用）：

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| filesystem_type | VARCHAR(16) | 'local' | 文件系统类型 |
| enable_memory_flush | TINYINT | 1 | 启用记忆刷盘 |
| enable_memory_maintenance | TINYINT | 1 | 启用记忆维护 |
| enable_compaction | TINYINT | 0 | 启用上下文压缩 |
| compaction_trigger_pct | INT | 80 | 压缩触发阈值(%) |
| compaction_keep_recent | INT | 10 | 保留最近N条消息 |
| enable_tool_result_eviction | TINYINT | 0 | 启用工具结果驱逐 |
| tool_result_eviction_max_chars | INT | 4000 | 驱逐阈值(字符数) |
| enable_long_term_memory | TINYINT | 0 | 启用长期记忆 |
| enable_plan | TINYINT | 0 | 启用计划执行 |
| enable_session_persistence | TINYINT | 1 | 启用会话持久化 |
| session_backend | VARCHAR(16) | 'redis' | 会话后端 |
| max_context_tokens | INT | 8000 | 最大上下文 Token 数 |

### Decision 5: Agent 生命周期状态机

**选择：三状态模型**

```
draft ──发布──→ active ──归档──→ archived
  ↑                                 │
  └────────────重新激活──────────────┘
```

- draft：草稿，仅创建者可见，不可对话
- active：已发布，可对话，出现在默认列表
- archived：已归档，不出现在默认列表，可重新激活

### Decision 6: 仪表盘统计 API

**选择：后端聚合查询**

- Agent 数量（按状态分组）
- 活跃会话数（从 Redis 查询）
- 今日对话量（从 chat_usage 按日统计）
- Token 用量概览（从 chat_usage 聚合）

### Decision 7: 前端高级配置区域

**选择：Agent 配置页 Tab 切换**

- 基本配置 Tab：名称、描述、模型选择、系统提示词、最大迭代次数（现有）
- 高级配置 Tab：工作区设置、记忆管理、循环增强、会话设置

## Risks / Trade-offs

| Risk | 描述 | Mitigation |
|------|------|------------|
| agentscope 库 API 不稳定 | 1.1.0-RC1 为预发布版，API 可能变化 | 封装适配层，隔离库变更影响 |
| Redis 运维复杂度 | 新增基础设施依赖 | 提供本地开发回退方案（内存模式） |
| 工作区磁盘占用 | 大量 Agent 工作区文件 | 设置磁盘配额、定期清理 |
| 记忆刷盘性能 | 频繁写文件影响性能 | 异步刷盘、批量写入 |
| 上下文压缩质量 | 压缩可能丢失关键信息 | 保留最近 N 条消息不压缩 |
| agent_definition 表膨胀 | 新增 13+ 配置字段 | 字段均为简单类型，影响可控 |

## Migration Plan

**迭代 4.1 部署步骤：**
1. 数据库迁移：agent_definition 新增配置字段
2. 重启后端：加载 WorkspaceManager、Memory Hook
3. 验证：创建 Agent → 对话 → 检查工作区 MEMORY.md

**迭代 4.2 部署步骤：**
1. 新增 Redis 依赖和配置
2. 数据库迁移：新增 agent_instance 表
3. 重启后端：SessionStore 切换到 Redis
4. 验证：对话 → 重启服务 → 恢复会话

**迭代 4.3 部署步骤：**
1. 数据库迁移：新增 agent_long_term_memory 表
2. 重启后端：加载 Compaction/Eviction/LTM/Plan Hook
3. 验证：长对话测试、大工具结果测试、跨会话记忆测试

**迭代 4.4 部署步骤：**
1. 数据库迁移：agent_definition.status 语义更新
2. 重启后端：Agent 生命周期状态机生效
3. 验证：创建 Agent → 发布 → 归档 → 重新激活

**回滚方案：**
- 各迭代独立，可单独回滚
- Redis 不可用时回退到内存 SessionStore
- 工作区文件为附加产物，不影响核心功能
