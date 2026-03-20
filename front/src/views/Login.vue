<template>
  <div class="fixed inset-0 z-50 bg-bg-base flex items-center justify-center p-4">
    <div class="w-full max-w-3xl bg-white border border-gray-200 rounded-2xl shadow-lg overflow-hidden">
      <div class="grid md:grid-cols-2">
        <!-- 左侧品牌区（桌面端显示） -->
        <div class="hidden md:flex flex-col justify-between bg-primary text-white p-8 relative overflow-hidden">
          <!-- 背景装饰 -->
          <div class="absolute top-0 left-0 w-full h-full opacity-10 pointer-events-none">
            <i class="fa-solid fa-shield-halved text-[200px] absolute -right-10 -bottom-10"></i>
            <i class="fa-solid fa-file-contract text-[100px] absolute top-10 left-10"></i>
          </div>
          
          <div class="relative z-10">
            <h1 class="text-[28px] font-bold mb-4 flex items-center">
              <i class="fa-solid fa-shield-halved mr-3"></i>瓶安保
            </h1>
            <p class="text-white/90 text-[14px]">智能气瓶合同续约管理平台</p>
            <p class="text-white/70 text-[12px] mt-2">为安保公司提供高效、便捷的合同管理服务</p>
          </div>
          <div class="relative z-10 text-white/80 text-[12px]">
            <p class="mb-1"><i class="fa-solid fa-check mr-2"></i>电子合同签约与续约</p>
            <p class="mb-1"><i class="fa-solid fa-check mr-2"></i>实时到期提醒</p>
            <p><i class="fa-solid fa-check mr-2"></i>数据统计分析</p>
          </div>
        </div>

        <!-- 右侧表单区 -->
        <div class="p-6 md:p-8">
          <div class="flex items-center justify-between">
            <h2 class="text-[20px] font-bold text-gray-800">
              欢迎登录
            </h2>
            <div class="md:hidden text-primary text-[24px]">
              <i class="fa-solid fa-shield-halved"></i>
            </div>
          </div>

          <div class="mt-5 text-sm text-gray-600 mb-6">
            没有账号？ <router-link to="/register" class="text-primary hover:underline">立即注册</router-link>
          </div>

          <!-- 登录表单 -->
          <div class="mt-6">
            <!-- 登录方式切换 -->
            <div class="flex text-xs mb-4 border-b border-gray-100">
              <div class="flex flex-col pb-2 mr-4 cursor-pointer flex-1" @click="loginType = 'password'">
                <span :class="loginType === 'password' ? 'text-primary border-b-2 border-primary font-medium pb-2' : 'text-gray-500 pb-2'">
                  密码登录
                </span>
                
              </div>
              <div class="flex flex-col pb-2 cursor-pointer flex-1" @click="loginType = 'code'">
                <span :class="loginType === 'code' ? 'text-primary border-b-2 border-primary font-medium pb-2' : 'text-gray-500 pb-2'">
                  验证码登录
                </span>
                
              </div>
            </div>

            <form class="space-y-4" @submit.prevent="handleLogin">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  <span v-if="loginType === 'password'">账号</span>
                  <span v-else>手机号码</span>
                  <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <span class="absolute left-3 top-2.5 text-gray-400">
                    <i :class="loginType === 'password' ? 'fa-solid fa-user' : 'fa-solid fa-mobile-screen'"></i>
                  </span>
                  <input 
                    v-model="loginForm.phone" 
                    :type="loginType === 'password' ? 'text' : 'tel'"
                    :placeholder="loginType === 'password' ? '请输入用户名或手机号' : '请输入手机号'"
                    @blur="validatePhoneFormat"
                    class="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                </div>
                <div v-if="phoneError" class="text-red-500 text-xs mt-1">{{ phoneError }}</div>
              </div>
              
              <div v-if="loginType === 'password'">
                <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                <div class="relative">
                  <span class="absolute left-3 top-2.5 text-gray-400"><i class="fa-solid fa-lock"></i></span>
                  <input 
                    v-model="loginForm.password" 
                    :type="showLoginPassword ? 'text' : 'password'" 
                    placeholder="请输入密码"
                    class="w-full border border-gray-300 rounded-lg pl-9 pr-11 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                  <button 
                    type="button"
                    class="absolute right-3 top-2 text-gray-400 hover:text-gray-600"
                    @click="showLoginPassword = !showLoginPassword">
                    <i :class="showLoginPassword ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'"></i>
                  </button>
                </div>
              </div>

              <div v-else>
                <label class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
                <div class="flex space-x-2">
                  <div class="relative flex-1">
                    <span class="absolute left-3 top-2.5 text-gray-400"><i class="fa-solid fa-shield-cat"></i></span>
                    <input 
                      v-model="loginForm.code" 
                      type="text" 
                      placeholder="请输入验证码"
                      class="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                  </div>
                  <button 
                    type="button"
                    @click="sendCode"
                    :disabled="countdown > 0"
                    class="px-3 py-2.5 bg-gray-50 border border-gray-300 rounded-lg text-xs text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed min-w-[100px]">
                    {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
                  </button>
                </div>
              </div>

              <div class="flex items-center justify-between text-sm">
                <label class="flex items-center cursor-pointer">
                  <input type="checkbox" class="mr-2 rounded text-primary focus:ring-primary">
                  <span class="text-gray-600">记住我</span>
                </label>
                <a href="#" class="text-primary hover:underline">忘记密码？</a>
              </div>
              <button 
                type="submit"
                class="w-full bg-primary text-white py-2.5 rounded-lg hover:bg-blue-600 transition font-medium shadow-md hover:shadow-lg transform active:scale-[0.98]">
                登录
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { sendCode as sendCodeApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const loginType = ref('password') // 'password' or 'code'
const countdown = ref(0)
const phoneError = ref('')
const showLoginPassword = ref(false)

const loginForm = ref({

  phone: '',
  password: '',
  code: ''
})

// 验证手机号格式
function validatePhoneFormat() {
  if (loginType.value === 'code' && loginForm.value.phone) {
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(loginForm.value.phone)) {
      phoneError.value = '请输入有效的手机号'
    } else {
      phoneError.value = ''
    }
  } else {
    phoneError.value = ''
  }
}

async function sendCode() {
  if (!loginForm.value.phone) {
    alert('请输入手机号')
    return
  }
  if (countdown.value > 0) return
  
  try {
    await sendCodeApi({ phone: loginForm.value.phone })
    alert('验证码已发送')
    
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    alert('发送验证码失败: ' + (error.message || '未知错误'))
  }
}

async function handleLogin() {
  const input = loginForm.value.phone.trim()
  const phoneRegex = /^1[3-9]\d{9}$/
  const isPhoneNumber = phoneRegex.test(input)

  // 验证用户输入
  if (!input) {
    alert(loginType.value === 'password' ? '请输入用户名或手机号' : '请输入手机号')
    return
  }

  // 密码登录验证
  if (loginType.value === 'password' && !loginForm.value.password) {
    alert('请输入密码')
    return
  }

  // 验证码登录验证
  if (loginType.value === 'code') {
    if (!isPhoneNumber) {
      alert('请输入有效的手机号')
      return
    }
    if (!loginForm.value.code) {
      alert('请输入验证码')
      return
    }
  }

  try {
    if (loginType.value === 'code') {
      // 手机号验证码登录
      await userStore.loginWithCode({
        phone: input,
        code: loginForm.value.code
      })
    } else {
      // 密码登录 - 根据输入自动判断是用户名还是手机号
      if (isPhoneNumber) {
        // 手机号密码登录
        await userStore.loginWithPhoneAndPassword({
          phone: input,
          password: loginForm.value.password
        })
      } else {
        // 用户名密码登录
        await userStore.loginWithUserAndPassword({
          username: input,
          password: loginForm.value.password
        })
      }
    }
    router.push('/')
  } catch (error) {
    alert('登录失败: ' + (error.message || '未知错误'))
  }
}
</script>

<style scoped>
</style>
