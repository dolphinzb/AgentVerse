/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="token_blacklist")
public class TokenBlacklist {
    @TableField(value="id")
    private Long id;
    @TableField(value="token_jti")
    private String tokenJti;
    @TableField(value="expires_at")
    private LocalDateTime expiresAt;
    @TableField(value="created_time")
    private LocalDateTime createdTime;

    @Generated
    public TokenBlacklist() {
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getTokenJti() {
        return this.tokenJti;
    }

    @Generated
    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    @Generated
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setTokenJti(String tokenJti) {
        this.tokenJti = tokenJti;
    }

    @Generated
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Generated
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenBlacklist)) {
            return false;
        }
        TokenBlacklist other = (TokenBlacklist)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$tokenJti = this.getTokenJti();
        String other$tokenJti = other.getTokenJti();
        if (this$tokenJti == null ? other$tokenJti != null : !this$tokenJti.equals(other$tokenJti)) {
            return false;
        }
        LocalDateTime this$expiresAt = this.getExpiresAt();
        LocalDateTime other$expiresAt = other.getExpiresAt();
        if (this$expiresAt == null ? other$expiresAt != null : !((Object)this$expiresAt).equals(other$expiresAt)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        return !(this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof TokenBlacklist;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $tokenJti = this.getTokenJti();
        result = result * 59 + ($tokenJti == null ? 43 : $tokenJti.hashCode());
        LocalDateTime $expiresAt = this.getExpiresAt();
        result = result * 59 + ($expiresAt == null ? 43 : ((Object)$expiresAt).hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "TokenBlacklist(id=" + this.getId() + ", tokenJti=" + this.getTokenJti() + ", expiresAt=" + String.valueOf(this.getExpiresAt()) + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ")";
    }
}

