<template>
  <div style="display: flex; height: calc(100vh - 60px)">
    <!-- 会话列表 -->
    <div style="width: 260px; border-right: 1px solid #e6e6e6; display: flex; flex-direction: column">
      <div style="padding: 12px; border-bottom: 1px solid #e6e6e6">
        <el-button v-permission="'chat:create'" type="primary" style="width: 100%" @click="showNewSessionDialog = true">开始新对话</el-button>
      </div>
      <div style="flex: 1; overflow-y: auto">
        <div
          v-for="session in chatStore.sessions"
          :key="session.sessionId"
          :class="['session-item', { active: session.sessionId === chatStore.currentSessionId }]"
          @click="selectSession(session.sessionId)"
          @contextmenu.prevent="handleContextMenu($event, session.sessionId)"
        >
          <div style="font-weight: 500; font-size: 14px">{{ session.agentName }}</div>
          <div style="font-size: 12px; color: #999; margin-top: 4px">
            {{ session.messages.length }} 条消息
          </div>
        </div>
      </div>
    </div>

    <!-- 对话区域 -->
    <div style="flex: 1; display: flex; flex-direction: column" v-if="currentSession">
      <div style="padding: 12px 16px; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between">
        <span style="font-weight: 500">{{ currentSession.agentName }}</span>
        <div style="display: flex; align-items: center; gap: 12px">
          <el-switch v-model="streamMode" active-text="流式" inactive-text="同步" />
          <el-button v-if="chatStore.streaming" type="danger" size="small" @click="handleInterrupt">中断</el-button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div ref="messageContainer" style="flex: 1; overflow-y: auto; padding: 16px">
        <div
          v-for="(msg, idx) in currentSession.messages"
          :key="idx"
          :style="{
            display: 'flex',
            justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start',
            marginBottom: '16px',
          }"
        >
          <div
            :style="{
              maxWidth: '70%',
              padding: '10px 14px',
              borderRadius: '8px',
              backgroundColor: msg.role === 'user' ? '#409eff' : '#f4f4f5',
              color: msg.role === 'user' ? '#fff' : '#333',
              whiteSpace: 'pre-wrap',
            }"
          >{{ msg.content }}</div>
        </div>
      </div>

      <!-- 输入框 -->
      <div style="padding: 12px 16px; border-top: 1px solid #e6e6e6; display: flex; gap: 8px">
        <el-input
          v-model="inputMessage"
          placeholder="输入消息..."
          @keyup.enter="handleSend"
          :disabled="chatStore.streaming"
        />
        <el-button type="primary" @click="handleSend" :disabled="!inputMessage.trim() || chatStore.streaming">发送</el-button>
      </div>
    </div>

    <!-- 无会话提示 -->
    <div v-else style="flex: 1; display: flex; align-items: center; justify-content: center; color: #999">
      <div style="text-align: center">
        <el-icon :size="48"><ChatDotRound /></el-icon>
        <p style="margin-top: 12px">选择或创建一个对话</p>
      </div>
    </div>

    <!-- 新建会话对话框 -->
    <el-dialog v-model="showNewSessionDialog" title="开始新对话" width="400px">
      <el-form label-width="80px">
        <el-form-item label="选择 Agent">
          <el-select v-model="selectedAgentId" placeholder="请选择 Agent" style="width: 100%">
            <el-option
              v-for="agent in agentStore.agents"
              :key="agent.id"
              :label="agent.name"
              :value="agent.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewSessionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSession" :disabled="!selectedAgentId">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { useAgentStore } from '@/stores/agent'
import { useChatStore } from '@/stores/chat'
import { ChatDotRound } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const agentStore = useAgentStore()
const chatStore = useChatStore()

const inputMessage = ref('')
const streamMode = ref(true)
const showNewSessionDialog = ref(false)
const selectedAgentId = ref('')
const messageContainer = ref<HTMLElement | null>(null)

const currentSession = computed(() => chatStore.getCurrentSession())

function selectSession(sessionId: string) {
  chatStore.currentSessionId = sessionId
  chatStore.fetchSessionHistory(sessionId)
}

async function handleCreateSession() {
  if (!selectedAgentId.value) return
  const agent = agentStore.agents.find(a => a.id === selectedAgentId.value)
  await chatStore.createSession(selectedAgentId.value, agent?.name || 'Agent')
  showNewSessionDialog.value = false
  selectedAgentId.value = ''
}

async function handleSend() {
  console.log('handleSend called, streamMode:', streamMode.value)
  if (!inputMessage.value.trim() || !chatStore.currentSessionId) return
  const content = inputMessage.value.trim()
  inputMessage.value = ''
  console.log('streaming before:', chatStore.streaming)

  if (streamMode.value) {
    console.log('Using streamMessage')
    await chatStore.streamMessage(chatStore.currentSessionId, content)
  } else {
    console.log('Using sendMessage')
    await chatStore.sendMessage(chatStore.currentSessionId, content)
  }
  console.log('streaming after:', chatStore.streaming)
}

async function handleInterrupt() {
  if (chatStore.currentSessionId) {
    await chatStore.interruptSession(chatStore.currentSessionId)
  }
}

async function handleContextMenu(_event: MouseEvent, sessionId: string) {
  try {
    await ElMessageBox.confirm('确定删除该会话吗？', '确认删除', { type: 'warning' })
    await chatStore.deleteSession(sessionId)
  } catch { /* cancelled */ }
}

async function scrollToBottom() {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

// Auto-scroll when messages change
watch(() => currentSession.value?.messages?.length, () => scrollToBottom())

// Auto-scroll when streaming content updates
watch(() => currentSession.value?.messages, () => scrollToBottom(), { deep: true })

onMounted(() => {
  // 对话页只能选已发布（active）的 Agent，过滤掉草稿/归档
  agentStore.fetchAgents(1, 100, 'active')
  // If navigated with agentId query param, auto-create session
  const agentId = route.query.agentId as string
  if (agentId) {
    selectedAgentId.value = agentId
    handleCreateSession()
  }
})
</script>

<style scoped>
.session-item {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}
.session-item:hover {
  background-color: #f5f7fa;
}
.session-item.active {
  background-color: #ecf5ff;
}
</style>
