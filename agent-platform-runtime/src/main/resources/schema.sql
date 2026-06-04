-- AgentVerse 数据库初始化脚本
-- 阶段一：最小可用 POC
-- 阶段二：安全与RBAC

-- ============================================================
-- 用户与权限表（Phase 2）
-- ============================================================

-- 创建系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    email VARCHAR(128),
    role_code VARCHAR(32) NOT NULL DEFAULT 'viewer',
    status INTEGER DEFAULT 1,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    deleted INTEGER DEFAULT 0
);

-- 创建系统角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY,
    role_code VARCHAR(32) NOT NULL UNIQUE,
    role_name VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    deleted INTEGER DEFAULT 0
);

-- 创建系统权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY,
    perm_code VARCHAR(64) NOT NULL UNIQUE,
    perm_name VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    deleted INTEGER DEFAULT 0
);

-- 创建角色-权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    perm_id BIGINT NOT NULL,
    created_time TIMESTAMP NOT NULL,
    CONSTRAINT uk_role_perm UNIQUE (role_id, perm_id)
);

-- 创建 Token 黑名单表
CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT PRIMARY KEY,
    token_jti VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(64),
    action VARCHAR(64) NOT NULL,
    target VARCHAR(256),
    detail TEXT,
    ip VARCHAR(64),
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Agent 相关表（Phase 1，Phase 2 修改）
-- ============================================================

-- 创建 Agent 定义表
CREATE TABLE IF NOT EXISTS agent_definition (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    sys_prompt TEXT,
    max_iterations INTEGER DEFAULT 10,
    workspace_mode VARCHAR(32) DEFAULT 'isolated',
    status VARCHAR(32) DEFAULT 'draft',
    current_version VARCHAR(64),
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_by VARCHAR(64),
    deleted INTEGER DEFAULT 0
);

-- 创建 Agent 版本表
CREATE TABLE IF NOT EXISTS agent_version (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64) NOT NULL,
    version VARCHAR(32) NOT NULL,
    snapshot_data TEXT NOT NULL,
    changelog TEXT,
    created_time TIMESTAMP NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_by VARCHAR(64),
    deleted INTEGER DEFAULT 0,
    CONSTRAINT fk_agent_version_agent FOREIGN KEY (agent_id) REFERENCES agent_definition(id) ON DELETE CASCADE,
    CONSTRAINT uk_agent_version UNIQUE (agent_id, version)
);

-- ============================================================
-- 索引
-- ============================================================

-- 用户与权限索引
CREATE INDEX IF NOT EXISTS idx_sys_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_sys_user_role_code ON sys_user(role_code);
CREATE INDEX IF NOT EXISTS idx_sys_user_deleted ON sys_user(deleted);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_jti ON token_blacklist(token_jti);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_expires ON token_blacklist(expires_at);
CREATE INDEX IF NOT EXISTS idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_action ON audit_log(action);
CREATE INDEX IF NOT EXISTS idx_audit_log_created_time ON audit_log(created_time);

-- Agent 相关索引
CREATE INDEX IF NOT EXISTS idx_agent_definition_status ON agent_definition(status);
CREATE INDEX IF NOT EXISTS idx_agent_definition_deleted ON agent_definition(deleted);
CREATE INDEX IF NOT EXISTS idx_agent_version_agent_id ON agent_version(agent_id);
CREATE INDEX IF NOT EXISTS idx_agent_version_deleted ON agent_version(deleted);

-- ============================================================
-- 表注释
-- ============================================================

COMMENT ON TABLE sys_user IS '系统用户表';
COMMENT ON COLUMN sys_user.id IS '用户 ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码（BCrypt 加密）';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.role_code IS '角色代码（admin/developer/operator/viewer）';
COMMENT ON COLUMN sys_user.status IS '状态（1:正常, 0:禁用）';

COMMENT ON TABLE sys_role IS '系统角色表';
COMMENT ON COLUMN sys_role.role_code IS '角色代码';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';

COMMENT ON TABLE sys_permission IS '系统权限表';
COMMENT ON COLUMN sys_permission.perm_code IS '权限代码（格式: module:action）';
COMMENT ON COLUMN sys_permission.perm_name IS '权限名称';

COMMENT ON TABLE sys_role_permission IS '角色-权限关联表';

COMMENT ON TABLE token_blacklist IS 'Token 黑名单表';
COMMENT ON COLUMN token_blacklist.token_jti IS 'JWT Token JTI';

COMMENT ON TABLE audit_log IS '审计日志表';
COMMENT ON COLUMN audit_log.user_id IS '操作用户 ID';
COMMENT ON COLUMN audit_log.action IS '操作类型';
COMMENT ON COLUMN audit_log.target IS '操作目标';
COMMENT ON COLUMN audit_log.detail IS '操作详情（JSON）';
COMMENT ON COLUMN audit_log.ip IS '请求 IP';

COMMENT ON TABLE agent_definition IS 'Agent 定义表';
COMMENT ON COLUMN agent_definition.id IS 'Agent ID';
COMMENT ON COLUMN agent_definition.name IS 'Agent 名称';
COMMENT ON COLUMN agent_definition.description IS 'Agent 描述';
COMMENT ON COLUMN agent_definition.sys_prompt IS '系统提示词';
COMMENT ON COLUMN agent_definition.max_iterations IS '最大迭代次数';
COMMENT ON COLUMN agent_definition.workspace_mode IS '工作区模式 (isolated/shared)';
COMMENT ON COLUMN agent_definition.status IS '状态 (draft/published/archived)';
COMMENT ON COLUMN agent_definition.current_version IS '当前版本 ID';
COMMENT ON COLUMN agent_definition.created_time IS '创建时间';
COMMENT ON COLUMN agent_definition.updated_time IS '更新时间';
COMMENT ON COLUMN agent_definition.created_by IS '创建人 ID';
COMMENT ON COLUMN agent_definition.updated_by IS '更新人 ID';
COMMENT ON COLUMN agent_definition.deleted IS '逻辑删除标志 (0:未删除, 1:已删除)';

COMMENT ON TABLE agent_version IS 'Agent 版本表';
COMMENT ON COLUMN agent_version.id IS '版本 ID';
COMMENT ON COLUMN agent_version.agent_id IS 'Agent ID';
COMMENT ON COLUMN agent_version.version IS '版本号';
COMMENT ON COLUMN agent_version.snapshot_data IS '版本快照数据 (JSON)';
COMMENT ON COLUMN agent_version.changelog IS '变更日志';
COMMENT ON COLUMN agent_version.created_time IS '创建时间';
COMMENT ON COLUMN agent_version.updated_time IS '更新时间';
COMMENT ON COLUMN agent_version.created_by IS '创建人 ID';
COMMENT ON COLUMN agent_version.updated_by IS '更新人 ID';
COMMENT ON COLUMN agent_version.deleted IS '逻辑删除标志 (0:未删除, 1:已删除)';
