package com.agentverse.common.security;

/**
 * ThreadLocal 用户上下文，存储当前请求的用户信息
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_CONTEXT = new ThreadLocal<>();

    private UserContext() {}

    /**
     * 设置当前用户信息
     */
    public static void set(UserInfo userInfo) {
        USER_CONTEXT.set(userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public static UserInfo get() {
        return USER_CONTEXT.get();
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getUserId() {
        UserInfo userInfo = USER_CONTEXT.get();
        return userInfo != null ? userInfo.userId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        UserInfo userInfo = USER_CONTEXT.get();
        return userInfo != null ? userInfo.username() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getRoleCode() {
        UserInfo userInfo = USER_CONTEXT.get();
        return userInfo != null ? userInfo.roleCode() : null;
    }

    /**
     * 是否为 admin 角色
     */
    public static boolean isAdmin() {
        return "admin".equals(getRoleCode());
    }

    /**
     * 清理 ThreadLocal（防止内存泄漏）
     */
    public static void clear() {
        USER_CONTEXT.remove();
    }

    /**
     * 用户信息 DTO
     */
    public record UserInfo(Long userId, String username, String roleCode) {}
}