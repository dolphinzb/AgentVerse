package com.agentverse.runtime.mapper;

import com.agentverse.common.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据角色代码查询权限列表（通过 sys_role_permission + sys_role 关联）
     */
    @Select("SELECT sp.perm_code FROM sys_permission sp " +
            "INNER JOIN sys_role_permission srp ON sp.id = srp.perm_id " +
            "INNER JOIN sys_role sr ON srp.role_id = sr.id " +
            "WHERE sr.role_code = #{roleCode} AND sp.deleted = 0 AND sr.deleted = 0")
    List<String> selectPermCodesByRoleCode(String roleCode);
}