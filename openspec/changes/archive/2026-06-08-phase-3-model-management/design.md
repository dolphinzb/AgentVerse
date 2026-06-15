## Context

AgentVerse 阶段一实现了最小 POC，阶段二建立了用户认证和 RBAC 权限体系。当前模型管理完全硬编码在 ModelFactory 中，从 application.yml 读取单一 Provider 配置，所有 Agent 共享同一个模型。

**当前状态：**
- ModelFactory：硬编码 dashscope/openai 两种 Provider，从 yml 读取配置
- AgentLoaderService：所有 Agent 使用 modelFactory.createModel() 获取全局单一模型
- API Key：明文存储在 application.yml
- 前端：Agent 创建/编辑无模型选择 UI

**关键约束：**
- API Key 必须加密存储（AES-256），运行时解密
- 模型配置存储在数据库，支持动态增删改，无需重启
- Provider/ModelConfig 按 created_by 数据隔离
- Agent 创建时必须选择模型
- 禁用 Provider 后，关联 Agent 对话应报错提示
- 支持自定义 HTTP Header（用于非标准 API 认证）

## Goals / Non-Goals

**Goals:**
- 预设供应商卡片 + 自定义模型卡片，一步添加模型
- Provider 可注册、管理，API Key 加密存储
- Provider 连接测试（验证 API Key 有效性）
- 模型实例可配置，支持多模型注册
- ModelFactory 从数据库动态加载模型（ProviderType 策略模式）
- Agent 可选择 Provider/Model（必填）
- Token 用量按会话记录，按模型聚合统计

**Non-Goals:**
- 模型负载均衡/故障转移
- 模型路由策略（如根据任务类型自动选择模型）
- Token 限流/配额管理
- 模型性能监控（延迟、成功率）
- 多租户模型隔离
- 成本计算（只记录 Token 用量，不计算金额）

## Decisions

### Decision 1: API Key 加密方案

**选择：AES-256 对称加密，密钥优先环境变量、回退 yml 配置**

| 方案 | 优点 | 缺点 |
|------|------|------|
| 环境变量优先 + yml 回退 | 生产安全，开发便利 | 需要两套配置逻辑 |
| 仅环境变量 | 最安全 | 本地开发需手动设置，未设置时启动失败 |
| 仅 yml 配置 | 开箱即用 | 密钥可能误提交 Git |

**结论**：优先从环境变量 `AGENTVERSE_ENCRYPTION_KEY` 读取，未设置时回退到 `application.yml` 的 `agent.encryption-key` 配置。生产环境用环境变量，开发环境用 yml。

### Decision 2: ProviderType 枚举设计

**选择：Java 枚举 + 策略模式**

ProviderType 的取值来源于 agentscope 库已支持的 ChatModel 类型。每个枚举常量实现 build/testConnection/getRecommendedModels 方法。

```java
public enum ProviderType {
    DASHSCOPE("dashscope", "DashScope", "https://dashscope.aliyuncs.com/api/v1") {
        @Override public ChatModel build(...) { /* DashScopeChatModel */ }
        @Override public ConnectionTestResult testConnection(ModelProvider provider) { /* 最小 prompt */ }
        @Override public List<String> getRecommendedModels() { return List.of("qwen-max", "qwen-plus", ...); }
    },
    OPENAI("openai", "OpenAI", "https://api.openai.com/v1") { ... },
    DEEPSEEK("deepseek", "DeepSeek", "https://api.deepseek.com/v1") { ... };

    private final String code;
    private final String displayName;
    private final String defaultBaseUrl;

    public abstract ChatModel build(ModelConfig config, String apiKey, String baseUrl, List<Header> headers);
    public abstract ConnectionTestResult testConnection(ModelProvider provider);
    public abstract List<String> getRecommendedModels();
}
```

**扩展方式**：新增供应商只需在枚举中添加一个常量。

**前端获取可选类型**：`GET /api/v1/model-providers/types` 返回 `[{code, displayName, defaultBaseUrl, recommendedModels}]`。

### Decision 3: ModelFactory 重构策略

**选择：数据库驱动 + ConcurrentHashMap 缓存**

