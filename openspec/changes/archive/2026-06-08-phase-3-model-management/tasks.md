## 1. Database Schema

- [x] 1.1 新增 model_provider 表：id、name、provider_type、api_key_encrypted、base_url、custom_headers(JSON)、status、审计字段
- [x] 1.2 新增 model_config 表：id、provider_id(FK)、model_name、display_name、max_tokens、temperature、top_p、is_default、status、审计字段
- [x] 1.3 新增 chat_usage 表：id、session_id、model_config_id(FK)、input_tokens、output_tokens、created_time
- [x] 1.4 修改 agent_definition 表：新增 model_config_id VARCHAR(64) NOT NULL 字段
- [x] 1.5 新增权限数据：model:create/update/delete/read，分配给 admin 和 developer 角色
- [x] 1.6 新增错误码：MODEL_PROVIDER_NOT_FOUND(1501)、MODEL_CONFIG_NOT_FOUND(1502)、MODEL_PROVIDER_DISABLED(1503)、API_KEY_ENCRYPTION_ERROR(1504)、MODEL_CONNECTION_TEST_FAILED(1505)、MODEL_CONFIG_IN_USE(1506)、MODEL_PROVIDER_IN_USE(1507)、MODEL_PROVIDER_TYPE_UNSUPPORTED(1508)

## 2. Backend - API Key 加密工具

- [x] 2.1 实现 AesEncryptUtil：AES-256 加密/解密，密钥优先从环境变量 AGENTVERSE_ENCRYPTION_KEY 读取，未设置时回退到 application.yml 的 agent.encryption-key 配置
- [x] 2.2 实现 EncryptionKeyInitializer：应用启动时检查密钥是否存在，均未设置则自动生成并打印到日志

## 3. Backend - ProviderType 枚举与预设配置

- [x] 3.1 实现 ProviderType 枚举：DASHSCOPE/OPENAI/DEEPSEEK，含 code/displayName/defaultBaseUrl/recommendedModels 字段
- [x] 3.2 实现 ProviderPresets 配置类：预设供应商列表（DashScope、OpenAI、DeepSeek），含 displayName/description/icon
- [x] 3.3 实现 ConnectionTestResult DTO：success、message

## 4. Backend - Provider 管理

- [x] 4.1 实现 ModelProvider Entity（继承 BaseEntity，providerType 使用 String 字段，customHeaders 为 String JSON 字段）
- [x] 4.2 实现 ModelProviderMapper（继承 BaseMapper）
- [x] 4.3 实现 ModelProviderService：CRUD + API Key 加密存储/解密 + 数据隔离（created_by）+ 连接测试（无需保存）
- [x] 4.4 实现 ModelProviderController：GET/POST/PUT/DELETE /api/v1/model-providers + POST /{id}/test + POST /test-connection + GET /types + GET /presets
- [x] 4.5 Provider DTO：ProviderCreateRequest、ProviderUpdateRequest、ProviderResponse、ProviderTypeResponse、ProviderPresetResponse、ConnectionTestRequest

## 5. Backend - 模型配置管理

- [x] 5.1 实现 ModelConfig Entity（继承 BaseEntity，含 temperature/topP 字段）
- [x] 5.2 实现 ModelConfigMapper（继承 BaseMapper）
- [x] 5.3 实现 ModelConfigService：CRUD + 数据隔离 + 默认模型逻辑 + listByProviderId
- [x] 5.4 实现 ModelConfigController：GET/POST/PUT/DELETE /api/v1/model-configs
- [x] 5.5 ModelConfig DTO：ModelConfigCreateRequest、ModelConfigUpdateRequest、ModelConfigResponse

## 6. Backend - 一步添加模型

- [x] 6.1 实现 ModelManagementService.addModel(ModelAddRequest)：查找或创建 Provider + 创建 ModelConfig，事务
- [x] 6.2 实现 ModelAddRequest DTO：providerType、apiKey、baseUrl、customHeaders、modelName、displayName、maxTokens、temperature、topP、isDefault
- [x] 6.3 实现 ModelManagementController：POST /api/v1/models/add
- [x] 6.4 Provider 复用逻辑：同一用户 + 同一 providerType + 同一 apiKey → 复用已有 Provider

## 7. Backend - ModelFactory 与 AgentLoaderService 重构

