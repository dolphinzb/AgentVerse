package com.agentverse.runtime.service;

import com.agentverse.common.entity.SysPermission;
import com.agentverse.runtime.mapper.SysPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限服务。
 * <p>按 roleCode 加载权限码列表。admin 返回所有权限，其他角色从关联表加载。
 */
@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 获取指定角色的所有权限码。
     *
     * @param roleCode 角色代码
     * @return 权限码列表
     */
    public List<String> getPermissionsByRoleCode(String roleCode) {
        if ("admin".equals(roleCode)) {
            List<SysPermission> allPermissions = sysPermissionMapper.selectList(null);
            return allPermissions.stream().map(SysPermission::getPermCode).collect(Collectors.toList());
        }
        return sysPermissionMapper.selectPermCodesByRoleCode(roleCode);
    }

    /**
     * 判断指定角色是否拥有指定权限码。admin 默认拥有所有权限。
     */
    public boolean hasPermission(String roleCode, String permCode) {
        if ("admin".equals(roleCode)) {
            return true;
        }
        List<String> permissions = getPermissionsByRoleCode(roleCode);
        return permissions.contains(permCode);
    }
}
