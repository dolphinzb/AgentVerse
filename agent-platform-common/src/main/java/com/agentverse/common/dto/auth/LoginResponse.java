/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto.auth;

import java.util.List;
import lombok.Generated;

public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    @Generated
    public String getAccessToken() {
        return this.accessToken;
    }

    @Generated
    public String getTokenType() {
        return this.tokenType;
    }

    @Generated
    public long getExpiresIn() {
        return this.expiresIn;
    }

    @Generated
    public UserInfo getUser() {
        return this.user;
    }

    @Generated
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Generated
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Generated
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Generated
    public void setUser(UserInfo user) {
        this.user = user;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginResponse)) {
            return false;
        }
        LoginResponse other = (LoginResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getExpiresIn() != other.getExpiresIn()) {
            return false;
        }
        String this$accessToken = this.getAccessToken();
        String other$accessToken = other.getAccessToken();
        if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
            return false;
        }
        String this$tokenType = this.getTokenType();
        String other$tokenType = other.getTokenType();
        if (this$tokenType == null ? other$tokenType != null : !this$tokenType.equals(other$tokenType)) {
            return false;
        }
        UserInfo this$user = this.getUser();
        UserInfo other$user = other.getUser();
        return !(this$user == null ? other$user != null : !((Object)this$user).equals(other$user));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof LoginResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $expiresIn = this.getExpiresIn();
        result = result * 59 + (int)($expiresIn >>> 32 ^ $expiresIn);
        String $accessToken = this.getAccessToken();
        result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
        String $tokenType = this.getTokenType();
        result = result * 59 + ($tokenType == null ? 43 : $tokenType.hashCode());
        UserInfo $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : ((Object)$user).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "LoginResponse(accessToken=" + this.getAccessToken() + ", tokenType=" + this.getTokenType() + ", expiresIn=" + this.getExpiresIn() + ", user=" + String.valueOf(this.getUser()) + ")";
    }

    @Generated
    public LoginResponse() {
    }

    @Generated
    public LoginResponse(String accessToken, String tokenType, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static class UserInfo {
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
            if (!(o instanceof UserInfo)) {
                return false;
            }
            UserInfo other = (UserInfo)o;
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
            return other instanceof UserInfo;
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
            return "LoginResponse.UserInfo(id=" + this.getId() + ", username=" + this.getUsername() + ", email=" + this.getEmail() + ", roleCode=" + this.getRoleCode() + ", permissions=" + String.valueOf(this.getPermissions()) + ")";
        }

        @Generated
        public UserInfo() {
        }

        @Generated
        public UserInfo(Long id, String username, String email, String roleCode, List<String> permissions) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.roleCode = roleCode;
            this.permissions = permissions;
        }
    }
}

