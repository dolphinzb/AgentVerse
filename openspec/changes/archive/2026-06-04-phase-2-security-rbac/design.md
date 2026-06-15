## Context

AgentVerse 阶段一已完成最小可运行 POC，所有接口均为匿名访问，无用户身份标识。用户创建的 Agent 无法区分归属，会话无用户关联，操作无审计记录。

阶段二目标是建立完整的用户认证和 RBAC 权限体系，确保：用户身份可追溯、角色权限受控、操作可审计。

**当前状态：**
- 后端：Spring Boot 3.3.x + MyBatis-Plus + H2（开发）/ PostgreSQL（生产）
- 前端：Vue 3 + Vite 6 + Element Plus + Pinia + Vue Router
- 现有 API：无认证的 Agent CRUD、Chat 接口

**关键约束：**
- 单租户（公司内部使用，不考虑多租户 SaaS）
- 使用已有 PostgreSQL，不引入 Redis
- Token 黑名单存在数据库而非 Redis
- 90 天审计日志保留

## Goals / Non-Goals

**Goals:**
- 用户可注册、登录、登出
- JWT Token 认证，24h 有效期
- 操作级 RBAC（agent:create/update/delete/read/publish, chat:create/read, admin:audit）
- 数据隔离：用户只能操作自己创建的 Agent（admin 除外）
- 审计日志：记录所有写操作，用户/操作/目标/详情/IP
- 前端：登录态管理、路由守卫、按钮级权限控制

**Non-Goals:**
- 多租户 SaaS 支持
- refresh_token（保持简单，24h Token 足够）
- 密码重置功能
- 外部 OAuth 登录（LDAP/SSO）
- Token 主动失效（登出仅客户端清除，黑名单可后续按需加）

## Decisions

### Decision 1: JWT Token 策略

**选择：仅 Access Token，24h 有效期，无 refresh_token**

| 方案 | 优点 | 缺点 |
|------|------|------|
| 仅 Access Token 24h | 简单，无需 refresh 机制，前端只需管理一个 Token | Token 泄露后攻击窗口 24h |
| Access + Refresh | 泄露窗口小，支持续期 | 实现复杂，refresh 流转需考虑并发、轮转 |
| PostgreSQL 黑名单 | 无需 Redis，PostgreSQL 已有 | 每次请求需查黑名单表（可忽略不计） |

**结论**：内部工具无强制合规要求，24h Token 可接受。简化实现，优先上线。

### Decision 2: Token 黑名单存储

**选择：PostgreSQL token_blacklist 表**

```sql
CREATE TABLE token_blacklist (
    id          BIGINT PRIMARY KEY DEFAULT nextval('seq_token_blacklist'),
    token_jti   VARCHAR(64) NOT NULL UNIQUE,  -- JWT ID
    expires_at  TIMESTAMP NOT NULL,           -- Token 过期时间（用于定时清理）
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

- JwtFilter 拦截每个请求时，解析 Token JTI，查黑名单表
- 登出时 Token JTI 写入黑名单
- 定时任务清理过期条目（可复用现有调度框架）

### Decision 3: 密码存储

**选择：BCrypt，强度 10（默认值）**

- Spring Security BCryptPasswordEncoder
- 注册时加密存储，登录时校验
- 密码最小长度 8 位

### Decision 4: RBAC 粒度

**选择：操作级权限**

权限 code 格式：`{module}:{action}`

| 权限 code | 说明 | admin | developer | operator | viewer |
|-----------|------|-------|-----------|----------|--------|
| agent:create | 创建 Agent | ✓ | ✓ | ✗ | ✗ |
| agent:update | 更新 Agent | ✓ | 自己的 | ✗ | ✗ |
| agent:delete | 删除 Agent | ✓ | 自己的 | ✗ | ✗ |
| agent:read | 查看 Agent | ✓ | ✓ | ✓ | ✓ |
| agent:publish | 发布 Agent | ✓ | 自己的 | ✗ | ✗ |
| chat:create | 创建会话 | ✓ | ✓ | ✓ | ✗ |
| chat:read | 读取会话 | ✓ | ✓ | ✓ | ✓ |
| admin:audit | 查看审计日志 | ✓ | ✗ | ✗ | ✗ |

### Decision 5: 数据隔离实现

**选择：MyBatis-Plus 拦截器 + ThreadLocal**

```
JwtAuthFilter          → 解析 Token → 提取 user_id
       ↓
