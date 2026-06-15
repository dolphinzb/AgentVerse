<template>
  <div>
    <!-- 顶部：预设供应商卡片 + 自定义模型卡片 -->
    <div style="margin-bottom: 24px">
      <h3 style="margin: 0 0 16px 0">添加模型</h3>
      <div style="display: flex; gap: 16px; flex-wrap: wrap">
        <el-card
          v-for="preset in modelStore.presets"
          :key="preset.providerType"
          shadow="hover"
          style="width: 220px; cursor: pointer"
          @click="openAddModelDialog(preset)"
        >
          <div style="text-align: center">
            <div style="font-size: 32px; margin-bottom: 8px">{{ preset.icon || '🤖' }}</div>
            <div style="font-size: 16px; font-weight: bold; margin-bottom: 4px">{{ preset.displayName }}</div>
            <div style="font-size: 12px; color: #909399; line-height: 1.4">{{ preset.description }}</div>
          </div>
        </el-card>
        <el-card
          shadow="hover"
          style="width: 220px; cursor: pointer; border-style: dashed"
          @click="showCustomDialog = true"
        >
          <div style="text-align: center">
            <el-icon style="font-size: 32px; color: #409eff; margin-bottom: 8px"><Plus /></el-icon>
            <div style="font-size: 16px; font-weight: bold; margin-bottom: 4px">自定义模型</div>
            <div style="font-size: 12px; color: #909399; line-height: 1.4">添加自定义 API 供应商的模型</div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 底部：已添加模型列表 -->
    <div>
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
        <h3 style="margin: 0">已添加模型</h3>
        <el-select v-model="providerFilter" placeholder="按供应商筛选" clearable style="width: 200px" @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option
            v-for="provider in modelStore.providers"
            :key="provider.id"
            :label="provider.name"
            :value="provider.id"
          />
        </el-select>
      </div>

      <el-table :data="modelStore.modelConfigs" v-loading="modelStore.configLoading" stripe>
        <el-table-column prop="displayName" label="显示名称" min-width="140">
          <template #default="{ row }">
            <span>{{ row.displayName || row.modelName }}</span>
            <el-tag v-if="row.isDefault" type="warning" size="small" style="margin-left: 6px">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="modelName" label="模型名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="providerType" label="供应商" width="140">
          <template #default="{ row }">
            {{ getProviderDisplayName(row.providerType) }}
          </template>
        </el-table-column>
        <el-table-column prop="maxTokens" label="Max Tokens" width="120" />
        <el-table-column prop="temperature" label="Temperature" width="120" />
        <el-table-column prop="topP" label="Top P" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status === 'active' ? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'model:update'" link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="warning" @click="handleTestConnection(row)">测试</el-button>
            <el-button v-permission="'model:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="modelStore.configTotal"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 添加预设模型对话框 -->
    <AddModelDialog
      v-model="showAddDialog"
      :preset="selectedPreset"
      :provider-types="modelStore.providerTypes"
      @saved="handleSaved"
    />

    <!-- 添加自定义模型对话框 -->
    <AddCustomModelDialog
      v-model="showCustomDialog"
      :provider-types="modelStore.providerTypes"
      @saved="handleSaved"
    />

    <!-- 编辑模型配置对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑模型配置" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="显示名称">
          <el-input v-model="editForm.displayName" placeholder="请输入显示名称" />
        </el-form-item>
        <el-form-item label="模型名称">
          <el-input v-model="editForm.modelName" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="Base URL">
          <el-input v-model="editForm.baseUrl" placeholder="请输入 API 基础地址" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="editForm.apiKey" type="password" show-password placeholder="留空则不修改" />
        </el-form-item>
        <el-form-item label="Temperature">
          <el-slider v-model="editForm.temperature" :min="0" :max="2" :step="0.1" show-input :show-input-controls="false" input-size="small" />
        </el-form-item>
        <el-form-item label="Max Tokens">
          <el-input-number v-model="editForm.maxTokens" :min="1" :max="200000" :step="256" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Top P">
          <el-slider v-model="editForm.topP" :min="0" :max="1" :step="0.1" show-input :show-input-controls="false" input-size="small" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="editForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editSubmitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { ModelConfig, ProviderPreset } from '@/api/model'
