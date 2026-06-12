import request from './request'

export interface DashboardStats {
  agentCount: Record<string, number>
  activeSessions: number
  todayChatCount: number
  tokenUsage: {
    inputTokens: number
    outputTokens: number
  }
}

export function getDashboardStats() {
  return request.get<DashboardStats>('/api/v1/dashboard/stats')
}
