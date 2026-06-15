<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2 style="margin: 0">Agent 管理</h2>
      <div>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 140px; margin-right: 12px" @change="handleSearch">
          <el-option label="全部" value="" />
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="active" />
          <el-option label="归档" value="archived" />
        </el-select>
        <el-button v-permission="'agent:create'" type="primary" @click="showCreateDialog = true">创建 Agent</el-button>
      </div>
    </div>

    <el-table :data="agentStore.agents" v-loading="agentStore.loading" stripe>
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="currentVersion" label="当前版本" width="120" />
      <el-table-column prop="createdTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/agents/${row.id}`)">详情</el-button>
          <el-button v-permission="'agent:update'" link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button v-permission="'agent:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 16px">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="agentStore.total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑 Agent' : '创建 Agent'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入 Agent 名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="系统提示词">
          <el-input v-model="form.sysPrompt" type="textarea" :rows="4" placeholder="请输入系统提示词" />
        </el-form-item>
        <el-form-item label="最大迭代次数">
          <el-input-number v-model="form.maxIterations" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { AgentResponse } from '@/api/agent'
import { useAgentStore } from '@/stores/agent'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const agentStore = useAgentStore()

const page = ref(1)
const pageSize = ref(10)
const statusFilter = ref('')
const showCreateDialog = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref('')
const submitting = ref(false)

const form = ref({
  name: '',
  description: '',
  sysPrompt: '',
  maxIterations: 10,
})

function fetchData() {
  agentStore.fetchAgents(page.value, pageSize.value, statusFilter.value || undefined)
}

/**
 * Agent 状态 → 中文标签。
 * 与后端 AgentDefinitionService 中 status 字段保持一致：draft / active / archived。
 */
function statusLabel(status?: string): string {
  if (status === 'active') return '已发布'
  if (status === 'archived') return '归档'
  return '草稿'
}

/** Agent 状态 → el-tag 类型。 */
function statusTagType(status?: string): 'success' | 'warning' | 'info' {
  if (status === 'active') return 'success'
  if (status === 'archived') return 'warning'
  return 'info'
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function openEditDialog(agent: AgentResponse) {
  isEdit.value = true
  editingId.value = agent.id
  form.value = {
    name: agent.name,
    description: agent.description || '',
    sysPrompt: agent.sysPrompt || '',
    maxIterations: agent.maxIterations,
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.value.name) {
    ElMessage.warning('请输入 Agent 名称')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await agentStore.updateAgent(editingId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await agentStore.createAgent(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    resetForm()
    fetchData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(agent: AgentResponse) {
  await ElMessageBox.confirm(`确定删除 Agent "${agent.name}" 吗？`, '确认删除', { type: 'warning' })
  await agentStore.deleteAgent(agent.id)
  ElMessage.success('删除成功')
  fetchData()
}

function resetForm() {
  form.value = { name: '', description: '', sysPrompt: '', maxIterations: 10 }
  isEdit.value = false
  editingId.value = ''
}

onMounted(fetchData)

// Watch showCreateDialog to also open dialogVisible
import { watch } from 'vue'
watch(showCreateDialog, (val) => {
  if (val) {
    resetForm()
    dialogVisible.value = true
    showCreateDialog.value = false
  }
})
</script>
