package com.agentverse.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类：生成 Token、解析 Token、校验签名、提取用户信息
 * 签名密钥从外部配置注入（非静态），避免硬编码泄露
 */
public class JwtUtils {

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 小时
    private static final String ISSUER = "AgentVerse";

    private static String secret;
    private static SecretKey signingKey;

    private JwtUtils() {}

    /**
     * 初始化签名密钥（由配置类调用）
     */
    public static void init(String jwtSecret) {
        secret = jwtSecret;
        signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Access Token
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param roleCode 角色代码
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username, String roleCode) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(userId))
                .id(UUID.randomUUID().toString()) // JTI
                .claim("username", username)
                .claim("role", roleCode)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token JWT Token 字符串
     * @return Claims
     * @throws JwtException Token 无效或过期
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中提取用户 ID
     *
     * @param token JWT Token 字符串
     * @return 用户 ID
     */
    public static Long getUserId(String token) {
        String subject = parseToken(token).getSubject();
        return Long.parseLong(subject);
    }

    /**
     * 从 Token 中提取用户名
     *
     * @param token JWT Token 字符串
     * @return 用户名
     */
    public static String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从 Token 中提取角色代码
     *
     * @param token JWT Token 字符串
     * @return 角色代码
     */
    public static String getRoleCode(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * 从 Token 中提取 JTI
     *
     * @param token JWT Token 字符串
     * @return JTI
     */
    public static String getJti(String token) {
        return parseToken(token).getId();
    }

    /**
     * 获取 Token 过期时间
     *
     * @param token JWT Token 字符串
     * @return 过期时间
     */
    public static Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token 字符串
     * @return true 有效, false 无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取 Token 过期秒数
     */
    public static long getExpiresInSeconds() {
        return EXPIRATION_MS / 1000;
    }
}