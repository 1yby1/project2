<template>
  <div class="fade-in">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-[20px] font-bold text-gray-800">续约管理</h2>
        <p class="text-gray-500 text-xs mt-1">管理即将到期的合同续约事宜</p>
      </div>
    </div>

    <!-- 续约策略（仅管理员可配置） -->
    <el-card v-if="currentRole === 'ADMIN' || currentRole === 'admin'" shadow="never" class="mb-6">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="font-bold text-gray-700">续约策略配置</span>
          <el-button type="primary" link @click="openStrategyDialog">+ 添加方案</el-button>
        </div>
      </template>
      <div class="grid grid-cols-3 gap-4">
        <div v-for="plan in renewalPlans" :key="plan.id" class="border border-gray-200 rounded p-4 hover:border-primary transition cursor-pointer bg-gray-50">
          <div class="flex justify-between items-start">
            <div>
              <h4 class="font-bold mb-1">{{ plan.name }}</h4>
              <p class="text-xs text-gray-500">续约时长: {{ plan.term }}</p>
            </div>
            <div class="flex gap-2">
              <el-tag v-if="plan.discount" type="danger" effect="plain" size="small">{{ plan.discount }}</el-tag>
              <el-button link type="primary" size="small" @click.stop="editStrategy(plan)">
                <i class="fa-solid fa-pen-to-square"></i>
              </el-button>
              <el-button link type="danger" size="small" @click.stop="deleteStrategyConfirm(plan.id)">
                <i class="fa-solid fa-trash"></i>
              </el-button>
            </div>
          </div>
          <div class="mt-3 text-xs text-gray-400">
            {{ plan.desc }}
          </div>
        </div>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="bg-white rounded shadow-sm border border-gray-200 px-4 pt-2">
      <!-- Tab 1: 续约申请记录 -->
      <el-tab-pane label="续约申请记录" name="records">
        <el-table :data="renewals" stripe style="width: 100%">
          <el-table-column prop="originalCode" label="原合同编号" width="150" />
          <el-table-column prop="unit" label="申请单位" width="180" v-if="currentRole === 'ADMIN' || currentRole === 'admin'" />
          <el-table-column prop="expireDate" label="原到期时间" width="120" />
          <el-table-column prop="term" label="续约年限" width="100" align="center" />
          <el-table-column label="续约金额" width="120" align="right">
            <template #default="{ row }">
              <span class="text-error font-bold">¥{{ row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="当前状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="light" round>{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openProcessModal(row)">
                {{ getActionText(row) }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Tab 2: 待续约合同 (仅单位用户可见) -->
      <el-tab-pane label="待续约合同" name="expiring" v-if="currentRole !== 'ADMIN' && currentRole !== 'admin'">
        <el-table :data="expiringContracts" stripe style="width: 100%">
          <el-table-column prop="code" label="合同编号" width="150" />
          <el-table-column prop="name" label="合同备注" />
          <el-table-column prop="signDate" label="签订日期" width="120" />
          <el-table-column prop="expireDate" label="到期日期" width="120">
            <template #default="{ row }">
              <span class="text-error font-bold">{{ row.expireDate }}</span>
            </template>
          </el-table-column>
          <el-table-column label="剩余天数" width="100" align="center">
            <template #default="{ row }">
              <el-tag type="danger" effect="plain">{{ row.daysLeft }}天</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150">
            <template #default="{ row }">
              <el-button
                v-if="!row.hasRenewalCase"
                type="primary"
                size="small"
                :disabled="row.daysLeft <= 0"
                @click="openApplyModal(row)">
                {{ row.daysLeft > 0 ? '申请续约' : '已到期不可续' }}
              </el-button>
              <el-button
                v-else
                type="info"
                size="small"
                @click="viewExistingRenewalCase(row)">
                查看续约
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 策略编辑对话框 -->
    <el-dialog
      v-model="showStrategyDialog"
      :title="strategyForm.id ? '编辑续约策略' : '添加续约策略'"
      width="600px"
      destroy-on-close>
      <el-form :model="strategyForm" label-width="120px">
        <el-form-item label="策略名称" required>
          <el-input v-model="strategyForm.strategyName" placeholder="请输入策略名称" />
        </el-form-item>
        <el-form-item label="续约年限" required>
          <el-input-number v-model="strategyForm.renewalYears" :min="0.5" :max="10" :step="0.5" />
          <span class="ml-2 text-gray-500">年</span>
        </el-form-item>
        <el-form-item label="是否推荐">
          <el-switch v-model="strategyForm.isRecommended" />
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="strategyForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="strategyForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStrategyDialog = false">取消</el-button>
        <el-button type="primary" @click="saveStrategy" :loading="strategyLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 续约全流程弹窗 -->
    <el-dialog
      v-model="showProcessModal"
      title="合同续约办理"
      width="900px"
      destroy-on-close
      top="5vh">
      
      <!-- 步骤条 -->
      <div class="px-8 mb-8">
        <el-steps :active="activeStep" finish-status="success" align-center>
          <el-step title="填写申请" description="单位用户"></el-step>
          <el-step title="合同预览" description="系统生成"></el-step>
          <el-step title="管理员确认" description="管理员"></el-step>
          <el-step title="对公缴费" description="单位用户"></el-step>
          <el-step title="电子签章" description="管理员"></el-step>
        </el-steps>
      </div>

      <div class="px-8 py-4 min-h-[300px]">
        <!-- Step 1: 填写申请 (仅新建时显示，流程中通常只读) -->
        <div v-if="activeStep === 0">
          <el-form :model="applyForm" label-width="100px">
            <el-form-item label="续约方案">
              <div class="grid grid-cols-3 gap-4 w-full">
                <div 
                  v-for="plan in renewalPlans" 
                  :key="plan.id"
                  @click="applyForm.planId = plan.id"
                  :class="['border rounded p-3 cursor-pointer transition', applyForm.planId === plan.id ? 'border-primary bg-blue-50' : 'border-gray-200 hover:border-blue-300']">
                  <div class="font-bold text-sm">{{ plan.name }}</div>
                  <div class="text-xs text-gray-500 mt-1">{{ plan.term }} / {{ plan.discount }}</div>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="预计金额">
              <span class="text-xl font-bold text-error">¥ {{ calculateEstimate }}</span>
            </el-form-item>
          </el-form>
        </div>

        <!-- Step 2: 合同预览 -->
        <div v-if="activeStep === 1">
          <div class="bg-gray-50 p-6 border border-gray-200 rounded font-serif text-sm leading-relaxed h-[300px] overflow-y-auto">
            <h3 class="text-center font-bold text-lg mb-6">气瓶租赁续约补充协议</h3>
            <p class="mb-4"><strong>甲方：</strong> {{ currentProcessRenewal?.unit || '红星气瓶充装站' }}</p>
            <p class="mb-4"><strong>乙方：</strong> 瓶安保科技有限公司</p>
            <p class="mb-4">鉴于甲乙双方于 2024年01月01日 签订了编号为 {{ currentProcessRenewal?.originalCode || 'CON-XXXX' }} 的《气瓶租赁合同》，现该合同即将到期。经双方友好协商，达成如下续约协议：</p>
            <p class="mb-4">1. 续约期限：自原合同到期之日起延长 <strong>{{ currentProcessRenewal?.term || '1年' }}</strong>。</p>
            <p class="mb-4">2. 续约费用：人民币 <strong>{{ currentProcessRenewal?.amount || '50,000' }}</strong> 元。</p>
            <p class="mb-4">3. 其他条款：除本协议约定的变更外，原合同其他条款继续有效。</p>
            <div class="mt-8 flex justify-between">
              <div>甲方签章：___________</div>
              <div>乙方签章：___________</div>
            </div>
          </div>
        </div>

        <!-- Step 3: 管理员确认 -->
        <div v-if="activeStep === 2" class="text-center py-8">
          <div v-if="currentRole === 'ADMIN' || currentRole === 'admin'">
            <i class="fa-solid fa-clipboard-check text-primary text-5xl mb-4"></i>
            <h3 class="text-lg font-bold mb-2">收到新的续约申请</h3>
            <p class="text-gray-500 mb-6">申请单位：{{ currentProcessRenewal?.unit }} | 金额：¥{{ currentProcessRenewal?.amount }}</p>
            <div class="bg-yellow-50 border border-yellow-200 p-4 rounded text-left text-sm mx-auto max-w-md">
              <p class="font-bold text-warning mb-2"><i class="fa-solid fa-triangle-exclamation mr-1"></i> 审核提示</p>
              <p>请核对该单位的历史履约情况及信用记录，确认无误后点击"确认通过"。</p>
            </div>
          </div>
          <div v-else>
            <el-result icon="info" title="等待管理员确认" sub-title="您的续约申请已提交，请耐心等待管理员审核。"></el-result>
          </div>
        </div>

        <!-- Step 4: 对公缴费 -->
        <div v-if="activeStep === 3">
          <div v-if="currentRole !== 'ADMIN' && currentRole !== 'admin'">
            <div class="flex justify-between items-center bg-blue-50 p-4 rounded border border-blue-100 mb-6">
              <span class="text-gray-700">应缴金额</span>
              <span class="text-2xl font-bold text-error">¥ {{ currentProcessRenewal?.amount }}</span>
            </div>
            <div class="grid grid-cols-2 gap-8">
              <div class="border rounded p-4 text-center cursor-pointer hover:border-primary">
                <i class="fa-solid fa-building-columns text-3xl text-gray-400 mb-2"></i>
                <div class="font-bold">银行转账</div>
                <div class="text-xs text-gray-500 mt-1">查看账户信息</div>
              </div>
              <div class="border rounded p-4 text-center cursor-pointer hover:border-primary border-primary bg-blue-50">
                <i class="fa-brands fa-alipay text-3xl text-blue-500 mb-2"></i>
                <div class="font-bold">支付宝企业付</div>
                <div class="text-xs text-gray-500 mt-1">推荐使用</div>
              </div>
            </div>
          </div>
          <div v-else>
            <el-result icon="info" title="等待用户缴费" sub-title="已发送缴费通知，等待用户完成支付。"></el-result>
          </div>
        </div>

        <!-- Step 5: 电子签章 -->
        <div v-if="activeStep === 4">
          <div v-if="currentRole === 'ADMIN' || currentRole === 'admin'">
            <div class="border border-gray-300 rounded h-48 flex items-center justify-center bg-gray-50 mb-4 relative overflow-hidden">
              <div class="absolute inset-0 flex items-center justify-center opacity-10 pointer-events-none">
                <span class="text-6xl font-bold transform -rotate-45">CONTRACT</span>
              </div>
              <p class="text-gray-400">合同签署区域</p>

              <!-- 模拟盖章动画效果 -->
              <div v-if="isSigned" class="absolute right-10 bottom-10 border-4 border-red-500 rounded-full w-24 h-24 flex items-center justify-center text-red-500 font-bold transform -rotate-12 opacity-80">
                已签署
              </div>
            </div>
            <div class="text-center">
              <el-button type="primary" size="large" @click="handleSign" :disabled="isSigned">
                <i class="fa-solid fa-file-signature mr-2"></i> {{ isSigned ? '已完成签署' : '加盖电子公章' }}
              </el-button>
            </div>
          </div>
          <div v-else>
            <!-- 状态6: 待签章 - 显示等待管理员签署 -->
            <el-result
              v-if="currentProcessRenewal?.statusCode === 6"
              icon="info"
              title="等待管理员签署"
              sub-title="您的续约申请已通过审核并完成缴费，正在等待管理员加盖电子公章。">
            </el-result>
            <!-- 状态7: 已完成 - 显示续约完成 -->
            <el-result
              v-else
              icon="success"
              title="续约完成"
              sub-title="管理员已完成签署，合同正式生效。">
              <template #extra>
                <el-button type="primary">下载合同PDF</el-button>
              </template>
            </el-result>
          </div>
        </div>

      </div>

      <template #footer>
        <div class="flex justify-between items-center">
          <div class="text-xs text-gray-400">
            <i class="fa-solid fa-circle-info mr-1"></i> 当前步骤：{{ stepTitles[activeStep] }}
          </div>
          <div>
            <el-button @click="showProcessModal = false">关闭</el-button>
            <!-- 动态按钮逻辑 -->
            <el-button 
              v-if="showNextButton" 
              type="primary" 
              @click="handleNextStep"
              :loading="loading">
              {{ nextButtonText }}
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllStrategies, getRenewalCases, createCase as createRenewalCase, updateCase, updateCaseStatus, createStrategy, updateStrategy, deleteStrategy } from '@/api/renewal'
import { getExpiringContracts } from '@/api/contract'

const userStore = useUserStore()
const { currentRole } = storeToRefs(userStore)

const activeTab = ref('records')

const renewalPlans = ref([])

const renewals = ref([])

// Strategy Dialog
const showStrategyDialog = ref(false)
const strategyLoading = ref(false)
const strategyForm = ref({
  id: null,
  strategyName: '',
  renewalYears: 1,
  isRecommended: 0,
  sortOrder: 0,
  status: 1
})

const expiringContracts = ref([])

// Modal State
const showProcessModal = ref(false)
const activeStep = ref(0)
const currentProcessRenewal = ref(null)
const isSigned = ref(false)
const loading = ref(false)

// Apply Form
const applyForm = ref({
  contractId: '',
  contractNo: '',
  contractName: '',
  planId: 1,
  expireDate: ''
})

const stepTitles = ['填写申请', '合同预览', '管理员确认', '对公缴费', '电子签章']

// Load data on mount
onMounted(() => {
  loadStrategies()
  loadRenewalCases()
  loadExpiringContracts()
})

// Load strategies from backend
async function loadStrategies() {
  try {
    const res = await getAllStrategies()
    if (res.code === 200) {
      renewalPlans.value = res.data
        .map(strategy => ({
          id: strategy.strategyId,
          name: strategy.strategyName,
          term: `${strategy.renewalYears}年`,
          discount: strategy.isRecommended ? '推荐' : '',
          desc: `续约${strategy.renewalYears}年`,
          sortOrder: strategy.sortOrder || 0,
          status: strategy.status
        }))
        // 按排序权重降序排列（权重越大越靠前）
        .sort((a, b) => b.sortOrder - a.sortOrder)
        // 只显示启用状态的策略
        .filter(strategy => strategy.status === 1)
    }
  } catch (error) {
    console.error('Failed to load strategies:', error)
    // 使用模拟数据作为后备
    renewalPlans.value = [
      { id: 1, name: '一年期标准方案', term: '1年', discount: '9折优惠', desc: '适合短期过渡，灵活方便', sortOrder: 10, status: 1 },
      { id: 2, name: '两年期优惠方案', term: '2年', discount: '8.5折优惠', desc: '中长期合作首选，性价比高', sortOrder: 20, status: 1 },
      { id: 3, name: '三年期特惠方案', term: '3年', discount: '8折优惠', desc: '长期稳定合作，最大力度优惠', sortOrder: 30, status: 1 }
    ]
  }
}

// Load renewal cases from backend
async function loadRenewalCases() {
  try {
    const params = {
      page: 1,
      size: 100
    }
    if (currentRole.value !== 'ADMIN') {
      // 如果不是管理员，只加载当前用户的续约案件
      // params.unitId = userStore.unitId
    }
    const res = await getRenewalCases(params)
    if (res.code === 200) {
      // MyBatis-Plus 返回的 IPage 对象有 records 字段
      const records = res.data.records || []
      renewals.value = records.map(item => ({
        id: item.caseId,
        originalCode: item.caseNo,
        unit: `单位ID: ${item.unitId}`, // TODO: 关联单位表获取单位名称
        expireDate: item.expireAt ? formatDate(item.expireAt) : '-',
        term: getTermFromStrategy(item.strategyId),
        amount: item.finalAmount || 0,
        status: getStatusText(item.status),
        statusCode: item.status,
        step: getStepFromStatus(item.status)
      }))

      // 更新待续约合同的状态
      checkExistingRenewalCases()
    }
  } catch (error) {
    console.error('Failed to load renewal cases:', error)
    ElMessage.error('加载续约案件失败')
  }
}

// Load expiring contracts from backend
async function loadExpiringContracts() {
  try {
    console.log('开始加载即将到期的合同...')
    const res = await getExpiringContracts()
    console.log('即将到期合同API响应:', res)
    if (res.code === 200 && res.data) {
      expiringContracts.value = res.data.map(contract => ({
        id: contract.contractId,
        code: contract.contractNo,
        name: `${contract.remark || '气瓶租赁合同'}`,
        signDate: contract.startDate ? formatDate(contract.startDate) : '-',
        expireDate: contract.endDate ? formatDate(contract.endDate) : '-',
        daysLeft: calculateDaysLeft(contract.endDate),
        hasRenewalCase: false // 标记是否已有续约案件
      }))
      console.log('即将到期合同加载成功，数量:', expiringContracts.value.length)

      // 检查每个合同是否已有续约案件
      checkExistingRenewalCases()
    }
  } catch (error) {
    console.error('Failed to load expiring contracts:', error)
    console.error('Error details:', error.response || error.message)
    ElMessage.error(`加载即将到期合同失败: ${error.message || '未知错误'}`)
  }
}

// 检查待续约合同是否已有续约案件
function checkExistingRenewalCases() {
  // 从 renewal_case_contract 表中获取合同ID与案件的关联
  // 但目前 renewals 列表中没有原合同ID，只有案件编号
  // 简化方案：通过合同编号匹配（caseNo == contractNo）
  expiringContracts.value.forEach(contract => {
    const existingCase = renewals.value.find(renewal =>
      renewal.originalCode === contract.code
    )
    if (existingCase) {
      contract.hasRenewalCase = true
      contract.renewalCaseId = existingCase.id
    }
  })
}

// Calculate days left until expiration
function calculateDaysLeft(endDate) {
  if (!endDate) return 0
  const today = new Date()
  const expireDate = new Date(endDate)
  const diffTime = expireDate - today
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 将中文日期格式转换为ISO格式（用于发送到后端）
function convertToISODate(dateStr) {
  if (!dateStr) return null
  // dateStr 格式可能是 "2025/1/20" 或 "2025-01-20"
  try {
    const date = new Date(dateStr.replace(/\//g, '-'))
    if (isNaN(date.getTime())) return null
    return date.toISOString()
  } catch (e) {
    console.error('Date conversion error:', e)
    return null
  }
}

// 根据策略ID获取期限（临时方案）
function getTermFromStrategy(strategyId) {
  const strategy = renewalPlans.value.find(s => s.id === strategyId)
  return strategy ? strategy.term : '-'
}

// Convert status code to text
function getStatusText(status) {
  const statusMap = {
    1: '填写中',
    2: '待单位确认',
    3: '待管理员确认',
    4: '待缴费',
    5: '待财务审核',
    6: '待签章',
    7: '已完成',
    '-1': '已取消',
    '-2': '已拒绝'
  }
  return statusMap[status] || '未知状态'
}

// Convert status to step
function getStepFromStatus(status) {
  const stepMap = {
    1: 0,
    2: 1,
    3: 2,
    4: 3,
    5: 3,
    6: 4,
    7: 4
  }
  return stepMap[status] || 0
}

const calculateEstimate = computed(() => {
  const plan = renewalPlans.value.find(p => p.id === applyForm.value.planId)
  return plan ? (plan.id === 1 ? '50,000' : plan.id === 2 ? '90,000' : '120,000') : '0'
})

function getStatusType(status) {
  const map = {
    '待确认': 'warning',
    '待缴费': 'primary',
    '待签名': 'info',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

function getActionText(row) {
  const statusCode = row.statusCode
  const role = currentRole.value
  const adminRole = role === 'ADMIN' || role === 'admin'

  // 已完成或已拒绝
  if (statusCode === 7 || statusCode === -2) return '查看详情'

  if (adminRole) {
    // 管理员视角
    if (statusCode === 3) return '去确认' // 待管理员确认
    if (statusCode === 6) return '去签章' // 待签章
    if (statusCode === 5) return '财务审核' // 待财务审核
    return '查看进度'
  } else {
    // 单位用户视角
    if (statusCode === 2) return '去确认' // 待单位确认
    if (statusCode === 4) return '去缴费' // 待缴费
    return '查看进度'
  }
}

function openApplyModal(contract) {
  if (contract.daysLeft <= 0) {
    ElMessage.warning('合同已到期，无法续约')
    return
  }
  activeStep.value = 0
  currentProcessRenewal.value = null
  applyForm.value = {
    contractId: contract.id,
    contractNo: contract.code, // 保存原合同编号
    contractName: `${contract.code} (${contract.name})`,
    planId: 1,
    expireDate: contract.expireDate // 保存原合同到期日期
  }
  showProcessModal.value = true
}

// 查看已有的续约案件
function viewExistingRenewalCase(contract) {
  // 找到对应的续约案件
  const renewalCase = renewals.value.find(r => r.originalCode === contract.code)
  if (renewalCase) {
    // 切换到续约记录标签页
    activeTab.value = 'records'
    // 打开续约案件详情
    openProcessModal(renewalCase)
  } else {
    ElMessage.warning('未找到对应的续约案件')
  }
}

function openProcessModal(row) {
  currentProcessRenewal.value = {
    ...row,
    id: row.id,
    unit: row.unit,
    originalCode: row.originalCode,
    term: row.term,
    amount: row.amount,
    statusCode: row.statusCode,
    status: row.status
  }
  activeStep.value = row.step
  showProcessModal.value = true
  isSigned.value = row.statusCode === 7
}

// Helper function to check if current user is admin
const isAdmin = computed(() => {
  const role = currentRole.value
  return role === 'ADMIN' || role === 'admin'
})

// Button Logic - Simplified and more robust
const showNextButton = computed(() => {
  // New case flow (no ID yet)
  if (!currentProcessRenewal.value || !currentProcessRenewal.value.id) {
    // Step 0: Fill application -> Preview
    if (activeStep.value === 0) return true
    // Step 1: Preview -> Submit
    if (activeStep.value === 1) return true
    return false
  }

  // Existing case - check status and role
  const statusCode = currentProcessRenewal.value.statusCode

  // Step 0: Fill application - show button if status is 1 (filling)
  // Allow both users and admins to fill and submit
  if (activeStep.value === 0) {
    return statusCode === 1
  }

  // Step 1: Contract Preview - show button if status is 1 (filling)
  // Allow both users and admins to submit
  if (activeStep.value === 1) {
    return statusCode === 1
  }

  // Step 2: Admin Confirmation - only admin can confirm, only for status 3
  if (activeStep.value === 2) {
    return isAdmin.value && statusCode === 3
  }

  // Step 3: Payment - only non-admin can pay, only for status 4
  if (activeStep.value === 3) {
    return !isAdmin.value && statusCode === 4
  }

  // Step 4: Signing - only admin can sign, only for status 6, and not already signed
  if (activeStep.value === 4) {
    return isAdmin.value && statusCode === 6 && !isSigned.value
  }

  return false
})

const nextButtonText = computed(() => {
  if (activeStep.value === 0) return '生成预览'
  if (activeStep.value === 1) return '提交申请'
  if (activeStep.value === 2) return '确认通过'
  if (activeStep.value === 3) return '确认支付'
  if (activeStep.value === 4) return '完成签署'
  return '下一步'
})

async function handleNextStep() {
  loading.value = true

  try {
    // Logic for each step
    if (activeStep.value === 0) {
      // Mock creating a temp renewal object for preview
      // Preserve existing statusCode if it exists (for loaded cases), otherwise keep it undefined for new cases
      const existingStatusCode = currentProcessRenewal.value?.statusCode
      const existingId = currentProcessRenewal.value?.id

      currentProcessRenewal.value = {
        ...currentProcessRenewal.value,
        unit: '红星气瓶充装站', // Mock
        originalCode: applyForm.value.contractName.split(' ')[0],
        term: renewalPlans.value.find(p => p.id === applyForm.value.planId).term,
        amount: calculateEstimate.value,
        statusCode: existingStatusCode,
        id: existingId
      }
      activeStep.value = 1
    } else if (activeStep.value === 1) {
      // Submit Application
      if (!currentProcessRenewal.value.id) {
        // New application - call backend API
        const caseData = {
          unitId: 1, // TODO: 从当前登录用户获取单位ID
          strategyId: applyForm.value.planId,
          status: 3, // 直接到待管理员确认
          originalAmount: parseFloat(calculateEstimate.value.replace(/,/g, '')),
          discountAmount: 0,
          finalAmount: parseFloat(calculateEstimate.value.replace(/,/g, '')),
          expireAt: applyForm.value.expireDate ? convertToISODate(applyForm.value.expireDate) : null, // 添加原合同到期时间
          caseNo: applyForm.value.contractNo // 使用原合同编号作为续约案件编号
        }
        // DEBUG: 打印发送的数据
        console.log('=== DEBUG: Sending case data ===')
        console.log('calculateEstimate.value:', calculateEstimate.value)
        console.log('expireDate from contract:', applyForm.value.expireDate)
        console.log('caseData:', caseData)
        // Pass the contract ID from the applyForm
        const res = await createRenewalCase(caseData, applyForm.value.contractId)
        if (res.code === 200) {
          ElMessage.success('续约申请已提交，等待管理员确认')
          showProcessModal.value = false
          activeTab.value = 'records'
          loadRenewalCases() // Reload the list
        } else {
          ElMessage.error(res.message || '提交失败')
        }
      } else {
        // 已有案件，检查状态是否为填写中
        if (currentProcessRenewal.value.statusCode === 1) {
          // 更新案件信息（包括金额和状态）
          const updateData = {
            unitId: 1, // TODO: 从当前登录用户获取单位ID
            strategyId: applyForm.value.planId,
            status: 3, // 直接到待管理员确认
            originalAmount: parseFloat(calculateEstimate.value.replace(/,/g, '')),
            discountAmount: 0,
            finalAmount: parseFloat(calculateEstimate.value.replace(/,/g, ''))
          }
          // DEBUG: 打印发送的数据
          console.log('=== DEBUG: Updating existing case data ===')
          console.log('Case ID:', currentProcessRenewal.value.id)
          console.log('calculateEstimate.value:', calculateEstimate.value)
          console.log('updateData:', updateData)

          const res = await updateCase(currentProcessRenewal.value.id, updateData)
          if (res.code === 200) {
            ElMessage.success('续约申请已提交，等待管理员确认')
            showProcessModal.value = false
            loadRenewalCases()
          } else {
            ElMessage.error(res.message || '提交失败')
          }
        } else {
          ElMessage.warning('当前状态不允许提交')
        }
      }
    } else if (activeStep.value === 2) {
      // Admin Confirm
      if (currentProcessRenewal.value.id && currentProcessRenewal.value.statusCode === 3) {
        const res = await updateCaseStatus(currentProcessRenewal.value.id, 4) // 待缴费
        if (res.code === 200) {
          ElMessage.success('已确认，等待用户缴费')
          showProcessModal.value = false
          loadRenewalCases()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } else {
        ElMessage.warning('当前状态不允许确认')
      }
    } else if (activeStep.value === 3) {
      // User Pay
      if (currentProcessRenewal.value.id && currentProcessRenewal.value.statusCode === 4) {
        const res = await updateCaseStatus(currentProcessRenewal.value.id, 6) // 待签章
        if (res.code === 200) {
          ElMessage.success('缴费成功')
          showProcessModal.value = false
          loadRenewalCases()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } else {
        ElMessage.warning('当前状态不允许缴费')
      }
    } else if (activeStep.value === 4) {
      // Admin Sign (Handled by handleSign, but this is the finish button)
      showProcessModal.value = false
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    loading.value = false
  }
}

async function handleSign() {
  try {
    if (currentProcessRenewal.value.id && currentProcessRenewal.value.statusCode === 6) {
      const res = await updateCaseStatus(currentProcessRenewal.value.id, 7) // 已完成
      if (res.code === 200) {
        isSigned.value = true
        currentProcessRenewal.value.statusCode = 7 // 更新本地状态
        ElMessage.success('签署完成，合同已生效')
        loadRenewalCases()
      } else {
        ElMessage.error(res.message || '签署失败')
      }
    } else {
      ElMessage.warning('当前状态不允许签署')
    }
  } catch (error) {
    console.error('签署失败:', error)
    ElMessage.error('签署失败，请重试')
  }
}

// Strategy management functions
function openStrategyDialog() {
  strategyForm.value = {
    id: null,
    strategyName: '',
    renewalYears: 1,
    isRecommended: 0,
    sortOrder: 0,
    status: 1
  }
  showStrategyDialog.value = true
}

function editStrategy(plan) {
  strategyForm.value = {
    id: plan.id,
    strategyName: plan.name,
    renewalYears: parseFloat(plan.term.replace('年', '')),
    isRecommended: plan.discount === '推荐' ? 1 : 0,
    sortOrder: plan.sortOrder || 0,
    status: plan.status || 1
  }
  showStrategyDialog.value = true
}

async function saveStrategy() {
  strategyLoading.value = true
  try {
    const data = {
      strategyName: strategyForm.value.strategyName,
      renewalYears: strategyForm.value.renewalYears,
      isRecommended: strategyForm.value.isRecommended ? 1 : 0,
      sortOrder: strategyForm.value.sortOrder,
      status: strategyForm.value.status
    }

    let res
    if (strategyForm.value.id) {
      // Update
      res = await updateStrategy(strategyForm.value.id, data)
    } else {
      // Create
      res = await createStrategy(data)
    }

    if (res.code === 200) {
      ElMessage.success(strategyForm.value.id ? '更新成功' : '创建成功')
      showStrategyDialog.value = false
      loadStrategies()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('保存策略失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    strategyLoading.value = false
  }
}

async function deleteStrategyConfirm(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个续约策略吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteStrategy(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadStrategies()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除策略失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

</script>

<style scoped>
/* Add any specific styles here */
</style>
