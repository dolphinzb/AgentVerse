package com.agentverse.runtime.service;

import com.agentverse.common.entity.SysPermission;
import com.agentverse.runtime.mapper.SysPermissionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色-权限服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 根据角色代码获取权限列表
     * admin 角色拥有全部权限
     */
    public List<String> getPermissionsByRoleCode(String roleCode) {
        if ("admin".equals(roleCode)) {
            // admin 拥有全部权限
            List<SysPermission> allPermissions = sysPermissionMapper.selectList(null);
            return allPermissions.stream()
                    .map(SysPermission::getPermCode)
                    .collect(Collectors.toList());
        }

        // 非 admin 角色：通过 sys_role_permission 关联查询
        return sysPermissionMapper.selectPermCodesByRoleCode(roleCode);
    }

    /**
     * 判断用户是否有指定权限（基于 roleCode）
     */
    public boolean hasPermission(String roleCode, String permCode) {
        if ("admin".equals(roleCode)) {
            return true;
        }
        List<String> permissions = getPermissionsByRoleCode(roleCode);
        return permissions.contains(permCode);
    }
}