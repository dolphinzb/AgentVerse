## 1. 项目结构与基础配置

- [x] 1.1 创建 Maven 父项目 pom.xml，定义版本管理和依赖管理（Spring Boot 3.x、MyBatis-Plus、AgentScope-Java）
- [x] 1.2 创建 agent-platform-common 模块，定义基础包结构（entity、dto、exception、utils）
- [x] 1.3 创建 agent-platform-runtime 模块，配置 Spring Boot 应用启动类
- [x] 1.4 创建 agent-platform-studio 模块（空模块，预留）
- [x] 1.5 创建 agent-platform-admin 模块（空模块，预留）
- [x] 1.6 在 runtime 模块中配置 application.yml（PostgreSQL、Redis 连接信息，不配置 ChromaDB）
- [x] 1.7 配置 Maven 构建插件（spring-boot-maven-plugin、编译插件）
- [x] 1.8 验证 Maven 多模块项目能够成功编译

## 2. 数据库层实现

- [x] 2.1 创建数据库 schema SQL 脚本，定义 agent_definition 表（id、name、description、sys_prompt、max_iterations、workspace_mode、status、current_version、created_at、updated_at）
- [x] 2.2 创建数据库 schema SQL 脚本，定义 agent_version 表（id、agent_id、version、snapshot_yaml、changelog、created_at）
- [x] 2.3 创建 AgentDefinition 实体类，添加 MyBatis-Plus 注解
- [x] 2.4 创建 AgentVersion 实体类，添加 MyBatis-Plus 注解
- [x] 2.5 创建 AgentDefinitionMapper 接口（继承 BaseMapper）
- [x] 2.6 创建 AgentVersionMapper 接口（继承 BaseMapper）
- [x] 2.7 在 runtime 模块中配置 MyBatis-Plus（数据源、mapper 扫描、分页插件）
- [x] 2.8 验证数据库连接和 mapper 注入正常

## 3. Agent Definition CRUD 实现

- [x] 3.1 创建 AgentCreateRequest DTO（name、description、sys_prompt、max_iterations）
- [x] 3.2 创建 AgentUpdateRequest DTO（name、description、sys_prompt、max_iterations）
- [x] 3.3 创建 AgentResponse DTO（完整 Agent 信息）
- [x] 3.4 创建 AgentListResponse DTO（分页列表响应）
- [x] 3.5 实现 AgentDefinitionService.createAgent() 方法（验证必填字段、生成 ID、设置默认 status=draft、插入数据库）
- [x] 3.6 实现 AgentDefinitionService.getAgentById() 方法（查询并返回完整信息）
- [x] 3.7 实现 AgentDefinitionService.listAgents() 方法（分页查询、支持 status 筛选）
- [x] 3.8 实现 AgentDefinitionService.updateAgent() 方法（更新记录、自动更新 updated_at）
- [x] 3.9 实现 AgentDefinitionService.deleteAgent() 方法（删除 agent_definition 记录）
- [x] 3.10 创建 AgentController，实现 POST /api/v1/agents 接口
- [x] 3.11 实现 GET /api/v1/agents/{id} 接口
- [x] 3.12 实现 GET /api/v1/agents 接口（分页查询）
- [x] 3.13 实现 PUT /api/v1/agents/{id} 接口
- [x] 3.14 实现 DELETE /api/v1/agents/{id} 接口
- [x] 3.15 实现全局异常处理（AgentNotFoundException 返回 404、ValidationException 返回 400）
- [x] 3.16 使用 Postman 或 curl 测试所有 Agent CRUD 接口

## 4. Agent 版本管理实现

- [x] 4.1 创建 AgentPublishRequest DTO（version、changelog）
- [x] 4.2 创建 AgentVersionResponse DTO（版本信息）
- [x] 4.3 实现 AgentVersionService.publishVersion() 方法（验证 Agent 存在、检查版本号唯一、序列化 Agent 配置为 YAML、插入 agent_version 表、更新 agent_definition.current_version）
- [x] 4.4 实现 AgentVersionService.listVersions() 方法（查询指定 Agent 的所有版本，按 created_at 倒序）
- [x] 4.5 实现 AgentVersionService.rollbackVersion() 方法（验证版本存在、解析 snapshot_yaml、更新 agent_definition 表、更新 current_version）
- [x] 4.6 在 AgentController 中实现 POST /api/v1/agents/{id}/publish 接口
- [x] 4.7 在 AgentController 中实现 GET /api/v1/agents/{id}/versions 接口
- [x] 4.8 在 AgentController 中实现 POST /api/v1/agents/{id}/rollback 接口
- [x] 4.9 测试版本发布、查询、回滚接口

