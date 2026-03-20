<template>
  <el-table 
    :data="contracts" 
    stripe
    border
    style="width: 100%"
    :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
    :row-style="{ height: '55px' }">
    <el-table-column label="合同编号" min-width="140">
      <template #default="{ row }">
        {{ row.contractNo || row.code || '-' }}
      </template>
    </el-table-column>
    <el-table-column v-if="showUnitColumn" label="签约单位" min-width="180">
      <template #default="{ row }">
        {{ row.unitName || row.unit || row.partyAName || '-' }}
      </template>
    </el-table-column>
    <el-table-column label="气瓶类型" min-width="120">
      <template #default="{ row }">
        {{ row.cylinderType || row.cylinderTypes || row.type || '-' }}
      </template>
    </el-table-column>
    <el-table-column prop="cylinderQty" label="数量" min-width="90" align="center" />
    <el-table-column  label="年限" min-width="80" align="center" >
      <template #default="{ row }">
        {{ row.startDate && row.endDate ? new Date(row.endDate).getFullYear() - new Date(row.startDate).getFullYear() : '-' }}
      </template>
    </el-table-column>
    <el-table-column label="生效/结束" min-width="200" align="center">
      <template #default="{ row }">
        <span class="text-xs text-gray-500">{{ row.startDate || '-' }} ~ {{ row.endDate || '-' }}</span>
      </template>
    </el-table-column>
    <el-table-column label="金额" min-width="120" align="right">
      <template #default="{ row }">
        <span class="text-error font-medium">¥{{ row.finalAmount ?? '-' }}</span>
      </template>
    </el-table-column>
    <el-table-column label="状态" min-width="100" align="center">
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.contractStatus)" size="small" effect="light" round>{{ getStatusLabel(row.contractStatus) }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" fixed="right" width="220" align="center">
      <template #default="{ row }">
        <el-button 
          link 
          type="primary" 
          size="small"
          @click="emit('view-detail', row)">
          详情
        </el-button>

        <template v-if="isAdmin">
          <!-- 状态1: 待确认 -->
          <el-button 
            v-if="row.contractStatus === 1"
            link 
            type="success" 
            size="small"
            @click="emit('confirm', row)">
            确认
          </el-button>
          <el-button 
            v-if="row.contractStatus === 1"
            link 
            type="danger" 
            size="small"
            @click="emit('return', row)">
            退回
          </el-button>
          <!-- 状态4: 待审核缴费 -->
          <el-button 
            v-if="row.contractStatus === 4"
            link 
            type="warning" 
            size="small"
            @click="emit('audit', row)">
            审核
          </el-button>
          <!-- 状态5: 待签章，可导出PDF签章 -->
          <el-button 
            v-if="row.contractStatus === 5"
            link 
            type="primary" 
            size="small"
            @click="emit('sign', row)">
            导出签章
          </el-button>
        </template>

        <template v-else>
          <!-- 状态3: 待缴费 -->
          <el-button 
            v-if="row.contractStatus === 3"
            link 
            type="warning" 
            size="small"
            @click="emit('payment', row)">
            去缴费
          </el-button>
          <!-- 状态2: 已退回，可重新提交 -->
          <el-button 
            v-if="row.contractStatus === 2"
            link 
            type="primary" 
            size="small"
            @click="emit('edit', row)">
            修改并重新提交
          </el-button>
        </template>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
interface Contract {
  id?: number
  contractNo?: string
  code?: string
  unit?: string
  unitName?: string
  partyAName?: string
  cylinderType?: string
  cylinderTypes?: string
  type?: string
  quantity?: number
  years?: number
  amount?: number | string
  startDate?: string
  endDate?: string
  status?: number | string
  remarks?: string
}

interface Props {
  contracts: Contract[]
  isAdmin?: boolean
  showUnitColumn?: boolean
}

withDefaults(defineProps<Props>(), {
  isAdmin: false,
  showUnitColumn: false
})

const emit = defineEmits<{
  'view-detail': [contract: Contract]
  'confirm': [contract: Contract]
  'return': [contract: Contract]
  'audit': [contract: Contract]
  'sign': [contract: Contract]
  'payment': [contract: Contract]
}>()

// 状态颜色映射（后端返回数值码）
const getStatusType = (status: number | string) => {
  const code = Number(status)
  const typeMap: Record<number, string> = {
    1: 'warning',   // 待确认
    2: 'info',      // 已退回
    3: 'warning',   // 待缴费
    4: 'warning',   // 待审核缴费
    5: 'primary',   // 待签章
    6: 'success',   // 已生效
    7: 'danger',    // 即将到期
    8: 'info',      // 已到期
    9: 'danger',    // 已作废
    [-1]: 'danger'  // 已拒绝
  }
  return typeMap[code] || 'info'
}

// 状态文案映射
const getStatusLabel = (status: number | string) => {
  const code = Number(status)
  const labelMap: Record<number, string> = {
    1: '待确认',
    2: '已退回',
    3: '待缴费',
    4: '待审核缴费',
    5: '待签章',
    6: '已生效',
    7: '即将到期',
    8: '已到期',
    9: '已作废',
    [-1]: '已拒绝'
  }
  return labelMap[code] || '-'
}
</script>
