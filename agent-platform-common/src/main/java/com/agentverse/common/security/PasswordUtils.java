package com.agentverse.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt 密码工具类：加密和校验
 */
public class PasswordUtils {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(10);

    private PasswordUtils() {}

    /**
     * BCrypt 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return true 匹配, false 不匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}