/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import com.agentverse.common.dto.AgentResponse;
import java.util.List;
import lombok.Generated;

public class AgentListResponse {
    private List<AgentResponse> agents;
    private Long total;
    private Integer page;
    private Integer pageSize;

    public AgentListResponse() {
    }

    public AgentListResponse(List<AgentResponse> agents, Long total, Integer page, Integer pageSize) {
        this.agents = agents;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Generated
    public List<AgentResponse> getAgents() {
        return this.agents;
    }

    @Generated
    public Long getTotal() {
        return this.total;
    }

    @Generated
    public Integer getPage() {
        return this.page;
    }

    @Generated
    public Integer getPageSize() {
        return this.pageSize;
    }

    @Generated
    public void setAgents(List<AgentResponse> agents) {
        this.agents = agents;
    }

    @Generated
    public void setTotal(Long total) {
        this.total = total;
    }

    @Generated
    public void setPage(Integer page) {
        this.page = page;
    }

    @Generated
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AgentListResponse)) {
            return false;
        }
        AgentListResponse other = (AgentListResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$total = this.getTotal();
        Long other$total = other.getTotal();
        if (this$total == null ? other$total != null : !((Object)this$total).equals(other$total)) {
            return false;
        }
        Integer this$page = this.getPage();
        Integer other$page = other.getPage();
        if (this$page == null ? other$page != null : !((Object)this$page).equals(other$page)) {
            return false;
        }
        Integer this$pageSize = this.getPageSize();
        Integer other$pageSize = other.getPageSize();
        if (this$pageSize == null ? other$pageSize != null : !((Object)this$pageSize).equals(other$pageSize)) {
            return false;
        }
        List<AgentResponse> this$agents = this.getAgents();
        List<AgentResponse> other$agents = other.getAgents();
        return !(this$agents == null ? other$agents != null : !((Object)this$agents).equals(other$agents));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AgentListResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        Integer $page = this.getPage();
        result = result * 59 + ($page == null ? 43 : ((Object)$page).hashCode());
        Integer $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : ((Object)$pageSize).hashCode());
        List<AgentResponse> $agents = this.getAgents();
        result = result * 59 + ($agents == null ? 43 : ((Object)$agents).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AgentListResponse(agents=" + String.valueOf(this.getAgents()) + ", total=" + this.getTotal() + ", page=" + this.getPage() + ", pageSize=" + this.getPageSize() + ")";
    }
}

