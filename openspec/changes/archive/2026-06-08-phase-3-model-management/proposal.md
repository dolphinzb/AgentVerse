## Why

AgentVerse 当前 ModelFactory 硬编码单一模型配置（从 application.yml 读取），所有 Agent 共享同一个模型，API Key 明文存储在 yml 中。这导致：

1. **无法多模型支持**：无法为不同 Agent 指定不同模型（如 Agent A 用 qwen-plus，Agent B 用 gpt-4o）
2. **安全隐患**：API Key 明文存储在配置文件中，违反安全规范
3. **无法追踪用量**：没有 Token 用量追踪，无法评估各模型使用情况
4. **无法动态管理**：新增 Provider/Model 需要修改配置文件并重启服务

阶段三目标是实现从硬编码单模型到多模型动态管理的完整演进，建立 Token 用量追踪基础。

## What Changes

**迭代 3.1：Provider 与模型管理**
- 新增数据库表：model_provider（供应商名称、类型、API Key 加密存储、Base URL、自定义 Header、状态）
- 新增数据库表：model_config（模型名、关联 Provider、Temperature/MaxTokens/TopP 参数、是否默认）
- 新增 ProviderType 枚举：定义支持的供应商类型（dashscope/openai/deepseek），取值来源于 agentscope 库已支持的 ChatModel 类型，后续新增供应商时扩展枚举即可
- 新增预设供应商配置：DashScope、OpenAI、DeepSeek 等预设卡片，Base URL 自动填充，推荐模型列表
- 实现 API Key AES-256 加密存储（优先环境变量密钥，回退 yml 配置）
- 实现 Provider 连接测试 API（验证 API Key 有效性）
- 实现一步添加模型 API（前端合并 Provider + ModelConfig 为一步操作）
- 前端：模型管理主页（预设供应商卡片 + 自定义模型卡片 + 已添加模型列表）

**迭代 3.2：模型选择与 Token 用量追踪**
- 新增数据库表：chat_usage（按会话记录 Token 用量）
- 修改 agent_definition 表：新增 model_config_id 字段（必填）
- Agent 创建/编辑时支持选择 Provider 和 Model（替代硬编码）
- 重构 ModelFactory：从数据库动态加载模型配置，ProviderType 枚举策略模式构建 ChatModel
- 重构 AgentLoaderService：根据 Agent 的 modelConfigId 加载对应模型，缓存 + 更新时失效
- ChatService 对话完成后记录 Token 用量
- 前端：Agent 配置页模型选择器升级（Provider 分组下拉框）
- 前端：Token 用量统计页面（按模型聚合调用次数、Token 消耗）

## Capabilities

### New Capabilities

- `model-provider-management`: Provider 注册、管理、API Key 加密存储、连接测试、自定义 Header；ProviderType 枚举定义供应商类型（dashscope/openai/deepseek）；预设供应商配置；前端一步添加模型
- `model-config-management`: 模型实例 CRUD、ModelFactory 动态加载（ProviderType 策略模式）
- `model-usage-tracking`: Token 用量按会话记录、按模型聚合统计

### Modified Capabilities

- `agent-definition-crud`: agent_definition 表新增 model_config_id 字段（必填）；Agent 创建/编辑支持模型选择；AgentResponse 增加模型信息
- `harness-agent-engine`: AgentLoaderService 从全局单模型改为按 Agent 的 modelConfigId 加载对应模型；ModelFactory 重构为数据库驱动 + 缓存
- `minimal-frontend`: Agent 创建/编辑弹窗增加模型选择器；新增模型管理页、Token 统计页

## Impact

- **代码结构**：agent-platform-admin 新增 model 包（Provider/ModelConfig Controller、Service、Entity、Mapper）；agent-platform-runtime 重构 ModelFactory、AgentLoaderService
- **数据库**：新增 3 张表（model_provider、model_config、chat_usage）；修改 1 张表（agent_definition 新增 model_config_id）
- **API**：新增 `/api/v1/model-providers/*`、`/api/v1/model-configs/*`、`/api/v1/models/add`、`/api/v1/model-stats`
- **前端**：新增 2 个页面（模型管理主页、Token 统计）；修改 Agent 创建/编辑弹窗
- **安全**：API Key 从明文 yml 迁移到数据库 AES-256 加密存储
- **权限**：新增 model:create/update/delete/read 权限
- **数据隔离**：Provider/ModelConfig 按 created_by 隔离（与 Agent 一致）
