import request from './request'

// ===== DTO 接口定义 =====

/** 供应商类型 */
export interface ProviderType {
  code: string
  displayName: string
  defaultBaseUrl: string
  recommendedModels: string[]
}

/** 预设供应商 */
export interface ProviderPreset {
  providerType: string
  displayName: string
  description: string
  icon: string
  defaultBaseUrl: string
}

/** 模型供应商 */
export interface ModelProvider {
  id: string
  name: string
  providerType: string
  providerTypeName: string
  baseUrl: string
  customHeaders: string
  status: string
  createdTime: string
  updatedTime: string
}

/** 模型配置 */
export interface ModelConfig {
  id: string
  providerId: string
  providerName: string
  providerType: string
  modelName: string
  displayName: string
  maxTokens: number
  temperature: number
  topP: number
  isDefault: number
  status: string
  createdTime: string
  updatedTime: string
}

/** 一步添加模型请求 */
export interface ModelAddRequest {
  providerType: string
  apiKey: string
  baseUrl?: string
  customHeaders?: string
  modelName: string
  displayName?: string
  maxTokens?: number
  temperature?: number
  topP?: number
  isDefault?: number
}

/** 连接测试请求（无需保存） */
export interface ConnectionTestRequest {
  providerType: string
  apiKey: string
  baseUrl?: string
  modelName?: string
}

/** 连接测试结果 */
export interface ConnectionTestResult {
  success: boolean
  message: string
  latency?: number
}

/** 模型用量统计 */
export interface ModelStat {
  modelConfigId: string
  modelName: string
  providerName: string
  providerType: string
  callCount: number
  totalInputTokens: number
  totalOutputTokens: number
}

/** 供应商列表响应（MyBatis-Plus Page） */
export interface ProviderListResponse {
  records: ModelProvider[]
  total: number
  current: number
  size: number
}

/** 模型配置列表响应（MyBatis-Plus Page） */
export interface ModelConfigListResponse {
  records: ModelConfig[]
  total: number
  current: number
  size: number
}

/** 供应商创建请求 */
export interface ProviderCreateRequest {
  providerType: string
  displayName: string
  baseUrl: string
  apiKey: string
}

/** 供应商更新请求 */
export interface ProviderUpdateRequest {
  displayName?: string
  baseUrl?: string
  apiKey?: string
}

/** 模型配置创建请求 */
export interface ModelConfigCreateRequest {
  providerId: string
  modelName: string
  displayName?: string
  maxTokens?: number
  temperature?: number
  topP?: number
  isDefault?: number
}

/** 模型配置更新请求 */
export interface ModelConfigUpdateRequest {
  displayName?: string
  maxTokens?: number
  temperature?: number
  topP?: number
  isDefault?: number
}

// ===== API 函数 =====

export const modelApi = {
  /** 获取所有供应商类型 */
  getProviderTypes() {
    return request.get<any, { data: ProviderType[] }>('/v1/model-providers/types')
  },

  /** 获取预设供应商 */
  getProviderPresets() {
    return request.get<any, { data: ProviderPreset[] }>('/v1/model-providers/presets')
  },

  /** 获取供应商列表（分页） */
  getProviders(params: { page: number; pageSize: number }) {
    return request.get<any, { data: ProviderListResponse }>('/v1/model-providers', { params })
  },

  /** 获取供应商详情 */
  getProvider(id: string) {
    return request.get<any, { data: ModelProvider }>(`/v1/model-providers/${id}`)
  },

  /** 创建供应商 */
  createProvider(data: ProviderCreateRequest) {
    return request.post<any, { data: ModelProvider }>('/v1/model-providers', data)
  },

  /** 更新供应商 */
  updateProvider(id: string, data: ProviderUpdateRequest) {
    return request.put<any, { data: ModelProvider }>(`/v1/model-providers/${id}`, data)
  },

  /** 删除供应商 */
  deleteProvider(id: string) {
    return request.delete(`/v1/model-providers/${id}`)
  },

  /** 连接测试（已保存的 Provider） */
  testConnection(id: string) {
    return request.post<any, { data: ConnectionTestResult }>(`/v1/model-providers/${id}/test`)
  },

  /** 连接测试（无需保存，前端填表单时直接测试） */
  testConnectionDirect(data: ConnectionTestRequest) {
    return request.post<any, { data: ConnectionTestResult }>('/v1/model-providers/test-connection', data)
  },

  /** 获取模型配置列表（分页，可选 providerId 筛选） */
  getModelConfigs(params: { page: number; pageSize: number; providerId?: string }) {
    return request.get<any, { data: ModelConfigListResponse }>('/v1/model-configs', { params })
  },

  /** 获取模型配置详情 */
  getModelConfig(id: string) {
    return request.get<any, { data: ModelConfig }>(`/v1/model-configs/${id}`)
  },

  /** 创建模型配置 */
  createModelConfig(data: ModelConfigCreateRequest) {
    return request.post<any, { data: ModelConfig }>('/v1/model-configs', data)
  },

  /** 更新模型配置 */
  updateModelConfig(id: string, data: ModelConfigUpdateRequest) {
    return request.put<any, { data: ModelConfig }>(`/v1/model-configs/${id}`, data)
  },

  /** 删除模型配置 */
  deleteModelConfig(id: string) {
    return request.delete(`/v1/model-configs/${id}`)
  },

  /** 一步添加模型 */
  addModel(data: ModelAddRequest) {
    return request.post<any, { data: ModelConfig }>('/v1/models/add', data)
  },

  /** 获取模型用量统计 */
  getModelStats() {
    return request.get<any, { data: ModelStat[] }>('/v1/model-stats')
  },
}
