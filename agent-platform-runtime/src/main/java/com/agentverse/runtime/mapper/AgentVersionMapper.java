package com.agentverse.runtime.mapper;

import com.agentverse.common.entity.AgentVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 版本 Mapper 接口
 */
@Mapper
public interface AgentVersionMapper extends BaseMapper<AgentVersion> {
}
