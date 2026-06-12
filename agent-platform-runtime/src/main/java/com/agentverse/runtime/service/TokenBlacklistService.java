/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.service;

import com.agentverse.common.entity.TokenBlacklist;
import com.agentverse.common.security.JwtUtils;
import com.agentverse.runtime.mapper.TokenBlacklistMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private final TokenBlacklistMapper tokenBlacklistMapper;

    public boolean isBlacklisted(String token) {
        try {
            String jti = JwtUtils.getJti(token);
            LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(TokenBlacklist::getTokenJti, (Object)jti);
            return this.tokenBlacklistMapper.selectCount((Wrapper)queryWrapper) > 0L;
        }
        catch (Exception e) {
            log.error("Failed to check token blacklist, blocking for safety: {}", (Object)e.getMessage());
            return true;
        }
    }

    public void blacklist(String token) {
        String jti = JwtUtils.getJti(token);
        Date expiration = JwtUtils.getExpiration(token);
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setTokenJti(jti);
        blacklist.setExpiresAt(LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault()));
        blacklist.setCreatedTime(LocalDateTime.now());
        this.tokenBlacklistMapper.insert(blacklist);
        log.info("Token blacklisted: jti={}", (Object)jti);
    }

    public void cleanExpired() {
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.lt(TokenBlacklist::getExpiresAt, (Object)LocalDateTime.now());
        long deleted = this.tokenBlacklistMapper.delete((Wrapper)queryWrapper);
        if (deleted > 0L) {
            log.info("Cleaned {} expired token blacklist entries", (Object)deleted);
        }
    }

    @Generated
    public TokenBlacklistService(TokenBlacklistMapper tokenBlacklistMapper) {
        this.tokenBlacklistMapper = tokenBlacklistMapper;
    }
}

