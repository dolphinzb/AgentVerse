/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class MessageRequest {
    @NotBlank(message="\u6d88\u606f\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u6d88\u606f\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a") String content;

    @Generated
    public MessageRequest() {
    }

    @Generated
    public String getContent() {
        return this.content;
    }

    @Generated
    public void setContent(String content) {
        this.content = content;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MessageRequest)) {
            return false;
        }
        MessageRequest other = (MessageRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$content = this.getContent();
        String other$content = other.getContent();
        return !(this$content == null ? other$content != null : !this$content.equals(other$content));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MessageRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MessageRequest(content=" + this.getContent() + ")";
    }
}

