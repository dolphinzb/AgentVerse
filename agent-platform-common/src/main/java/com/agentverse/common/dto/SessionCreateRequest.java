package com.agentverse.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建会话请求
 */
@Data
public class SessionCreateRequest {

    /**
     * Agent ID
     */
    @NotBlank(message = "agentId 不能为空")
    private String agentId;
}
