/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="chat_usage")
public class ChatUsage
implements Serializable {
    @TableId(type=IdType.ASSIGN_ID)
    private String id;
    @TableField(value="session_id")
    private String sessionId;
    @TableField(value="model_config_id")
    private String modelConfigId;
    @TableField(value="input_tokens")
    private Long inputTokens;
    @TableField(value="output_tokens")
    private Long outputTokens;
    @TableField(value="created_time")
    private LocalDateTime createdTime;

    @Generated
    public ChatUsage() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getSessionId() {
        return this.sessionId;
    }

    @Generated
    public String getModelConfigId() {
        return this.modelConfigId;
    }

    @Generated
    public Long getInputTokens() {
        return this.inputTokens;
    }

    @Generated
    public Long getOutputTokens() {
        return this.outputTokens;
    }

    @Generated
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Generated
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Generated
    public void setModelConfigId(String modelConfigId) {
        this.modelConfigId = modelConfigId;
    }

    @Generated
    public void setInputTokens(Long inputTokens) {
        this.inputTokens = inputTokens;
    }

    @Generated
    public void setOutputTokens(Long outputTokens) {
        this.outputTokens = outputTokens;
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
        if (!(o instanceof ChatUsage)) {
            return false;
        }
        ChatUsage other = (ChatUsage)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$inputTokens = this.getInputTokens();
        Long other$inputTokens = other.getInputTokens();
        if (this$inputTokens == null ? other$inputTokens != null : !((Object)this$inputTokens).equals(other$inputTokens)) {
            return false;
        }
        Long this$outputTokens = this.getOutputTokens();
        Long other$outputTokens = other.getOutputTokens();
        if (this$outputTokens == null ? other$outputTokens != null : !((Object)this$outputTokens).equals(other$outputTokens)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$sessionId = this.getSessionId();
        String other$sessionId = other.getSessionId();
        if (this$sessionId == null ? other$sessionId != null : !this$sessionId.equals(other$sessionId)) {
            return false;
        }
        String this$modelConfigId = this.getModelConfigId();
        String other$modelConfigId = other.getModelConfigId();
        if (this$modelConfigId == null ? other$modelConfigId != null : !this$modelConfigId.equals(other$modelConfigId)) {
            return false;
        }
        LocalDateTime this$createdTime = this.getCreatedTime();
        LocalDateTime other$createdTime = other.getCreatedTime();
        return !(this$createdTime == null ? other$createdTime != null : !((Object)this$createdTime).equals(other$createdTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ChatUsage;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $inputTokens = this.getInputTokens();
        result = result * 59 + ($inputTokens == null ? 43 : ((Object)$inputTokens).hashCode());
        Long $outputTokens = this.getOutputTokens();
        result = result * 59 + ($outputTokens == null ? 43 : ((Object)$outputTokens).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $sessionId = this.getSessionId();
        result = result * 59 + ($sessionId == null ? 43 : $sessionId.hashCode());
        String $modelConfigId = this.getModelConfigId();
        result = result * 59 + ($modelConfigId == null ? 43 : $modelConfigId.hashCode());
        LocalDateTime $createdTime = this.getCreatedTime();
        result = result * 59 + ($createdTime == null ? 43 : ((Object)$createdTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ChatUsage(id=" + this.getId() + ", sessionId=" + this.getSessionId() + ", modelConfigId=" + this.getModelConfigId() + ", inputTokens=" + this.getInputTokens() + ", outputTokens=" + this.getOutputTokens() + ", createdTime=" + String.valueOf(this.getCreatedTime()) + ")";
    }
}

