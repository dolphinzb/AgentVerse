<template>
  <el-dialog v-model="visible" :title="`添加模型 - ${preset?.displayName || ''}`" width="560px" @close="handleClose">
    <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
      <el-form-item label="供应商">
        <el-input :model-value="preset?.displayName" disabled />
      </el-form-item>
      <el-form-item label="描述">
        <el-input :model-value="preset?.description" disabled type="textarea" :rows="2" />
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
        <el-select
          v-model="form.modelName"
          filterable
          allow-create
          default-first-option
          placeholder="选择或输入模型名称"
          style="width: 100%"
        >
          <el-option
            v-for="model in recommendedModels"
            :key="model"
            :label="model"
            :value="model"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="显示名称">
        <el-input v-model="form.displayName" placeholder="可选，模型的显示名称" />
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
import { View, Hide } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useModelStore } from '@/stores/model'
import { modelApi } from '@/api/model'
import type { ProviderPreset, ProviderType } from '@/api/model'

const props = defineProps<{
  modelValue: boolean
  preset: ProviderPreset | null
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
  apiKey: '',
  baseUrl: '',
  modelName: '',
  displayName: '',
  temperature: 0.7,
  maxTokens: 4096,
  topP: 1.0,
  isDefault: false,
})

const rules: FormRules = {
  apiKey: [{ required: true, message: '请输入 API Key', trigger: 'blur' }],
  baseUrl: [{ required: true, message: '请输入 Base URL', trigger: 'blur' }],
  modelName: [{ required: true, message: '请选择或输入模型名称', trigger: 'change' }],
}

/** 推荐模型列表 */
const recommendedModels = computed(() => {
  if (!props.preset) return []
  const type = props.providerTypes.find((t) => t.code === props.preset!.providerType)
  return type?.recommendedModels || []
})

/** 当 preset 变化时，重置表单并填充默认值 */
watch(
  () => props.preset,
  (val) => {
    if (val) {
      form.value = {
        apiKey: '',
        baseUrl: val.defaultBaseUrl || '',
        modelName: '',
        displayName: '',
        temperature: 0.7,
        maxTokens: 4096,
        topP: 1.0,
        isDefault: false,
      }
    }
  },
  { immediate: true }
)

/** 连接测试 */
async function handleTestConnection() {
  if (!form.value.apiKey) {
    ElMessage.warning('请先输入 API Key')
    return
  }
  if (!form.value.modelName) {
    ElMessage.warning('请先选择或输入模型名称')
    return
  }
  testing.value = true
  try {
    const res = await modelApi.testConnectionDirect({
      providerType: props.preset!.providerType,
      apiKey: form.value.apiKey,
      baseUrl: form.value.baseUrl || undefined,
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
    await modelStore.addModel({
      providerType: props.preset!.providerType,
      apiKey: form.value.apiKey,
      baseUrl: form.value.baseUrl,
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
}
</script>
