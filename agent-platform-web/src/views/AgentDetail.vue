<template>
  <div v-loading="agentStore.loading">
    <div v-if="agentStore.currentAgent" style="max-width: 800px">
      <div style="display: flex; align-items: center; margin-bottom: 20px">
        <el-button @click="router.push('/agents')" :icon="ArrowLeft" text>返回列表</el-button>
        <h2 style="margin: 0 0 0 12px">{{ agent.name }}</h2>
        <el-tag :type="agent.status === 'published' ? 'success' : 'info'" style="margin-left: 12px">
          {{ agent.status === 'published' ? '已发布' : '草稿' }}
        </el-tag>
      </div>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ agent.id }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ agent.name }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ agent.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="系统提示词" :span="2">
          <pre style="margin: 0; white-space: pre-wrap">{{ agent.sysPrompt || '-' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="最大迭代次数">{{ agent.maxIterations }}</el-descriptions-item>
        <el-descriptions-item label="工作区模式">{{ agent.workspaceMode }}</el-descriptions-item>
        <el-descriptions-item label="当前版本">{{ agent.currentVersion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ agent.createdTime }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px; display: flex; gap: 12px">
        <el-button v-permission="'agent:publish'" type="primary" @click="showPublishDialog = true">发布版本</el-button>
        <el-button @click="handleShowVersions">版本历史</el-button>
        <el-button v-permission="'chat:create'" @click="router.push({ path: '/chat', query: { agentId: agent.id } })">开始对话</el-button>
      </div>
    </div>

    <!-- 发布版本对话框 -->
    <el-dialog v-model="showPublishDialog" title="发布版本" width="450px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="版本号" required>
          <el-input v-model="publishForm.version" placeholder="例如：v1.0.0" />
        </el-form-item>
        <el-form-item label="变更日志">
          <el-input v-model="publishForm.changelog" type="textarea" :rows="3" placeholder="请输入变更日志" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePublish" :loading="publishing">发布</el-button>
      </template>
    </el-dialog>

    <!-- 版本历史对话框 -->
    <el-dialog v-model="showVersionsDialog" title="版本历史" width="600px">
      <el-table :data="agentStore.versions" stripe>
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="changelog" label="变更日志" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleRollback(row.version)">回滚</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useAgentStore } from '@/stores/agent'

const route = useRoute()
const router = useRouter()
const agentStore = useAgentStore()

const agent = computed(() => agentStore.currentAgent!)
const showPublishDialog = ref(false)
const showVersionsDialog = ref(false)
const publishing = ref(false)

const publishForm = ref({
  version: '',
  changelog: '',
})

async function handlePublish() {
  if (!publishForm.value.version) {
    ElMessage.warning('请输入版本号')
    return
  }
  publishing.value = true
  try {
    await agentStore.publishVersion(agent.value.id, publishForm.value.version, publishForm.value.changelog)
    ElMessage.success('发布成功')
    showPublishDialog.value = false
    publishForm.value = { version: '', changelog: '' }
    agentStore.fetchAgent(agent.value.id)
  } finally {
    publishing.value = false
  }
}

async function handleShowVersions() {
  await agentStore.fetchVersions(agent.value.id)
  showVersionsDialog.value = true
}

async function handleRollback(version: string) {
  await ElMessageBox.confirm(`确定回滚到版本 ${version} 吗？`, '确认回滚', { type: 'warning' })
  await agentStore.rollbackVersion(agent.value.id, version)
  ElMessage.success('回滚成功')
  agentStore.fetchAgent(agent.value.id)
  agentStore.fetchVersions(agent.value.id)
}

onMounted(() => {
  const id = route.params.id as string
  agentStore.fetchAgent(id)
})
</script>
