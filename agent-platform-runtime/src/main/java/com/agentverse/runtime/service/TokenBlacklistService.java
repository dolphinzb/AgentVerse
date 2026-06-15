package com.agentverse.runtime.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.agentverse.common.entity.TokenBlacklist;
import com.agentverse.common.security.JwtUtils;
import com.agentverse.runtime.mapper.TokenBlacklistMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;

/**
 * Token 黑名单服务。
 * <p>
 * 用于在 JWT 注销/续签时吊销 token，黑名单条目存数据库。
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);

    private final TokenBlacklistMapper tokenBlacklistMapper;

    /**
     * 检查 token 是否已被吊销。
     *
     * @param token JWT token
     * @return true 表示已吊销或解析失败
     */
    public boolean isBlacklisted(String token) {
        try {
            String jti = JwtUtils.getJti(token);
            LambdaQueryWrapper<TokenBlacklist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TokenBlacklist::getTokenJti, jti);
            return tokenBlacklistMapper.selectCount(queryWrapper) > 0L;
        } catch (Exception e) {
            log.error("Failed to check token blacklist, blocking for safety: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 将 token 加入黑名单。
     *
     * @param token JWT token
     */
    public void blacklist(String token) {
        String jti = JwtUtils.getJti(token);
        Date expiration = JwtUtils.getExpiration(token);
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setTokenJti(jti);
        blacklist.setExpiresAt(LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault()));
        blacklist.setCreatedTime(LocalDateTime.now());
        tokenBlacklistMapper.insert(blacklist);
        log.info("Token blacklisted: jti={}", jti);
    }

    /**
     * 清理已过期的黑名单条目，定时任务调用。
     */
    public void cleanExpired() {
        LambdaQueryWrapper<TokenBlacklist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(TokenBlacklist::getExpiresAt, LocalDateTime.now());
        long deleted = tokenBlacklistMapper.delete(queryWrapper);
        if (deleted > 0L) {
            log.info("Cleaned {} expired token blacklist entries", deleted);
        }
    }
}
