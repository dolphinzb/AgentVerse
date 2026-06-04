import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type UserInfo } from '@/api/auth'

const TOKEN_KEY = 'agentverse_token'
const USER_KEY = 'agentverse_user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<UserInfo | null>(loadUser())
  const permissions = ref<string[]>(user.value?.permissions || [])

  function loadUser(): UserInfo | null {
    try {
      const raw = localStorage.getItem(USER_KEY)
      return raw ? JSON.parse(raw) : null
    } catch {
      return null
    }
  }

  function saveToken(t: string) {
    token.value = t
    localStorage.setItem(TOKEN_KEY, t)
  }

  function saveUser(u: UserInfo) {
    user.value = u
    permissions.value = u.permissions || []
    localStorage.setItem(USER_KEY, JSON.stringify(u))
  }

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.roleCode === 'admin')
  const roleCode = computed(() => user.value?.roleCode || '')

  function hasPermission(perm: string): boolean {
    return permissions.value.includes(perm)
  }

  async function login(username: string, password: string) {
    const res = await authApi.login({ username, password })
    saveToken(res.data.accessToken)
    saveUser(res.data.user)
  }

  async function register(username: string, password: string, email?: string) {
    await authApi.register({ username, password, email })
  }

  async function logout() {
    try {
      await authApi.logout()
    } finally {
      token.value = ''
      user.value = null
      permissions.value = []
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }

  async function fetchMe() {
    const res = await authApi.me()
    saveUser(res.data)
  }

  return {
    token,
    user,
    permissions,
    isLoggedIn,
    isAdmin,
    roleCode,
    hasPermission,
    login,
    register,
    logout,
    fetchMe,
    saveToken,
    saveUser,
  }
})