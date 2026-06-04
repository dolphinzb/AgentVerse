import { defineStore } from 'pinia'
import { ref } from 'vue'
import { agentApi, type AgentResponse, type AgentVersionResponse } from '@/api/agent'

export const useAgentStore = defineStore('agent', () => {
  const agents = ref<AgentResponse[]>([])
  const total = ref(0)
  const currentAgent = ref<AgentResponse | null>(null)
  const versions = ref<AgentVersionResponse[]>([])
  const loading = ref(false)

  async function fetchAgents(page = 1, pageSize = 10, status?: string) {
    loading.value = true
    try {
      const res = await agentApi.list({ page, pageSize, status })
      agents.value = res.data.agents
      total.value = res.data.total
    } finally {
      loading.value = false
    }
  }

  async function fetchAgent(id: string) {
    loading.value = true
    try {
      const res = await agentApi.getById(id)
      currentAgent.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function createAgent(data: { name: string; description?: string; sysPrompt?: string; maxIterations?: number }) {
    const res = await agentApi.create(data)
    return res.data
  }

  async function updateAgent(id: string, data: { name?: string; description?: string; sysPrompt?: string; maxIterations?: number }) {
    const res = await agentApi.update(id, data)
    return res.data
  }

  async function deleteAgent(id: string) {
    await agentApi.delete(id)
  }

  async function publishVersion(id: string, version: string, changelog?: string) {
    const res = await agentApi.publish(id, { version, changelog })
    return res.data
  }

  async function fetchVersions(id: string) {
    const res = await agentApi.listVersions(id)
    versions.value = res.data
  }

  async function rollbackVersion(id: string, version: string) {
    const res = await agentApi.rollback(id, version)
    return res.data
  }

  return {
    agents, total, currentAgent, versions, loading,
    fetchAgents, fetchAgent, createAgent, updateAgent, deleteAgent,
    publishVersion, fetchVersions, rollbackVersion,
  }
})
