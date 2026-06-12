<template>
  <div>
    <h2 style="margin: 0 0 16px 0">用量统计</h2>

    <el-table :data="modelStore.stats" v-loading="modelStore.loading" stripe>
      <el-table-column prop="modelName" label="模型名称" min-width="160" />
      <el-table-column prop="providerName" label="供应商" width="140" />
      <el-table-column prop="callCount" label="调用次数" width="120" />
      <el-table-column prop="totalInputTokens" label="输入 Tokens" width="140">
        <template #default="{ row }">
          {{ formatNumber(row.totalInputTokens) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalOutputTokens" label="输出 Tokens" width="140">
        <template #default="{ row }">
          {{ formatNumber(row.totalOutputTokens) }}
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!modelStore.loading && modelStore.stats.length === 0" description="暂无用量数据" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useModelStore } from '@/stores/model'

const modelStore = useModelStore()

function formatNumber(num: number): string {
  if (num === undefined || num === null) return '0'
  return num.toLocaleString()
}

onMounted(async () => {
  await modelStore.fetchStats()
})
</script>
