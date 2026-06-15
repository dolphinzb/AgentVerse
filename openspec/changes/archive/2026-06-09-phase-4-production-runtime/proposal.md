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

## Capabilities

### New Capabilities

- `workspace-management`: WorkspaceManager 用户工作区隔离、FilesystemFactory 文件系统配置、用户上下文 Hook
- `memory-management`: MemoryFlushHook 记忆刷盘、MemoryMaintenanceHook 记忆维护、LongTermMemory 跨会话长期记忆
- `session-persistence`: Redis 会话持久化、SessionPersistenceHook 集成
- `loop-enhancement`: CompactionHook 上下文压缩、ToolResultEvictionHook 工具结果驱逐、PlanNotebook 任务规划、recoverFromOverflow 溢出恢复
- `agent-lifecycle`: Agent 生命周期状态机（draft/active/archived）、发布流程
- `dashboard`: 仪表盘首页统计 API 和前端页面

### Modified Capabilities

- `harness-agent-engine`: AgentLoaderService 扩展 workspace/filesystem/memory hook/compaction/toolResultEviction/longTermMemory/planNotebook/session 配置
- `chat-service`: 会话持久化切换到 Redis、Token 用量记录从占位实现改为真实提取
- `agent-definition-crud`: agent_definition 表新增多个运行时配置字段（workspace_mode、filesystem_type、enable_memory_flush 等）、status 字段语义化（draft/active/archived）
- `minimal-frontend`: Agent 配置页增加高级配置区域、对话页增加 Token 统计、新增仪表盘页

## Impact

- **代码结构**：agent-platform-runtime 新增 workspace/memory/session/hook 包；重构 AgentLoaderService 和 ChatService
- **数据库**：新增 agent_long_term_memory 表、agent_instance 表；修改 agent_definition 表新增多个配置字段
- **基础设施**：新增 Redis 依赖（spring-data-redis + extensions-session-redis）
- **API**：新增仪表盘统计 API；修改 Agent CRUD API（新增配置字段、status 语义变更）
- **前端**：新增仪表盘页面；修改 Agent 配置页、对话页
- **文件系统**：新增 workspace 目录结构
