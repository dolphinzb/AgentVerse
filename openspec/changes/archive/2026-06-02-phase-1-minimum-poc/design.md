## Context

AgentVerse 是一个企业级智能体开发运营平台，采用多模块单进程架构。第一阶段需要验证核心链路：创建最简 Agent → 纯对话。

**当前状态**：项目从零开始，需要搭建完整的 Maven 多模块项目结构。

**约束条件**：
- 只验证核心链路，不实现复杂功能（Tool/Skill/Knowledge/SubAgent/Workflow）
- 模型配置硬编码在代码中，不建子表
- 对话/会话使用内存存储，不做数据库持久化
- 外部依赖 PostgreSQL、Redis，不容器化
- ChromaDB 延迟到阶段五（知识库 RAG）再接入，阶段一不依赖

**利益相关者**：开发团队、早期试用用户

## Goals / Non-Goals

**Goals:**
- 建立可扩展的 Maven 多模块项目结构（9 个子模块 + 1 个前端项目）
- 实现 Agent CRUD API，支持创建、查询、更新、删除、发布版本
- 实现 HarnessAgent 引擎构建，从数据库配置加载可执行的 Agent
- 实现同步对话和 SSE 流式对话 API
- 实现最简前端（Agent 列表、配置、对话页面）
- 验证核心架构可行性，为后续阶段奠定基础

**Non-Goals:**
- 不实现用户认证和 RBAC 权限控制（阶段二）
- 不实现多模型管理（阶段三）
- 不实现对话/会话持久化（阶段四）
- 不实现 Tool/Skill/Knowledge/SubAgent（阶段五及以后）
- 不实现工作流引擎（阶段八）
- 不实现可观测性（阶段九）
- 不实现 OpenAI 兼容 API（阶段十）

## Decisions

### 1. Maven 多模块结构

**决策**：创建 9 个子模块（common、security、runtime、studio、tool、skill、knowledge、workflow、observability、admin）+ 1 个前端项目（web），但阶段一只实现 common、runtime、web。

**理由**：
- 预留完整的模块结构，后续阶段只需实现对应模块，无需重构项目结构
- 模块间依赖关系清晰：所有业务模块依赖 common，runtime 依赖所有业务模块
- 符合"按需裁剪"原则：未实现的模块可以暂时不引入依赖

**替代方案**：
- 方案 B：阶段一只创建 common、runtime、web 三个模块 → 后续需要新增模块时重构 pom.xml，增加迁移成本
- 方案 C：使用微服务架构 → 增加运维复杂度，不符合单进程设计原则

### 2. 数据库表设计

**决策**：阶段一创建 2 张核心表：
- `agent_definition`：Agent 定义（name、description、sys_prompt、max_iterations、workspace_mode 等）
- `agent_version`：版本快照（version、snapshot_yaml、changelog、publish_status）

**理由**：
- agent_definition 存储 Agent 的核心配置，支持 CRUD 操作
- agent_version 支持版本发布和回滚，作为后续功能的预留实现，阶段一就建表
- 模型配置硬编码在代码中（ModelFactory），不建 agent_model_config 子表，简化阶段一实现
- 不建工具/技能/知识库/SubAgent 相关子表，避免过早设计

**替代方案**：
- 方案 B：只建 agent_definition，不建 agent_version → 无法支持版本发布，与 plan.md 的发布 API 不符
- 方案 C：建完整的 9 张子表 → 过度设计，阶段一不需要这些功能

### 3. 对话/会话内存存储

**决策**：使用 `Map<String, List<Message>>` 存储会话消息，Key 为 session_id，Value 为消息列表。

**理由**：
- 阶段一目标是验证核心链路，不需要持久化
- 内存存储简化实现，避免引入 Redis/PostgreSQL 会话管理的复杂度
- 服务重启后会话丢失，但 POC 阶段可以接受
- 阶段四实现持久化时，只需替换存储层，不影响上层 API

**替代方案**：
- 方案 B：使用 Redis 存储 → 增加阶段一复杂度，与"最简 POC"目标不符
- 方案 C：使用 PostgreSQL 存储 → 需要建 chat_session/message_record 表，增加阶段一工作量

**风险**：
- 内存存储无容量限制，大量会话可能导致 OOM → 阶段一只用于演示和测试，不会有大量并发
- 服务重启后数据丢失 → POC 阶段可接受，阶段四实现持久化

### 4. ModelFactory 硬编码

**决策**：ModelFactory 直接在代码中硬编码模型配置（provider、model、temperature、max_tokens 等），不建 agent_model_config 子表。

