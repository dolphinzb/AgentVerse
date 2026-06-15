/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = BlockDto.Text.class, name = "text"),
        @JsonSubTypes.Type(value = BlockDto.Reasoning.class, name = "reasoning"),
        @JsonSubTypes.Type(value = BlockDto.ToolUse.class, name = "tool_use"),
        @JsonSubTypes.Type(value = BlockDto.ToolResult.class, name = "tool_result"),
        @JsonSubTypes.Type(value = BlockDto.Image.class, name = "image")
})
public interface BlockDto {

    public record Image(String url, String mimeType) implements BlockDto {
    }

    public record ToolResult(String name, Object result, boolean isError) implements BlockDto {
    }

    public record ToolUse(String name, Map<String, Object> args) implements BlockDto {
    }

    public record Reasoning(String text) implements BlockDto {
    }

    public record Text(String text) implements BlockDto {
    }
}
