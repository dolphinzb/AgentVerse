import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      component: Layout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/agents',
        },
        {
          path: 'agents',
          name: 'AgentList',
          component: () => import('@/views/AgentList.vue'),
        },
        {
          path: 'agents/:id',
          name: 'AgentDetail',
          component: () => import('@/views/AgentDetail.vue'),
        },
        {
          path: 'chat',
          name: 'Chat',
          component: () => import('@/views/ChatView.vue'),
        },
        {
          path: 'admin/audit-logs',
          name: 'AuditLog',
          component: () => import('@/views/AuditLogView.vue'),
          meta: { requiresAdmin: true },
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('agentverse_token')

  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.guest && token) {
    next('/')
    return
  }

  if (to.meta.requiresAdmin) {
    try {
      const raw = localStorage.getItem('agentverse_user')
      const user = raw ? JSON.parse(raw) : null
      if (!user || user.roleCode !== 'admin') {
        next('/')
        return
      }
    } catch {
      next('/')
      return
    }
  }

  next()
})

export default router