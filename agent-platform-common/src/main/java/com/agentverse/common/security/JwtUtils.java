/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

public class JwtUtils {
    private static final long EXPIRATION_MS = 86400000L;
    private static final String ISSUER = "AgentVerse";
    private static String secret;
    private static SecretKey signingKey;

    private JwtUtils() {
    }

    public static void init(String jwtSecret) {
        secret = jwtSecret;
        signingKey = Keys.hmacShaKeyFor((byte[])jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(Long userId, String username, String roleCode) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 86400000L);
        return Jwts.builder().issuer(ISSUER).subject(String.valueOf(userId)).id(UUID.randomUUID().toString()).claim("username", (Object)username).claim("role", (Object)roleCode).issuedAt(now).expiration(expiration).signWith((Key)signingKey).compact();
    }

    public static Claims parseToken(String token) {
        return (Claims)Jwts.parser().verifyWith(signingKey).build().parseSignedClaims((CharSequence)token).getPayload();
    }

    public static Long getUserId(String token) {
        String subject = JwtUtils.parseToken(token).getSubject();
        return Long.parseLong(subject);
    }

    public static String getUsername(String token) {
        return (String)JwtUtils.parseToken(token).get("username", String.class);
    }

    public static String getRoleCode(String token) {
        return (String)JwtUtils.parseToken(token).get("role", String.class);
    }

    public static String getJti(String token) {
        return JwtUtils.parseToken(token).getId();
    }

    public static Date getExpiration(String token) {
        return JwtUtils.parseToken(token).getExpiration();
    }

    public static boolean validateToken(String token) {
        try {
            JwtUtils.parseToken(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static long getExpiresInSeconds() {
        return 86400L;
    }
}

