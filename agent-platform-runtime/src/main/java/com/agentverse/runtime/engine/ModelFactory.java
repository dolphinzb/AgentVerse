package com.agentverse.runtime.engine;

import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.OpenAIChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 模型工厂，硬编码模型配置（阶段一）
 * 阶段三实现多模型管理时重构此类
 */
@Slf4j
@Component
public class ModelFactory {

    @Value("${agent.model.provider:dashscope}")
    private String provider;

    @Value("${agent.model.name:qwen-max}")
    private String modelName;

    @Value("${agent.model.api-key:your-api-key-here}")
    private String apiKey;

    @Value("${agent.model.temperature:0.7}")
    private double temperature;

    @Value("${agent.model.max-tokens:4096}")
    private int maxTokens;

    @Value("${agent.model.top-p:0.9}")
    private double topP;

    /**
     * 创建预配置的 ChatModel 实例
     */
    public Object createModel() {
        log.info("Creating model: provider={}, model={}", provider, modelName);

        GenerateOptions options = GenerateOptions.builder()
                .temperature(temperature)
                .maxTokens(maxTokens)
                .topP(topP)
                .build();

        return switch (provider.toLowerCase()) {
            case "dashscope" -> DashScopeChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .stream(true)
                    .defaultOptions(options)
                    .build();
            case "openai" -> OpenAIChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .stream(true)
                    .generateOptions(options)
                    .build();
            default -> throw new IllegalArgumentException("Unsupported model provider: " + provider);
        };
    }
}
