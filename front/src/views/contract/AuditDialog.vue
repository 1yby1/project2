<template>
  <el-dialog
    v-model="visible"
    title="缴费审核"
    width="500px"
    append-to-body
    @update:model-value="handleClose">
    <div v-if="contract">
      <div class="mb-4">
        <span class="text-gray-600">申请单位：</span>
        <span class="font-bold">{{ contract.unitName }}</span>
      </div>
      <div class="bg-yellow-50 p-4 rounded mb-4 border border-yellow-200">
        <div class="flex items-center text-warning font-bold mb-2">
          <i class="fa-solid fa-money-bill mr-2"></i> 待审核金额
        </div>
        <div class="text-2xl font-bold text-gray-800">¥ {{ contract.finalAmount }}</div>
      </div>
      
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">审核意见</label>
        <el-input 
          v-model="auditRemark" 
          type="textarea" 
          :rows="3" 
          placeholder="请输入审核意见（驳回时必填）...">
        </el-input>
      </div>
    </div>
    <template #footer>
      <el-button type="danger" plain @click="handleReject">驳回</el-button>
      <el-button type="success" @click="handleApprove">审核通过</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Contract {
  unit: string
  amount: string
}

interface Props {
  modelValue: boolean
  contract: Contract | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'reject': [remark: string]
  'approve': [remark: string]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const auditRemark = ref('')

const handleClose = () => {
  emit('update:modelValue', false)
  auditRemark.value = ''
}

const handleReject = () => {
  emit('reject', auditRemark.value)
}

const handleApprove = () => {
  emit('approve', auditRemark.value)
}
</script>
