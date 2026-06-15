## Why

AgentVerse 需要一个最小可验证 POC 来验证核心架构可行性。在添加复杂功能（Tool/Skill/Knowledge/SubAgent/Workflow）之前，必须先跑通「创建最简 Agent → 纯对话」的完整链路，确保 HarnessAgent 引擎、数据库设计、前后端集成等基础设施工作正常。这是后续所有迭代的基础。

## What Changes

- 新建 Maven 多模块项目骨架（9 个子模块 + 1 个前端项目）
- 新建 agent-platform-common 模块（ApiResponse、BizException、BaseEntity 等基础设施）
- 新建 agent-platform-runtime 模块（Spring Boot 启动入口、HarnessAgent 引擎、对话服务）
- 新建 agent-platform-web 模块（Vue 3 最简前端：Agent 列表、配置、对话）
- 新建数据库 schema（agent_definition、agent_version），模型配置在代码中硬编码，不建子表
- 对话与会话使用内存存储（`Map<String, List<Message>>`），不做持久化（持久化在阶段四实现）
- 新建 REST API（Agent CRUD、Chat 同步/流式对话、会话管理）
- 集成外部 PostgreSQL、Redis（不容器化），ChromaDB 延迟到阶段五（知识库 RAG）再接入
- **不包含**：用户认证、RBAC、Tool/Skill/Knowledge/SubAgent、工作流、可观测性

## Capabilities

### New Capabilities

- `project-skeleton`: Maven 多模块项目结构、common 基础库（DTO/Entity/Exception/Util）、全局异常处理、WebFlux 配置
- `agent-crud`: Agent 定义与 CRUD API（agent_definition 主表 + agent_version 版本快照，模型配置在代码中硬编码）
- `harness-engine`: HarnessAgent 引擎构建（AgentLoaderService、ModelFactory、Agent 生命周期管理）
- `chat-service`: 对话服务（同步对话、SSE 流式对话、会话管理），会话和消息存储在内存 `Map<String, List<Message>>` 中，不做数据库持久化
- `minimal-frontend`: 最简前端（Vue 3 + Vite + Element Plus、Agent 列表页、Agent 配置页、对话页、SSE 流式渲染、版本发布按钮）

### Modified Capabilities

（无，这是项目第一阶段，没有现有能力需要修改）

## Impact

- **代码结构**：创建完整的 Maven 多模块项目（parent pom + 9 个子模块），但阶段一只实现 common、runtime、web 三个模块
- **数据库**：新建 2 张核心表（agent_definition、agent_version），模型配置在代码中硬编码，不建子表。对话/会话使用内存存储，不做数据库持久化（阶段四实现）。ChromaDB 在阶段五接入
- **外部依赖**：集成 AgentScope-Java 1.1.0-RC1 HarnessAgent、Spring Boot 3.3.x、MyBatis-Plus 3.5.x、Vue 3 + Vite 6
- **API**：新增 `/api/v1/agents`（Agent CRUD）、`/api/v1/chat/{agentId}/send`（同步对话）、`/api/v1/chat/{agentId}/stream`（SSE 流式）、`/api/v1/chat/sessions`（会话管理）
- **部署**：需要外部 PostgreSQL 15+、Redis 7+（不在项目内容器化部署）
- **后续阶段**：阶段二（安全权限）、阶段三（模型管理）等将在此基础上增量构建
