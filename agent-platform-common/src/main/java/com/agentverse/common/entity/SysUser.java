/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="sys_user")
public class SysUser {
    @TableField(value="id")
    private Long id;
    @TableField(value="username")
    private String username;
    @TableField(value="password")
    private String password;
    @TableField(value="email")
    private String email;
    @TableField(value="role_code")
    private String roleCode;
    @TableField(value="status")
    private Integer status;
    @TableField(value="created_time")
    private LocalDateTime createdTime;
    @TableField(value="updated_time")
    private LocalDateTime updatedTime;
    @TableField(value="deleted")
    private Integer deleted;

    @Generated
    public SysUser() {
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getRoleCode() {
        return this.roleCode;
    }

    @Generated
    public Integer getStatus() {
        return this.status;
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
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Generated
    public void setStatus(Integer status) {
        this.status = status;
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
        if (!(o instanceof SysUser)) {
            return false;
        }
        SysUser other = (SysUser)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        Integer this$deleted = this.getDeleted();
        Integer other$deleted = other.getDeleted();
        if (this$deleted == null ? other$deleted != null : !((Object)this$deleted).equals(other$deleted)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$roleCode = this.getRoleCode();
        String other$roleCode = other.getRoleCode();
        if (this$roleCode == null ? other$roleCode != null : !this$roleCode.equals(other$roleCode)) {
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
        return other instanceof SysUser;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        Integer $deleted = this.getDeleted();
        result = result * 59 + ($deleted == null ? 43 : ((Object)$deleted).hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $roleCode = this.getRoleCode();
        result = result * 59 + ($roleCode == null ? 43 : $roleCode.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        LocalDateTime $updatedTime = this.getUpdatedTime();
        result = result * 59 + ($updatedTime == null ? 43 : ((Object)$updatedTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SysUser(id=" + this.getId() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", email=" + this.getEmail() + ", roleCode=" + this.getRoleCode() + ", status=" + this.getStatus() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ", updatedTime=" + String.valueOf(this.getUpdatedTime()) + ", deleted=" + this.getDeleted() + ")";
    }
}

