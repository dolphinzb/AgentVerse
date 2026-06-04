package com.agentverse.runtime.service;

import com.agentverse.common.entity.TokenBlacklist;
import com.agentverse.common.security.JwtUtils;
import com.agentverse.runtime.mapper.TokenBlacklistMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Token 黑名单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistMapper tokenBlacklistMapper;

    /**
     * 判断 Token 是否在黑名单中
     * 数据库异常时偏向安全：返回 true（阻止放行）
     */
    public boolean isBlacklisted(String token) {
        try {
            String jti = JwtUtils.getJti(token);
            LambdaQueryWrapper<TokenBlacklist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TokenBlacklist::getTokenJti, jti);
            return tokenBlacklistMapper.selectCount(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("Failed to check token blacklist, blocking for safety: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 将 Token 加入黑名单
     * 数据库异常时向上抛出，确保登出操作失败可感知
     */
    public void blacklist(String token) {
        String jti = JwtUtils.getJti(token);
        Date expiration = JwtUtils.getExpiration(token);

        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setTokenJti(jti);
        blacklist.setExpiresAt(LocalDateTime.ofInstant(
                expiration.toInstant(), ZoneId.systemDefault()));
        blacklist.setCreatedTime(LocalDateTime.now());

        tokenBlacklistMapper.insert(blacklist);
        log.info("Token blacklisted: jti={}", jti);
    }

    /**
     * 清理过期的黑名单记录
     */
    public void cleanExpired() {
        LambdaQueryWrapper<TokenBlacklist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(TokenBlacklist::getExpiresAt, LocalDateTime.now());
        long deleted = tokenBlacklistMapper.delete(queryWrapper);
        if (deleted > 0) {
            log.info("Cleaned {} expired token blacklist entries", deleted);
        }
    }
}