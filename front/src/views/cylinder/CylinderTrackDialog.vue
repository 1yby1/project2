<template>
  <el-dialog
    v-model="visible"
    title="气瓶追踪详情"
    width="800px"
    @close="handleClose">
    <div class="mb-4 bg-gray-50 p-3 rounded flex justify-between">
      <div>
        <span class="text-gray-500 mr-2">合同编号:</span>
        <span class="font-bold">{{ contract?.contractNo }}</span>
      </div>
      <div>
        <span class="text-gray-500 mr-2">气瓶类型:</span>
        <el-tag size="small">{{ contract?.cylinderType }}</el-tag>
      </div>
      <div>
        <span class="text-gray-500 mr-2">总数量:</span>
        <span class="font-mono font-bold">{{ contract?.quantity }}</span>
      </div>
    </div>

    <div class="flex justify-between items-center mb-4">
      <el-input 
        v-model="searchCode"
        placeholder="输入气瓶编码搜索"
        prefix-icon="Search"
        clearable
        style="width: 240px"
        @input="handleFilter" />
        
      <el-radio-group v-model="statusFilter" size="small" @change="handleFilter">
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="normal">正常</el-radio-button>
        <el-radio-button label="issue">异常</el-radio-button>
      </el-radio-group>
    </div>

    <el-table 
      :data="pagedList"
      border
      stripe
      style="width: 100%"
      height="400">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="cylinderCode" label="气瓶编码" min-width="120" sortable />
      <el-table-column prop="manufactureDate" label="制造日期" width="120" align="center" />
      <el-table-column prop="lastInspectDate" label="最近检验" width="120" align="center" />
      <el-table-column label="当前状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" align="center">
        <template #default>
          <el-button link type="primary" size="small">轨迹</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="mt-4 flex justify-end">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, prev, pager, next"
        background />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface Props {
  modelValue: boolean
  contract: any
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const searchCode = ref('')
const statusFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

// 模拟数据 - 实际应从后端API ContractCylinderItem 加载
const cylinderList = ref<any[]>([])
const fullList = ref<any[]>([])

const generateMockData = () => {
  if (!props.contract) return
  const list = []
  for (let i = 1; i <= props.contract.quantity; i++) {
    const isNormal = Math.random() > 0.05
    list.push({
      id: i,
      cylinderCode: `QP-${props.contract.cylinderType === 'LPG钢瓶' ? 'LPG' : 'LNG'}-${20240000 + i}`,
      manufactureDate: '2023-05-15',
      lastInspectDate: '2024-01-10',
      status: isNormal ? '正常' : '异常'
    })
  }
  fullList.value = list
  handleFilter()
}

watch(() => props.contract, (newVal) => {
  if (newVal) {
    generateMockData()
  }
}, { immediate: true })

const filteredList = computed(() => {
  let list = fullList.value
  
  if (searchCode.value) {
    list = list.filter(item => item.cylinderCode.includes(searchCode.value))
  }
  
  if (statusFilter.value !== 'all') {
    const targetStatus = statusFilter.value === 'normal' ? '正常' : '异常'
    list = list.filter(item => item.status === targetStatus)
  }
  
  return list
})

const total = computed(() => filteredList.value.length)

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredList.value.slice(start, end)
})

const handleFilter = () => {
  currentPage.value = 1
}

const handleClose = () => {
  emit('update:modelValue', false)
}
</script>
