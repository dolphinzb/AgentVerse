## ADDED Requirements

### Requirement: Agent 创建
系统 SHALL 提供 `POST /api/v1/agents` API，接受 JSON 请求体（name、description、sys_prompt、max_iterations），在 `agent_definition` 表中创建一条新记录，返回创建的 Agent 完整信息（含自动生成的 id、status=draft、created_at、updated_at）。

#### Scenario: 成功创建 Agent
- **WHEN** 客户端发送合法的 POST 请求，包含 name、description、sys_prompt
- **THEN** 系统在 `agent_definition` 表创建记录，status 默认值为 `draft`，返回 200 和完整 Agent 信息

#### Scenario: 缺少必填字段 name
- **WHEN** 客户端发送 POST 请求，但缺少 name 字段
- **THEN** 系统返回 400 错误，包含明确的错误信息指出 name 是必填字段

### Requirement: Agent 查询详情
系统 SHALL 提供 `GET /api/v1/agents/{id}` API，根据 Agent ID 查询并返回完整的 Agent 信息。

#### Scenario: 查询存在的 Agent
- **WHEN** 客户端请求一个存在的 Agent ID
- **THEN** 系统返回 200 和该 Agent 的完整信息

#### Scenario: 查询不存在的 Agent
- **WHEN** 客户端请求一个不存在的 Agent ID
- **THEN** 系统返回 404 错误

### Requirement: Agent 列表分页查询
系统 SHALL 提供 `GET /api/v1/agents` API，支持分页查询和按状态筛选。

#### Scenario: 默认分页查询
- **WHEN** 客户端发送不带参数的 GET 请求
- **THEN** 系统返回第一页（默认每页 10 条）Agent 列表，包含总数和分页信息

#### Scenario: 按状态筛选
- **WHEN** 客户端发送带 `status=draft` 参数的 GET 请求
- **THEN** 系统只返回 status 为 draft 的 Agent 列表

### Requirement: Agent 更新
系统 SHALL 提供 `PUT /api/v1/agents/{id}` API，更新指定 Agent 的信息，自动更新 `updated_at` 字段。

#### Scenario: 成功更新 Agent
- **WHEN** 客户端发送合法的 PUT 请求更新 Agent 的 name 和 description
- **THEN** 系统更新 `agent_definition` 表对应记录，`updated_at` 自动更新为当前时间，返回 200 和更新后的完整信息

#### Scenario: 更新不存在的 Agent
- **WHEN** 客户端尝试更新一个不存在的 Agent ID
- **THEN** 系统返回 404 错误

### Requirement: Agent 删除
系统 SHALL 提供 `DELETE /api/v1/agents/{id}` API，删除指定 Agent 及其所有关联数据。

#### Scenario: 成功删除 Agent
- **WHEN** 客户端发送 DELETE 请求删除一个存在的 Agent
- **THEN** 系统从 `agent_definition` 表删除该记录，返回 200

#### Scenario: 删除不存在的 Agent
- **WHEN** 客户端尝试删除一个不存在的 Agent ID
- **THEN** 系统返回 404 错误
