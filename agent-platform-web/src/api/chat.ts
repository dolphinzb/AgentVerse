import request from './request'

export interface SessionCreateRequest {
  agentId: string
}

export interface SessionResponse {
  sessionId: string
  agentId: string
  agentName: string
  createdAt: string
}

export interface MessageRequest {
  content: string
}

export interface MessageResponse {
  role: string
  content: string
  timestamp: string
}

export const chatApi = {
  // 列出当前用户的所有会话（v2 新增；v1 没有此接口）
  // 用于 F5 刷新 / 首次进入对话页时还原左侧会话栏
  listSessions() {
    return request.get<any, { data: SessionResponse[] }>('/v2/chat/sessions')
  },
  createSession(data: SessionCreateRequest) {
    return request.post<any, { data: SessionResponse }>('/v2/chat/sessions', data)
  },
  sendMessage(sessionId: string, data: MessageRequest) {
    return request.post<any, { data: MessageResponse }>(`/v2/chat/sessions/${sessionId}/messages`, data)
  },
  getSessionHistory(sessionId: string) {
    return request.get<any, { data: MessageResponse[] }>(`/v2/chat/sessions/${sessionId}/messages`)
  },
  deleteSession(sessionId: string) {
    return request.delete(`/v2/chat/sessions/${sessionId}`)
  },
  interruptSession(sessionId: string) {
    return request.post<any, { data: boolean }>(`/v2/chat/sessions/${sessionId}/interrupt`)
  },
}
