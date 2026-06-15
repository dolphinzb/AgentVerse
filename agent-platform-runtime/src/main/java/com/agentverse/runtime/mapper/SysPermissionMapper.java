/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.mapper;

import com.agentverse.common.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysPermissionMapper
extends BaseMapper<SysPermission> {
    @Select(value={"SELECT sp.perm_code FROM sys_permission sp INNER JOIN sys_role_permission srp ON sp.id = srp.perm_id INNER JOIN sys_role sr ON srp.role_id = sr.id WHERE sr.role_code = #{roleCode} AND sp.deleted = 0 AND sr.deleted = 0"})
    public List<String> selectPermCodesByRoleCode(String var1);
}

