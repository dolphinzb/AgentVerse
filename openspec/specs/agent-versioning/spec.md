## ADDED Requirements

### Requirement: Agent 版本发布
系统 SHALL 提供 `POST /api/v1/agents/{id}/publish` API，为指定 Agent 创建一个版本快照。

#### Scenario: 成功发布版本
- **WHEN** 客户端对已存在的 Agent 发送发布请求，包含 version（如 "v1.0"）和可选的 changelog
- **THEN** 系统在 `agent_version` 表创建一条记录（包含 agent_id、version、snapshot_yaml、changelog、created_at），并更新 `agent_definition` 表的 `current_version` 字段，返回 200 和版本信息

#### Scenario: 发布不存在的 Agent
- **WHEN** 客户端尝试对不存在的 Agent ID 发布版本
- **THEN** 系统返回 404 错误

#### Scenario: 版本号重复
- **WHEN** 客户端尝试使用已存在的版本号发布
- **THEN** 系统返回 400 错误，指出版本号已存在

### Requirement: Agent 版本历史查询
系统 SHALL 提供 `GET /api/v1/agents/{id}/versions` API，查询指定 Agent 的所有版本历史。

#### Scenario: 查询版本历史
- **WHEN** 客户端请求一个 Agent 的版本列表
- **THEN** 系统返回 200 和该 Agent 的所有版本记录（按 created_at 倒序排列）

### Requirement: Agent 版本回滚
系统 SHALL 提供 `POST /api/v1/agents/{id}/rollback` API，将 Agent 回滚到指定的历史版本。

#### Scenario: 成功回滚版本
- **WHEN** 客户端发送回滚请求，指定目标 version
- **THEN** 系统从 `agent_version` 表读取该版本的 snapshot_yaml，解析后更新 `agent_definition` 表，并更新 `current_version` 字段，返回 200

#### Scenario: 回滚到不存在的版本
- **WHEN** 客户端尝试回滚到一个不存在的版本号
- **THEN** 系统返回 404 错误
