## ADDED Requirements

### Requirement: 用户注册
系统 SHALL 提供 `POST /api/v1/auth/register` API，接受 username、password、email 参数，创建新用户记录（密码 BCrypt 加密存储），返回用户基本信息。

#### Scenario: 成功注册
- **WHEN** 客户端发送合法的 POST 请求，包含 username（唯一）、password（至少 8 字符）、email
- **THEN** 系统在 `sys_user` 表创建记录，密码使用 BCrypt 加密，返回 200 和用户基本信息（不含密码）

#### Scenario: 用户名已存在
- **WHEN** 客户端发送注册请求，但 username 已被占用
- **THEN** 系统返回 409 Conflict，包含错误信息指出用户名冲突

#### Scenario: 密码不符合要求
- **WHEN** 客户端发送注册请求，但 password 少于 8 字符
- **THEN** 系统返回 400 Bad Request，包含错误信息指出密码要求

### Requirement: 用户登录
系统 SHALL 提供 `POST /api/v1/auth/login` API，接受 username、password 参数，校验通过后返回 JWT Access Token（24h 有效期）。

#### Scenario: 登录成功
- **WHEN** 客户端发送合法的登录请求，username 和 password 正确
- **THEN** 系统验证密码 BCrypt 匹配，生成 JWT Token 返回 200，包含 access_token、expires_in（86400秒）、user 基本信息

#### Scenario: 用户名不存在
- **WHEN** 客户端发送登录请求，但 username 不存在
- **THEN** 系统返回 401 Unauthorized，包含通用错误信息（不暴露用户名是否存在）

#### Scenario: 密码错误
- **WHEN** 客户端发送登录请求，但 password 错误
- **THEN** 系统返回 401 Unauthorized

### Requirement: 用户登出
系统 SHALL 提供 `POST /api/v1/auth/logout` API，接受当前 JWT Token，将 Token JTI 加入黑名单。

#### Scenario: 登出成功
- **WHEN** 已登录用户发送登出请求
- **THEN** 系统将 Token JTI 写入 `token_blacklist` 表，返回 200

### Requirement: 获取当前用户信息
系统 SHALL 提供 `GET /api/v1/auth/me` API，返回当前登录用户的基本信息。

#### Scenario: 获取成功
- **WHEN** 已登录用户发送 GET 请求到 /auth/me
- **THEN** 系统返回 200，包含用户 id、username、email、roles 列表

#### Scenario: 未登录访问
- **WHEN** 未登录用户发送 GET 请求到 /auth/me
- **THEN** 系统返回 401 Unauthorized

### Requirement: JWT Token 校验
系统 SHALL 在每个需要认证的 API 请求中校验 JWT Token，校验失败返回 401。

#### Scenario: Token 有效
- **WHEN** 请求携带有效的 Authorization: Bearer {token} 头
- **THEN** JwtFilter 解析 Token，提取 user_id 注入 UserContext，请求继续

#### Scenario: Token 缺失
- **WHEN** 请求不携带 Authorization 头或格式不正确
- **THEN** 系统返回 401 Unauthorized

#### Scenario: Token 已过期
- **WHEN** 请求携带过期 Token
- **THEN** 系统返回 401 Unauthorized，包含 Token 已过期的错误信息

#### Scenario: Token 在黑名单中
- **WHEN** 请求携带已登出的 Token（JTI 在黑名单）
- **THEN** 系统返回 401 Unauthorized
