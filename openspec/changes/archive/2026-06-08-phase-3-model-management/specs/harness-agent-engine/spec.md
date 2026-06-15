# harness-agent-engine（修改）

## 变更说明

AgentLoaderService 从全局单模型改为按 Agent 的 modelConfigId 加载对应模型；ModelFactory 重构为数据库驱动 + ConcurrentHashMap 缓存。

## ModelFactory 重构

### 旧逻辑

- 从 application.yml 读取硬编码配置
- 全局单一 ChatModel，所有 Agent 共享

### 新逻辑

- 接收 modelConfigId，从数据库加载 ModelConfig + Provider
- 委托 ProviderType 枚举策略构建 ChatModel
- ConcurrentHashMap 缓存，key 为 modelConfigId
- 缓存失效方法：
  - `evictModel(configId)` — 清除单个模型缓存
  - `evictByProvider(providerId)` — 清除该 Provider 下所有模型缓存
  - `evictAll()` — 清除全部缓存

## AgentLoaderService 重构

### 旧逻辑

- 调用 modelFactory.createModel() 获取全局单一模型

### 新逻辑

- 从 AgentDefinition.modelConfigId 通过 ModelFactory.getModel() 获取模型
- ConcurrentHashMap 缓存，key 为 agentId
- 缓存失效方法：
  - `evictAgent(agentId)` — Agent 更新时清除

## 缓存失效策略

| 操作 | 失效范围 |
|------|---------|
| Provider 更新 | ModelFactory.evictByProvider + 关联 Agent evict |
| ModelConfig 更新 | ModelFactory.evictModel + 使用该 config 的 Agent evict |
| Agent 更新 | AgentLoaderService.evictAgent |