UserContextFilter      → user_id 存入 ThreadLocal
       ↓
MyBatisPlusMetaObjectHandler → 所有实体 save/update 时自动填充 created_by
       ↓
Service 层查询         → BaseEntity 已注入 user_id 条件（admin 除外）
```

- admin 角色绕过数据隔离
- developer 角色只能操作 created_by = 当前用户 的 Agent

### Decision 6: 审计日志切面

**选择：AOP 拦截所有 @PostMapping/@PutMapping/@DeleteMapping**

```java
@Aspect
@Component
public class AuditLogAspect {
    // 拦截 Controller 层所有写操作
    // 记录：user_id, action, target, detail, ip, created_at
}
```

- 不拦截 @GetMapping（读操作不记审计）
- detail 字段存储请求参数 JSON（脱敏处理密码）
- AccessLogFilter 单独记录所有请求的 方法/路径/耗时/状态码

### Decision 7: 审计日志保留

**选择：90 天保留，超期物理删除**

- `audit_log.expires_at = created_at + 90 days`
- 定时任务每天凌晨清理 `expires_at < NOW()` 的记录
- data.sql 提供初始化审计日志保留 90 天的说明

### Decision 8: 技术栈

| 组件 | 选择 | 说明 |
|------|------|------|
| JWT 库 | jjwt 0.12.x | Java 标准，API 清晰 |
| 安全框架 | Spring Security 6 | 与 Spring Boot 3 兼容 |
| 前端认证 | localStorage + Axios 拦截器 | 简单够用 |
| 按钮权限 | v-permission 指令 | Vue 自定义指令 |

## Risks / Trade-offs

| Risk | 描述 | Mitigation |
|------|------|------------|
| 密码明文传输 | 前端到后端 HTTP 请求密码明文 | 生产环境强制 HTTPS |
| Token 泄露 24h | Access Token 泄露后攻击窗口 24h | 内部工具风险可控；后续可加 refresh_token |
| 审计日志性能 | 高频写操作时 AOP 可能影响性能 | 审计日志异步写入；后续可加消息队列 |
| 初始化 admin 账号 | 首个账号谁来创建 | data.sql 提供 INSERT 语句，手动执行 |

## Migration Plan

**Phase 2 部署步骤：**

1. **数据库迁移**：执行 schema.sql（新建 6 张表 + 修改 1 张表）
2. **初始化数据**：执行 data.sql（角色、权限、首个 admin 账号）
3. **重启后端**：加载新模块，Spring Security 配置生效
4. **重启前端**：加载新登录页、路由守卫
5. **验证流程**：
   - 管理员登录 → 查看审计日志（空）
   - 创建其他角色用户 → 验证权限隔离
   - 执行业务操作 → 验证审计日志记录

**回滚方案：**
- 数据库回滚：运行 rollback-schema.sql（删除新增表，恢复 agent_definition）
- 代码回滚：Git revert Phase 2 commit

## Open Questions

1. **首个 admin 账号的密码**：data.sql 中 BCrypt 加密后的密码是默认值 `admin123`，是否需要改成随机密码首次登录强制修改？
2. **developer 角色数据隔离的边界**：是否允许 developer 查看其他人的 Agent（只读）？还是完全隔离？
3. **审计日志异步写入**：当前设计是同步写入，高并发场景是否需要改成异步？