import { modelApi } from '@/api/model'
import AddCustomModelDialog from '@/components/AddCustomModelDialog.vue'
import AddModelDialog from '@/components/AddModelDialog.vue'
import { useModelStore } from '@/stores/model'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const modelStore = useModelStore()

const page = ref(1)
const pageSize = ref(20)
const providerFilter = ref('')

const showAddDialog = ref(false)
const showCustomDialog = ref(false)
const selectedPreset = ref<ProviderPreset | null>(null)

const editDialogVisible = ref(false)
const editSubmitting = ref(false)
const editForm = ref({
  id: '',
  providerId: '',
  displayName: '',
  modelName: '',
  baseUrl: '',
  apiKey: '',
  temperature: 0.7,
  maxTokens: 4096,
  topP: 1.0,
  isDefault: 0,
})

/** 获取供应商显示名称 */
function getProviderDisplayName(providerType: string): string {
  const type = modelStore.providerTypes.find((t) => t.code === providerType)
  return type?.displayName || providerType
}

/** 加载数据 */
function fetchData() {
  modelStore.fetchModelConfigs(page.value, pageSize.value, providerFilter.value || undefined)
}

/** 筛选变化 */
function handleFilterChange() {
  page.value = 1
  fetchData()
}

/** 打开添加预设模型对话框 */
function openAddModelDialog(preset: ProviderPreset) {
  selectedPreset.value = preset
  showAddDialog.value = true
}

/** 保存成功后刷新 */
function handleSaved() {
  fetchData()
  // 同时刷新供应商列表（可能新增了供应商）
  modelStore.fetchProviders()
}

/** 打开编辑对话框 */
async function openEditDialog(config: ModelConfig) {
  // 获取供应商详情以填充 baseUrl
  const provider = await modelApi.getProvider(config.providerId)
  editForm.value = {
    id: config.id,
    providerId: config.providerId,
    displayName: config.displayName || '',
    modelName: config.modelName || '',
    baseUrl: provider.data?.baseUrl || '',
    apiKey: '', // apiKey 不回显，留空表示不修改
    temperature: config.temperature,
    maxTokens: config.maxTokens,
    topP: config.topP,
    isDefault: config.isDefault,
  }
  editDialogVisible.value = true
}

/** 提交编辑 */
async function handleEditSubmit() {
  editSubmitting.value = true
  try {
    // 更新供应商信息（baseUrl、apiKey）
    const providerUpdate: any = {}
    if (editForm.value.baseUrl) {
      providerUpdate.baseUrl = editForm.value.baseUrl
    }
    if (editForm.value.apiKey) {
      providerUpdate.apiKey = editForm.value.apiKey
    }
    
    // 如果有供应商字段需要更新
    if (Object.keys(providerUpdate).length > 0) {
      await modelApi.updateProvider(editForm.value.providerId, providerUpdate)
    }

    // 更新模型配置
    await modelApi.updateModelConfig(editForm.value.id, {
      displayName: editForm.value.displayName || undefined,
      modelName: editForm.value.modelName || undefined,
      temperature: editForm.value.temperature,
      maxTokens: editForm.value.maxTokens,
      topP: editForm.value.topP,
      isDefault: editForm.value.isDefault,
    })
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchData()
  } finally {
    editSubmitting.value = false
  }
}

/** 连接测试 */
async function handleTestConnection(config: ModelConfig) {
  try {
    const result = await modelStore.testModelConfig(config.id)
    if (result.success) {
      ElMessage.success(`连接成功！延迟: ${result.latency || '-'}ms`)
    } else {
      ElMessage.error(`连接失败: ${result.message}`)
    }
  } catch (error) {
    console.error('连接测试出错:', error)
    ElMessage.error('连接测试出错')
  }
}

/** 删除模型 */
async function handleDelete(config: ModelConfig) {
  await ElMessageBox.confirm(
    `确定删除模型 "${config.displayName || config.modelName}" 吗？`,
    '确认删除',
    { type: 'warning' }
  )
  await modelStore.deleteModel(config.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(async () => {
  // 并行加载所有基础数据
  await Promise.all([
    modelStore.fetchProviderTypes(),
    modelStore.fetchPresets(),
    modelStore.fetchProviders(),
  ])
  fetchData()
})
</script>
