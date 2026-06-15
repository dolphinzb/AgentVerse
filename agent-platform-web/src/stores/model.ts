import {
  modelApi,
  type ModelAddRequest,
  type ModelConfig,
  type ModelProvider,
  type ModelStat,
  type ProviderPreset,
  type ProviderType,
} from '@/api/model'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useModelStore = defineStore('model', () => {
  // ===== 状态 =====
  const providers = ref<ModelProvider[]>([])
  const providerTotal = ref(0)
  const modelConfigs = ref<ModelConfig[]>([])
  const configTotal = ref(0)
  const providerTypes = ref<ProviderType[]>([])
  const presets = ref<ProviderPreset[]>([])
  const stats = ref<ModelStat[]>([])
  const loading = ref(false)
  const configLoading = ref(false)

  // ===== Actions =====

  /** 获取供应商类型列表 */
  async function fetchProviderTypes() {
    const res = await modelApi.getProviderTypes()
    providerTypes.value = res.data
  }

  /** 获取预设供应商列表 */
  async function fetchPresets() {
    const res = await modelApi.getProviderPresets()
    presets.value = res.data
  }

  /** 获取供应商列表 */
  async function fetchProviders(page = 1, pageSize = 100) {
    loading.value = true
    try {
      const res = await modelApi.getProviders({ page, pageSize })
      providers.value = res.data.records
      providerTotal.value = res.data.total
    } finally {
      loading.value = false
    }
  }

  /** 获取模型配置列表 */
  async function fetchModelConfigs(page = 1, pageSize = 100, providerId?: string) {
    configLoading.value = true
    try {
      const res = await modelApi.getModelConfigs({ page, pageSize, providerId })
      modelConfigs.value = res.data.records
      configTotal.value = res.data.total
    } finally {
      configLoading.value = false
    }
  }

  /** 一步添加模型 */
  async function addModel(data: ModelAddRequest) {
    const res = await modelApi.addModel(data)
    return res.data
  }

  /** 删除模型配置 */
  async function deleteModel(id: string) {
    await modelApi.deleteModelConfig(id)
  }

  /** 连接测试（已保存的 Provider，可选指定 modelName） */
  async function testConnection(id: string, modelName?: string) {
    const res = await modelApi.testConnection(id, modelName)
    return res.data
  }

  /** 连接测试（通过模型配置 ID） */
  async function testModelConfig(configId: string) {
    const res = await modelApi.testModelConfig(configId)
    return res.data
  }

  /** 获取模型用量统计 */
  async function fetchStats() {
    loading.value = true
    try {
      const res = await modelApi.getModelStats()
      stats.value = res.data
    } finally {
      loading.value = false
    }
  }

  return {
    providers,
    providerTotal,
    modelConfigs,
    configTotal,
    providerTypes,
    presets,
    stats,
    loading,
    configLoading,
    fetchProviderTypes,
    fetchPresets,
    fetchProviders,
    fetchModelConfigs,
    addModel,
    deleteModel,
    testConnection,
    testModelConfig,
    fetchStats,
  }
})
