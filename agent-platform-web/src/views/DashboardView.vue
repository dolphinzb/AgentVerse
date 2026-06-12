<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>Agent 数量</span>
          </template>
          <div class="stat-content">
            <div class="stat-value">{{ totalAgents }}</div>
            <div class="stat-detail">
              <span>草稿: {{ stats.agentCount?.draft || 0 }}</span>
              <span>活跃: {{ stats.agentCount?.active || 0 }}</span>
              <span>归档: {{ stats.agentCount?.archived || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>活跃会话</span>
          </template>
          <div class="stat-content">
            <div class="stat-value">{{ stats.activeSessions || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>今日对话数</span>
          </template>
          <div class="stat-content">
            <div class="stat-value">{{ stats.todayChatCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>Token 用量</span>
          </template>
          <div class="stat-content">
            <div class="stat-value">{{ totalTokens }}</div>
            <div class="stat-detail">
              <span>输入: {{ stats.tokenUsage?.inputTokens || 0 }}</span>
              <span>输出: {{ stats.tokenUsage?.outputTokens || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getDashboardStats, type DashboardStats } from '@/api/dashboard'

const stats = ref<DashboardStats>({
  agentCount: {},
  activeSessions: 0,
  todayChatCount: 0,
  tokenUsage: {
    inputTokens: 0,
    outputTokens: 0
  }
})

const totalAgents = computed(() => {
  const count = stats.value.agentCount
  return (count.draft || 0) + (count.active || 0) + (count.archived || 0)
})

const totalTokens = computed(() => {
  const usage = stats.value.tokenUsage
  return (usage.inputTokens || 0) + (usage.outputTokens || 0)
})

async function fetchStats() {
  try {
    const res = await getDashboardStats()
    stats.value = (res as any).data || res
  } catch (e) {
    console.error('获取仪表盘数据失败', e)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-content {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.stat-detail {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
