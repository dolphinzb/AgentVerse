package com.agentverse.runtime.mapper;

import com.agentverse.common.entity.TokenBlacklist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Token 黑名单 Mapper
 */
@Mapper
public interface TokenBlacklistMapper extends BaseMapper<TokenBlacklist> {
}