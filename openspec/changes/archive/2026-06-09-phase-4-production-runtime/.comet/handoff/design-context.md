# Comet Design Handoff

- Change: phase-4-production-runtime
- Phase: design
- Mode: compact
- Context hash: a1b2c3d4-handoff-placeholder

Generated-by: comet-handoff.sh

OpenSpec remains the canonical capability spec. This handoff is a deterministic, source-traceable context pack, not an agent-authored summary.

## openspec/changes/phase-4-production-runtime/proposal.md

- Source: openspec/changes/phase-4-production-runtime/proposal.md
- Lines: 1-77
- SHA256: 1793a68b19eb8209f3f57c16bb166ff47e969be2

```md
## Why

AgentVerse 阶段一至三已实现最小 POC（Agent CRUD + 对话）、安全体系（用户认证 + RBAC）、模型管理（多模型动态管理 + Token 用量追踪）。但当前 Agent 引擎仍处于 POC 水平：

1. **会话不持久化**：SessionStore 使用纯内存 ConcurrentHashMap，服务重启后所有会话和消息丢失
2. **无工作区隔离**：AgentDefinition 有 workspaceMode 字段但无实际逻辑，不同用户/Agent 无文件隔离
3. **无记忆管理**：对话结束后记忆丢失，无法跨会话保持上下文
4. **循环能力弱**：长对话 Token 溢出无处理、大工具结果无驱逐、无长期记忆、无任务规划
5. **生命周期不完整**：Agent 无 draft/active/archived 状态管理，无发布流程
6. **Token 用量占位**：ChatService 硬编码 inputTokens=0, outputTokens=0

阶段四目标是将 Agent 引擎从 POC 升级为生产级，利用 HarnessAgent 的内置能力（记忆钩子、上下文压缩、会话持久化、工作区管理、计划执行等）实现生产化。

## What Changes

**迭代 4.1：Workspace + 记忆管理**
- 实现 WorkspaceManager 用户隔离（workspace/user/{userId}/agent/{agentId} 目录结构）
- 实现 FilesystemFactory（根据 agent_definition.filesystem_type 选择 local 模式）
- 集成 MemoryFlushHook（对话结束时自动将记忆刷盘到工作区 MEMORY.md）
- 集成 MemoryMaintenanceHook（定期整理记忆：去重、归档、摘要）
- 实现用户上下文 Hook（注入 user_id 到 HarnessAgent RuntimeContext）
- 扩展 AgentLoaderService：添加 workspace + filesystem + memory hook 配置
- 前端：Agent 配置页增加"高级配置"区域（工作区模式、文件系统、记忆开关）

**迭代 4.2：会话增强与 Token 计量**
- 集成 extensions-session-redis（SessionPersistenceHook 切换到 Redis 后端）
- 实现 Token 用量记录 Hook（通过 ChatUsage 统计每次对话的 input/output tokens）
- 实现 agent_instance 表 + 活跃 Agent 实例追踪
- 前端：对话页面增加 Token 消耗实时统计
- 前端：仪表盘首页（Agent 数量、活跃会话数、今日对话量、Token 用量概览卡片）
- 实现仪表盘统计 API

**迭代 4.3：智能体循环增强**
- 实现 agent_long_term_memory 表的 Schema、Entity、Mapper
- 集成 CompactionHook（上下文自动压缩）
- 集成 ToolResultEvictionHook（工具结果过大时驱逐到文件）
- 集成 recoverFromOverflow()（上下文溢出自动恢复机制）
- 集成 LongTermMemory（跨会话长期记忆）
- 集成 PlanNotebook（任务规划能力）
- 扩展 AgentLoaderService：添加 compaction/toolResultEviction/longTermMemory/planNotebook 配置
- 前端：Agent 设计器增加"循环增强"配置区域

**迭代 4.4：Agent 生命周期完善**
- 重构 AgentLoaderService：对接模型管理基础设施
- 实现 Agent 生命周期状态机（draft → active → archived）
- 实现 agent_definition 表的 status 字段管理
- Agent 创建时默认状态为 draft，发布后变为 active
- 前端：Agent 列表页增加状态筛选
- 前端：Agent 详情页增加生命周期状态展示
```

Full source: openspec/changes/phase-4-production-runtime/proposal.md

## openspec/changes/phase-4-production-runtime/design.md

- Source: openspec/changes/phase-4-production-runtime/design.md
- SHA256: de784d5bea5cb0933b262341fa39b2641ab162da

[TRUNCATED - see full source]

Full source: openspec/changes/phase-4-production-runtime/design.md

## openspec/changes/phase-4-production-runtime/tasks.md

- Source: openspec/changes/phase-4-production-runtime/tasks.md
- SHA256: 7d661c6ccdbd1fc6c77e259b63f7d9ef64866404

[TRUNCATED - see full source]

Full source: openspec/changes/phase-4-production-runtime/tasks.md
