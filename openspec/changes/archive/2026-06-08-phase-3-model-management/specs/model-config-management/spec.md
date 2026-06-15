# model-config-management

## 概述

模型配置 CRUD、ModelFactory 动态加载（ProviderType 策略模式）、一步添加模型（Provider 复用）。

## 数据模型

### model_config 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) PK | 雪花算法生成 |
| provider_id | VARCHAR(64) NOT NULL FK | 关联 model_provider.id |
| model_name | VARCHAR(128) NOT NULL | 模型标识（如 qwen-plus、gpt-4o） |
| display_name | VARCHAR(128) | 前端显示名 |
| max_tokens | INTEGER DEFAULT 4096 | 最大输出 Token |
| temperature | DOUBLE PRECISION DEFAULT 0.7 | 温度参数 |
| top_p | DOUBLE PRECISION DEFAULT 0.9 | Top-P 参数 |
| is_default | INTEGER DEFAULT 0 | 是否默认模型（0/1） |
| status | VARCHAR(16) DEFAULT 'active' | active / disabled |
| created_time | TIMESTAMP NOT NULL | |
| updated_time | TIMESTAMP NOT NULL | |
| created_by | BIGINT | 数据隔离 |
| updated_by | VARCHAR(64) | |
| deleted | INTEGER DEFAULT 0 | 逻辑删除 |

## API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /v1/model-configs | 模型配置分页列表（按 created_by 隔离） | model:read |
| GET | /v1/model-configs/{id} | 模型配置详情 | model:read |
| GET | /v1/model-configs/default | 当前用户默认模型 | model:read |
| POST | /v1/model-configs | 创建模型配置 | model:create |
| PUT | /v1/model-configs/{id} | 更新模型配置 | model:update |
| DELETE | /v1/model-configs/{id} | 删除模型配置（有 Agent 引用时拒绝） | model:delete |
| POST | /v1/models/add | 一步添加模型（合并 Provider + ModelConfig） | model:create |

## 一步添加模型逻辑

1. 查找或创建 Provider：同一用户 + 同一 providerType + 同一 apiKey → 复用已有 Provider
2. 创建 ModelConfig：关联 Provider，设置模型参数
3. 事务保证原子性

## ModelFactory 动态加载

- 接收 modelConfigId，从数据库加载 ModelConfig + Provider
- 委托 ProviderType 枚举策略构建 ChatModel
- ConcurrentHashMap 缓存，避免每次对话查数据库
- 缓存失效策略：
  - Provider 更新 → evictByProvider + 关联 Agent evict
  - ModelConfig 更新 → evictModel + 关联 Agent evict
  - Agent 更新 → evictAgent

## 默认模型

- 每个用户只能有一个默认模型（is_default=1）
- 设置新默认时自动清除旧默认

## 数据隔离

ModelConfig 按 `created_by` 隔离，与 Provider 一致。

## 错误码

| 错误码 | 名称 | 触发场景 |
|--------|------|---------|
| 1502 | MODEL_CONFIG_NOT_FOUND | ModelConfig ID 不存在 |
| 1506 | MODEL_CONFIG_IN_USE | 删除 ModelConfig 时有 Agent 引用 |
