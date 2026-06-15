## ADDED Requirements

### Requirement: Agent 列表页面
系统 SHALL 提供 Agent 列表页面，展示所有 Agent 的基本信息和操作按钮。

#### Scenario: 查看 Agent 列表
- **WHEN** 用户访问 Agent 列表页面
- **THEN** 页面显示所有 Agent 的表格（包含 id、name、description、status、created_at），支持分页

#### Scenario: 筛选 Agent
- **WHEN** 用户选择状态筛选条件（如 draft/published）
- **THEN** 页面只显示符合条件的 Agent 列表

#### Scenario: 创建新 Agent
- **WHEN** 用户点击"创建 Agent"按钮
- **THEN** 页面弹出创建表单（包含 name、description、sys_prompt、max_iterations 字段）

### Requirement: Agent 创建/编辑表单
系统 SHALL 提供 Agent 创建和编辑的表单界面。

#### Scenario: 成功创建 Agent
- **WHEN** 用户填写完整表单并提交
- **THEN** 前端调用 `POST /api/v1/agents` API，成功后跳转到 Agent 列表页

#### Scenario: 表单验证失败
- **WHEN** 用户未填写必填字段（如 name）就提交
- **THEN** 前端显示验证错误提示，不发送 API 请求

#### Scenario: 编辑现有 Agent
- **WHEN** 用户在列表中点击"编辑"按钮
- **THEN** 页面弹出编辑表单，预填充该 Agent 的现有数据

### Requirement: Agent 版本发布
系统 SHALL 提供 Agent 版本发布功能。

#### Scenario: 发布新版本
- **WHEN** 用户在 Agent 详情页点击"发布版本"按钮
- **THEN** 页面弹出发布表单（version、changelog），提交后调用 `POST /api/v1/agents/{id}/publish` API

#### Scenario: 查看版本历史
- **WHEN** 用户在 Agent 详情页点击"版本历史"
- **THEN** 页面显示该 Agent 的所有版本列表（version、changelog、created_at）

#### Scenario: 回滚版本
- **WHEN** 用户在版本历史中点击"回滚"按钮
- **THEN** 前端调用 `POST /api/v1/agents/{id}/rollback` API，成功后刷新页面显示回滚后的状态

### Requirement: 对话界面
系统 SHALL 提供对话界面，允许用户与 Agent 进行交互。

#### Scenario: 创建新会话
- **WHEN** 用户在对话页面选择 Agent 并点击"开始对话"
- **THEN** 前端调用 `POST /api/v1/chat/sessions` API，创建会话后进入对话界面

#### Scenario: 发送消息（同步模式）
- **WHEN** 用户在输入框输入消息并点击发送
- **THEN** 前端调用 `POST /api/v1/chat/sessions/{sessionId}/messages` API，显示用户消息和 Agent 响应

#### Scenario: 流式对话（SSE 模式）
- **WHEN** 用户开启"流式模式"开关并发送消息
- **THEN** 前端建立 SSE 连接，逐步显示 Agent 生成的响应内容

#### Scenario: 查看历史会话
- **WHEN** 用户在会话列表中点击历史会话
- **THEN** 前端调用 `GET /api/v1/chat/sessions/{sessionId}/messages` API，加载并显示历史消息

#### Scenario: 中断对话
- **WHEN** 用户在 Agent 响应过程中点击"中断"按钮
- **THEN** 前端调用中断 API，停止显示后续响应

### Requirement: 响应式布局
系统 SHALL 提供响应式布局，适配不同屏幕尺寸。

#### Scenario: 桌面端访问
- **WHEN** 用户使用桌面浏览器（宽度 > 768px）访问
- **THEN** 页面显示完整的侧边栏导航和主内容区

#### Scenario: 移动端访问
- **WHEN** 用户使用移动设备（宽度 ≤ 768px）访问
- **THEN** 页面隐藏侧边栏，显示汉堡菜单，主内容区占满屏幕
