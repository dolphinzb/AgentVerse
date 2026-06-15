# Comet Design Handoff

- Change: session-harness-native-refactor
- Phase: design
- Mode: compact
- Context hash: 443703f8a46e968406059202980d705b7728f51581bad9a5091a347def39938c

Generated-by: comet-handoff.sh

OpenSpec remains the canonical capability spec. This handoff is a deterministic, source-traceable context pack, not an agent-authored summary.

---

## openspec/changes/session-harness-native-refactor/proposal.md

- Source: openspec/changes/session-harness-native-refactor/proposal.md
- Lines: 1-130
- SHA256: A136BA2FFCD652C99C723B46C99D2F9C5237A022D479AEDE3CCB6400B4E62268

[TRUNCATED]

```md
# Session 存储让位给 HarnessAgent：消除影子库与保真度损失

## Why

ChatService 自建 SessionStore 持久化"user/assistant 纯文本"，与 HarnessAgent 并行。三大问题：影子库/能力绕过/Dual-write 漂移。

## What Changes

- 拆 SessionStore → SessionIndex + 委派
- 引入 AgentScope SessionSpec 后端（extensions-session-redis）
- 捕获所有事件类型（HookEvent）
- 分布式中断（InterruptBus via Redis Pub/Sub）
- Token 用量由 TokenUsageHook 在 PostCallEvent 提取
- Legacy 迁移（启动时异步）

## 关键设计

1. SessionIndex 仅元数据，不存消息
2. SessionSpec 复用 agentscope 后端
3. MessageResponse 增 blocks 字段（保留 content 兼容）
4. InterruptBus 用 Redis Pub/Sub
5. Token 用量由 HookEvent（PostCallEvent）提取
6. Legacy 迁移一次性、删除老 key
```

Full source: openspec/changes/session-harness-native-refactor/proposal.md

---

## openspec/changes/session-harness-native-refactor/design.md

- Source: openspec/changes/session-harness-native-refactor/design.md
- Lines: 1-160
- SHA256: 27F6D9631F32047598E2107A29B4E930F43AFD264C27408C47EBF4913C79308F

[TRUNCATED]

```md
# Design — HarnessAgent 接管会话权威

## 6 个关键设计决策

1. SessionIndex 仅元数据
2. SessionSpec 复用 agentscope 后端
3. MessageResponse 增 blocks 字段
4. InterruptBus 用 Redis Pub/Sub
5. Token 用量由 HookEvent（PostCallEvent）提取
6. Legacy 迁移一次性

## HookEvent 映射表

| 事件 | 用途 |
|------|------|
| PreCallEvent / PostCallEvent | agent 调用前后；PostCall 提取 token |
| PreReasoningEvent / PostReasoningEvent / ReasoningChunkEvent | 推理流 |
| PreActingEvent / PostActingEvent / ActingChunkEvent | 工具流 |
| PreSummaryEvent / PostSummaryEvent / SummaryChunkEvent | 摘要流 |
| ErrorEvent | 错误 |

## 实施分阶段

Phase 1（接口层）: SessionIndex / MessageResponse.blocks
Phase 2（持久化后端）: SessionSpec / MessageProjector / LegacySessionMigrator
Phase 3（辅助子系统）: InterruptBus / TokenUsageHook / 清理旧实现
```

Full source: openspec/changes/session-harness-native-refactor/design.md

---

## openspec/changes/session-harness-native-refactor/tasks.md

- Source: openspec/changes/session-harness-native-refactor/tasks.md
- Lines: 1-65
- SHA256: 2BF1632AB542F26156EA6021FEDF9F7C6BE7A66A1B2FF53D2B2A0B85320581FA

[TRUNCATED]

```md
# Tasks — session-harness-native-refactor

Phase 1（接口层）: 1.1-1.7, 7 个任务
Phase 2（持久化后端）: 2.1-2.7, 7 个任务
Phase 3（辅助子系统）: 3.1-3.6, 8 个任务（含 3.3a/3.3b 拆分）
验证清单: V1-V6, 6 项
```

Full source: openspec/changes/session-harness-native-refactor/tasks.md

---

## openspec/changes/session-harness-native-refactor/specs/harness-native-session/spec.md

- Source: openspec/changes/session-harness-native-refactor/specs/harness-native-session/spec.md
- SHA256: 94F61E709BFA2AFC358FCE1819D70F543DE5229E565204222192872BD4F2E9CC

新增能力：HarnessAgent 接管 session 存储权威；SessionIndex 仅元数据；MessageResponse 增 blocks 字段。

---

## openspec/changes/session-harness-native-refactor/specs/distributed-interrupt/spec.md

- Source: openspec/changes/session-harness-native-refactor/specs/distributed-interrupt/spec.md
- SHA256: 60365B3260E8F2973A36197B8686E14F89C915FB32B3520194EBE3A8CA97146A

新增能力：Redis Pub/Sub 跨实例中断；本地命中优先；订阅 agentverse:interrupt:*。

---

## openspec/changes/session-harness-native-refactor/specs/chat-service/spec.md

- Source: openspec/changes/session-harness-native-refactor/specs/chat-service/spec.md
- SHA256: 3A2DAC2592AD43A493B6BD1FD921187C55785842E6C7FF722464F1149F89609C

修改：ChatService 不再写 SessionStore；消息由 HarnessAgent 内部管理；读路径走 MessageProjector。

---

## openspec/changes/session-harness-native-refactor/specs/session-persistence-dashboard/spec.md

- Source: openspec/changes/session-harness-native-refactor/specs/session-persistence-dashboard/spec.md
- SHA256: 34BF14197EF7F64EFFC78B7E5BD2005999272D35588EDD409FF1EDEF0B362715

修改：SessionStore 拆为 SessionIndex + SessionSpec；Redis key 变更；新增 LegacySessionMigrator。

---

## openspec/changes/session-harness-native-refactor/specs/loop-enhancements/spec.md

- Source: openspec/changes/session-harness-native-refactor/specs/loop-enhancements/spec.md
- SHA256: 98B79DA7F3E7CE0D2B8A8612AE3DDDBE44CC29920779A32107D6DFF50AA3310C

修改：SessionSpec 后端配置移入 SessionSpecConfig Bean；TokenUsageHook 在 PostCallEvent 提取 tokens。

---

## openspec/changes/session-harness-native-refactor/specs/harness-agent-engine/spec.md

- Source: openspec/changes/session-harness-native-refactor/specs/harness-agent-engine/spec.md
- SHA256: E32406BE8DC3995494D9729736366EA104FFFF4B72EA34D0E2A0B5958D71135D

修改：删除 isValidEventType 手动过滤；runningAgents → InterruptBus。
