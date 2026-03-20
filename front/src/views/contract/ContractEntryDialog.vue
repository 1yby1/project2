<template>
  <el-dialog
    v-model="visible"
    title="录入初始合同"
    width="700px"
    destroy-on-close
    @update:model-value="handleClose">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="单条录入" name="single">
        <el-form :model="form" label-width="100px" class="mt-4">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="签约单位" required>
                <el-select v-model="form.unit" placeholder="请选择单位" style="width: 100%">
                  <el-option label="红星气瓶充装站" value="红星气瓶充装站"></el-option>
                  <el-option label="蓝天液化气公司" value="蓝天液化气公司"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="合同编号" required>
                <el-input v-model="form.code" placeholder="请输入合同编号"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="气瓶类型" required>
                <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                  <el-option label="LPG钢瓶" value="LPG钢瓶"></el-option>
                  <el-option label="LNG钢瓶" value="LNG钢瓶"></el-option>
                  <el-option label="氧气瓶" value="氧气瓶"></el-option>
                  <el-option label="乙炔瓶" value="乙炔瓶"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="气瓶数量" required>
                <el-input-number v-model="form.quantity" :min="1" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="合同金额" required>
                <el-input v-model="form.amount" placeholder="请输入金额">
                  <template #prepend>¥</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="生效日期" required>
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="到期日期" required>
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="批量导入" name="batch">
        <div class="text-center py-8">
          <i class="fa-solid fa-file-excel text-green-600 text-5xl mb-4"></i>
          <h4 class="font-bold text-gray-700">上传 Excel 合同清单</h4>
          <p class="text-gray-500 text-xs mt-2 mb-6">
            请下载 <a href="#" class="text-primary hover:underline">标准模板</a> 后上传。
          </p>
          <el-upload
            class="upload-demo"
            drag
            action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
            multiple>
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 xls/xlsx 文件，且不超过 500kb
              </div>
            </template>
          </el-upload>
        </div>
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交录入</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'

interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [form: any, activeTab: string]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const activeTab = ref('single')
const form = reactive({
  unit: '',
  code: '',
  type: '',
  quantity: 1,
  amount: '',
  startDate: '',
  endDate: ''
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleSubmit = () => {
  emit('submit', { ...form }, activeTab.value)
}
</script>
