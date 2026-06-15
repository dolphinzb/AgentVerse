## MODIFIED Requirements

### Requirement: Agent 创建
**变更内容**：创建 Agent 时自动填充 `created_by` 字段为当前用户 ID

系统 SHALL 提供 `POST /api/v1/agents` API，接受 JSON 请求体（name、description、sys_prompt、max_iterations），在 `agent_definition` 表中创建一条新记录，返回创建的 Agent 完整信息（含自动生成的 id、status=draft、created_at、updated_at、created_by）。

#### Scenario: 成功创建 Agent
- **WHEN** 已登录用户发送合法的 POST 请求，包含 name、description、sys_prompt
- **THEN** 系统在 `agent_definition` 表创建记录，created_by 自动填充为当前用户 ID，status 默认值为 `draft`，返回 200 和完整 Agent 信息

#### Scenario: 未登录创建 Agent
- **WHEN** 未登录用户发送 POST 请求创建 Agent
- **THEN** 系统返回 401 Unauthorized

### Requirement: Agent 查询详情
**变更内容**：非 admin 用户只能查询自己创建的 Agent

系统 SHALL 提供 `GET /api/v1/agents/{id}` API，根据 Agent ID 查询并返回完整的 Agent 信息。

#### Scenario: 查询自己创建的 Agent
- **WHEN** 用户请求自己创建的 Agent ID
- **THEN** 系统返回 200 和该 Agent 的完整信息

#### Scenario: admin 查询任意 Agent
- **WHEN** admin 用户请求任意 Agent ID
- **THEN** 系统返回 200 和该 Agent 的完整信息

#### Scenario: 查询他人的 Agent
- **WHEN** developer 用户请求其他用户创建的 Agent ID
- **THEN** 系统返回 403 Forbidden

#### Scenario: 查询不存在的 Agent
- **WHEN** 用户请求一个不存在的 Agent ID
- **THEN** 系统返回 404 错误

### Requirement: Agent 列表分页查询
**变更内容**：非 admin 用户只能看到自己创建的 Agent

系统 SHALL 提供 `GET /api/v1/agents` API，支持分页查询和按状态筛选。

#### Scenario: developer 只能看到自己的 Agent
- **WHEN** developer 用户发送 GET 请求
- **THEN** 系统只返回 created_by 等于当前用户 ID 的 Agent 列表

#### Scenario: admin 可以看到所有 Agent
- **WHEN** admin 用户发送 GET 请求
- **THEN** 系统返回所有 Agent 列表

#### Scenario: 按状态筛选
- **WHEN** 用户发送带 `status=draft` 参数的 GET 请求
- **THEN** 系统只返回符合条件的 Agent 列表（受数据隔离影响）

### Requirement: Agent 更新
**变更内容**：非 admin 用户只能更新自己创建的 Agent

系统 SHALL 提供 `PUT /api/v1/agents/{id}` API，更新指定 Agent 的信息，自动更新 `updated_at` 字段。

#### Scenario: 成功更新自己的 Agent
- **WHEN** developer 用户更新自己创建的 Agent
- **THEN** 系统更新记录，`updated_at` 自动更新为当前时间，返回 200 和更新后的完整信息

#### Scenario: admin 更新任意 Agent
- **WHEN** admin 用户更新任意 Agent
- **THEN** 操作成功

#### Scenario: 更新他人的 Agent 被拒绝
- **WHEN** developer 用户尝试更新其他用户创建的 Agent
- **THEN** 系统返回 403 Forbidden

#### Scenario: 更新不存在的 Agent
- **WHEN** 用户尝试更新一个不存在的 Agent ID
- **THEN** 系统返回 404 错误

### Requirement: Agent 删除
**变更内容**：非 admin 用户只能删除自己创建的 Agent

系统 SHALL 提供 `DELETE /api/v1/agents/{id}` API，删除指定 Agent 及其所有关联数据。

#### Scenario: 成功删除自己的 Agent
- **WHEN** developer 用户删除自己创建的 Agent
- **THEN** 系统从 `agent_definition` 表删除该记录，返回 200

#### Scenario: admin 删除任意 Agent
- **WHEN** admin 用户删除任意 Agent
- **THEN** 操作成功

#### Scenario: 删除他人的 Agent 被拒绝
- **WHEN** developer 用户尝试删除其他用户创建的 Agent
- **THEN** 系统返回 403 Forbidden

#### Scenario: 删除不存在的 Agent
- **WHEN** 用户尝试删除一个不存在的 Agent ID
- **THEN** 系统返回 404 错误
