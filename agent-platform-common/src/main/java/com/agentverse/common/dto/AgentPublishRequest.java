package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Agent 发布版本请求
 */
@Data
public class AgentPublishRequest {

    /**
     * 版本号（格式：vX.Y.Z）
     */
    @NotBlank(message = "版本号不能为空")
    @Pattern(regexp = "^v\\d+\\.\\d+\\.\\d+$", message = "版本号格式必须为 vX.Y.Z（例如：v1.0.0）")
    private String version;

    /**
     * 变更日志
     */
    @Size(max = 1000, message = "变更日志长度不能超过 1000 个字符")
    private String changelog;
}
