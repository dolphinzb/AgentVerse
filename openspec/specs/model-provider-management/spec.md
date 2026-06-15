# model-provider-management

## 概述

模型供应商注册、管理、API Key 加密存储、连接测试、自定义 Header。ProviderType 枚举定义供应商类型（dashscope/openai/deepseek），预设供应商配置，前端一步添加模型。

## 数据模型

### model_provider 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) PK | 雪花算法生成 |
| name | VARCHAR(128) NOT NULL | 供应商名称 |
| provider_type | VARCHAR(32) NOT NULL | 枚举值：dashscope / openai / deepseek |
| api_key_encrypted | TEXT NOT NULL | AES-256-GCM 加密后的 API Key |
| base_url | VARCHAR(512) | 自定义 Base URL（可选） |
| custom_headers | TEXT | 自定义 HTTP Header，JSON 格式 |
| status | VARCHAR(16) DEFAULT 'active' | active / disabled |
| created_time | TIMESTAMP NOT NULL | |
| updated_time | TIMESTAMP NOT NULL | |
| created_by | BIGINT | 数据隔离 |
| updated_by | VARCHAR(64) | |
| deleted | INTEGER DEFAULT 0 | 逻辑删除 |

## API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /v1/model-providers/types | 可选供应商类型（含推荐模型列表） | model:read |
| GET | /v1/model-providers/presets | 预设供应商列表 | model:read |
| GET | /v1/model-providers | Provider 分页列表（按 created_by 隔离） | model:read |
| GET | /v1/model-providers/{id} | Provider 详情 | model:read |
| POST | /v1/model-providers | 创建 Provider | model:create |
| PUT | /v1/model-providers/{id} | 更新 Provider | model:update |
| DELETE | /v1/model-providers/{id} | 删除 Provider（有关联 ModelConfig 时拒绝） | model:delete |
| POST | /v1/model-providers/{id}/test | 连接测试（已保存的 Provider，可选指定 modelName） | model:update |
| POST | /v1/model-providers/test-connection | 连接测试（无需保存，前端填表单时直接测试） | model:create |

## ProviderType 枚举

| 枚举值 | code | displayName | defaultBaseUrl | recommendedModels |
|--------|------|-------------|----------------|-------------------|
| DASHSCOPE | dashscope | DashScope | https://dashscope.aliyuncs.com/compatible-mode/v1 | qwen-max, qwen-plus, qwen-turbo, qwen-long |
| OPENAI | openai | OpenAI | https://api.openai.com/v1 | gpt-4o, gpt-4o-mini, gpt-4-turbo, gpt-3.5-turbo |
| DEEPSEEK | deepseek | DeepSeek | https://api.deepseek.com | deepseek-chat, deepseek-reasoner, deepseek-v4-flash, deepseek-v4-pro |

## 加密方案

- 算法：AES-256-GCM
- 密钥来源优先级：环境变量 `AGENTVERSE_ENCRYPTION_KEY` → `application.yml` 的 `agent.encryption-key` → 自动生成临时密钥
- 加密后格式：Base64(IV + ciphertext + GCM tag)

## 连接测试

- DASHSCOPE：发送最小 prompt（qwen-turbo, max_tokens=1）验证 API Key
- OPENAI/DEEPSEEK：调用 /models 端点验证 API Key 权限
- 超时：10s 连接 + 15s 请求

## 数据隔离

Provider 按 `created_by` 隔离：admin 可查看全部，其他角色只能操作自己创建的。

## 错误码

| 错误码 | 名称 | 触发场景 |
|--------|------|---------|
| 1501 | MODEL_PROVIDER_NOT_FOUND | Provider ID 不存在 |
| 1503 | MODEL_PROVIDER_DISABLED | Provider 已禁用 |
| 1504 | API_KEY_ENCRYPTION_ERROR | AES 加密/解密失败 |
| 1505 | MODEL_CONNECTION_TEST_FAILED | 连接测试失败 |
| 1507 | MODEL_PROVIDER_IN_USE | 删除 Provider 时有 ModelConfig 关联 |
| 1508 | MODEL_PROVIDER_TYPE_UNSUPPORTED | 不支持的供应商类型 |
