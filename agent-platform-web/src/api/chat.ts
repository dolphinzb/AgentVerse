import request from './request'

export interface SessionCreateRequest {
  agentId: string
}

export interface SessionResponse {
  sessionId: string
  agentId: string
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
  createSession(data: SessionCreateRequest) {
    return request.post<any, { data: SessionResponse }>('/v1/chat/sessions', data)
  },
  sendMessage(sessionId: string, data: MessageRequest) {
    return request.post<any, { data: MessageResponse }>(`/v1/chat/sessions/${sessionId}/messages`, data)
  },
  getSessionHistory(sessionId: string) {
    return request.get<any, { data: MessageResponse[] }>(`/v1/chat/sessions/${sessionId}/messages`)
  },
  deleteSession(sessionId: string) {
    return request.delete(`/v1/chat/sessions/${sessionId}`)
  },
  interruptSession(sessionId: string) {
    return request.post<any, { data: boolean }>(`/v1/chat/sessions/${sessionId}/interrupt`)
  },
}
