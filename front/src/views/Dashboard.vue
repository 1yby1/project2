<template>
  <div class="fade-in">
    <!-- 欢迎语 & 状态条 -->
    <div class="mb-6 flex justify-between items-end">
      <div>
        <h2 class="text-[24px] font-bold text-gray-800">欢迎回来，{{ userName }}！</h2>
        <p class="text-gray-500 text-xs mt-1">今天是 {{ currentDate }}</p>
      </div>
      <div class="text-right">
        <p class="text-xs text-gray-500">
          {{ isAdmin ? '管理员身份' : '单位：' + (userUnit || '未绑定') }}
        </p>
      </div>
    </div>

    <!-- 关键数据卡片 -->
    <div class="grid grid-cols-4 gap-6 mb-8">
      <!-- 通用卡片（所有用户可见） -->
      <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200 cursor-pointer hover:shadow-md transition" @click="handleCardClick('validContracts')">
        <p class="text-gray-500 text-xs mb-1">有效合同数</p>
        <p class="text-[28px] font-bold text-primary">{{ stats.validContracts }}</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200 cursor-pointer hover:shadow-md transition" @click="handleCardClick('pendingRenewals')">
        <p class="text-gray-500 text-xs mb-1">待处理续约</p>
        <p class="text-[28px] font-bold text-warning">{{ stats.pendingRenewals }}</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
        <p class="text-gray-500 text-xs mb-1">本月新增合同</p>
        <p class="text-[28px] font-bold text-success">{{ stats.newContracts }}</p>
      </div>
      <!-- 倒计时卡片 (高亮) -->
      <div class="bg-primary bg-opacity-5 p-6 rounded-lg border border-primary border-opacity-20 relative overflow-hidden group cursor-pointer" @click="showRenewalModal = true">
        <p class="text-white text-xs mb-1 font-medium">最近即将到期</p>
        <p class="text-[28px] font-bold text-white">{{ stats.expiringDays }} 天</p>
        <div class="absolute -right-4 -bottom-4 text-white opacity-10 text-[64px]">
          <i class="fa-solid fa-clock"></i>
        </div>
      </div>

      <!-- 管理员专属卡片 -->
      <template v-if="isAdmin">
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200 cursor-pointer hover:shadow-md transition" @click="handleCardClick('totalUsers')">
          <p class="text-gray-500 text-xs mb-1">总用户数</p>
          <p class="text-[28px] font-bold text-primary">{{ stats.totalUsers }}</p>
        </div>
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200 cursor-pointer hover:shadow-md transition" @click="handleCardClick('totalUnits')">
          <p class="text-gray-500 text-xs mb-1">总单位数</p>
          <p class="text-[28px] font-bold text-info">{{ stats.totalUnits }}</p>
        </div>
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <p class="text-gray-500 text-xs mb-1">本月新增用户</p>
          <p class="text-[28px] font-bold text-success">{{ stats.newUsersThisMonth }}</p>
        </div>
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200 cursor-pointer hover:shadow-md transition" @click="handleCardClick('pendingApprovals')">
          <p class="text-gray-500 text-xs mb-1">待审批事项</p>
          <p class="text-[28px] font-bold text-warning">{{ stats.pendingApprovals }}</p>
        </div>
      </template>
    </div>

    <!-- 核心区域：根据用户角色显示不同内容 -->
    <div class="mb-8">
      <!-- 普通用户：我的服务 -->
      <template v-if="!isAdmin">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-[16px] font-bold">我的服务</h3>
          <a href="#" class="text-primary text-xs hover:underline">查看全部 →</a>
        </div>

        <div class="grid grid-cols-3 gap-6">
          <div v-for="service in services" :key="service.id" class="bg-white rounded-lg p-6 shadow-sm border border-gray-200 hover:shadow-md transition cursor-pointer">
            <div class="flex items-start justify-between mb-4">
              <div>
                <div class="text-[48px] mb-2" :style="{color: service.color}">
                  <i :class="service.icon"></i>
                </div>
                <h4 class="font-bold text-gray-800 mb-1">{{ service.name }}</h4>
                <p class="text-xs text-gray-500">{{ service.desc }}</p>
              </div>
              <span :class="service.statusClass" class="px-2 py-0.5 rounded text-xs">{{ service.status }}</span>
            </div>
            <div class="border-t pt-3 text-xs text-gray-600 space-y-1">
              <p>合同编号：{{ service.contractNo }}</p>
              <p>到期时间：{{ service.expireDate }}</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 管理员：管理功能快速入口 -->
      <template v-else>
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-[16px] font-bold">管理功能</h3>
          <a href="#" class="text-primary text-xs hover:underline">查看全部 →</a>
        </div>

        <div class="grid grid-cols-3 gap-6">
          <div class="bg-white rounded-lg p-6 shadow-sm border border-gray-200 hover:shadow-md transition cursor-pointer" @click="handleCardClick('userManagement')">
            <div class="flex items-start justify-between mb-4">
              <div>
                <div class="text-[48px] mb-2 text-primary">
                  <i class="fa-solid fa-users"></i>
                </div>
                <h4 class="font-bold text-gray-800 mb-1">用户管理</h4>
                <p class="text-xs text-gray-500">管理系统用户</p>
              </div>
              <span class="bg-blue-100 text-primary border border-blue-200 px-2 py-0.5 rounded text-xs">查看</span>
            </div>
            <div class="border-t pt-3 text-xs text-gray-600">
              <p>总用户数：{{ stats.totalUsers }}</p>
            </div>
          </div>

          <div class="bg-white rounded-lg p-6 shadow-sm border border-gray-200 hover:shadow-md transition cursor-pointer" @click="handleCardClick('unitManagement')">
            <div class="flex items-start justify-between mb-4">
              <div>
                <div class="text-[48px] mb-2 text-info">
                  <i class="fa-solid fa-building"></i>
                </div>
                <h4 class="font-bold text-gray-800 mb-1">单位管理</h4>
                <p class="text-xs text-gray-500">管理注册单位</p>
              </div>
              <span class="bg-blue-100 text-primary border border-blue-200 px-2 py-0.5 rounded text-xs">查看</span>
            </div>
            <div class="border-t pt-3 text-xs text-gray-600">
              <p>总单位数：{{ stats.totalUnits }}</p>
            </div>
          </div>

          <div class="bg-white rounded-lg p-6 shadow-sm border border-gray-200 hover:shadow-md transition cursor-pointer" @click="handleCardClick('contractManagement')">
            <div class="flex items-start justify-between mb-4">
              <div>
                <div class="text-[48px] mb-2 text-success">
                  <i class="fa-solid fa-file-contract"></i>
                </div>
                <h4 class="font-bold text-gray-800 mb-1">合同管理</h4>
                <p class="text-xs text-gray-500">管理所有合同</p>
              </div>
              <span class="bg-blue-100 text-primary border border-blue-200 px-2 py-0.5 rounded text-xs">查看</span>
            </div>
            <div class="border-t pt-3 text-xs text-gray-600">
              <p>有效合同数：{{ stats.validContracts }}</p>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 底部：通知公告 (次要) -->
    <div class="bg-white rounded-lg p-6 shadow-sm border border-gray-200">
      <h3 class="text-[14px] font-bold mb-4">系统公告</h3>
      <ul class="space-y-3">
        <li v-for="notice in notices" :key="notice.id" class="flex items-center text-sm text-gray-600 hover:text-primary cursor-pointer transition">
          <i class="fa-solid fa-circle text-[6px] mr-3 text-primary"></i>
          <span class="flex-1">{{ notice.title }}</span>
          <span class="text-xs text-gray-400">{{ notice.date }}</span>
        </li>
      </ul>
    </div>
  </div>

  <!-- 合同详情对话框 -->
  <ContractDetailDialog
    v-model="showRenewalModal"
    :contract="nearestExpiringContract"
    @renew="handleRenew"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { getContractList } from '@/api/contract'
