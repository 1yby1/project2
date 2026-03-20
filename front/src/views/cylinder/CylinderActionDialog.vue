<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="气瓶编号">
        <el-input :model-value="props.cylinder.cylinderNo" readonly />
      </el-form-item>

      <template v-if="type === 'status'">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(props.cylinder.cylinderStatus)">
            {{ getStatusLabel(props.cylinder.cylinderStatus) }}
          </el-tag>
        </el-form-item>

        <el-form-item label="新状态" prop="cylinderStatus" required>
          <el-select v-model="formData.cylinderStatus" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="检验中" :value="3" />
            <el-option label="报废" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入状态变更原因"
          />
        </el-form-item>
      </template>

      <template v-if="type === 'inspect'">
        <el-form-item label="检验日期" prop="inspectDate" required>
          <el-date-picker
            v-model="formData.inspectDate"
            type="date"
            placeholder="选择检验日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="检验结果" prop="inspectResult" required>
          <el-select v-model="formData.inspectResult" placeholder="请选择检验结果" style="width: 100%">
            <el-option label="合格" value="合格" />
            <el-option label="不合格" value="不合格" />
          </el-select>
        </el-form-item>

        <el-form-item label="检验机构">
          <el-input v-model="formData.inspectOrg" placeholder="请输入检验机构" />
        </el-form-item>

        <el-form-item label="下次检验日期" prop="nextInspectDate" required>
          <el-date-picker
            v-model="formData.nextInspectDate"
            type="date"
            placeholder="选择下次检验日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入检验备注"
          />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { updateCylinderStatus, recordInspection } from '@/api/cylinder'

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

interface Props {
  cylinder: CylinderRecord
  type: 'status' | 'inspect'
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'success'): void
  (e: 'close'): void
}>()

const dialogVisible = ref(true)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const title = computed(() => {
  return props.type === 'status' ? '更新气瓶状态' : '记录气瓶检验'
})

const formData = reactive({
  registerId: props.cylinder.registerId,
  cylinderStatus: props.cylinder.cylinderStatus,
  inspectDate: '',
  inspectResult: '',
  inspectOrg: '',
  nextInspectDate: '',
  remark: ''
})

const rules: FormRules = {
  cylinderStatus: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  inspectDate: [
    { required: true, message: '请选择检验日期', trigger: 'change' }
  ],
  inspectResult: [
    { required: true, message: '请选择检验结果', trigger: 'change' }
  ],
  nextInspectDate: [
    { required: true, message: '请选择下次检验日期', trigger: 'change' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (props.type === 'status') {
        const response = await updateCylinderStatus({
          registerId: formData.registerId,
          cylinderStatus: formData.cylinderStatus,
          remark: formData.remark
        })
        
        if (response.data.code === 200) {
          ElMessage.success('状态更新成功')
          emit('success')
          handleClose()
        } else {
          ElMessage.error(response.data.message || '状态更新失败')
        }
      } else {
        const response = await recordInspection({
          registerId: formData.registerId,
          inspectDate: formData.inspectDate,
          inspectResult: formData.inspectResult,
          inspectOrg: formData.inspectOrg,
          nextInspectDate: formData.nextInspectDate,
          remark: formData.remark
        })
        
        if (response.data.code === 200) {
          ElMessage.success('检验记录成功')
          emit('success')
          handleClose()
        } else {
          ElMessage.error(response.data.message || '检验记录失败')
        }
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleClose = () => {
  emit('close')
}

const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusLabel = (status: number) => {
  const labelMap: Record<number, string> = {
    1: '正常',
    2: '使用中',
    3: '检验中',
    4: '报废'
  }
  return labelMap[status] || '未知'
}
</script>

<style scoped lang="scss">
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
