package com.agentverse.runtime.mapper;

import com.agentverse.common.entity.AgentDefinition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 定义 Mapper 接口
 */
@Mapper
public interface AgentDefinitionMapper extends BaseMapper<AgentDefinition> {
}