- ModelFactory 接收 modelConfigId，委托 ProviderType.build() 构建对应 ChatModel
- 本地缓存避免每次对话查数据库
- Provider/ModelConfig 更新时主动清除缓存

### Decision 4: 数据库表设计

**model_provider 表**：id、name、provider_type、api_key_encrypted、base_url、custom_headers(JSON)、status、审计字段

**model_config 表**：id、provider_id(FK)、model_name、display_name、max_tokens、temperature、top_p、is_default、status、审计字段

**chat_usage 表**：id、session_id、model_config_id(FK)、input_tokens、output_tokens、created_time

**agent_definition 修改**：新增 model_config_id VARCHAR(64) NOT NULL

### Decision 5: Token 用量追踪

**选择：按会话记录，只存 Token 用量，不计算成本**

- chat_usage 只记录 input_tokens / output_tokens
- 统计页面 SQL 聚合：按 model_config 分组 SUM tokens
- 不计算金额，避免计费不精确的问题

### Decision 6: 连接测试实现

**选择：按 ProviderType 分发**

- DASHSCOPE：发送最小 prompt 验证
- OPENAI/DEEPSEEK：调用 /models 接口验证 API Key 权限
- 统一返回 { success: boolean, message: string }

### Decision 7: 权限与数据隔离

| 权限 code | 说明 | admin | developer | operator | viewer |
|-----------|------|-------|-----------|----------|--------|
| model:create | 创建 Provider/Model | ✓ | ✓ | ✗ | ✗ |
| model:update | 更新 Provider/Model | ✓ | ✓ | ✗ | ✗ |
| model:delete | 删除 Provider/Model | ✓ | ✗ | ✗ | ✗ |
| model:read | 查看 Provider/Model | ✓ | 自己的 | 自己的 | 自己的 |

Provider/ModelConfig 按 created_by 隔离，与 AgentDefinition 一致。

### Decision 8: 前端 UX 设计

**选择：预设供应商卡片 + 自定义模型卡片，一步添加模型**

- 预设供应商（DashScope/OpenAI/DeepSeek）：点击卡片 → 填 API Key + 选模型 → 保存
- 自定义模型：点击卡片 → 选 API 类型 + 填所有参数 + 自定义 Header → 保存
- 后端仍为 Provider + ModelConfig 两个实体，前端合并为一步操作
- Provider 复用：同一用户 + 同一 providerType + 同一 apiKey → 复用已有 Provider

### Decision 9: 前端路由设计

| 路由 | 页面 | 权限 |
|------|------|------|
| /models | 模型管理主页（供应商卡片 + 模型列表） | model:read |
| /models/stats | Token 用量统计 | model:read |

## Risks / Trade-offs

| Risk | 描述 | Mitigation |
|------|------|------------|
| AES 密钥泄露 | 环境变量泄露导致所有 API Key 可解密 | 生产环境使用密钥管理服务；开发环境密钥不提交 Git |
| 模型缓存不一致 | Provider 更新后缓存未及时清除 | 更新时主动清除缓存 |
| 自定义 Header 安全 | 用户可注入任意 HTTP Header | 校验 Header key/value 格式，禁止内部 Header |
| 连接测试耗时 | 调用外部 API 验证可能超时 | 设置 10s 超时 |
| 禁用 Provider 影响 | 禁用后关联 Agent 对话失败 | 前端提示；Agent 列表标注模型状态 |

## Migration Plan

**Phase 3 部署步骤：**

1. **数据库迁移**：执行 schema.sql（新建 3 张表 + 修改 1 张表 + 新增权限数据）
2. **数据迁移**：将 application.yml 中的 API Key 迁移到 model_provider 表（AES 加密）
3. **重启后端**：加载新模块
4. **重启前端**：加载新页面
5. **验证流程**：
   - 点击 DashScope 卡片 → 填 API Key → 选择 qwen-plus → 连接测试 → 保存
   - 点击自定义模型 → 选 OpenAI 兼容 → 填参数 → 保存
   - 创建 Agent 选择模型 → 对话 → 查看 Token 统计

**回滚方案：**
- 数据库回滚：删除新增表，移除 agent_definition.model_config_id 字段
- 代码回滚：Git revert Phase 3 commit
- ModelFactory 回退到 yml 配置模式
