/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.mapper;

import com.agentverse.runtime.model.entity.ModelProvider;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ModelProviderMapper
extends BaseMapper<ModelProvider> {
    @Select(value={"SELECT COUNT(*) FROM model_config WHERE provider_id = #{providerId} AND deleted = 0"})
    public long countModelConfigsByProviderId(String var1);
}

