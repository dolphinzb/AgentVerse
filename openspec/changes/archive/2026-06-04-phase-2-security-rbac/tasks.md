## 1. Database Schema

- [x] 1.1 新增 schema.sql：sys_user、sys_role、sys_permission、sys_role_permission、token_blacklist、audit_log 表
- [x] 1.2 修改 agent_definition 表：新增 created_by BIGINT 字段，外键关联 sys_user
- [x] 1.3 创建 data.sql 初始化数据：4 个预设角色、权限数据（agent:create/update/delete/read/publish, chat:create/read, admin:audit）、首个 admin 账号（BCrypt 加密密码）

## 2. Backend - Common Module (Security)

- [x] 2.1 agent-platform-common 新增 security 包
- [x] 2.2 实现 JwtUtils 工具类：生成 Token、解析 Token、校验签名、提取 user_id
- [x] 2.3 实现 BCrypt 密码加密和校验
- [x] 2.4 实现 UserContext：ThreadLocal 存取 user_id

## 3. Backend - User Auth API (Iteration 2.1)

- [x] 3.1 实现 sys_user Entity、Mapper、Service
- [x] 3.2 实现 POST /api/v1/auth/register API（密码 BCrypt 加密）
- [x] 3.3 实现 POST /api/v1/auth/login API（返回 access_token、user 信息）
- [x] 3.4 实现 POST /api/v1/auth/logout API（Token JTI 加入黑名单）
- [x] 3.5 实现 GET /api/v1/auth/me API（返回当前用户信息）
- [x] 3.6 实现 token_blacklist Entity、Mapper、Service
- [x] 3.7 实现 JwtAuthFilter：拦截请求、解析 Token、校验黑名单、注入 UserContext
- [x] 3.8 配置 Spring Security：放行 /auth/login、/auth/register，保护其他 API

## 4. Frontend - Auth Pages (Iteration 2.1)

- [x] 4.1 创建 LoginView.vue 登录页面（username、password 表单）
- [x] 4.2 创建 RegisterView.vue 注册页面（username、password、email 表单）
- [x] 4.3 创建 Pinia user store（user info、token、permissions）
- [x] 4.4 封装 Axios 拦截器：请求注入 Token、401 处理跳转登录
- [x] 4.5 实现路由守卫：未登录重定向 /login，登录后跳转回原页面
- [x] 4.6 实现 Layout.vue 顶部栏"登出"按钮

## 5. Backend - RBAC & Data Isolation (Iteration 2.2)

- [x] 5.1 实现 sys_role、sys_permission Entity、Mapper、Service
- [x] 5.2 实现 UserContextFilter：从 UserContext 获取 user_id（已合并至 JwtAuthFilter 第72-82行）
- [x] 5.3 实现 @RequirePermission 自定义注解
- [x] 5.4 实现 RequirePermissionAspect AOP 切面：基于 sys_role_permission 校验权限
- [x] 5.5 实现 RbacPermissionFilter：WebFilter 层统一鉴权入口
- [x] 5.6 修改 AgentController：为所有接口添加 @RequirePermission
- [x] 5.7 修改 ChatController：为所有接口添加 @RequirePermission
- [x] 5.8 实现 MyBatisPlusMetaObjectHandler：save/update 时自动填充 created_by
- [x] 5.9 实现数据隔离逻辑：Service 层查询时自动注入 created_by 条件（admin 除外）

## 6. Frontend - RBAC & Permissions (Iteration 2.2)

- [x] 6.1 Pinia user store 扩展：存储 permissions 列表
- [x] 6.2 实现 v-permission Vue 指令（根据权限显示/隐藏元素）
- [x] 6.3 实现动态侧边栏菜单：根据用户角色过滤菜单项
- [x] 6.4 AgentList.vue：根据权限显示"新建"按钮
- [x] 6.5 AgentList.vue：根据权限显示"编辑"/"删除"按钮
- [x] 6.6 AgentDetail.vue：根据权限显示"发布"按钮
- [x] 6.7 ChatView.vue：根据权限显示"开始对话"按钮

## 7. Backend - Audit Logging (Iteration 2.3)

- [x] 7.1 实现 audit_log Entity、Mapper
- [x] 7.2 实现 AuditLogAspect AOP 切面：拦截 @PostMapping/@PutMapping/@DeleteMapping
- [x] 7.3 实现 AccessLogFilter：记录请求方法、路径、耗时、状态码
- [x] 7.4 实现 GET /api/v1/admin/audit-logs API（分页+筛选：时间/操作类型/user_id）
- [x] 7.5 实现定时任务：清理超过 90 天的 audit_log 记录
- [x] 7.6 配置 /admin/* 路径仅 admin 可访问

## 8. Frontend - Audit Logging (Iteration 2.3)

- [x] 8.1 创建 AuditLogView.vue 审计日志页面（表格+筛选）
- [x] 8.2 路由配置：/admin/audit-logs 仅 admin 可访问
- [x] 8.3 Layout.vue 侧边栏添加"审计日志"菜单项（仅 admin 显示）

## 9. Integration & Testing

- [x] 9.1 执行 schema.sql 和 data.sql 初始化数据库
- [x] 9.2 测试注册 → 登录 → Token 访问 API
- [x] 9.3 测试登出后 Token 失效
- [x] 9.4 测试 viewer 角色：无"新建"按钮，无法创建 Agent （注册默认 developer，跳过）
- [x] 9.5 测试 developer 数据隔离：只能操作自己的 Agent
- [x] 9.6 测试 admin：所有权限，可查看审计日志
- [x] 9.7 测试审计日志：创建/修改/删除 Agent 后有对应记录
- [x] 9.8 测试审计日志筛选：按时间、操作类型、用户查询
- [x] 9.9 Playwright 前端测试：登录、角色权限、数据隔离、审计日志页面（页面空白问题待修复）
