<template>
  <div class="bg-bg-base text-gray-800 h-screen flex flex-col text-sm">
    <!-- 全局顶部导航 -->
    <header class="h-[64px] bg-[#001529] text-white flex items-center justify-between px-6 shadow-md z-30 flex-shrink-0">
      <div class="flex items-center">
        <div class="text-[20px] font-bold mr-8 flex items-center">
          <i class="fa-solid fa-shield-halved mr-3 text-primary"></i>瓶安保后台
        </div>
        <!-- 显示当前角色 -->
        <div class="bg-white/10 rounded px-4 py-1.5 text-xs text-gray-300">
          <i class="fa-solid fa-user-tag mr-2"></i>
          <span v-if="currentRole === 'ADMIN'">管理员</span>
          <span v-else-if="currentRole === 'USER'">单位用户</span>
          <span v-else-if="currentRole === 'UNIT_ADMIN'">单位管理员</span>
          <span v-else>{{ currentRole }}</span>
        </div>
      </div>
      <div class="flex items-center space-x-6">
        <!-- 消息通知 -->
        <div class="relative group cursor-pointer">
          <button @click="goToMessage" class="relative">
            <i class="fa-regular fa-bell text-[18px]"></i>
            <span v-if="unreadCount > 0" class="absolute -top-2 -right-2 w-5 h-5 bg-error text-white text-[10px] rounded-full flex items-center justify-center font-bold">
              {{ unreadCount }}
            </span>
          </button>
        </div>
        
        <!-- 用户信息 & 个人中心 -->
        <div class="relative group">
          <div class="flex items-center cursor-pointer" @click="toggleUserMenu">
            <span class="opacity-80 flex items-center">
              <span>{{ userName }}</span>
              <span v-if="userUnit" class="mx-1">·</span>
              <span v-if="userUnit" class="text-xs">{{ userUnit }}</span>
            </span>
            <div class="w-8 h-8 bg-primary rounded-full flex items-center justify-center text-xs text-white ml-2 font-bold">A</div>
            <i class="fa-solid fa-chevron-down text-xs ml-2 opacity-50"></i>
          </div>
          
          <!-- 下拉菜单 -->
          <div v-show="showUserMenu" class="absolute right-0 top-12 bg-white border border-gray-200 rounded shadow-lg z-40 w-48">
            <div @click="goToProfile" class="px-4 py-3 hover:bg-gray-50 cursor-pointer flex items-center border-b text-gray-700">
              <i class="fa-solid fa-user mr-2 text-primary"></i> 个人中心
            </div>
            <div @click="goToMessage" class="px-4 py-3 hover:bg-gray-50 cursor-pointer flex items-center border-b text-gray-700">
              <i class="fa-solid fa-envelope mr-2 text-primary"></i> 我的消息
              <span v-if="unreadCount > 0" class="ml-auto bg-error text-white text-xs px-1.5 rounded-full">{{ unreadCount }}</span>
            </div>
            <div @click="handleLogout" class="px-4 py-3 hover:bg-gray-50 cursor-pointer flex items-center text-error">
              <i class="fa-solid fa-right-from-bracket mr-2"></i> 退出登录
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="flex flex-1 overflow-hidden">
      <!-- 侧边栏 -->
      <aside class="w-[220px] bg-white border-r border-gray-200 flex flex-col shadow-sm z-10 overflow-y-auto">
        <nav class="py-4 space-y-1">
          <!-- 首页概览 -->
          <router-link 
            to="/dashboard" 
            class="px-6 py-3 text-gray-600 hover:text-primary cursor-pointer transition flex items-center"
            active-class="text-primary bg-blue-50">
            <i class="fa-solid fa-chart-line w-5 mr-2"></i> 首页概览
          </router-link>
          
          <!-- 合同管理 -->
          <router-link 
            to="/contract" 
            class="px-6 py-3 text-gray-600 hover:text-primary cursor-pointer transition flex items-center"
            active-class="text-primary bg-blue-50">
            <i class="fa-solid fa-file-contract w-5 mr-2"></i> 合同管理
          </router-link>
          
          <!-- 气瓶管理 -->
          <router-link 
            to="/cylinder" 
            class="px-6 py-3 text-gray-600 hover:text-primary cursor-pointer transition flex items-center"
            active-class="text-primary bg-blue-50">
            <i class="fa-solid fa-fire-extinguisher w-5 mr-2"></i> 气瓶管理
          </router-link>
          
          <!-- 续约管理 -->
          <router-link 
            to="/renewal" 
            class="px-6 py-3 text-gray-600 hover:text-primary cursor-pointer transition flex items-center"
            active-class="text-primary bg-blue-50">
            <i class="fa-solid fa-clock-rotate-left w-5 mr-2"></i> 续约管理
          </router-link>
          
          <!-- 系统管理 (仅管理员可见) -->
          <div v-if="currentRole === 'ADMIN'">
            <div @click="toggleSubmenu" class="px-6 py-3 text-gray-600 hover:text-primary hover:bg-gray-50 cursor-pointer flex items-center justify-between transition">
              <span class="flex items-center">
                <i class="fa-solid fa-cog w-5 mr-2"></i> 系统管理
              </span>
              <i :class="showSubmenu ? 'fa-chevron-down' : 'fa-chevron-right'" class="fa-solid text-xs"></i>
            </div>
            <!-- 二级菜单 -->
            <div v-show="showSubmenu" class="bg-gray-50">
              <router-link 
                to="/unit-mgr" 
                class="px-6 py-2 pl-12 text-gray-600 hover:text-primary cursor-pointer transition flex items-center text-xs"
                active-class="text-primary">
                单位管理
              </router-link>
              <router-link 
                to="/user-mgr" 
                class="px-6 py-2 pl-12 text-gray-600 hover:text-primary cursor-pointer transition flex items-center text-xs"
                active-class="text-primary">
                用户管理
              </router-link>
            </div>
          </div>

          <!-- 个人中心 & 消息 -->
          <div class="border-t border-gray-200 mt-4 pt-4">
            <router-link 
              to="/profile" 
              class="px-6 py-3 text-gray-600 hover:text-primary cursor-pointer transition flex items-center"
              active-class="text-primary bg-blue-50">
              <i class="fa-solid fa-user w-5 mr-2"></i> 个人中心
            </router-link>
            <router-link 
              to="/message" 
              class="px-6 py-3 text-gray-600 hover:text-primary cursor-pointer transition flex items-center relative"
              active-class="text-primary bg-blue-50">
              <i class="fa-solid fa-envelope w-5 mr-2"></i> 我的消息
              <span v-if="unreadCount > 0" class="ml-auto bg-error text-white text-xs px-1.5 rounded-full">{{ unreadCount }}</span>
            </router-link>
          </div>
        </nav>
      </aside>

      <!-- 主内容区 -->
      <main class="flex-1 overflow-y-auto p-6 relative">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const userStore = useUserStore()
const { currentRole, unreadCount, userName, userUnit } = storeToRefs(userStore)

const showUserMenu = ref(false)
const showSubmenu = ref(false)

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

function toggleSubmenu() {
  showSubmenu.value = !showSubmenu.value
}

function goToProfile() {
  router.push('/profile')
  showUserMenu.value = false
}

function goToMessage() {
  router.push('/message')
  showUserMenu.value = false
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

// 点击外部关闭菜单
document.addEventListener('click', (e) => {
  if (!e.target.closest('.group')) {
    showUserMenu.value = false
  }
})
</script>

<style scoped>
</style>
