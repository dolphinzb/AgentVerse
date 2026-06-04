import request from './request'

export interface AgentCreateRequest {
  name: string
  description?: string
  sysPrompt?: string
  maxIterations?: number
}

export interface AgentUpdateRequest {
  name?: string
  description?: string
  sysPrompt?: string
  maxIterations?: number
}

export interface AgentResponse {
  id: string
  name: string
  description: string
  sysPrompt: string
  maxIterations: number
  workspaceMode: string
  status: string
  currentVersion: string
  createdTime: string
  updatedTime: string
}

export interface AgentListResponse {
  agents: AgentResponse[]
  total: number
  page: number
  pageSize: number
}

export interface AgentPublishRequest {
  version: string
  changelog?: string
}

export interface AgentVersionResponse {
  id: string
  agentId: string
  version: string
  snapshotData: string
  changelog: string
  createdTime: string
}

export const agentApi = {
  create(data: AgentCreateRequest) {
    return request.post<any, { data: AgentResponse }>('/v1/agents', data)
  },
  getById(id: string) {
    return request.get<any, { data: AgentResponse }>(`/v1/agents/${id}`)
  },
  list(params: { page: number; pageSize: number; status?: string }) {
    return request.get<any, { data: AgentListResponse }>('/v1/agents', { params })
  },
  update(id: string, data: AgentUpdateRequest) {
    return request.put<any, { data: AgentResponse }>(`/v1/agents/${id}`, data)
  },
  delete(id: string) {
    return request.delete(`/v1/agents/${id}`)
  },
  publish(id: string, data: AgentPublishRequest) {
    return request.post<any, { data: AgentVersionResponse }>(`/v1/agents/${id}/publish`, data)
  },
  listVersions(id: string) {
    return request.get<any, { data: AgentVersionResponse[] }>(`/v1/agents/${id}/versions`)
  },
  rollback(id: string, version: string) {
    return request.post<any, { data: AgentVersionResponse }>(`/v1/agents/${id}/rollback`, null, { params: { version } })
  },
}
