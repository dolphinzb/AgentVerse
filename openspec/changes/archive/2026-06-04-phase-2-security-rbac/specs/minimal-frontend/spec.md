## MODIFIED Requirements

### Requirement: Agent 列表页面
**变更内容**：需要登录才能访问；developer 只能看到自己创建的 Agent；新建按钮按权限显示

系统 SHALL 提供 Agent 列表页面，展示 Agent 的基本信息和操作按钮。

#### Scenario: 已登录用户查看 Agent 列表
- **WHEN** 已登录用户访问 Agent 列表页面
- **THEN** 页面显示符合条件的 Agent 表格（developer 只显示自己创建的，admin 显示全部）

#### Scenario: 未登录用户访问 Agent 列表
- **WHEN** 未登录用户访问 Agent 列表页面
- **THEN** 自动跳转到登录页面

#### Scenario: 按权限显示操作按钮
- **WHEN** viewer 用户访问 Agent 列表
- **THEN** 不显示"编辑"、"删除"、"发布"按钮
- **WHEN** developer 用户访问
- **THEN** 只在自己创建的 Agent 行显示"编辑"、"删除"、"发布"按钮
- **WHEN** admin 用户访问
- **THEN** 显示所有操作按钮

### Requirement: Agent 创建/编辑表单
**变更内容**：未登录用户不能创建/编辑

系统 SHALL 提供 Agent 创建和编辑的表单界面。

#### Scenario: 成功创建 Agent
- **WHEN** 已登录且有 agent:create 权限的用户填写完整表单并提交
- **THEN** 前端调用 `POST /api/v1/agents` API，成功后跳转到 Agent 列表页

#### Scenario: 无权限用户不能创建
- **WHEN** 无 agent:create 权限的用户访问创建 Agent 页面
- **THEN** 页面不显示创建按钮，或访问时返回 403

#### Scenario: 未登录访问创建页面
- **WHEN** 未登录用户访问 Agent 创建页面
- **THEN** 自动跳转到登录页面

### Requirement: Agent 版本发布
**变更内容**：按权限控制发布按钮

系统 SHALL 提供 Agent 版本发布功能。

#### Scenario: 有发布权限的用户看到发布按钮
- **WHEN** 有 agent:publish 权限的用户在 Agent 详情页
- **THEN** 显示"发布版本"按钮

#### Scenario: 无发布权限的用户看不到发布按钮
- **WHEN** 无 agent:publish 权限的用户在 Agent 详情页
- **THEN** 不显示"发布版本"按钮

### Requirement: 对话界面
**变更内容**：需要登录且有 chat:create 权限

系统 SHALL 提供对话界面，允许用户与 Agent 进行交互。

#### Scenario: 已登录用户创建会话
- **WHEN** 已登录且有 chat:create 权限的用户在对话页面选择 Agent 并点击"开始对话"
- **THEN** 前端调用 `POST /api/v1/chat/sessions` API，创建会话后进入对话界面

#### Scenario: 无 chat:create 权限的用户
- **WHEN** 无 chat:create 权限的用户访问对话页面
- **THEN** 不显示"开始对话"按钮或提示无权限

#### Scenario: 未登录用户访问对话页面
- **WHEN** 未登录用户访问对话页面
- **THEN** 自动跳转到登录页面

### Requirement: 响应式布局
**变更内容**：登录页面也需要响应式适配

系统 SHALL 提供响应式布局，适配不同屏幕尺寸。

#### Scenario: 桌面端访问
- **WHEN** 用户使用桌面浏览器（宽度 > 768px）访问
- **THEN** 页面显示完整的侧边栏导航和主内容区

#### Scenario: 移动端访问
- **WHEN** 用户使用移动设备（宽度 ≤ 768px）访问
- **THEN** 页面隐藏侧边栏，显示汉堡菜单，主内容区占满屏幕

## ADDED Requirements

### Requirement: 登录/注册页面
系统 SHALL 提供登录页面和注册页面。

#### Scenario: 用户登录成功
- **WHEN** 用户在登录页面输入正确的用户名和密码
- **THEN** 前端存储 JWT Token，调用 /auth/me 获取用户信息，跳转到首页或来源页面

#### Scenario: 用户登录失败
- **WHEN** 用户输入错误的用户名或密码
- **THEN** 前端显示错误提示，不存储 Token

#### Scenario: 用户注册成功
- **WHEN** 用户在注册页面填写用户名（唯一）、密码（8位以上）、邮箱并提交
- **THEN** 前端调用注册 API，成功后自动登录并跳转到首页

#### Scenario: 用户注册失败（用户名已存在）
- **WHEN** 用户注册时填写的用户名已被占用
- **THEN** 前端显示错误提示

### Requirement: 路由守卫
系统 SHALL 实现前端路由守卫，保护需要认证的页面。

#### Scenario: 已登录用户访问受保护页面
- **WHEN** 已登录用户访问任何受保护页面（如 /agents）
- **THEN** 页面正常显示

#### Scenario: 未登录用户访问受保护页面
- **WHEN** 未登录用户访问受保护页面
- **THEN** 自动跳转到 /login?redirect=原始路径，登录成功后跳转回原始路径

#### Scenario: 已登录用户访问登录页
- **WHEN** 已登录用户访问 /login 页面
- **THEN** 自动跳转到首页

### Requirement: Token 管理
系统 SHALL 在 Axios 拦截器中自动注入 Token，并处理 401 响应。

#### Scenario: 请求自动注入 Token
- **WHEN** 前端发起任何 API 请求
- **THEN** Axios 请求拦截器从 localStorage 读取 Token，添加到 Authorization 头

#### Scenario: Token 过期返回 401
- **WHEN** API 返回 401 状态码
- **THEN** Axios 响应拦截器清除本地 Token，跳转到登录页面

### Requirement: 用户状态管理
系统 SHALL 使用 Pinia 管理用户登录状态。

#### Scenario: 登录后用户信息存储
- **WHEN** 用户登录成功
- **THEN** Pinia store 存储用户信息（id、username、email、roles）和权限列表

#### Scenario: 登出
- **WHEN** 用户点击"登出"按钮
- **THEN** 清除 localStorage 中的 Token，Pinia store 重置，跳转到登录页

### Requirement: v-permission 指令
系统 SHALL 提供 v-permission Vue 指令，根据用户权限控制元素可见性。

#### Scenario: 有权限时渲染元素
- **WHEN** 用户拥有 agent:create 权限
- **THEN** v-permission="'agent:create'" 的元素正常渲染

#### Scenario: 无权限时不渲染元素
- **WHEN** 用户没有 agent:create 权限
- **THEN** v-permission="'agent:create'" 的元素不渲染（DOM 中不存在）

### Requirement: 审计日志页面（admin）
系统 SHALL 提供审计日志页面，仅 admin 可访问。

#### Scenario: admin 查看审计日志
- **WHEN** admin 用户访问 /admin/audit-logs
- **THEN** 显示审计日志表格（时间、用户、操作类型、目标、IP）

#### Scenario: 非 admin 不能访问审计日志
- **WHEN** 非 admin 用户访问 /admin/audit-logs
- **THEN** 返回 403 或跳转到其他页面
