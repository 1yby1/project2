<template>
  <el-dialog
    v-model="dialogVisible"
    title="气瓶列表"
    width="1200px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="toolbar">
      <el-space>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索气瓶编号/RFID标签"
          clearable
          style="width: 250px"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" icon="Search" @click="handleSearch">
          搜索
        </el-button>
        <el-button icon="Refresh" @click="loadCylinderList">
          刷新
        </el-button>
      </el-space>

      <div class="stats">
        <el-tag type="info">总数: {{ total }}</el-tag>
        <el-tag type="success">正常: {{ statusStats.normal }}</el-tag>
        <el-tag type="warning">即将到期: {{ statusStats.dueSoon }}</el-tag>
        <el-tag type="danger">已过期: {{ statusStats.overdue }}</el-tag>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="cylinderList"
      border
      stripe
      max-height="500"
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

      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showDetail(row)">
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 气瓶详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="气瓶详细信息"
      width="700px"
      append-to-body
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
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

interface CylinderRecord {
  registerId: number
  contractId: number
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

interface Props {
  contractId: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'close'): void
}>()

const dialogVisible = ref(true)
const detailDialogVisible = ref(false)
const loading = ref(false)
const searchKeyword = ref('')
const cylinderList = ref<CylinderRecord[]>([])
const allCylinderList = ref<CylinderRecord[]>([])
const currentCylinder = ref<CylinderRecord | null>(null)

// 统计信息
const total = computed(() => cylinderList.value.length)

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

// 加载气瓶列表
const loadCylinderList = async () => {
  loading.value = true
  try {
    const response = await request.get(`/cylinder/register/contract/${props.contractId}`)
    if (response.data.code === 200) {
      allCylinderList.value = response.data.data || []
      cylinderList.value = allCylinderList.value
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

// 搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    cylinderList.value = allCylinderList.value
    return
  }

  const keyword = searchKeyword.value.toLowerCase()
  cylinderList.value = allCylinderList.value.filter(cylinder => {
    return (
      cylinder.cylinderNo?.toLowerCase().includes(keyword) ||
      cylinder.rfidTag?.toLowerCase().includes(keyword)
    )
  })
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
    return 'text-danger' // 已过期
  } else if (daysUntilInspect <= 30) {
    return 'text-warning' // 即将到期
  }
  return ''
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    1: 'success',  // 正常
    2: 'info',     // 使用中
    3: 'warning',  // 检验中
    4: 'danger'    // 报废
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

// 关闭对话框
const handleClose = () => {
  emit('close')
}

// 组件挂载时加载数据
onMounted(() => {
  loadCylinderList()
})
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .stats {
    display: flex;
    gap: 10px;
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
