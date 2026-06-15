/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Generated;

public class RegisterRequest {
    @NotBlank(message="Username is required")
    @Size(min=3, max=64, message="Username must be 3-64 characters")
    private @NotBlank(message="Username is required") @Size(min=3, max=64, message="Username must be 3-64 characters") String username;
    @NotBlank(message="Password is required")
    @Size(min=8, max=128, message="Password must be 8-128 characters")
    private @NotBlank(message="Password is required") @Size(min=8, max=128, message="Password must be 8-128 characters") String password;
    private String email;

    public String toString() {
        return "RegisterRequest{username='" + this.username + "', email='" + this.email + "', password='***'}";
    }

    @Generated
    public RegisterRequest() {
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RegisterRequest)) {
            return false;
        }
        RegisterRequest other = (RegisterRequest)o;
        if (!other.canEqual(this)) {
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
        return !(this$email == null ? other$email != null : !this$email.equals(other$email));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof RegisterRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        return result;
    }
}