import { getUserList } from '@/api/user'
import { getUnitList } from '@/api/unit'
import { useRouter } from 'vue-router'
import ContractDetailDialog from './contract/ContractDetailDialog.vue'

const userStore = useUserStore()
const { userName, userUnit, currentRole } = storeToRefs(userStore)
const router = useRouter()

// 判断是否为管理员
const isAdmin = computed(() => currentRole.value === 'ADMIN')

const showRenewalModal = ref(false)
const nearestExpiringContract = ref(null)

// 处理合同续约
const handleRenew = (contract) => {
  // 跳转到续约页面，并传递合同ID
  router.push({
    path: '/renewal',
    query: { contractId: contract.contractId || contract.id }
  })
}

// 卡片点击事件处理函数
const handleCardClick = (type) => {
  if (type === 'validContracts') {
    router.push('/contract')
  } else if (type === 'pendingRenewals') {
    router.push('/renewal')
  } else if (type === 'totalUsers' || type === 'userManagement') {
    router.push('/user-mgr')
  } else if (type === 'totalUnits' || type === 'unitManagement') {
    router.push('/unit-mgr')
  } else if (type === 'contractManagement') {
    router.push('/contract')
  }
}

const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
})

const stats = ref({
  // 通用字段
  validContracts: 0,
  pendingRenewals: 0,
  newContracts: 0,
  expiringDays: '-',
  // 管理员专属字段
  totalUsers: 0,
  totalUnits: 0,
  newUsersThisMonth: 0,
  pendingApprovals: 0
})

