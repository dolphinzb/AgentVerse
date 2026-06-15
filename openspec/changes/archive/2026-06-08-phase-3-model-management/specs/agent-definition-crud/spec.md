# agent-definition-crud（修改）

## 变更说明

agent_definition 表新增 model_config_id 字段，Agent 创建/编辑支持模型选择，AgentResponse 增加模型信息。

## 数据模型变更

### agent_definition 表新增字段

| 字段 | 类型 | 说明 |
|------|------|------|
| model_config_id | VARCHAR(64) NOT NULL | 关联 model_config.id，创建时必填 |

## API 变更

### POST /v1/agents

- 请求新增 `modelConfigId`（必填）
- 后端校验 modelConfigId 有效性和数据隔离

### PUT /v1/agents/{id}

- 请求新增 `modelConfigId`（可选，更新模型绑定）

### GET /v1/agents/{id}

- 响应新增 `modelConfigId`、`providerName`、`modelName`

## 业务规则

- Agent 创建时必须选择模型（modelConfigId 必填）
- 更新 Agent 时可更换模型
- 删除 ModelConfig 时检查是否有 Agent 引用，有则拒绝（1506）
- 禁用 Provider 后，关联 Agent 对话报错（1503）