## 5. HarnessAgent 引擎实现

- [x] 5.1 在 runtime 模块的 pom.xml 中添加 AgentScope-Java 依赖
- [x] 5.2 创建 ModelFactory 类，实现硬编码的模型配置（provider、model、temperature、max_tokens）
- [x] 5.3 实现 ModelFactory.createModel() 方法，返回预配置的 ChatModel 实例
- [x] 5.4 创建 AgentLoaderService 类，注入 AgentDefinitionMapper
- [x] 5.5 实现 AgentLoaderService.loadAgent() 方法（从数据库读取 agent_definition、调用 HarnessAgent.builder() 构建实例）
- [x] 5.6 配置 HarnessAgent 基础参数（name、sysPrompt、model、maxIters、workspace_mode）
- [x] 5.7 实现 AgentLoaderService 的 Agent 实例缓存（Map<agentId, HarnessAgent>）
- [x] 5.8 创建 HarnessAgentEngine 类，注入 AgentLoaderService
- [x] 5.9 实现 HarnessAgentEngine.buildAgent() 方法（加载或从缓存获取 HarnessAgent）
- [x] 5.10 实现 HarnessAgentEngine.executeChat() 方法（构建 Agent、调用 chat 方法、返回响应）
- [x] 5.11 实现 HarnessAgentEngine.streamChat() 方法（构建 Agent、调用 streamChat 方法、返回 Flux<String>）
- [x] 5.12 实现 HarnessAgentEngine.interruptChat() 方法（中断正在执行的 Agent）
- [x] 5.13 测试 HarnessAgentEngine 能够成功构建 Agent 并执行对话

## 6. Chat Service 实现

- [x] 6.1 创建 SessionStore 类，使用 Map<String, List<Message>> 存储会话消息
- [x] 6.2 实现 SessionStore.createSession() 方法（生成 sessionId、初始化空消息列表）
- [x] 6.3 实现 SessionStore.getSession() 方法（获取会话消息列表）
- [x] 6.4 实现 SessionStore.addMessage() 方法（添加用户消息或 Agent 响应）
- [x] 6.5 实现 SessionStore.deleteSession() 方法（移除会话）
- [x] 6.6 创建 Message 类（role、content、timestamp）
- [x] 6.7 创建 SessionCreateRequest DTO（agentId）
- [x] 6.8 创建 SessionResponse DTO（sessionId、agentId、created_at）
- [x] 6.9 创建 MessageRequest DTO（content）
- [x] 6.10 创建 MessageResponse DTO（role、content、timestamp）
- [x] 6.11 创建 ChatService 类，注入 SessionStore 和 HarnessAgentEngine
- [x] 6.12 实现 ChatService.createSession() 方法（验证 Agent 存在、创建会话、返回 sessionId）
- [x] 6.13 实现 ChatService.sendMessage() 方法（验证会话存在、添加用户消息、调用 executeChat、添加 Agent 响应、返回响应）
- [x] 6.14 实现 ChatService.streamMessage() 方法（验证会话存在、添加用户消息、调用 streamChat、返回 Flux、流式添加响应）
- [x] 6.15 实现 ChatService.getSessionHistory() 方法（返回会话的所有消息）
- [x] 6.16 实现 ChatService.deleteSession() 方法（删除会话）
- [x] 6.17 创建 ChatController，实现 POST /api/v1/chat/sessions 接口
- [x] 6.18 实现 POST /api/v1/chat/sessions/{sessionId}/messages 接口（同步对话）
- [x] 6.19 实现 GET /api/v1/chat/sessions/{sessionId}/stream 接口（SSE 流式对话，使用 text/event-stream）
- [x] 6.20 实现 GET /api/v1/chat/sessions/{sessionId}/messages 接口（查询历史）
- [x] 6.21 实现 DELETE /api/v1/chat/sessions/{sessionId} 接口
- [x] 6.22 实现 POST /api/v1/chat/sessions/{sessionId}/interrupt 接口（中断对话）
- [x] 6.23 测试同步对话接口
- [x] 6.24 测试 SSE 流式对话接口
- [x] 6.25 测试会话管理和历史查询接口

## 7. 前端项目搭建

