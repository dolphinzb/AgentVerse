# workspace-memory-management

## 概述

工作区隔离（按 userId/agentId 目录结构）、文件系统工厂、记忆 Hook 开关、AgentConfigAssembler 封装 HarnessAgent Builder 调用。

## 数据模型

### agent_definition 新增字段（迭代 4.1）

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| filesystem_type | VARCHAR(16) | 'local' | 文件系统类型（local/remote/sandbox） |
| enable_memory_flush | INTEGER | 1 | 启用记忆刷盘（MemoryFlushHook） |
| enable_memory_maintenance | INTEGER | 1 | 启用记忆维护（MemoryMaintenanceHook） |
| enable_compaction | INTEGER | 0 | 启用上下文压缩 |
| compaction_trigger_pct | INTEGER | 80 | 压缩触发阈值（消息数） |
| compaction_keep_recent | INTEGER | 10 | 压缩保留最近 N 条消息 |
| enable_tool_result_eviction | INTEGER | 0 | 启用工具结果驱逐 |
| tool_result_eviction_max_chars | INTEGER | 4000 | 驱逐阈值（字符数） |
| enable_long_term_memory | INTEGER | 0 | 启用长期记忆 |
| enable_plan | INTEGER | 0 | 启用计划执行 |
| enable_session_persistence | INTEGER | 1 | 启用会话持久化 |
| session_backend | VARCHAR(16) | 'redis' | 会话后端（redis/memory） |
| max_context_tokens | INTEGER | 8000 | 最大上下文 Token 数 |

## WorkspaceManager

- 基础目录从 `application.yml` 读取（`agent.workspace.base-dir`，默认 `./workspace`）
- `resolve(userId, agentId)` → 创建 `baseDir/user/{userId}/agent/{agentId}/` 目录
- 首次访问时 `Files.createDirectories()`，幂等

## FilesystemFactory

- 当前仅支持 `LocalFilesystemSpec`
- 工厂方法：`create(String filesystemType)` → 根据 `filesystem_type` 返回对应 Spec
- `filesystem_type = "local"` → `LocalFilesystemSpec`
- 后续可扩展 `remote`、`sandbox` 类型

## AgentConfigAssembler

封装 HarnessAgent Builder 调用，隔离 agentscope API 变更：

- `.workspace(path)` → WorkspaceManager 解析的路径
- `.filesystem(LocalFilesystemSpec)` → FilesystemFactory 创建
- `.disableMemoryHooks()` → 当 enableMemoryFlush=0
- `.compaction(CompactionConfig)` → 当 enableCompaction=1
- `.toolResultEviction(ToolResultEvictionConfig)` → 当 enableToolResultEviction=1
- `.longTermMemoryMode(LongTermMemoryMode.BOTH)` → 当 enableLongTermMemory=1
- `.enablePlan()` → 当 enablePlan=1
- `.disableSessionPersistence()` → 当 enableSessionPersistence=0
- `.maxContextTokens(int)` → 当 maxContextTokens 非空

## 前端

Agent 配置页新增"高级配置"Tab，包含所有运行时配置字段的表单。
