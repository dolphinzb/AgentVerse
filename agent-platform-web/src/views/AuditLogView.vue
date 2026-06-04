<template>
  <div>
    <h2 style="margin: 0 0 16px">审计日志</h2>

    <el-form :inline="true" :model="filter" style="margin-bottom: 16px">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="filter.dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          style="width: 360px"
        />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="filter.action" placeholder="全部" clearable style="width: 160px">
          <el-option label="创建 Agent" value="agent:create" />
          <el-option label="更新 Agent" value="agent:update" />
          <el-option label="删除 Agent" value="agent:delete" />
          <el-option label="发布 Agent" value="agent:publish" />
          <el-option label="创建会话" value="chat:create" />
          <el-option label="删除会话" value="chat:delete" />
          <el-option label="注册" value="auth:register" />
          <el-option label="登出" value="auth:logout" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="filter.userId" placeholder="用户ID" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="records" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户" width="120" />
      <el-table-column prop="action" label="操作" width="140">
        <template #default="{ row }">
          <el-tag :type="actionTagType(row.action)" size="small">{{ row.action }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="target" label="目标" min-width="150" show-overflow-tooltip />
      <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="140" />
      <el-table-column prop="createdTime" label="时间" width="180" />
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 16px">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'

interface AuditLogRecord {
  id: number
  userId: number
  username: string
  action: string
  target: string
  detail: string
  ip: string
  createdTime: string
}

interface PageResult {
  records: AuditLogRecord[]
  total: number
  current: number
  size: number
}

const loading = ref(false)
const records = ref<AuditLogRecord[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const filter = reactive({
  dateRange: null as [string, string] | null,
  action: '',
  userId: '',
})

function actionTagType(action: string) {
  if (action.includes('create')) return 'success'
  if (action.includes('update')) return 'warning'
  if (action.includes('delete')) return 'danger'
  if (action.includes('publish')) return ''
  if (action.includes('auth')) return 'info'
  return 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      page: page.value,
      pageSize: pageSize.value,
    }
    if (filter.dateRange) {
      params.startTime = filter.dateRange[0]
      params.endTime = filter.dateRange[1]
    }
    if (filter.action) {
      params.action = filter.action
    }
    if (filter.userId) {
      params.userId = Number(filter.userId)
    }
    // request 拦截器已解包 ApiResponse，res 即为 { code, message, data, timestamp }
    const res = await request.get<any, any>('/v1/admin/audit-logs', { params })
    const pageData = res.data as PageResult
    records.value = pageData?.records || []
    total.value = pageData?.total || 0
  } catch (e) {
    console.error('Failed to fetch audit logs', e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function handleReset() {
  filter.dateRange = null
  filter.action = ''
  filter.userId = ''
  page.value = 1
  fetchData()
}

onMounted(fetchData)
</script>