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
              注册账号
            </h2>
            <div class="md:hidden text-primary text-[24px]">
              <i class="fa-solid fa-shield-halved"></i>
            </div>
          </div>

          <div class="mt-5 text-sm text-gray-600 mb-6">
            已有账号？ <router-link to="/login" class="text-primary hover:underline">立即登录</router-link>
          </div>

          <!-- 注册表单 -->
          <div class="mt-6">
            <form class="space-y-4" @submit.prevent="handleRegister">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                <input
                    v-model="registerForm.username"
                    type="text"
                    placeholder="请输入用户名"
                    class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">手机号码</label>
                <input
                    v-model="registerForm.phone"
                    type="tel"
                    placeholder="请输入手机号"
                    class="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
                <div class="flex space-x-2">
                  <input
                      v-model="registerForm.code"
                      type="text"
                      placeholder="请输入验证码"
                      class="flex-1 border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                  <button
                      type="button"
                      @click="sendCode"
                      :disabled="countdown > 0"
                      class="px-3 py-2.5 bg-gray-50 border border-gray-300 rounded-lg text-xs text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed min-w-[110px]">
                    {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
                  </button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">设置密码</label>
                <div class="relative">
                  <input
                      v-model="registerForm.password"
                      :type="showRegisterPassword ? 'text' : 'password'"
                      placeholder="设置登录密码"
                      class="w-full border border-gray-300 rounded-lg pr-11 px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                  <button
                      type="button"
                      class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                      @click="showRegisterPassword = !showRegisterPassword">
                    <i :class="showRegisterPassword ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'"></i>
                  </button>
                </div>
              </div>

              <div class="flex items-start text-xs text-gray-500">
                <input type="checkbox" class="mt-0.5 mr-2">
                <span>我已阅读并同意 <a href="#" class="text-primary hover:underline">《用户协议》</a> 和 <a href="#" class="text-primary hover:underline">《隐私政策》</a></span>
              </div>
              <button
                  type="submit"
                  class="w-full bg-primary text-white py-2.5 rounded-lg hover:bg-blue-600 transition font-medium">
                注册
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

const registerForm = ref({
  username: '',
  phone: '',
  password: '',
  code: ''
})

const countdown = ref(0)
const showRegisterPassword = ref(false)

async function sendCode() {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!registerForm.value.phone || !phoneRegex.test(registerForm.value.phone)) {
    alert('请输入有效的手机号')
    return
  }
  if (countdown.value > 0) return

  try {
    await sendCodeApi({ phone: registerForm.value.phone })
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

async function handleRegister() {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!registerForm.value.username || !registerForm.value.phone || !registerForm.value.password || !registerForm.value.code) {
    alert('请填写完整信息')
    return
  }

  if (!phoneRegex.test(registerForm.value.phone)) {
    alert('请输入有效的手机号')
    return
  }

  try {
    await userStore.register({
      username: registerForm.value.username,
      phone: registerForm.value.phone,
      password: registerForm.value.password,
      code: registerForm.value.code
    })
    alert('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    alert('注册失败: ' + (error.message || '未知错误'))
  }
}
</script>

<style scoped>
</style>
