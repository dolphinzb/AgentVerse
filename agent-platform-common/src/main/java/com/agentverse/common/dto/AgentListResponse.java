package com.agentverse.common.dto;

import lombok.Data;

import java.util.List;

/**
 * Agent 列表响应
 */
@Data
public class AgentListResponse {

    /**
     * Agent 列表
     */
    private List<AgentResponse> agents;

    /**
     * 总数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer pageSize;

    public AgentListResponse() {
    }

    public AgentListResponse(List<AgentResponse> agents, Long total, Integer page, Integer pageSize) {
        this.agents = agents;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }
}