- [x] 7.1 使用 Vite 创建 Vue 3 + TypeScript 项目（agent-platform-web）
- [x] 7.2 安装依赖（Element Plus、Pinia、Vue Router、Axios）
- [x] 7.3 配置 Element Plus（自动导入或全局注册）
- [x] 7.4 创建基础布局组件（Layout.vue，包含侧边栏和主内容区）
- [x] 7.5 配置 Vue Router，定义路由（/agents、/agents/:id、/chat）
- [x] 7.6 创建 Pinia store（agentStore、chatStore）
- [x] 7.7 配置 Axios 实例，设置 baseURL 和拦截器
- [x] 7.8 创建 API 服务模块（agentApi.ts、chatApi.ts）
- [x] 7.9 验证前端项目能够成功启动

## 8. 前端 Agent 管理页面

- [x] 8.1 创建 Agent 列表页面（AgentList.vue），使用 Element Plus Table 组件
- [x] 8.2 实现分页功能（使用 el-pagination）
- [x] 8.3 实现状态筛选（使用 el-select，选项：全部/draft/published）
- [x] 8.4 实现"创建 Agent"按钮，点击弹出创建表单对话框
- [x] 8.5 创建 Agent 表单组件（AgentForm.vue），包含 name、description、sys_prompt、max_iterations 字段
- [x] 8.6 实现表单验证（name 必填）
- [x] 8.7 实现创建 Agent 功能（调用 agentApi.createAgent，成功后关闭对话框并刷新列表）
- [x] 8.8 实现编辑 Agent 功能（点击"编辑"按钮，打开表单并预填充数据）
- [x] 8.9 实现删除 Agent 功能（点击"删除"按钮，弹出确认框，调用 agentApi.deleteAgent）
- [x] 8.10 创建 Agent 详情页面（AgentDetail.vue），显示完整 Agent 信息
- [x] 8.11 在详情页添加"发布版本"按钮，点击弹出版本发布表单（version、changelog）
- [x] 8.12 实现版本发布功能（调用 agentApi.publishVersion，成功后刷新页面）
- [x] 8.13 在详情页添加"版本历史"按钮，点击弹出版本历史对话框
- [x] 8.14 实现版本历史列表（使用 el-table，显示 version、changelog、created_at）
- [x] 8.15 在版本历史列表中添加"回滚"按钮，实现版本回滚功能（调用 agentApi.rollbackVersion）
- [x] 8.16 测试前端 Agent 管理功能（创建、编辑、删除、发布、回滚）

## 9. 前端对话界面

- [x] 9.1 创建对话页面（ChatView.vue），包含会话列表和对话区域
- [x] 9.2 实现会话列表组件（SessionList.vue），使用 el-menu 或 el-list
- [x] 9.3 实现"开始对话"功能（选择 Agent，调用 chatApi.createSession，添加到会话列表）
- [x] 9.4 创建对话区域组件（ChatArea.vue），包含消息列表和输入框
- [x] 9.5 实现消息列表渲染（使用 el-scrollbar，区分用户消息和 Agent 响应样式）
- [x] 9.6 实现消息输入框（使用 el-input，支持回车发送）
- [x] 9.7 实现同步对话功能（调用 chatApi.sendMessage，显示用户消息和 Agent 响应）
- [x] 9.8 实现"流式模式"开关（el-switch）
- [x] 9.9 实现 SSE 流式对话功能（使用 EventSource 或 fetch，逐步显示 Agent 响应）
- [x] 9.10 实现流式响应的实时渲染（逐字显示，添加打字机效果）
- [x] 9.11 实现"中断"按钮，点击调用 chatApi.interruptSession
- [x] 9.12 实现查看历史会话功能（点击会话列表项，调用 chatApi.getSessionHistory，加载历史消息）
- [x] 9.13 实现删除会话功能（右键会话或添加删除按钮，调用 chatApi.deleteSession）
- [x] 9.14 测试前端对话功能（同步模式、流式模式、历史查看、中断）

## 10. 集成测试与验证

- [x] 10.1 启动后端服务（runtime 模块）
- [x] 10.2 启动前端开发服务器
- [x] 10.3 端到端测试：创建 Agent → 发布版本 → 开始对话 → 流式对话 → 查看历史
- [x] 10.4 端到端测试：编辑 Agent → 发布新版本 → 回滚到旧版本
- [x] 10.5 测试响应式布局（调整浏览器窗口大小，验证移动端适配）
- [x] 10.6 记录已知问题和后续优化点
- [x] 10.7 编写 README.md，说明如何启动和测试项目
