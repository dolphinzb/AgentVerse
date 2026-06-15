/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto.auth;

import java.util.List;
import lombok.Generated;

public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String roleCode;
    private List<String> permissions;

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getUsername() {
        return this.username;
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
    public List<String> getPermissions() {
        return this.permissions;
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
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Generated
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserInfoResponse)) {
            return false;
        }
        UserInfoResponse other = (UserInfoResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
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
        List<String> this$permissions = this.getPermissions();
        List<String> other$permissions = other.getPermissions();
        return !(this$permissions == null ? other$permissions != null : !((Object)this$permissions).equals(other$permissions));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserInfoResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $roleCode = this.getRoleCode();
        result = result * 59 + ($roleCode == null ? 43 : $roleCode.hashCode());
        List<String> $permissions = this.getPermissions();
        result = result * 59 + ($permissions == null ? 43 : ((Object)$permissions).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UserInfoResponse(id=" + this.getId() + ", username=" + this.getUsername() + ", email=" + this.getEmail() + ", roleCode=" + this.getRoleCode() + ", permissions=" + String.valueOf(this.getPermissions()) + ")";
    }

    @Generated
    public UserInfoResponse() {
    }

    @Generated
    public UserInfoResponse(Long id, String username, String email, String roleCode, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleCode = roleCode;
        this.permissions = permissions;
    }
}