- [x] 7.1 重构 ModelFactory：接收 modelConfigId，委托 ProviderType 构建 ChatModel，ConcurrentHashMap 缓存，evictModel/evictByProvider/evictAll 方法
- [x] 7.2 重构 AgentLoaderService：根据 Agent 的 modelConfigId 通过 ModelFactory.getModel() 获取模型，evictAgent 方法
- [x] 7.3 缓存失效：Provider 更新 → evictByProvider + 关联 Agent evict；ModelConfig 更新 → evictModel + 关联 Agent evict；Agent 更新 → evictAgent

## 8. Backend - Agent 模型绑定

- [x] 8.1 修改 AgentDefinition Entity：新增 modelConfigId 字段（String，NOT NULL）
- [x] 8.2 修改 AgentCreateRequest/AgentUpdateRequest：新增 modelConfigId 字段（创建时必填）
- [x] 8.3 修改 AgentResponse：新增 modelConfigId、providerName、modelName
- [x] 8.4 修改 AgentDefinitionService：创建/更新 Agent 时绑定模型，校验 modelConfigId 有效性和数据隔离
- [x] 8.5 禁用 Provider 后关联 Agent 对话报错提示（MODEL_PROVIDER_DISABLED）

## 9. Backend - Token 用量记录

- [x] 9.1 实现 ChatUsage Entity（继承 BaseEntity）
- [x] 9.2 实现 ChatUsageMapper（继承 BaseMapper）
- [x] 9.3 实现 ChatUsageService：保存用量记录、按模型聚合统计
- [x] 9.4 修改 ChatService：对话完成后记录 token 用量（当前为占位实现，待 agentscope 库支持后提取真实数据）
- [x] 9.5 实现模型统计 API：GET /api/v1/model-stats（按模型聚合调用次数、Token 消耗）

## 10. Frontend - 模型管理主页

- [x] 10.1 创建 api/model.ts：Provider、ModelConfig、ModelAdd、ModelStats、Presets、Types、ConnectionTest API 封装
- [x] 10.2 创建 stores/model.ts：Pinia store（providers、modelConfigs、stats、presets、providerTypes）
- [x] 10.3 创建 ModelManagement.vue：预设供应商卡片（DashScope/OpenAI/DeepSeek）+ 自定义模型卡片 + 已添加模型列表（按 Provider 分组）
- [x] 10.4 创建 AddModelDialog.vue：预设供应商添加模型弹窗（API Key + Base URL 自动填充 + 模型名称下拉框 + 参数滑块 + 连接测试按钮）
- [x] 10.5 创建 AddCustomModelDialog.vue：自定义模型添加弹窗（API 类型下拉框 + API Key + Base URL + 模型名称 + 自定义 Header 动态表单 + 参数滑块 + 连接测试按钮）
- [x] 10.6 路由配置：/models
- [x] 10.7 侧边栏新增"模型管理"菜单

## 11. Frontend - Agent 模型选择与 Token 统计

- [x] 11.1 修改 AgentList.vue 创建/编辑弹窗：新增模型选择器（Provider 分组下拉框 → Model 二级下拉框），modelConfigId 必填
- [x] 11.2 修改 AgentDetail.vue：显示模型信息（Provider 名称 + 模型名称）
- [x] 11.3 创建 ModelStatsView.vue：Token 用量统计页面（按模型聚合：调用次数、输入 Token、输出 Token）
- [x] 11.4 路由配置：/models/stats
- [x] 11.5 Agent 创建页默认选择模型（从 model_config.is_default 读取）

## 12. Integration & Testing

- [x] 12.1 执行 schema.sql 和 data.sql 迁移
- [x] 12.2 测试预设供应商添加模型：DashScope 卡片 → 填 API Key → 选模型 → 连接测试 → 保存
- [x] 12.3 测试自定义模型添加：选 OpenAI 兼容 → 填参数 + 自定义 Header → 保存
- [x] 12.4 测试 Provider 复用：同一用户添加同类型模型时复用已有 Provider
- [x] 12.5 测试 Agent 创建时选择模型 → 对话使用正确模型
- [x] 12.6 测试 Token 统计：对话后查看 Token 消耗
- [x] 12.7 测试禁用 Provider → 关联 Agent 对话报错
- [x] 12.8 测试权限：model:create/update/delete/read 角色隔离
- [x] 12.9 测试数据隔离：developer 只能看自己的 Provider/ModelConfig
