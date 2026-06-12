package com.agentverse.common.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

/**
 * 消息内容块(blocks-only 架构)
 * <p>每个块是一条结构化内容(纯文本、思考、工具调用、工具结果、图片),通过 {@code type} 字段多态序列化。
 * <p>前端按 block 类型分别渲染,避免流式输出时把 thinking 混入最终答案。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BlockDto.Text.class,       name = "text"),
        @JsonSubTypes.Type(value = BlockDto.Reasoning.class,  name = "reasoning"),
        @JsonSubTypes.Type(value = BlockDto.ToolUse.class,    name = "tool_use"),
        @JsonSubTypes.Type(value = BlockDto.ToolResult.class, name = "tool_result"),
        @JsonSubTypes.Type(value = BlockDto.Image.class,      name = "image")
})
public sealed interface BlockDto permits BlockDto.Text, BlockDto.Reasoning, BlockDto.ToolUse, BlockDto.ToolResult, BlockDto.Image {

    /** 纯文本块 */
    @JsonTypeName("text")
    record Text(String text) implements BlockDto {}

    /** 思考过程块(模型 chain-of-thought) */
    @JsonTypeName("reasoning")
    record Reasoning(String text) implements BlockDto {}

    /** 工具调用块(模型决定调用工具) */
    @JsonTypeName("tool_use")
    record ToolUse(String name, Map<String, Object> args) implements BlockDto {}

    /** 工具结果块(工具执行返回) */
    @JsonTypeName("tool_result")
    record ToolResult(String name, Object result, boolean isError) implements BlockDto {}

    /** 图片块 */
    @JsonTypeName("image")
    record Image(String url, String mimeType) implements BlockDto {}
}
