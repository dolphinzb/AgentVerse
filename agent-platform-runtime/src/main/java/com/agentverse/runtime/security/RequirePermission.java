package com.agentverse.runtime.security;

import java.lang.annotation.*;

/**
 * 权限校验注解，标注在 Controller 方法上
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 所需权限代码，格式: module:action
     */
    String value();
}