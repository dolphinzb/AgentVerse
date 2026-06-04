-- AgentVerse 数据初始化脚本
-- Phase 2: 安全与 RBAC 初始化数据

-- ============================================================
-- 初始化角色数据（4 个预设角色）
-- ============================================================
INSERT INTO sys_role (id, role_code, role_name, description, created_time, updated_time, deleted)
SELECT 1, 'admin', '管理员', '系统管理员，拥有全部权限', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'admin');

INSERT INTO sys_role (id, role_code, role_name, description, created_time, updated_time, deleted)
SELECT 2, 'developer', '开发者', 'Agent 开发者，可创建和管理自己的 Agent', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'developer');

INSERT INTO sys_role (id, role_code, role_name, description, created_time, updated_time, deleted)
SELECT 3, 'operator', '运营人员', '可查看 Agent 和使用对话功能', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'operator');

INSERT INTO sys_role (id, role_code, role_name, description, created_time, updated_time, deleted)
SELECT 4, 'viewer', '观察者', '只读权限，仅可查看 Agent 和对话', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'viewer');

-- ============================================================
-- 初始化权限数据（8 个操作级权限）
-- ============================================================
INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 1, 'agent:create', '创建 Agent', '创建新的 Agent 定义', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'agent:create');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 2, 'agent:update', '更新 Agent', '更新已有的 Agent 定义', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'agent:update');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 3, 'agent:delete', '删除 Agent', '删除 Agent 定义', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'agent:delete');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 4, 'agent:read', '查看 Agent', '查看 Agent 定义列表和详情', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'agent:read');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 5, 'agent:publish', '发布 Agent', '发布 Agent 版本', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'agent:publish');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 6, 'chat:create', '创建会话', '创建新的聊天会话', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'chat:create');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 7, 'chat:read', '查看会话', '查看聊天会话和消息', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'chat:read');

INSERT INTO sys_permission (id, perm_code, perm_name, description, created_time, updated_time, deleted)
SELECT 8, 'admin:audit', '查看审计日志', '查看和管理审计日志', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'admin:audit');

-- ============================================================
-- 初始化角色-权限关联数据
-- ============================================================

-- admin: 全部权限 (perm 1-8)
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 1, 1, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 1);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 2, 1, 2, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 2);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 3, 1, 3, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 3);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 4, 1, 4, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 4);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 5, 1, 5, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 5);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 6, 1, 6, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 6);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 7, 1, 7, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 7);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 8, 1, 8, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1 AND perm_id = 8);

-- developer: agent:create, agent:update, agent:delete, agent:read, agent:publish, chat:create, chat:read (perm 1-7)
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 9, 2, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 1);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 10, 2, 2, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 2);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 11, 2, 3, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 3);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 12, 2, 4, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 4);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 13, 2, 5, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 5);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 14, 2, 6, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 6);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 15, 2, 7, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 2 AND perm_id = 7);

-- operator: agent:read, chat:create, chat:read (perm 4,6,7)
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 16, 3, 4, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 3 AND perm_id = 4);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 17, 3, 6, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 3 AND perm_id = 6);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 18, 3, 7, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 3 AND perm_id = 7);

-- viewer: agent:read, chat:read (perm 4,7)
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 19, 4, 4, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 4 AND perm_id = 4);
INSERT INTO sys_role_permission (id, role_id, perm_id, created_time)
SELECT 20, 4, 7, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 4 AND perm_id = 7);

-- ============================================================
-- 初始化用户数据
-- ============================================================

-- admin 用户（密码: admin123, BCrypt 加密）
INSERT INTO sys_user (id, username, password, email, role_code, status, created_time, updated_time, deleted)
SELECT 1, 'admin', '$2b$10$JMFOFptdZq6kQ4ioccNLceJeAqsfjZug4mpxA54vprNQa34WQPbAa',
       'admin@agentverse.local', 'admin', 1, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

-- developer 用户（密码: dev123, BCrypt 加密）
INSERT INTO sys_user (id, username, password, email, role_code, status, created_time, updated_time, deleted)
SELECT 2, 'developer', '$2b$10$iR5.XEjWsLtQUgdNaqWAg.DnB2vfEvecAfpsaT/JiXhtcBRP3wj.S',
       'developer@agentverse.local', 'developer', 1, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'developer');

-- ============================================================
-- 测试数据（Phase 1 保留）
-- ============================================================
INSERT INTO agent_definition (id, name, description, sys_prompt, max_iterations, workspace_mode, status, created_time, updated_time, deleted)
SELECT 'test-agent-001', '测试 Agent', '这是一个用于测试的 Agent', '你是一个友好的助手', 10, 'isolated', 'draft', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM agent_definition WHERE id = 'test-agent-001');

INSERT INTO agent_version (id, agent_id, version, snapshot_data, changelog, created_time, updated_time, deleted)
SELECT 'test-version-001', 'test-agent-001', 'v1.0.0', '{"name":"测试 Agent","sysPrompt":"你是一个友好的助手"}', '初始版本', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM agent_version WHERE id = 'test-version-001');
