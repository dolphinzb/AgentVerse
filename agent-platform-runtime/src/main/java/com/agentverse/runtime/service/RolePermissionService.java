/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.service;

import com.agentverse.common.entity.SysPermission;
import com.agentverse.runtime.mapper.SysPermissionMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RolePermissionService.class);
    private final SysPermissionMapper sysPermissionMapper;

    public List<String> getPermissionsByRoleCode(String roleCode) {
        if ("admin".equals(roleCode)) {
            List allPermissions = this.sysPermissionMapper.selectList(null);
            return allPermissions.stream().map(SysPermission::getPermCode).collect(Collectors.toList());
        }
        return this.sysPermissionMapper.selectPermCodesByRoleCode(roleCode);
    }

    public boolean hasPermission(String roleCode, String permCode) {
        if ("admin".equals(roleCode)) {
            return true;
        }
        List<String> permissions = this.getPermissionsByRoleCode(roleCode);
        return permissions.contains(permCode);
    }

    @Generated
    public RolePermissionService(SysPermissionMapper sysPermissionMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
    }
}