**理由**：
- 阶段一不需要多模型管理，硬编码简化实现
- 后续阶段引入多模型管理时，只需重构 ModelFactory 和 AgentLoaderService，不影响其他模块
- 避免过早设计 agent_model_config 表结构

**替代方案**：
- 方案 B：建 agent_model_config 子表 → 增加阶段一工作量，与"最简 POC"目标不符

**风险**：
- 硬编码模型配置不够灵活 → 阶段一可以接受，阶段三实现多模型管理

### 5. 外部依赖不容器化

**决策**：PostgreSQL、Redis 作为外部已有组件，通过连接配置接入，不在项目内容器化部署。ChromaDB 延迟到阶段五（知识库 RAG）再接入。

**理由**：
- 假设开发环境已有 PostgreSQL、Redis，简化阶段一部署
- ChromaDB 在阶段五（知识库 RAG）才需要，阶段一不需要，避免不必要的依赖
- 避免引入 Docker Compose 配置，减少阶段一工作量
- 后续阶段可以添加 Docker Compose 支持

**替代方案**：
- 方案 B：使用 Docker Compose 容器化部署 → 增加阶段一工作量，与"最简 POC"目标不符
- 方案 C：阶段一就接入 ChromaDB → 阶段一不需要向量存储，过早引入增加复杂度

**风险**：
- 开发者需要手动安装 PostgreSQL、Redis → 提供安装文档和连接配置示例

### 6. HarnessAgent 引擎构建

**决策**：实现 AgentLoaderService，从 agent_definition 表读取配置，调用 HarnessAgent.Builder 构建 Agent。

**理由**：
- HarnessAgent 是 AgentScope-Java 的生产级 Agent 引擎，封装了 ReActAgent
- 阶段一只配置最简参数：name()、description()、sysPrompt()、model()、maxIters()
- 不配置 toolkit、skillBox、knowledges、subagents，避免引入复杂功能
- 实现 Agent 生命周期管理（创建 → 加载 → 缓存 → 销毁）

**替代方案**：
- 方案 B：直接使用 ReActAgent → 缺少生产级能力（记忆、会话、工作区等），与 plan.md 不符
- 方案 C：配置完整的 HarnessAgent 参数 → 阶段一不需要这些功能，过度设计

### 7. 前端技术选型

**决策**：使用 Vue 3 + Vite 6 + TypeScript + Element Plus + Pinia。

**理由**：
- Vue 3 组合式 API 适合复杂前端应用
- Vite 6 提供快速开发体验
- TypeScript 提供类型安全
- Element Plus 提供企业级 UI 组件
- Pinia 是 Vue 官方推荐的状态管理

**替代方案**：
- 方案 B：使用 React → 团队可能更熟悉 Vue，降低学习成本
- 方案 C：使用 Ant Design Vue → Element Plus 文档更完善，社区更活跃

## Risks / Trade-offs

### 1. 内存存储风险

**风险**：对话/会话使用内存存储，服务重启后数据丢失。

**缓解**：
- 阶段一只用于演示和测试，不会有大量并发
- 阶段四实现持久化时，只需替换存储层，不影响上层 API
- 提供文档说明限制，避免用户误以为数据已持久化

### 2. 硬编码模型配置风险

**风险**：ModelFactory 硬编码模型配置，不够灵活。

**缓解**：
- 阶段一可以接受，只用于演示和测试
- 阶段三实现多模型管理时，重构 ModelFactory 和 AgentLoaderService

### 3. 外部依赖安装风险

**风险**：开发者需要手动安装 PostgreSQL、Redis。

**缓解**：
- 提供详细的安装文档和连接配置示例
- 提供 application.yml 模板，开发者只需修改连接信息
- ChromaDB 延迟到阶段五，阶段一不需要安装

### 4. 模块结构过度设计风险

**风险**：阶段一创建 9 个子模块，但只实现 3 个，可能被认为是过度设计。

**缓解**：
- 预留完整的模块结构，后续阶段只需实现对应模块，无需重构项目结构
- 未实现的模块可以暂时不引入依赖，不影响编译和运行
- 符合"按需裁剪"原则

### 5. 版本发布功能不完整风险

**风险**：阶段一建了 agent_version 表并实现发布按钮，但不实现版本回滚功能。

**缓解**：
- 阶段一实现版本发布 API（创建 agent_version 记录）和前端发布按钮
- 阶段七实现完整的版本管理（发布、回滚、对比、导入/导出）
- 预留表结构，后续只需实现 API，无需修改数据库
- 前端发布按钮调用发布 API，用户可以看到发布状态

## Open Questions

无。所有问题已解决：
- ChromaDB 延迟到阶段五（知识库 RAG）再接入
- agent_version 表作为后续功能的预留实现，阶段一就建表
- 前端需要版本发布按钮
