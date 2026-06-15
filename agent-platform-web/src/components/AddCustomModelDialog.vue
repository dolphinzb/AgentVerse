<template>
  <el-dialog v-model="visible" title="添加自定义模型" width="600px" @close="handleClose">
    <el-form :model="form" label-width="110px" :rules="rules" ref="formRef">
      <el-form-item label="API 类型" prop="providerType">
        <el-select v-model="form.providerType" placeholder="选择 API 类型" style="width: 100%">
          <el-option
            v-for="pt in providerTypes"
            :key="pt.code"
            :label="pt.displayName"
            :value="pt.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="API Key" prop="apiKey">
        <el-input v-model="form.apiKey" :type="showApiKey ? 'text' : 'password'" placeholder="请输入 API Key">
          <template #suffix>
            <el-icon style="cursor: pointer" @click="showApiKey = !showApiKey">
              <View v-if="!showApiKey" />
              <Hide v-else />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="Base URL" prop="baseUrl">
        <el-input v-model="form.baseUrl" placeholder="请输入 Base URL" />
      </el-form-item>
      <el-form-item label="模型名称" prop="modelName">
        <el-input v-model="form.modelName" placeholder="请输入模型名称" />
      </el-form-item>
      <el-form-item label="显示名称">
        <el-input v-model="form.displayName" placeholder="可选，模型的显示名称" />
      </el-form-item>
      <el-form-item label="自定义 Headers">
        <div style="width: 100%">
          <div v-for="(header, index) in customHeaders" :key="index" style="display: flex; gap: 8px; margin-bottom: 8px">
            <el-input v-model="header.key" placeholder="Header 名称" style="flex: 1" />
            <el-input v-model="header.value" placeholder="Header 值" style="flex: 1" />
            <el-button type="danger" link @click="customHeaders.splice(index, 1)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <el-button type="primary" link @click="customHeaders.push({ key: '', value: '' })">+ 添加 Header</el-button>
        </div>
      </el-form-item>
      <el-form-item label="Temperature">
        <el-slider v-model="form.temperature" :min="0" :max="2" :step="0.1" show-input :show-input-controls="false" input-size="small" />
      </el-form-item>
      <el-form-item label="Max Tokens">
        <el-input-number v-model="form.maxTokens" :min="1" :max="200000" :step="256" style="width: 100%" />
      </el-form-item>
      <el-form-item label="Top P">
        <el-slider v-model="form.topP" :min="0" :max="1" :step="0.1" show-input :show-input-controls="false" input-size="small" />
      </el-form-item>
      <el-form-item label="设为默认">
        <el-switch v-model="form.isDefault" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleTestConnection" :loading="testing">连接测试</el-button>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { View, Hide, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useModelStore } from '@/stores/model'
import { modelApi } from '@/api/model'
import type { ProviderType } from '@/api/model'

const props = defineProps<{
  modelValue: boolean
  providerTypes: ProviderType[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void
  (e: 'saved'): void
}>()

const modelStore = useModelStore()
const formRef = ref<FormInstance>()
const showApiKey = ref(false)
const testing = ref(false)
const saving = ref(false)

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const form = ref({
  providerType: '',
  apiKey: '',
  baseUrl: '',
  modelName: '',
  displayName: '',
  temperature: 0.7,
  maxTokens: 4096,
  topP: 1.0,
  isDefault: false,
})

const customHeaders = ref<{ key: string; value: string }[]>([])

const rules: FormRules = {
  providerType: [{ required: true, message: '请选择 API 类型', trigger: 'change' }],
  apiKey: [{ required: true, message: '请输入 API Key', trigger: 'blur' }],
  baseUrl: [{ required: true, message: '请输入 Base URL', trigger: 'blur' }],
  modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
}

/** 当 API 类型变化时，自动填充默认 Base URL */
watch(
  () => form.value.providerType,
  (val) => {
    if (val) {
      const type = props.providerTypes.find((t) => t.code === val)
      if (type && type.defaultBaseUrl) {
        form.value.baseUrl = type.defaultBaseUrl
      }
    }
  }
)

/** 连接测试 */
async function handleTestConnection() {
  if (!form.value.providerType) {
    ElMessage.warning('请先选择 API 类型')
    return
  }
  if (!form.value.apiKey) {
    ElMessage.warning('请先输入 API Key')
    return
  }
  testing.value = true
  try {
    const res = await modelApi.testConnectionDirect({
      providerType: form.value.providerType,
      apiKey: form.value.apiKey,
      baseUrl: form.value.baseUrl,
      modelName: form.value.modelName,
    })
    const result = res.data
    if (result.success) {
      ElMessage.success(result.message)
    } else {
      ElMessage.error(result.message)
    }
  } catch (e: any) {
    ElMessage.error('连接测试失败: ' + (e.message || '未知错误'))
  } finally {
    testing.value = false
  }
}

/** 保存 */
async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    // 构建自定义 Headers JSON 字符串
    const headersObj: Record<string, string> = {}
    for (const h of customHeaders.value) {
      if (h.key.trim() && h.value.trim()) {
        headersObj[h.key.trim()] = h.value.trim()
      }
    }
    const customHeadersStr = Object.keys(headersObj).length > 0 ? JSON.stringify(headersObj) : undefined

    await modelStore.addModel({
      providerType: form.value.providerType,
      apiKey: form.value.apiKey,
      baseUrl: form.value.baseUrl,
      customHeaders: customHeadersStr,
      modelName: form.value.modelName,
      displayName: form.value.displayName || undefined,
      maxTokens: form.value.maxTokens,
      temperature: form.value.temperature,
      topP: form.value.topP,
      isDefault: form.value.isDefault ? 1 : 0,
    })
    ElMessage.success('模型添加成功')
    emit('saved')
    handleClose()
  } finally {
    saving.value = false
  }
}

function handleClose() {
  visible.value = false
  formRef.value?.resetFields()
  customHeaders.value = []
}
</script>
