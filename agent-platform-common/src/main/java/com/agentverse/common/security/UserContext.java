/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.security;

public class UserContext {
    private static final ThreadLocal<UserInfo> USER_CONTEXT = new ThreadLocal();

    private UserContext() {
    }

    public static void set(UserInfo userInfo) {
        USER_CONTEXT.set(userInfo);
    }

    public static UserInfo get() {
        return USER_CONTEXT.get();
    }

    public static Long getUserId() {
        UserInfo userInfo = USER_CONTEXT.get();
        return userInfo != null ? userInfo.userId() : null;
    }

    public static String getUsername() {
        UserInfo userInfo = USER_CONTEXT.get();
        return userInfo != null ? userInfo.username() : null;
    }

    public static String getRoleCode() {
        UserInfo userInfo = USER_CONTEXT.get();
        return userInfo != null ? userInfo.roleCode() : null;
    }

    public static boolean isAdmin() {
        return "admin".equals(UserContext.getRoleCode());
    }

    public static void clear() {
        USER_CONTEXT.remove();
    }

    public record UserInfo(Long userId, String username, String roleCode) {
    }
}

