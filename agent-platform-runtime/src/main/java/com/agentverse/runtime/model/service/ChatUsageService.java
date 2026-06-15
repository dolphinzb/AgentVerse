/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.agentverse.common.security.UserContext;
import com.agentverse.runtime.model.dto.ModelStatResponse;
import com.agentverse.runtime.model.entity.ChatUsage;
import com.agentverse.runtime.model.entity.ModelConfig;
import com.agentverse.runtime.model.entity.ModelProvider;
import com.agentverse.runtime.model.mapper.ChatUsageMapper;
import com.agentverse.runtime.model.mapper.ModelConfigMapper;
import com.agentverse.runtime.model.mapper.ModelProviderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.Generated;

@Service
public class ChatUsageService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ChatUsageService.class);
    private final ChatUsageMapper chatUsageMapper;
    private final ModelConfigMapper modelConfigMapper;
    private final ModelProviderMapper modelProviderMapper;

    public void saveUsage(String sessionId, String modelConfigId, Long inputTokens, Long outputTokens) {
        log.info(
                "\u4fdd\u5b58\u5bf9\u8bdd\u7528\u91cf: sessionId={}, modelConfigId={}, inputTokens={}, outputTokens={}",
                new Object[] { sessionId, modelConfigId, inputTokens, outputTokens });
        ChatUsage usage = new ChatUsage();
        usage.setSessionId(sessionId);
        usage.setModelConfigId(modelConfigId);
        usage.setInputTokens(inputTokens != null ? inputTokens : 0L);
        usage.setOutputTokens(outputTokens != null ? outputTokens : 0L);
        usage.setCreatedTime(LocalDateTime.now());
        this.chatUsageMapper.insert(usage);
    }

    public List<ModelStatResponse> getStats(Long userId) {
        List<ModelConfig> modelConfigs;
        log.info("\u83b7\u53d6\u6a21\u578b\u7528\u91cf\u7edf\u8ba1: userId={}", (Object) userId);
        LambdaQueryWrapper<ModelConfig> configWrapper = new LambdaQueryWrapper<ModelConfig>();
        if (userId != null && !UserContext.isAdmin()) {
            configWrapper.eq(ModelConfig::getCreatedBy, userId);
        }
        if ((modelConfigs = this.modelConfigMapper.selectList(configWrapper)).isEmpty()) {
            return new ArrayList<ModelStatResponse>();
        }
        List<String> visibleConfigIds = modelConfigs.stream().map(ModelConfig::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ChatUsage> usageWrapper = new LambdaQueryWrapper<ChatUsage>();
        usageWrapper.in(ChatUsage::getModelConfigId, visibleConfigIds);
        List<ChatUsage> usageRecords = this.chatUsageMapper.selectList(usageWrapper);
        Map<String, List<ChatUsage>> groupedByConfig = usageRecords.stream()
                .collect(Collectors.groupingBy(ChatUsage::getModelConfigId));
        Map<String, ModelProvider> providerMap = modelConfigs.stream().map(ModelConfig::getProviderId).distinct()
                .collect(Collectors.toMap(pid -> pid,
                        pid -> (ModelProvider) this.modelProviderMapper.selectById((Serializable) ((Object) pid))));
        ArrayList<ModelStatResponse> stats = new ArrayList<ModelStatResponse>();
        for (ModelConfig config : modelConfigs) {
            String configId = config.getId();
            List<ChatUsage> records = groupedByConfig.getOrDefault(configId, new ArrayList<ChatUsage>());
            ModelStatResponse stat = new ModelStatResponse();
            stat.setModelConfigId(configId);
            stat.setModelName(config.getModelName());
            ModelProvider provider = providerMap.get(config.getProviderId());
            if (provider != null) {
                stat.setProviderName(provider.getName());
                stat.setProviderType(provider.getProviderType());
            }
            stat.setCallCount(Long.valueOf(records.size()));
            stat.setTotalInputTokens(
                    records.stream().mapToLong(u -> u.getInputTokens() != null ? u.getInputTokens() : 0L).sum());
            stat.setTotalOutputTokens(
                    records.stream().mapToLong(u -> u.getOutputTokens() != null ? u.getOutputTokens() : 0L).sum());
            stats.add(stat);
        }
        return stats;
    }

    @Generated
    public ChatUsageService(ChatUsageMapper chatUsageMapper, ModelConfigMapper modelConfigMapper,
            ModelProviderMapper modelProviderMapper) {
        this.chatUsageMapper = chatUsageMapper;
        this.modelConfigMapper = modelConfigMapper;
        this.modelProviderMapper = modelProviderMapper;
    }
}