<template>
  <div class="cylinder-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon class="title-icon"><List /></el-icon>
          气瓶列表管理
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>气瓶管理</el-breadcrumb-item>
          <el-breadcrumb-item>气瓶列表</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-row">
        <el-space wrap>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索气瓶编号/RFID标签"
            clearable
            style="width: 280px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <el-select
            v-model="selectedContractId"
            placeholder="选择合同"
            clearable
            filterable
            style="width: 300px"
            @change="handleContractChange"
          >
            <el-option
              v-for="contract in contractList"
              :key="contract.contractId"
              :label="`${contract.contractNo} - ${contract.unitName}`"
              :value="contract.contractId"
            />
          </el-select>

          <el-select
            v-model="selectedStatus"
            placeholder="气瓶状态"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="正常" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="检验中" :value="3" />
            <el-option label="报废" :value="4" />
          </el-select>

          <el-select
            v-model="inspectionFilter"
            placeholder="检验状态"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="即将到期(30天内)" value="dueSoon" />
            <el-option label="已过期" value="overdue" />
            <el-option label="正常" value="normal" />
          </el-select>

          <el-button type="primary" icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button icon="Refresh" @click="resetAndLoad">
            重置
          </el-button>
        </el-space>
      </div>
    </el-card>

    <!-- 统计信息 -->
    <el-card class="stats-card" shadow="never">
      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">总数</div>
        </div>
        <div class="stat-item success">
          <div class="stat-value">{{ statusStats.normal }}</div>
          <div class="stat-label">正常</div>
        </div>
        <div class="stat-item warning">
          <div class="stat-value">{{ statusStats.dueSoon }}</div>
          <div class="stat-label">即将到期</div>
        </div>
        <div class="stat-item danger">
          <div class="stat-value">{{ statusStats.overdue }}</div>
          <div class="stat-label">已过期</div>
        </div>
      </div>
    </el-card>

    <!-- 气瓶列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="cylinderList"
        border
        stripe
        :default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        
        <el-table-column prop="cylinderNo" label="气瓶编号" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="showDetail(row)">
              {{ row.cylinderNo }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column prop="contractNo" label="合同编号" width="150" show-overflow-tooltip />

        <el-table-column prop="rfidTag" label="RFID标签" width="150" show-overflow-tooltip />

        <el-table-column prop="manufacturer" label="制造商" width="120" show-overflow-tooltip />

        <el-table-column prop="manufactureDate" label="制造日期" width="110" align="center">
          <template #default="{ row }">
            {{ formatDate(row.manufactureDate) }}
          </template>
        </el-table-column>

        <el-table-column prop="designPressure" label="设计压力(MPa)" width="120" align="center" />

        <el-table-column prop="volume" label="容积(L)" width="100" align="center" />

        <el-table-column prop="lastInspectDate" label="上次检验" width="110" align="center">
          <template #default="{ row }">
            {{ formatDate(row.lastInspectDate) }}
          </template>
        </el-table-column>

        <el-table-column prop="nextInspectDate" label="下次检验" width="110" align="center">
          <template #default="{ row }">
            <span :class="getInspectDateClass(row.nextInspectDate)">
              {{ formatDate(row.nextInspectDate) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="cylinderStatus" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.cylinderStatus)">
              {{ getStatusLabel(row.cylinderStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="存放位置" min-width="120" show-overflow-tooltip />

        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">
              详情
            </el-button>
            <el-button link type="warning" size="small" @click="updateStatus(row)">
              状态
            </el-button>
            <el-button link type="success" size="small" @click="recordInspect(row)">
              检验
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 气瓶详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="气瓶详细信息"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentCylinder">
        <el-descriptions-item label="气瓶编号" span="2">
          {{ currentCylinder.cylinderNo }}
        </el-descriptions-item>
        
        <el-descriptions-item label="RFID标签">
          {{ currentCylinder.rfidTag || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentCylinder.cylinderStatus)">
            {{ getStatusLabel(currentCylinder.cylinderStatus) }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="制造商">
          {{ currentCylinder.manufacturer || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="制造日期">
          {{ formatDate(currentCylinder.manufactureDate) }}
        </el-descriptions-item>

        <el-descriptions-item label="设计压力">
          {{ currentCylinder.designPressure || '-' }} MPa
        </el-descriptions-item>
        
        <el-descriptions-item label="容积">
          {{ currentCylinder.volume || '-' }} L
        </el-descriptions-item>

        <el-descriptions-item label="空瓶重量">
          {{ currentCylinder.weightEmpty || '-' }} kg
        </el-descriptions-item>
        
        <el-descriptions-item label="存放位置">
          {{ currentCylinder.location || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="上次检验日期">
          {{ formatDate(currentCylinder.lastInspectDate) }}
        </el-descriptions-item>
        
        <el-descriptions-item label="下次检验日期">
          <span :class="getInspectDateClass(currentCylinder.nextInspectDate)">
            {{ formatDate(currentCylinder.nextInspectDate) }}
          </span>
        </el-descriptions-item>

        <el-descriptions-item label="备注" span="2">
          {{ currentCylinder.remark || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="登记时间">
          {{ formatDateTime(currentCylinder.createTime) }}
        </el-descriptions-item>
        
        <el-descriptions-item label="更新时间">
          {{ formatDateTime(currentCylinder.updateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 气瓶操作对话框 -->
    <CylinderActionDialog
      v-if="actionDialogVisible && currentCylinder"
      :cylinder="currentCylinder"
      :type="actionType"
      @success="handleActionSuccess"
      @close="actionDialogVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, List } from '@element-plus/icons-vue'
import { queryCylinderList, getCylindersByContract } from '@/api/cylinder'
import { getContractList } from '@/api/contract'
import CylinderActionDialog from './cylinder/CylinderActionDialog.vue'

interface CylinderRecord {
  registerId: number
  contractId: number
  contractNo?: string
  cylinderTypeId: number
  cylinderNo: string
  rfidTag?: string
  manufacturer?: string
  manufactureDate?: string
  designPressure?: number
  volume?: number
  weightEmpty?: number
  lastInspectDate?: string
  nextInspectDate?: string
  cylinderStatus: number
  location?: string
  remark?: string
  createTime: string
  updateTime: string
}

interface Contract {
  contractId: number
  contractNo: string
  unitName: string
}

const route = useRoute()

const loading = ref(false)
const searchKeyword = ref('')
const selectedContractId = ref<number | null>(null)
const selectedStatus = ref<number | null>(null)
const inspectionFilter = ref<string | null>(null)
const cylinderList = ref<CylinderRecord[]>([])
const contractList = ref<Contract[]>([])
const currentCylinder = ref<CylinderRecord | null>(null)
const detailDialogVisible = ref(false)
const actionDialogVisible = ref(false)
const actionType = ref<'status' | 'inspect'>('status')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0) // 后端返回的总数

const statusStats = computed(() => {
  const now = new Date()
  const stats = {
    normal: 0,
    dueSoon: 0,
    overdue: 0
  }

  cylinderList.value.forEach(cylinder => {
    if (!cylinder.nextInspectDate) {
      stats.normal++
      return
    }

    const nextInspectDate = new Date(cylinder.nextInspectDate)
    const daysUntilInspect = Math.floor((nextInspectDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))

    if (daysUntilInspect < 0) {
      stats.overdue++
    } else if (daysUntilInspect <= 30) {
      stats.dueSoon++
    } else {
      stats.normal++
    }
  })

  return stats
})

// 加载合同列表
const loadContractList = async () => {
  try {
    const response = await getContractList({
      pageNum: 1,
      pageSize: 1000,
      contractStatus: 6 // 只加载已生效的合同
    })
    console.log('合同列表响应:', response)
    if (response.code === 200) {
      contractList.value = response.data?.records || []
    }
    console.log('加载的合同列表:', contractList.value)
  } catch (error) {
    console.error('加载合同列表失败:', error)
  }
}

// 加载气瓶列表 - 后端分页
const loadCylinderList = async () => {
  loading.value = true
  try {
    // 构建查询条件
    const queryParams: any = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    // 添加可选筛选条件
    if (selectedContractId.value) {
      queryParams.contractId = selectedContractId.value
    }
    if (selectedStatus.value !== null) {
      queryParams.cylinderStatus = selectedStatus.value
    }
    if (searchKeyword.value.trim()) {
      queryParams.cylinderNo = searchKeyword.value.trim()
    }
    if (inspectionFilter.value) {
      queryParams.inspectionStatus = inspectionFilter.value
    }

    const response = await queryCylinderList(queryParams)
    
    if (response.code === 200) {
      const pageData = response.data
      cylinderList.value = pageData?.records || []
      console.log('加载的气瓶列表:', cylinderList.value)
      // 获取后端返回的总数
      total.value = pageData?.total || 0
    } else {
      ElMessage.error(response.data.message || '加载失败')
    }
  } catch (error: unknown) {
    console.error('加载气瓶列表失败:', error)
    ElMessage.error((error as Error).message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 合同变更
const handleContractChange = () => {
  currentPage.value = 1
  loadCylinderList()
}

// 搜索和筛选 - 后端分页
const handleSearch = () => {
  currentPage.value = 1
  loadCylinderList()
}

// 重置并加载
const resetAndLoad = () => {
  searchKeyword.value = ''
  selectedContractId.value = null
  selectedStatus.value = null
  inspectionFilter.value = null
  currentPage.value = 1
  pageSize.value = 20
  loadCylinderList()
}

// 显示详情
const showDetail = (cylinder: CylinderRecord) => {
  currentCylinder.value = cylinder
  detailDialogVisible.value = true
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dateStr.split(' ')[0]
}

// 格式化日期时间
const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dateStr
}

// 获取检验日期样式类
const getInspectDateClass = (dateStr?: string) => {
  if (!dateStr) return ''
  
  const now = new Date()
  const inspectDate = new Date(dateStr)
  const daysUntilInspect = Math.floor((inspectDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))

  if (daysUntilInspect < 0) {
    return 'text-danger'
  } else if (daysUntilInspect <= 30) {
    return 'text-warning'
  }
  return ''
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: number) => {
  const labelMap: Record<number, string> = {
    1: '正常',
    2: '使用中',
    3: '检验中',
    4: '报废'
  }
  return labelMap[status] || '未知'
}

// 更新状态
const updateStatus = (cylinder: CylinderRecord) => {
  currentCylinder.value = cylinder
  actionType.value = 'status'
  actionDialogVisible.value = true
}

// 记录检验
const recordInspect = (cylinder: CylinderRecord) => {
  currentCylinder.value = cylinder
  actionType.value = 'inspect'
  actionDialogVisible.value = true
}

// 操作成功回调
const handleActionSuccess = () => {
  actionDialogVisible.value = false
  loadCylinderList() // 刷新列表
}

// 分页大小变化
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadCylinderList()
}

// 当前页变化
const handlePageChange = (newPage: number) => {
  currentPage.value = newPage
  loadCylinderList()
}

// 组件挂载时加载数据
onMounted(() => {
  // 检查路由参数中是否有合同ID
  const contractId = route.query.contractId as string
  if (contractId) {
    selectedContractId.value = Number(contractId)
  }
  
  loadContractList()
  loadCylinderList()
})
</script>

<style scoped lang="scss">
.cylinder-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .header-left {
    .page-title {
      display: flex;
      align-items: center;
      font-size: 24px;
      font-weight: 600;
      margin: 0 0 8px 0;
      color: #303133;

      .title-icon {
        margin-right: 10px;
        font-size: 28px;
      }
    }
  }
}

.filter-card {
  margin-bottom: 20px;

  .filter-row {
    display: flex;
    gap: 12px;
  }
}

.stats-card {
  margin-bottom: 20px;

  .stats-container {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .stat-item {
      text-align: center;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      color: white;

      &.success {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.warning {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.danger {
        background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
      }

      .stat-value {
        font-size: 32px;
        font-weight: bold;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }
}

.table-card {
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}

.text-danger {
  color: var(--el-color-danger);
  font-weight: bold;
}

.text-warning {
  color: var(--el-color-warning);
  font-weight: bold;
}
</style>
