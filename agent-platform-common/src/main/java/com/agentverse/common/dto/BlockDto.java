/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Map;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="type")
@JsonSubTypes(value={@JsonSubTypes.Type(value=Text.class, name="text"), @JsonSubTypes.Type(value=Reasoning.class, name="reasoning"), @JsonSubTypes.Type(value=ToolUse.class, name="tool_use"), @JsonSubTypes.Type(value=ToolResult.class, name="tool_result"), @JsonSubTypes.Type(value=Image.class, name="image")})
public interface BlockDto {

    public record Image(String url, String mimeType) implements BlockDto
    {
    }

    public record ToolResult(String name, Object result, boolean isError) implements BlockDto
    {
    }

    public record ToolUse(String name, Map<String, Object> args) implements BlockDto
    {
    }

    public record Reasoning(String text) implements BlockDto
    {
    }

    public record Text(String text) implements BlockDto
    {
    }
}

