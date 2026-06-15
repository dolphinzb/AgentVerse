## 迭代 4.1：Workspace + 记忆管理

- [x] 1.1 数据库迁移：agent_definition 表新增配置字段（filesystem_type、enable_memory_flush、enable_memory_maintenance、enable_compaction、compaction_trigger_pct、compaction_keep_recent、enable_tool_result_eviction、tool_result_eviction_max_chars、enable_long_term_memory、enable_plan、enable_session_persistence、session_backend、max_context_tokens）
- [x] 1.2 实现 WorkspaceManager：根据 userId + agentId 创建隔离目录（workspace/user/{userId}/agent/{agentId}）
- [x] 1.3 实现 FilesystemFactory：根据 filesystem_type 构建 LocalFilesystemSpec
- [x] 1.4 集成 MemoryFlushHook：对话结束时自动将记忆刷盘到工作区 MEMORY.md
- [x] 1.5 集成 MemoryMaintenanceHook：定期整理记忆（去重、归档、摘要）
- [x] 1.6 实现用户上下文 Hook：注入 user_id 到 HarnessAgent RuntimeContext
- [x] 1.7 扩展 AgentLoaderService：添加 .workspace()、.filesystem()、.disableMemoryHooks()、.disableMemoryMaintenance() 配置
- [x] 1.8 修改 AgentDefinition Entity：新增配置字段
- [x] 1.9 修改 Agent CRUD DTO：新增配置字段（AgentCreateRequest、AgentUpdateRequest、AgentResponse）
- [x] 1.10 前端：Agent 配置页增加"高级配置"Tab（工作区模式、文件系统、记忆开关）
- [x] 1.11 提交：feat(runtime): Workspace 隔离 + 记忆管理

## 迭代 4.2：会话增强与 Token 计量

- [x] 2.1 新增 Redis 依赖：spring-data-redis + commons-pool2
- [x] 2.2 新增 Redis 配置：application.yml 添加 spring.data.redis 配置
- [x] 2.3 集成 extensions-session-redis：SessionPersistenceHook 切换到 Redis 后端
- [x] 2.4 重构 SessionStore：从纯内存 ConcurrentHashMap 切换到 Redis（保持接口兼容）
- [x] 2.5 实现 Token 用量记录 Hook：从模型响应提取真实 input/output tokens（或保持占位待库支持）
- [x] 2.6 数据库迁移：新增 agent_instance 表（id、agent_id、session_id、status、started_at、last_active_at）
- [x] 2.7 实现 AgentInstance Entity/Mapper/Service：活跃 Agent 实例追踪
- [x] 2.8 前端：对话页面增加 Token 消耗实时统计（当前会话累计 input/output tokens）
- [x] 2.9 实现仪表盘统计 API：GET /api/v1/dashboard（Agent 数量、活跃会话数、今日对话量、Token 用量概览）
- [x] 2.10 前端：仪表盘首页（Agent 数量、活跃会话数、今日对话量、Token 用量概览卡片）
- [x] 2.11 前端：路由和侧边栏配置（/dashboard）
- [x] 2.12 提交：feat(runtime): Redis 会话持久化 + Token 计量 + 仪表盘

## 迭代 4.3：智能体循环增强

- [x] 3.1 数据库迁移：新增 agent_long_term_memory 表（id、agent_id、memory_type、content、metadata_json、created_time）
- [x] 3.2 实现 AgentLongTermMemory Entity/Mapper/Service
- [x] 3.3 集成 CompactionHook：上下文自动压缩（配置 compaction_trigger_pct、compaction_keep_recent）
- [x] 3.4 集成 ToolResultEvictionHook：工具结果过大时驱逐到文件（配置 tool_result_eviction_max_chars）
- [x] 3.5 集成 recoverFromOverflow()：上下文溢出自动恢复机制
- [x] 3.6 集成 LongTermMemory：跨会话长期记忆（配置 agent_long_term_memory 表）
- [x] 3.7 集成 PlanNotebook：任务规划能力（配置 enable_plan 字段）
- [x] 3.8 扩展 AgentLoaderService：添加 .compaction()、.toolResultEviction()、.longTermMemory()、.planNotebook() 配置
- [x] 3.9 前端：Agent 设计器增加"循环增强"配置区域（上下文压缩开关/阈值、工具结果驱逐阈值、长期记忆开关、计划执行开关）
- [x] 3.10 提交：feat(runtime): 循环增强（压缩/驱逐/长期记忆/计划执行）

## 迭代 4.4：Agent 生命周期完善

- [x] 4.1 重构 AgentLoaderService：对接模型管理基础设施（确保从 model_config 表读取模型配置）
- [x] 4.2 实现 Agent 生命周期状态机：draft → active → archived 转换逻辑
- [x] 4.3 修改 AgentDefinitionService：创建时默认 draft，发布 API，归档 API，重新激活 API
- [x] 4.4 新增 Agent 发布/归档/重新激活 API：POST /api/v1/agents/{id}/publish、POST /api/v1/agents/{id}/archive、POST /api/v1/agents/{id}/reactivate
- [x] 4.5 修改 Agent 列表查询：默认只返回 active 状态，支持 status 筛选
- [x] 4.6 draft 状态 Agent 仅创建者可见，不可对话
- [x] 4.7 前端：Agent 列表页增加状态筛选（全部/draft/active/archived）
- [x] 4.8 前端：Agent 详情页增加生命周期状态展示和操作按钮（发布/归档/重新激活）
- [x] 4.9 提交：feat(runtime): Agent 生命周期状态机（draft/active/archived）

## 迭代 4.5：Build 阶段 Bug 修复（comet-verify 入口补登）

- [x] 5.1 修复 DashboardController.getStats() 未用 ApiResponse 包装：前端 request.ts 拦截器要求 res.code === 200，原实现直接返回 DashboardStatsResponse 导致 "请求失败" 报错
- [x] 5.2 修复 AgentVersionService.publishVersion() 状态值不一致：将 setStatus("published") 改为 setStatus("active")，与 AgentDefinitionService、前端 statusLabel、schema.sql 注释保持一致
- [x] 5.3 修复 AgentDefinition.status 字段 Javadoc 过时：将 "draft/published/archived" 改为 "draft/active/archived"
- [x] 5.4 状态：用户已决定本轮 3 修复暂不 git commit，工作区改动保留待用户后续处理（不阻塞 build → verify 流转；遗留改动由用户决定是否提交或拆分独立 change）
