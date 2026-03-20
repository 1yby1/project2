<template>
  <el-dialog
    v-model="visible"
    title="退回申请"
    width="500px"
    append-to-body
    @update:model-value="handleClose">
    <div class="mb-4">
      <label class="block text-sm font-medium text-gray-700 mb-2">退回原因</label>
      <el-input
        v-model="reason"
        type="textarea"
        :rows="4"
        placeholder="请输入退回原因...">
      </el-input>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="danger" @click="handleConfirm">确认退回</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'confirm': [reason: string]
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

const handleConfirm = () => {
  emit('confirm', reason.value)
}
</script>
