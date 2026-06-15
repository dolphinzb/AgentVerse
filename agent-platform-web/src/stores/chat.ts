import { chatApi, type MessageResponse } from '@/api/chat'
import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'

export interface SessionInfo {
  sessionId: string
  agentId: string
  agentName: string
  createdAt: string
  messages: MessageResponse[]
}

export interface ReactiveMessage {
  role: string
  content: string
  timestamp: string
}

export const useChatStore = defineStore('chat', () => {
  const sessions = ref<SessionInfo[]>([])
  const currentSessionId = ref<string | null>(null)
  const streaming = ref(false)

  function getCurrentSession(): SessionInfo | undefined {
    return sessions.value.find(s => s.sessionId === currentSessionId.value)
  }

  async function createSession(agentId: string, agentName: string) {
    const res = await chatApi.createSession({ agentId })
    const session: SessionInfo = {
      sessionId: res.data.sessionId,
      agentId: res.data.agentId,
      agentName,
      createdAt: res.data.createdAt,
      messages: [],
    }
    sessions.value.unshift(session)
    currentSessionId.value = session.sessionId
    return session
  }

  async function sendMessage(sessionId: string, content: string) {
    const res = await chatApi.sendMessage(sessionId, { content })
    const session = sessions.value.find(s => s.sessionId === sessionId)
    if (session) {
      session.messages.push({ role: 'user', content, timestamp: new Date().toISOString() })
      session.messages.push(res.data)
    }
    return res.data
  }

  async function streamMessage(sessionId: string, content: string): Promise<void> {
    const log = (msg: string, ...args: any[]) => console.log(`[${new Date().toISOString()}] ${msg}`, ...args)

    log('streamMessage started, sessionId:', sessionId)
    const session = sessions.value.find(s => s.sessionId === sessionId)
    if (!session) {
      log('session not found, returning')
      return
    }

    session.messages.push({ role: 'user', content, timestamp: new Date().toISOString() } as MessageResponse)
    const assistantMsg = reactive<MessageResponse>({ role: 'assistant', content: '', timestamp: new Date().toISOString() })
    session.messages.push(assistantMsg as MessageResponse)
    streaming.value = true
    log('streaming set to true')

    const controller = new AbortController()
    const signal = controller.signal

    try {
      log('about to fetch...')
      // 原生 fetch 不会经过 request.ts 的 axios 拦截器，需要手动塞 Bearer 头
      const token = localStorage.getItem('agentverse_token')
      const headers: Record<string, string> = { Accept: 'text/event-stream' }
      if (token) {
        headers.Authorization = `Bearer ${token}`
      }
      const response = await fetch(
        `/api/v2/chat/sessions/${sessionId}/stream?content=${encodeURIComponent(content)}`,
        { signal, headers },
      )
      log('fetch completed, response:', response.status, response.statusText)
      log('response.body:', response.body)

      if (!response.body) {
        log('response.body is null, setting streaming to false and returning')
        streaming.value = false
        return
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      let totalChunks = 0
      log('starting to read stream...')

      while (true) {
        const { done, value } = await reader.read()
        if (done) {
          log('Stream done (reader closed), total chunks:', totalChunks)
          break
        }
        totalChunks++
        const text = decoder.decode(value, { stream: true })
        buffer += text

        // 按行解析，处理可能跨 chunk 的情况
        const lines = buffer.split('\n')
        buffer = lines.pop() || '' // 最后一行可能不完整，保留

        for (const line of lines) {
          const trimmed = line.trim()
          if (trimmed.startsWith('data:')) {
            const data = trimmed.slice(5).trim()
            if (data) {
              assistantMsg.content += data
            }
          }
        }
      }

      // 处理剩余的 buffer
      const remaining = buffer.trim()
      if (remaining && remaining.startsWith('data:')) {
        const data = remaining.slice(5).trim()
        if (data) {
          assistantMsg.content += data
        }
      }

      streaming.value = false
      log('streamMessage completed')
    } catch (e: any) {
      if (e.name === 'AbortError') {
        log('Fetch aborted (expected)')
      } else {
        console.error('Stream error:', e)
      }
      streaming.value = false
    }
  }

  async function fetchSessionHistory(sessionId: string) {
    const res = await chatApi.getSessionHistory(sessionId)
    const session = sessions.value.find(s => s.sessionId === sessionId)
    if (session) {
      session.messages = res.data
    }
    currentSessionId.value = sessionId
  }

  async function deleteSession(sessionId: string) {
    await chatApi.deleteSession(sessionId)
    sessions.value = sessions.value.filter(s => s.sessionId !== sessionId)
    if (currentSessionId.value === sessionId) {
      currentSessionId.value = sessions.value.length > 0 ? sessions.value[0].sessionId : null
    }
  }

  async function interruptSession(sessionId: string) {
    await chatApi.interruptSession(sessionId)
    streaming.value = false
  }

  return {
    sessions, currentSessionId, streaming,
    getCurrentSession, createSession, sendMessage, streamMessage,
    fetchSessionHistory, deleteSession, interruptSession,
  }
})
