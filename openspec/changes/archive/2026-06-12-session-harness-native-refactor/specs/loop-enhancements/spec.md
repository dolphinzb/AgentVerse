# loop-enhancements（修改）

## 变更说明

`AgentConfigAssembler` 在装配 HarnessAgent Builder 时，根据 `enable_session_persistence` 字段决定是否调用 `.session(SessionSpec)`。`SessionSpec` 后端配置（Redis URL、Key 前缀）由 `SessionSpecConfig` 提供。

## Builder 配置变更

### 旧逻辑

```java
HarnessAgent.builder()
    .session(sessionSpec)   // sessionSpec 由 AgentDefinition 字段驱动
    .build();
```

**问题**：`SessionSpec` 后端类型（Redis/MySQL）写死在某处，没有统一配置。

### 新逻辑

```java
@Configuration
public class SessionSpecConfig {
    @Bean
    public SessionSpec sessionSpec(RedisProperties props) {
        return RedisSessionSpec.builder()
            .url(props.getUrl())
            .keyPrefix("agentverse:as:")
            .build();
    }
}
```

```java
// AgentConfigAssembler
HarnessAgent.Builder builder = HarnessAgent.builder()
    .model(model)
    .workspace(workspace)
    .filesystem(filesystemSpec)
    .compaction(compactionConfig)
    .toolResultEviction(evictionConfig)
    .longTermMemory(ltmConfig)
    .planNotebook(planConfig);

if (enableSessionPersistence) {
    builder.session(sessionSpec);  // 注入 RedisSessionSpec Bean
}

// Hook 注册
if (tokenUsageHook != null) {
    builder.hook(tokenUsageHook);
}

return builder.build();
```

## 关键字段语义

| 字段 | 含义 | 启用条件 |
|------|------|---------|
| `enable_session_persistence` | 持久化会话到 Redis | 默认为 true（生产） |
| `enable_compaction` | 上下文压缩 | 默认 true |
| `enable_tool_result_eviction` | 工具结果驱逐 | 默认 true |
| `enable_long_term_memory` | 跨会话长期记忆 | 默认 true |
| `enable_plan` | 计划执行 | 默认 true |

`enable_session_persistence = false` 时不调用 `.session()`，会话仅保留在内存（重启即丢），用于无状态测试。

## Token 用量钩子

`TokenUsageHook` 实现 `io.agentscope.core.hook.Hook` 接口，监听 `PostCallEvent`：

```java
public class TokenUsageHook implements Hook {
    @Override
    public <T> Event hook(HookEvent<T> event) {
        if (event instanceof PostCallEvent e) {
            Map<String, Object> meta = e.metadata();
            Long input = getLong(meta, "input_tokens");
            Long output = getLong(meta, "output_tokens");
            if (input != null && output != null) {
                chatUsageService.saveUsage(
                    sessionContext.sessionId(),
                    sessionContext.modelConfigId(),
                    input, output
                );
            }
        }
        return event.proceed();
    }
}
```

降级链：钩子 metadata 无 tokens → `Msg.metadata.get("input_tokens")` → `text.length() / 4` 估算 + WARN 日志。

## 数据模型

无新增表。复用 `chat_usage`。

## 错误码

无新增错误码。

## RBAC 权限

无新增权限。
