/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(10);

    private PasswordUtils() {
    }

    public static String encode(String rawPassword) {
        return ENCODER.encode((CharSequence)rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches((CharSequence)rawPassword, encodedPassword);
    }
}

