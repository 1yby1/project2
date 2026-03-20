<template>
  <el-dialog
    v-model="visible"
    title="申请作废合同"
    width="520px"
    append-to-body
    @update:model-value="handleClose">
    <div v-if="contract">
      <div class="text-sm text-gray-600 mb-4">
        您正在申请作废合同 <span class="font-bold text-gray-800">{{ contract.code }}</span>，请谨慎操作。
      </div>
      
      <div class="mb-4">
        <label class="block text-sm font-semibold text-gray-700 mb-2">
          作废原因<span class="text-red-500">*</span>
        </label>
        <el-input
          v-model="reason"
          type="textarea"
          :rows="4"
          placeholder="请填写作废原因（如：误签、业务终止、信息填错等）">
        </el-input>
      </div>

      <div class="bg-gray-50 border border-gray-200 p-3 rounded text-xs text-gray-700">
        <i class="fa-solid fa-circle-info mr-1 text-primary"></i> 提交后需等待管理员审核，审核通过后合同将正式失效。
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="warning" @click="handleSubmit">提交申请</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

interface Contract {
  code: string
}

interface Props {
  modelValue: boolean
  contract: Contract | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [reason: string]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const reason = ref('')

watch(visible, (newVal) => {
  if (!newVal) {
    reason.value = ''
  }
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleSubmit = () => {
  emit('submit', reason.value)
}
</script>
