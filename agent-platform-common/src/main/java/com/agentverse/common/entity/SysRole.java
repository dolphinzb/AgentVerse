/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="sys_role")
public class SysRole {
    @TableField(value="id")
    private Long id;
    @TableField(value="role_code")
    private String roleCode;
    @TableField(value="role_name")
    private String roleName;
    @TableField(value="description")
    private String description;
    @TableField(value="created_time")
    private LocalDateTime createdTime;
    @TableField(value="updated_time")
    private LocalDateTime updatedTime;
    @TableField(value="deleted")
    private Integer deleted;

    @Generated
    public SysRole() {
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getRoleCode() {
        return this.roleCode;
    }

    @Generated
    public String getRoleName() {
        return this.roleName;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Generated
    public LocalDateTime getUpdatedTime() {
        return this.updatedTime;
    }

    @Generated
    public Integer getDeleted() {
        return this.deleted;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Generated
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Generated
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Generated
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Generated
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SysRole)) {
            return false;
        }
        SysRole other = (SysRole)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$deleted = this.getDeleted();
        Integer other$deleted = other.getDeleted();
        if (this$deleted == null ? other$deleted != null : !((Object)this$deleted).equals(other$deleted)) {
            return false;
        }
        String this$roleCode = this.getRoleCode();
        String other$roleCode = other.getRoleCode();
        if (this$roleCode == null ? other$roleCode != null : !this$roleCode.equals(other$roleCode)) {
            return false;
        }
        String this$roleName = this.getRoleName();
        String other$roleName = other.getRoleName();
        if (this$roleName == null ? other$roleName != null : !this$roleName.equals(other$roleName)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        if (this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime)) {
            return false;
        }
        LocalDateTime this$updatedTime = this.getUpdatedTime();
        LocalDateTime other$updatedTime = other.getUpdatedTime();
        return !(this$updatedTime == null ? other$updatedTime != null : !((Object)this$updatedTime).equals(other$updatedTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SysRole;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $deleted = this.getDeleted();
        result = result * 59 + ($deleted == null ? 43 : ((Object)$deleted).hashCode());
        String $roleCode = this.getRoleCode();
        result = result * 59 + ($roleCode == null ? 43 : $roleCode.hashCode());
        String $roleName = this.getRoleName();
        result = result * 59 + ($roleName == null ? 43 : $roleName.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        LocalDateTime $updatedTime = this.getUpdatedTime();
        result = result * 59 + ($updatedTime == null ? 43 : ((Object)$updatedTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SysRole(id=" + this.getId() + ", roleCode=" + this.getRoleCode() + ", roleName=" + this.getRoleName() + ", description=" + this.getDescription() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ", updatedTime=" + String.valueOf(this.getUpdatedTime()) + ", deleted=" + this.getDeleted() + ")";
    }
}

