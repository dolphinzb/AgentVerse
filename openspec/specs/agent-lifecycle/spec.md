# agent-lifecycle

## 概述

Agent 生命周期状态机（draft → active → archived），前端状态筛选 + 操作按钮，对话创建时仅展示已发布 Agent。

## 生命周期状态机

```
draft ──publish──→ active ──archive──→ archived
  ↑                                    │
  └──────────reactivate────────────────┘
```

| 状态 | 可见性 | 对话 | 说明 |
|------|--------|------|------|
| draft | 仅创建者可见 | 不可对话 | 草稿状态，编辑中 |
| active | 默认列表可见 | 可对话 | 已发布，正常运行 |
| archived | 不在默认列表 | 不可对话 | 已归档，可重新激活 |

## API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /v1/agents/{id}/lifecycle/publish | draft → active，校验 modelConfigId 非空 | agent:publish |
| POST | /v1/agents/{id}/lifecycle/archive | active → archived，清除 Agent 缓存 | agent:archive |
| POST | /v1/agents/{id}/lifecycle/reactivate | archived → active | agent:reactivate |
| GET | /v1/agents?status=active | 按状态筛选 | agent:read |

## ChatService 校验

对话前校验 Agent status：
- `draft` → 抛出 `BizException(ErrorCode.AGENT_NOT_PUBLISHED)`
- `archived` → 抛出 `BizException(ErrorCode.AGENT_ARCHIVED)`
- `active` → 正常对话

## AgentLoaderService 校验

加载 Agent 时校验状态：
- `draft` → 抛出 `BizException(ErrorCode.AGENT_NOT_PUBLISHED)`
- `archived` → 抛出 `BizException(ErrorCode.AGENT_ARCHIVED)`

## 错误码

| 错误码 | 名称 | 触发场景 |
|--------|------|---------|
| 1601 | AGENT_NOT_PUBLISHED | 草稿 Agent 不可对话/加载 |
| 1602 | AGENT_ARCHIVED | 已归档 Agent 不可对话/加载 |
| 1603 | AGENT_ALREADY_PUBLISHED | 非 draft 状态尝试发布 |
| 1604 | AGENT_NOT_ACTIVE | 非 active 状态尝试归档 |

## RBAC 权限

| 权限代码 | 名称 | admin | developer | operator |
|----------|------|-------|-----------|----------|
| agent:archive | 归档 Agent | Y | Y | - |
| agent:reactivate | 重新激活 Agent | Y | Y | - |
| dashboard:read | 查看仪表盘 | Y | Y | Y |

## 前端

### AgentList.vue

- 状态筛选标签页：全部 / 草稿 / 已发布 / 已归档
- 状态列：颜色标签（draft=warning, active=success, archived=info）

### AgentDetail.vue

- 草稿状态：显示"发布"按钮（弹出版本号对话框，同时 draft→active + 创建版本快照）
- 已发布状态：显示"发布新版本"、"版本历史"、"开始对话"、"归档"
- 已归档状态：显示"重新激活"

### ChatView.vue

- 新建对话时 Agent 下拉列表仅展示 `status === 'active'` 的 Agent
