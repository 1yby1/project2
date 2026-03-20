<template>
  <el-dialog
    v-model="visible"
    title="申请新合同"
    width="900px"
    top="5vh"
    @update:model-value="handleClose">
    <el-form :model="localForm" label-position="top">
      <div class="grid grid-cols-2 gap-8">
        <div class="bg-gray-50 p-4 rounded-lg border border-gray-100">
          <h3 class="font-bold text-gray-700 mb-4 border-l-4 border-primary pl-2">甲方（申请单位）信息</h3>
          <el-form-item label="单位名称">
            <el-input :model-value="localForm.unitName" readonly class="bg-gray-100"></el-input>
          </el-form-item>
          <el-form-item label="社会信用代码">
            <el-input :model-value="localForm.unitCode" readonly class="bg-gray-100"></el-input>
          </el-form-item>
          <el-form-item label="单位地址">
            <el-input :model-value="localForm.unitAddress" type="textarea" :rows="2" readonly class="bg-gray-100"></el-input>
          </el-form-item>
          <el-form-item label="经营气瓶类型">
            <el-input :model-value="localForm.unitTypes" readonly class="bg-gray-100"></el-input>
          </el-form-item>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="负责人">
                <el-input :model-value="localForm.unitPerson" readonly class="bg-gray-100"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="负责人电话">
                <el-input :model-value="localForm.unitPhone" readonly class="bg-gray-100"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="开户银行">
            <el-input :model-value="localForm.unitBank" readonly class="bg-gray-100"></el-input>
          </el-form-item>
          <el-form-item label="银行账号">
            <el-input :model-value="localForm.unitAccount" readonly class="bg-gray-100"></el-input>
          </el-form-item>
        </div>

        <div class="bg-blue-50 p-4 rounded-lg border border-blue-100">
          <h3 class="font-bold text-gray-700 mb-4 border-l-4 border-blue-400 pl-2">乙方（服务公司）信息</h3>
          <el-form-item label="公司名称">
            <el-input :model-value="localForm.compName" readonly class="bg-gray-100"></el-input>
          </el-form-item>
          <el-form-item label="统一信用代码">
            <el-input :model-value="localForm.compCode" readonly></el-input>
          </el-form-item>
          <el-form-item label="公司地址">
            <el-input :model-value="localForm.compAddress" type="textarea" :rows="2" readonly></el-input>
          </el-form-item>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="联系人">
                <el-input :model-value="localForm.compPerson" readonly></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话">
                <el-input :model-value="localForm.compPhone" readonly></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="开户银行">
            <el-input :model-value="localForm.compBank" readonly></el-input>
          </el-form-item>
          <el-form-item label="银行账号">
            <el-input :model-value="localForm.compAccount" readonly></el-input>
          </el-form-item>
        </div>
      </div>

      <div class="mt-6 pt-6 border-t border-gray-200">
        <h3 class="font-bold text-gray-700 mb-4 border-l-4 border-primary pl-2">合同详情信息</h3>
        <div class="bg-gray-50 p-6 rounded-lg border border-gray-100">
          <el-row :gutter="24">
            <!-- 第一行：基本信息 -->
            <el-col :span="8">
              <el-form-item label="合同编号" required>
                <el-input :model-value="localForm.contractNo" readonly placeholder="自动生成" class="w-full"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="气瓶类型" required>
                <el-select 
                  v-model="localForm.cylinderTypeId" 
                  placeholder="请选择类型" 
                  class="w-full" 
                  style="width: 100%"
                  @change="handleCylinderTypeChange">
                  <el-option 
                    v-for="item in cylinderTypeList" 
                    :key="item.cylinder_type_id" 
                    :label="item.name" 
                    :value="item.cylinder_type_id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="气瓶数量 (个)" required>
                <el-input-number v-model="localForm.quantity" :min="1" :step="10" class="!w-full" style="width: 100%" controls-position="right"></el-input-number>
              </el-form-item>
            </el-col>
          
            <!-- 第二行：期限信息 -->
            <el-col :span="8">
              <el-form-item label="生效日期" required>
                <el-date-picker 
                  v-model="localForm.startDate" 
                  type="date" 
                  placeholder="选择生效日期"
                  class="!w-full"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="续约年限 (年)" required>
                <el-input-number v-model="localForm.years" :min="1" :max="10" class="!w-full" style="width: 100%" controls-position="right"></el-input-number>
              </el-form-item>
            </el-col>
              <el-col :span="8">
              <el-form-item label="预计结束日期">
                <el-input :model-value="calculatedEndDate" readonly placeholder="系统自动计算" class="bg-gray-50"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 第三行：金额总计 (突出显示) -->
          <div class="mt-4 pt-4 border-t border-gray-200 flex justify-end items-center">
            <span class="text-gray-500 mr-4">预计合同总金额:</span>
            <span class="text-3xl font-bold text-red-500 font-mono">
              <small class="text-lg mr-1">¥</small>{{ calculatedAmount.toLocaleString() }}
            </span>
          </div>
        </div>
      </div>

      <!-- 气瓶列表导入区域 -->
      <div class="mt-6 pt-6 border-t border-gray-200">
        <h3 class="font-bold text-gray-700 mb-4 border-l-4 border-primary pl-2">
          气瓶列表信息
          <span class="text-sm font-normal text-gray-500 ml-2">（请上传合同所涉及的气瓶清单）</span>
        </h3>
        <div class="bg-blue-50 p-6 rounded-lg border border-blue-100">
          <el-alert
            title="温馨提示"
            type="info"
            :closable="false"
            class="mb-4"
          >
            <p>请按照模板格式填写您单位的气瓶清单，包含气瓶编号、RFID标签、制造日期等信息。</p>
          </el-alert>
          
          <div class="flex items-center gap-4">
            <el-button 
              type="primary" 
              plain 
              icon="Download"
              @click="handleDownloadCylinderTemplate"
              :loading="templateDownloading"
            >
              下载气瓶导入模板
            </el-button>
            
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :limit="1"
              accept=".xlsx,.xls"
              :on-change="handleCylinderFileChange"
              :on-exceed="handleFileExceed"
              :file-list="cylinderFileList"
              class="flex-1"
            >
              <el-button type="success" icon="Upload">
                {{ cylinderFileList.length > 0 ? '更换文件' : '上传气瓶清单' }}
              </el-button>
            </el-upload>
          </div>
          
          <div v-if="cylinderFileList.length > 0" class="mt-4">
            <el-tag type="success" size="large">
              <i class="fa-solid fa-file-excel mr-2"></i>
              已选择: {{ cylinderFileList[0].name }}
            </el-tag>
            <el-button 
              link 
              type="danger" 
              class="ml-2"
              @click="removeCylinderFile"
            >
              移除
            </el-button>
          </div>
        </div>
      </div>
    </el-form>
    <template #footer>
      <div class="flex justify-between items-center">
        <div class="text-xs text-gray-400">
          <i class="fa-solid fa-circle-info mr-1"></i> 提交后将由平台管理员进行审核
        </div>
        <div>
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" plain @click="handlePreview">预览合同</el-button>
          <el-button type="success" @click="handleSubmit" :loading="submitting">提交合同</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, watch, ref, onMounted } from 'vue'
