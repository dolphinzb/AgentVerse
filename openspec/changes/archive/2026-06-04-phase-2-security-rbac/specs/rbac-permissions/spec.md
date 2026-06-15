## ADDED Requirements

### Requirement: 权限注解校验
系统 SHALL 提供 `@RequirePermission` 注解，标注在 Controller 方法上，基于 `sys_role_permission` 表校验当前用户是否拥有指定权限。

#### Scenario: 用户拥有所需权限
- **WHEN** 用户拥有 agent:create 权限，访问标注 @RequirePermission("agent:create") 的接口
- **THEN** 请求继续，权限校验通过

#### Scenario: 用户缺少所需权限
- **WHEN** 用户仅有 agent:read 权限，访问标注 @RequirePermission("agent:create") 的接口
- **THEN** 系统返回 403 Forbidden

#### Scenario: admin 角色绕过权限校验
- **WHEN** admin 角色用户访问任意接口（除特别标注需单独校验的）
- **THEN** 权限校验通过，admin 拥有全部权限

### Requirement: 数据隔离
系统 SHALL 确保 developer 角色用户只能操作自己创建的 Agent（created_by = 当前用户），admin 角色可操作所有 Agent。

#### Scenario: developer 创建 Agent
- **WHEN** developer 用户创建一个 Agent
- **THEN** Agent 的 created_by 自动填充为该用户 ID

#### Scenario: developer 修改自己的 Agent
- **WHEN** developer 用户修改自己创建的 Agent
- **THEN** 操作成功

#### Scenario: developer 修改他人的 Agent
- **WHEN** developer 用户尝试修改其他用户创建的 Agent
- **THEN** 系统返回 403 Forbidden

#### Scenario: developer 删除自己的 Agent
- **WHEN** developer 用户删除自己创建的 Agent
- **THEN** 操作成功

#### Scenario: developer 删除他人的 Agent
- **WHEN** developer 用户尝试删除其他用户创建的 Agent
- **THEN** 系统返回 403 Forbidden

#### Scenario: admin 操作任意 Agent
- **WHEN** admin 用户修改任意 Agent
- **THEN** 操作成功，admin 绕过数据隔离

### Requirement: 角色权限分配
系统 SHALL 预定义 4 个角色及其权限：

| 角色 | 权限 |
|------|------|
| admin | 全部权限（agent:*, chat:*, admin:audit）|
| developer | agent:create, agent:update（自己）, agent:delete（自己）, agent:read, agent:publish（自己）, chat:*, 无 admin:audit |
| operator | agent:read, chat:*（无 create）, 无 admin:audit |
| viewer | agent:read, chat:read, 无 admin:audit |

#### Scenario: 查看预设角色
- **WHEN** 查询 sys_role 表
- **THEN** 返回 admin、developer、operator、viewer 四个角色记录

#### Scenario: 查看角色权限
- **WHEN** 查询 sys_role_permission 关联表
- **THEN** 返回各角色与权限的对应关系

### Requirement: 前端路由守卫
系统 SHALL 在前端实现路由守卫，未登录用户访问需要认证的页面时自动跳转登录页。

#### Scenario: 已登录用户访问受保护页面
- **WHEN** 已登录用户访问 /agents 页面
- **THEN** 页面正常显示

#### Scenario: 未登录用户访问受保护页面
- **WHEN** 未登录用户访问 /agents 页面
- **THEN** 自动跳转到 /login 页面

#### Scenario: 登录后跳转回原始页面
- **WHEN** 用户从 /agents 页面跳转到 /login，登录成功后
- **THEN** 应跳转回 /agents 或首页

### Requirement: 前端按钮级权限控制
系统 SHALL 提供 v-permission 指令，根据用户权限动态显示/隐藏按钮。

#### Scenario: 用户有权限时显示按钮
- **WHEN** 用户拥有 agent:create 权限
- **THEN** v-permission="'agent:create'" 的按钮正常显示

#### Scenario: 用户无权限时隐藏按钮
- **WHEN** 用户没有 agent:create 权限
- **THEN** v-permission="'agent:create'" 的按钮不渲染（DOM 中不可见）

### Requirement: 前端侧边栏动态菜单
系统 SHALL 根据用户角色动态显示侧边栏菜单项。

#### Scenario: admin 看到所有菜单
- **WHEN** admin 用户登录
- **THEN** 侧边栏显示 Agent 管理、聊天、审计日志（admin:audit 菜单）

#### Scenario: viewer 看到受限菜单
- **WHEN** viewer 用户登录
- **THEN** 侧边栏只显示 Agent 列表、聊天，无审计日志菜单
