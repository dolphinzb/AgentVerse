## ADDED Requirements

### Requirement: Token 黑名单存储
系统 SHALL 提供 `token_blacklist` 表，存储已登出 Token 的 JTI 和过期时间。

#### Scenario: 登出时写入黑名单
- **WHEN** 用户调用 POST /api/v1/auth/logout
- **THEN** Token JTI 和过期时间写入 token_blacklist 表

#### Scenario: 黑名单 Token 被拦截
- **WHEN** 已登出 Token 再次请求任何 API
- **THEN** JwtFilter 查询 token_blacklist 表发现 JTI 存在，返回 401

### Requirement: Token 黑名单清理
系统 SHALL 定时清理 token_blacklist 表中已过期（expires_at < NOW()）的记录。

#### Scenario: 清理过期黑名单记录
- **WHEN** 定时任务执行
- **THEN** 删除 token_blacklist 表中 expires_at 早于当前时间的记录（这些 Token 本身已无法使用）

### Requirement: JTI 作为 Token 唯一标识
系统 SHALL 在 JWT Token 中包含 jti（JWT ID）字段，作为黑名单查找的唯一键。

#### Scenario: Token 包含 JTI
- **WHEN** 系统生成 JWT Token
- **THEN** Token Payload 包含 jti 字段（UUID 格式），用于唯一标识该 Token
