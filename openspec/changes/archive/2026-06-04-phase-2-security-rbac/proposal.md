## Why

AgentVerse 阶段一实现了最小可运行 POC，所有接口均为匿名访问。在投入生产使用前，必须添加用户认证和 RBAC 权限控制，确保：1）用户身份可追溯；2）不同角色只能操作授权范围内的资源；3）所有写操作可审计。这是平台安全的基座，也是后续阶段（工具集、知识库、工作流）能安全扩展的前提。

## What Changes

**迭代 2.1：用户体系与 JWT 认证**
- 新增数据库表：sys_user、sys_role、sys_permission、sys_role_permission、token_blacklist
- 新增用户注册 API（密码 BCrypt 加密存储）
- 新增登录 API（返回 access_token，24h 有效期）
- 新增登出 API（Token 加入黑名单）
- 新增获取当前用户信息 API（GET /auth/me）
- 前端新增登录/注册页面，Token 存储于 localStorage

**迭代 2.2：RBAC 权限与数据隔离**
- 新增 JwtAuthFilter（解析 Token、校验签名、提取用户信息）
- 新增 UserContextFilter（user_id 注入 ThreadLocal）
- 新增 @RequirePermission 注解 + AOP 拦截器
- 新增 RbacPermissionFilter（WebFilter 层统一鉴权）
- agent_definition 表新增 created_by 字段，查询时自动注入 user_id 实现数据隔离
- 初始化 4 个预设角色（admin/developer/operator/viewer）及各模块操作权限
- 前端侧边栏按角色动态显示/隐藏菜单，按钮级权限控制（v-permission 指令）

**迭代 2.3：审计日志**
- 新增 audit_log 表，记录用户/操作/目标/详情/IP/时间
- 新增审计日志切面（AOP 拦截所有写操作）
- 新增 AccessLogFilter（记录请求方法、路径、耗时、状态码）
- 新增审计日志查询 API（GET /admin/audit-logs，分页+时间/操作类型/用户筛选）
- 前端新增审计日志页面
- 审计日志保留 90 天，超期自动清理

**初始化数据**
- 提供 data.sql 脚本，初始化 4 个预设角色、权限数据、首个 admin 账号

## Capabilities

### New Capabilities

- `user-auth`: 用户注册、登录、登出、JWT Token 管理（24h 有效期，BCrypt 密码存储）
- `rbac-permissions`: 操作级 RBAC（agent:create/update/delete/read/publish, chat:create/read, admin:audit）、@RequirePermission 注解、数据隔离（created_by 自动注入）
- `audit-logging`: 审计日志切面、AccessLogFilter、audit_log 表设计、90 天保留策略、查询 API
- `token-blacklist`: 登出 Token 黑名单（数据库存储，JwtFilter 校验）

### Modified Capabilities

- `agent-crud`: agent_definition 表新增 created_by 字段，与 sys_user 表关联；创建/更新时自动注入 user_id
- `minimal-frontend`: 新增登录/注册页面、路由守卫（未登录重定向）、Token 管理、Pinia 用户状态、动态菜单、v-permission 指令

## Impact

- **代码结构**：agent-platform-common 新增 security 子包（JWT 工具、Security 配置）；agent-platform-runtime 新增 auth、rbac、audit 包
- **数据库**：新增 6 张表（sys_user、sys_role、sys_permission、sys_role_permission、token_blacklist、audit_log）；修改 1 张表（agent_definition 新增 created_by）
- **外部依赖**：jjwt 0.12.x（JWT 处理）、Spring Security 6.x
- **API**：新增 `/api/v1/auth/*`（login/logout/register/me）、`/api/v1/admin/audit-logs`；现有 API 全部新增权限校验
- **前端**：新增登录/注册视图、审计日志视图；路由守卫；Pinia user store；v-permission 指令
- **部署**：继续使用外部 PostgreSQL（无需 Redis）；初始化脚本需手动执行创建首个 admin 账号
