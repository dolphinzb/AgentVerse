<template>
  <el-container style="height: 100vh">
    <el-aside :width="isCollapsed ? '64px' : '200px'" style="border-right: 1px solid #e6e6e6; transition: width 0.3s">
      <div style="padding: 16px; text-align: center; font-size: 18px; font-weight: bold; white-space: nowrap; overflow: hidden">
        {{ isCollapsed ? 'AV' : 'AgentVerse' }}
      </div>
      <el-menu :collapse="isCollapsed" :default-active="route.path" router>
        <el-menu-item index="/agents">
          <el-icon><Monitor /></el-icon>
          <template #title>Agent 管理</template>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>对话</template>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/admin/audit-logs">
          <el-icon><Document /></el-icon>
          <template #title>审计日志</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e6e6e6; padding: 0 16px">
        <el-icon style="cursor: pointer; font-size: 20px" @click="isCollapsed = !isCollapsed">
          <Fold v-if="!isCollapsed" />
          <Expand v-else />
        </el-icon>
        <div style="display: flex; align-items: center; gap: 12px">
          <span style="font-size: 14px; color: #606266">{{ userStore.user?.username || '' }}</span>
          <el-tag size="small" :type="userStore.isAdmin ? 'danger' : 'info'">
            {{ userStore.roleCode }}
          </el-tag>
          <el-button link type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Monitor, ChatDotRound, Fold, Expand, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>