const services = ref([
  {
    id: 1,
    name: '瓶安保-标准版',
    desc: '气瓶监管台账系统',
    icon: 'fa-solid fa-database',
    color: '#1677ff',
    status: '生效中',
    statusClass: 'bg-green-100 text-success border border-green-200',
    contractNo: 'CON-2024001',
    expireDate: '2024-12-31'
  },
  {
    id: 2,
    name: '气瓶溯源系统',
    desc: '全生命周期追踪',
    icon: 'fa-solid fa-route',
    color: '#52c41a',
    status: '生效中',
    statusClass: 'bg-green-100 text-success border border-green-200',
    contractNo: 'CON-2024002',
    expireDate: '2025-06-30'
  },
  {
    id: 3,
    name: '智能预警模块',
    desc: '实时风险监控',
    icon: 'fa-solid fa-triangle-exclamation',
    color: '#faad14',
    status: '待续约',
    statusClass: 'bg-yellow-100 text-warning border border-yellow-200',
    contractNo: 'CON-2024003',
    expireDate: '2024-11-15'
  }
])

const notices = ref([
  { id: 1, title: '系统将于本周五晚进行例行维护，预计2小时', date: '2024-10-15' },
  { id: 2, title: '新版电子签章功能已上线，支持移动端签署', date: '2024-10-12' },
  { id: 3, title: '关于优化续约流程的公告', date: '2024-10-10' }
])

// 获取实时统计数据
const fetchStats = async () => {
  try {
    const now = new Date()
    const currentYear = now.getFullYear()
    const currentMonth = now.getMonth()

    // 获取所有合同（所有用户都需要）
    const response = await getContractList({
      pageNum: 1,
      pageSize: 1000, // 获取足够多的合同
      keyword: '',
      unitName: ''
    })

    if (response.code === 200 && response.data) {
      const allContracts = response.data.records || []

      // 1. 有效合同数（状态为6-已生效）
      stats.value.validContracts = allContracts.filter(contract => contract.contractStatus === 6).length

      // 2. 待处理续约（状态为7-即将到期）
      stats.value.pendingRenewals = allContracts.filter(contract => contract.contractStatus === 7).length

      // 3. 本月新增合同
      stats.value.newContracts = allContracts.filter(contract => {
        const createDate = new Date(contract.createdAt)
        return createDate.getFullYear() === currentYear && createDate.getMonth() === currentMonth
      }).length

      // 4. 最近即将到期天数
      const activeContracts = allContracts.filter(contract =>
        [6, 7].includes(contract.contractStatus) && contract.endDate
      )

      if (activeContracts.length > 0) {
        // 计算每个合同的剩余天数
        const contractsWithDays = activeContracts.map(contract => {
          const endDate = new Date(contract.endDate + 'T00:00:00+08:00')
          const diff = endDate.getTime() - now.getTime()
          return {
            ...contract,
            daysLeft: Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)))
          }
        })

        // 筛选出大于零的天数，即未到期的合同
        const upcomingContracts = contractsWithDays.filter(item => item.daysLeft > 0)

        if (upcomingContracts.length > 0) {
          // 取最小的剩余天数
          const minDaysLeft = Math.min(...upcomingContracts.map(item => item.daysLeft))
          stats.value.expiringDays = minDaysLeft

          // 保存最近即将到期的合同
          nearestExpiringContract.value = upcomingContracts.find(item => item.daysLeft === minDaysLeft)
        } else {
          // 如果没有未到期的合同，显示"0"
          stats.value.expiringDays = 0
          nearestExpiringContract.value = null
        }
      } else {
        // 如果没有有效合同，显示"-"
        stats.value.expiringDays = '-'
      }
    }

    // 管理员额外获取系统级数据
    if (isAdmin.value) {
      // 获取用户数据
      try {
        const userResponse = await getUserList({
          page: 1,
          size: 1000
        })
        console.log('用户数据响应:', userResponse)
        if (userResponse.code === 200 && userResponse.data) {
          const allUsers = userResponse.data.records || []
          stats.value.totalUsers = allUsers.length

          // 本月新增用户
          stats.value.newUsersThisMonth = allUsers.filter(user => {
            const createDate = new Date(user.createdAt)
            return createDate.getFullYear() === currentYear && createDate.getMonth() === currentMonth
          }).length
        }
      } catch (userError) {
        console.error('获取用户数据失败:', userError)
      }

      // 获取单位数据
      try {
        const unitResponse = await getUnitList({
          pageNum: 1,
          pageSize: 1000
        })
        if (unitResponse.code === 200 && unitResponse.data) {
          stats.value.totalUnits = unitResponse.data.records.length
        }
      } catch (unitError) {
        console.error('获取单位数据失败:', unitError)
      }

      // 待审批事项（暂时硬编码）
      stats.value.pendingApprovals = 2
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
</style>
