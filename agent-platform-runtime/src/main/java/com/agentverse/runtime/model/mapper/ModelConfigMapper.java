/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.mapper;

import com.agentverse.runtime.model.entity.ModelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ModelConfigMapper
extends BaseMapper<ModelConfig> {
    @Select(value={"SELECT COUNT(*) FROM agent_definition WHERE model_config_id = #{modelConfigId} AND deleted = 0"})
    public int countAgentsByModelConfigId(String var1);
}

