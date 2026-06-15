# loop-enhancements

## 概述

智能体循环增强：上下文压缩（Compaction）、工具结果驱逐（Eviction）、长期记忆（LTM）、计划执行（Plan）。

## 数据模型

### agent_long_term_memory 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) PK | 记忆 ID |
| agent_id | VARCHAR(64) NOT NULL | 关联 agent_definition.id |
| memory_type | VARCHAR(32) NOT NULL | 记忆类型（episodic/semantic/procedural） |
| content | TEXT NOT NULL | 记忆内容 |
| metadata_json | TEXT | 元数据 JSON |
| created_time | TIMESTAMP NOT NULL | 创建时间 |

索引：`idx_agent_long_term_memory_agent_id`

## CompactionHook

- Builder 配置：`.compaction(CompactionConfig.builder().triggerMessages(80).keepMessages(10).build())`
- 当 `enable_compaction = 1` 时启用
- 参数从 `agent_definition` 的 `compaction_trigger_pct`、`compaction_keep_recent` 读取

## ToolResultEvictionHook

- Builder 配置：`.toolResultEviction(ToolResultEvictionConfig.builder().maxResultChars(4000).build())`
- 当 `enable_tool_result_eviction = 1` 时启用
- 参数从 `tool_result_eviction_max_chars` 读取

## LongTermMemory

- Builder 配置：`.longTermMemoryMode(LongTermMemoryMode.BOTH)`
- 当 `enable_long_term_memory = 1` 时启用
- 跨会话记忆：会话结束时保存重要信息到 `agent_long_term_memory` 表，新会话开始时加载

## PlanNotebook

- Builder 配置：`.enablePlan()`
- 当 `enable_plan = 1` 时启用
- Agent 可拆解复杂任务为步骤，逐步执行
