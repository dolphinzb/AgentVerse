/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Generated;

public abstract class BaseEntity
implements Serializable {
    @TableId(type=IdType.ASSIGN_ID)
    private String id;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    @TableField(fill=FieldFill.INSERT)
    private Long createdBy;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private String updatedBy;
    @TableLogic
    @TableField(fill=FieldFill.INSERT)
    private Integer deleted;

    @Generated
    public BaseEntity() {
    }

    @Generated
    public String getId() {
        return this.id;
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
    public Long getCreatedBy() {
        return this.createdBy;
    }

    @Generated
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Generated
    public Integer getDeleted() {
        return this.deleted;
    }

    @Generated
    public void setId(String id) {
        this.id = id;
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
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Generated
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
        if (!(o instanceof BaseEntity)) {
            return false;
        }
        BaseEntity other = (BaseEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$createdBy = this.getCreatedBy();
        Long other$createdBy = other.getCreatedBy();
        if (this$createdBy == null ? other$createdBy != null : !((Object)this$createdBy).equals(other$createdBy)) {
            return false;
        }
        Integer this$deleted = this.getDeleted();
        Integer other$deleted = other.getDeleted();
        if (this$deleted == null ? other$deleted != null : !((Object)this$deleted).equals(other$deleted)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        if (this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime)) {
            return false;
        }
        LocalDateTime this$updatedTime = this.getUpdatedTime();
        LocalDateTime other$updatedTime = other.getUpdatedTime();
        if (this$updatedTime == null ? other$updatedTime != null : !((Object)this$updatedTime).equals(other$updatedTime)) {
            return false;
        }
        String this$updatedBy = this.getUpdatedBy();
        String other$updatedBy = other.getUpdatedBy();
        return !(this$updatedBy == null ? other$updatedBy != null : !this$updatedBy.equals(other$updatedBy));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof BaseEntity;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $createdBy = this.getCreatedBy();
        result = result * 59 + ($createdBy == null ? 43 : ((Object)$createdBy).hashCode());
        Integer $deleted = this.getDeleted();
        result = result * 59 + ($deleted == null ? 43 : ((Object)$deleted).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        LocalDateTime $updatedTime = this.getUpdatedTime();
        result = result * 59 + ($updatedTime == null ? 43 : ((Object)$updatedTime).hashCode());
        String $updatedBy = this.getUpdatedBy();
        result = result * 59 + ($updatedBy == null ? 43 : $updatedBy.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "BaseEntity(id=" + this.getId() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ", updatedTime=" + String.valueOf(this.getUpdatedTime()) + ", createdBy=" + this.getCreatedBy() + ", updatedBy=" + this.getUpdatedBy() + ", deleted=" + this.getDeleted() + ")";
    }
}