import { getCylinderTypeList } from '@/api/contract'
import { downloadCylinderTemplate as apiDownloadCylinderTemplate } from '@/api/cylinder'
import { ElMessage, UploadFile, UploadFiles, UploadInstance } from 'element-plus'

const cylinderTypeList = ref<any[]>([])
const templateDownloading = ref(false)
const cylinderFileList = ref<UploadFile[]>([])
const uploadRef = ref<UploadInstance>()
const submitting = ref(false)

const PRICE_MAP: Record<number, number> = {
  1: 100,  // LPG
  2: 100,  // CNG
  3: 120,  // O2
  4: 150,  // N2
  5: 90,   // CO2
  6: 110,  // AR
  7: 200,  // H2
  8: 180   // ACETYLENE
}

interface Props {
  modelValue: boolean
  form: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'preview': []
  'update:form': [form: any]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 创建本地可编辑副本
const localForm = reactive({ ...props.form })

// 监听 props 变化同步到本地
watch(() => props.form, (newForm) => {
  Object.assign(localForm, newForm)
}, { deep: true })

// 监听本地变化同步回父组件
watch(localForm, (newVal) => {
  emit('update:form', { ...newVal })
}, { deep: true })

// 初始化气瓶类型列表
onMounted(async () => {
  try {
    const res = await getCylinderTypeList()
    console.log('气瓶类型列表', res)
    if (res.code === 200 && res.data) {
      cylinderTypeList.value = res.data
    }
  } catch (error) {
    console.error('获取气瓶类型失败', error)
  }
})

const calculatedAmount = computed(() => {
  const price = PRICE_MAP[localForm.cylinderTypeId] || 100
  return price * localForm.quantity * localForm.years
})

const calculatedEndDate = computed(() => {
  if (!localForm.startDate) return '-'
  const startDate = new Date(localForm.startDate)
  const endDate = new Date(startDate)
  endDate.setFullYear(endDate.getFullYear() + localForm.years)
  return endDate.toISOString().split('T')[0]
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const handlePreview = () => {
  emit('preview')
}

// 处理气瓶类型选择变化
const handleCylinderTypeChange = () => {
  const selected = cylinderTypeList.value.find(item => item.cylinder_type_id === localForm.cylinderTypeId)
  if (selected) {
    localForm.cylinderTypeName = selected.name
    // 保持兼容旧字段，便于其他地方直接读名称
    localForm.type = selected.name
  }
}

// 下载气瓶模板
const handleDownloadCylinderTemplate = async () => {
  templateDownloading.value = true
  try {
    const response = await apiDownloadCylinderTemplate()
    if (response.code === 200 && response.data) {
      const link = document.createElement('a')
      link.href = response.data
      link.download = '气瓶导入模板.xlsx'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success('模板下载成功')
    } else {
      ElMessage.error(response.message || '下载失败')
    }
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败')
  } finally {
    templateDownloading.value = false
  }
}

// 处理气瓶文件选择
const handleCylinderFileChange = (file: UploadFile, files: UploadFiles) => {
  const fileName = file.name
  if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
    ElMessage.warning('只支持 .xlsx 和 .xls 格式的文件')
    files.pop()
    return
  }
  
  if (file.size && file.size > 5 * 1024 * 1024) {
    ElMessage.warning('文件大小不能超过 5MB')
    files.pop()
    return
  }
  
  cylinderFileList.value = files
}

// 文件超出限制
const handleFileExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

// 移除气瓶文件
const removeCylinderFile = () => {
  cylinderFileList.value = []
  uploadRef.value?.clearFiles()
}

// 提交合同
const handleSubmit = () => {
  // 验证必填项
  if (!localForm.cylinderTypeId) {
    ElMessage.warning('请选择气瓶类型')
    return
  }
  if (!localForm.quantity || localForm.quantity < 1) {
    ElMessage.warning('请输入气瓶数量')
    return
  }
  if (!localForm.startDate) {
    ElMessage.warning('请选择生效日期')
    return
  }
  if (!localForm.years || localForm.years < 1) {
    ElMessage.warning('请输入续约年限')
    return
  }
  
  // 将气瓶文件信息保存到表单，供预览和提交使用
  if (cylinderFileList.value.length > 0) {
    localForm.cylinderFile = cylinderFileList.value[0]
  }
  
  // 触发预览事件，由父组件处理实际提交
  emit('preview')
}
</script>
