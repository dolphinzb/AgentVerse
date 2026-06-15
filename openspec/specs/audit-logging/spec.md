## ADDED Requirements

### Requirement: 审计日志记录
系统 SHALL 通过 AOP 切面拦截所有 @PostMapping、@PutMapping、@DeleteMapping 方法，记录审计日志到 `audit_log` 表。

#### Scenario: 创建操作记录审计日志
- **WHEN** 用户调用 POST /api/v1/agents 创建 Agent
- **THEN** audit_log 表新增一条记录，包含 user_id、action="agent:create"、target=新 Agent ID、detail=请求参数 JSON、ip=请求 IP、created_at

#### Scenario: 更新操作记录审计日志
- **WHEN** 用户调用 PUT /api/v1/agents/{id} 更新 Agent
- **THEN** audit_log 表新增一条记录，包含 user_id、action="agent:update"、target=Agent ID、detail=变更内容 JSON

#### Scenario: 删除操作记录审计日志
- **WHEN** 用户调用 DELETE /api/v1/agents/{id} 删除 Agent
- **THEN** audit_log 表新增一条记录，包含 user_id、action="agent:delete"、target=Agent ID、detail=空或删除的 Agent 信息

#### Scenario: 读取操作不记录审计日志
- **WHEN** 用户调用 GET /api/v1/agents
- **THEN** 不记录审计日志（读操作不拦截）

### Requirement: 审计日志查询
系统 SHALL 提供 `GET /api/v1/admin/audit-logs` API，支持分页查询和筛选。

#### Scenario: 默认分页查询
- **WHEN** 管理员发送 GET /api/v1/admin/audit-logs 不带参数
- **THEN** 返回第一页（每页 20 条）审计日志列表，按时间倒序

#### Scenario: 按时间范围筛选
- **WHEN** 管理员发送 GET /api/v1/admin/audit-logs?start_time=2024-01-01&end_time=2024-01-31
- **THEN** 只返回该时间范围内的审计日志

#### Scenario: 按操作类型筛选
- **WHEN** 管理员发送 GET /api/v1/admin/audit-logs?action=agent:create
- **THEN** 只返回 agent:create 操作的审计日志

#### Scenario: 按用户筛选
- **WHEN** 管理员发送 GET /api/v1/admin/audit-logs?user_id=123
- **THEN** 只返回该用户的审计日志

#### Scenario: 非 admin 用户不能访问
- **WHEN** 非 admin 用户发送 GET /api/v1/admin/audit-logs
- **THEN** 系统返回 403 Forbidden

### Requirement: 审计日志保留 90 天
系统 SHALL 自动清理超过 90 天的审计日志记录。

#### Scenario: 定时清理过期日志
- **WHEN** 定时任务每天执行
- **THEN** 删除 audit_log 表中 created_at 早于 90 天的记录

### Requirement: 访问日志记录
系统 SHALL 提供 AccessLogFilter，记录所有 HTTP 请求的方法、路径、耗时、状态码。

#### Scenario: 记录请求访问日志
- **WHEN** 任意 HTTP 请求到达系统
- **THEN** AccessLogFilter 记录 method、path、duration_ms、status_code 到日志或单独表

### Requirement: 前端审计日志页面
系统 SHALL 提供审计日志页面，支持表格展示和筛选功能。

#### Scenario: 查看审计日志列表
- **WHEN** admin 用户访问 /admin/audit-logs 页面
- **THEN** 显示审计日志表格，包含时间、用户、操作类型、目标、IP 列

#### Scenario: 筛选审计日志
- **WHEN** admin 用户选择时间范围和操作类型，点击查询
- **THEN** 表格刷新显示符合条件的审计日志

#### Scenario: 非 admin 看不到审计日志入口
- **WHEN** 非 admin 用户登录
- **THEN** 侧边栏不显示审计日志菜单项
