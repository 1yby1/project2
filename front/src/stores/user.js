import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { 
  loginWithUserAndPassword as loginUserApi, 
  loginWithPhoneAndPassword as loginPhoneApi,
  loginwithCode as loginCodeApi,
  register as registerApi 
} from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const isLoggedIn = ref(!!localStorage.getItem('token'))
  const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const currentRole = ref(currentUser.value?.role || 'USER') // 'ADMIN' or 'USER'
  const unreadCount = ref(3)

  const userName = computed(() => currentUser.value?.username || '游客')
  const userUnit = computed(() => currentUser.value?.unit || '')

  function handleLoginSuccess(data) {
    const token = data.token
    console.log('received token:', data)
    // 后端返回的用户信息
    const user = {
      username: data.username, 
      role: data.roleName,  // 后端返回 'ADMIN' 或 'USER'
      unitId: data.unitId,
      unit: data.unitName
    }
    
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    
    isLoggedIn.value = true
    currentUser.value = user
    currentRole.value = data.roleName  // 使用后端返回的角色名
    
    return data
  }

  async function loginWithUserAndPassword(loginForm) {
    const response = await loginUserApi(loginForm)
    console.log('login response data:', response)
    return handleLoginSuccess(response.data)
  }

  async function loginWithPhoneAndPassword(loginForm) {
    const data = await loginPhoneApi(loginForm)
    return handleLoginSuccess(data)
  }

  async function loginWithCode(loginForm) {
    const data = await loginCodeApi(loginForm)
    return handleLoginSuccess(data)
  }

  async function register(registerForm) {
    const data = await registerApi(registerForm)
    return data
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    isLoggedIn.value = false
    currentUser.value = null
    currentRole.value = 'USER'
  }

  function markAllRead() {
    unreadCount.value = 0
  }

  return {
    isLoggedIn,
    currentUser,
    currentRole,
    unreadCount,
    userName,
    userUnit,
    loginWithUserAndPassword,
    loginWithPhoneAndPassword,
    loginWithCode,
    register,
    logout,
    markAllRead
  }
})
