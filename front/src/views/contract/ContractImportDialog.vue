<template>
  <el-dialog
    v-model="visible"
    title="批量导入合同"
    width="700px"
    destroy-on-close
    :close-on-click-modal="false">
    
    <div class="import-container">
      <!-- 步骤指示 -->
      <el-steps :active="currentStep" align-center class="mb-6">
        <el-step title="下载模板" />
        <el-step title="上传文件" />
        <el-step title="导入结果" />
      </el-steps>

      <!-- 步骤1: 下载模板 -->
      <div v-if="currentStep === 0" class="step-content">
        <el-alert
          title="使用说明"
          type="info"
          :closable="false"
          class="mb-4">
          <template #default>
            <div class="text-sm space-y-2">
              <p>1. 请先下载导入模板，按照模板格式填写数据</p>
              <p>2. 模板中带*的列为必填项</p>
              <p>3. 填写完成后，在下一步上传Excel文件</p>
              <p>4. 系统会自动验证数据并导入</p>
            </div>
          </template>
        </el-alert>

        <div class="text-center py-8">
          <el-button 
            type="primary" 
            size="large"
            :loading="downloadLoading"
            @click="handleDownloadTemplate">
            <i class="fa-solid fa-download mr-2"></i>
            下载导入模板
          </el-button>
          <p class="text-gray-500 text-sm mt-4">建议使用 Microsoft Excel 或 WPS 打开</p>
        </div>

        <div class="text-right mt-6">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="currentStep = 1">
            下一步
            <i class="fa-solid fa-arrow-right ml-1"></i>
          </el-button>
        </div>
      </div>

      <!-- 步骤2: 上传文件 -->
      <div v-if="currentStep === 1" class="step-content">
        <el-alert
          title="上传提示"
          type="warning"
          :closable="false"
          class="mb-4">
          <template #default>
            <div class="text-sm space-y-1">
              <p>• 仅支持 .xlsx 或 .xls 格式的Excel文件</p>
              <p>• 文件大小不超过 10MB</p>
              <p>• 每次最多导入 1000 条记录</p>
            </div>
          </template>
        </el-alert>

        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          accept=".xlsx,.xls">
          <div class="upload-area">
            <i class="fa-solid fa-cloud-arrow-up text-6xl text-gray-400 mb-4"></i>
            <div class="el-upload__text">
              将Excel文件拖到此处，或<em>点击上传</em>
            </div>
            <div class="el-upload__tip text-xs text-gray-500 mt-2">
              支持 .xlsx 和 .xls 格式
            </div>
          </div>
        </el-upload>

        <div v-if="uploadFile" class="mt-4 p-3 bg-blue-50 rounded flex items-center justify-between">
          <div class="flex items-center">
            <i class="fa-solid fa-file-excel text-green-600 text-2xl mr-3"></i>
            <div>
              <p class="font-medium">{{ uploadFile.name }}</p>
              <p class="text-xs text-gray-500">{{ formatFileSize(uploadFile.size) }}</p>
            </div>
          </div>
          <el-button 
            type="danger" 
            size="small" 
            text
            @click="handleRemoveFile">
            <i class="fa-solid fa-trash"></i>
          </el-button>
        </div>

        <div class="text-right mt-6">
          <el-button @click="currentStep = 0">
            <i class="fa-solid fa-arrow-left mr-1"></i>
            上一步
          </el-button>
          <el-button 
            type="primary" 
            :disabled="!uploadFile"
            :loading="importing"
            @click="handleImport">
            <i class="fa-solid fa-file-import mr-2"></i>
            开始导入
          </el-button>
        </div>
      </div>

      <!-- 步骤3: 导入结果 -->
      <div v-if="currentStep === 2" class="step-content">
        <div v-if="importResult" class="result-summary mb-6">
          <el-result
            :icon="importResult.failureCount === 0 ? 'success' : 'warning'"
            :title="importResult.failureCount === 0 ? '导入成功' : '导入完成（部分失败）'">
            <template #sub-title>
              <div class="text-base space-y-1">
                <p class="text-green-600">
                  <i class="fa-solid fa-circle-check mr-2"></i>
                  成功: {{ importResult.successCount }} 条
                </p>
                <p v-if="importResult.failureCount > 0" class="text-red-600">
                  <i class="fa-solid fa-circle-xmark mr-2"></i>
                  失败: {{ importResult.failureCount }} 条
                </p>
              </div>
            </template>
          </el-result>
        </div>

        <!-- 错误详情 -->
        <div v-if="importResult && importResult.errors && importResult.errors.length > 0" class="error-details">
          <div class="flex items-center justify-between mb-3">
            <h4 class="text-base font-medium text-red-600">
              <i class="fa-solid fa-exclamation-triangle mr-2"></i>
              错误详情
            </h4>
            <el-button 
              size="small" 
              type="primary"
              @click="exportErrors">
              <i class="fa-solid fa-download mr-1"></i>
              导出错误信息
            </el-button>
          </div>
          
          <el-table 
            :data="importResult.errors" 
            border
            size="small"
            max-height="300"
            class="error-table">
            <el-table-column prop="rowNum" label="行号" width="80" align="center" />
            <el-table-column prop="errorMsg" label="错误信息" show-overflow-tooltip />
          </el-table>
        </div>

        <div class="text-right mt-6">
          <el-button @click="handleClose">关闭</el-button>
          <el-button 
            v-if="importResult && importResult.failureCount > 0"
            type="warning" 
            @click="handleRetry">
            <i class="fa-solid fa-rotate-right mr-1"></i>
            重新导入
          </el-button>
          <el-button 
            v-if="importResult && importResult.successCount > 0"
            type="primary" 
            @click="handleViewContracts">
            <i class="fa-solid fa-list mr-1"></i>
            查看导入的合同
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox, type UploadInstance, type UploadFile } from 'element-plus'
import { downloadTemplate, importContracts } from '@/api/contract'

interface ErrorRow {
  rowNum: number
  errorMsg: string
}

interface ImportResult {
  successCount: number
  failureCount: number
  errors: ErrorRow[]
}

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'success': []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const currentStep = ref(0)
const downloadLoading = ref(false)
const uploadRef = ref<UploadInstance>()
const uploadFile = ref<File | null>(null)
const importing = ref(false)
const importResult = ref<ImportResult | null>(null)

// 下载模板
const handleDownloadTemplate = async () => {
  downloadLoading.value = true
  try {
    const res = await downloadTemplate()
    if (res.code === 200 && res.data) {
      // 打开新窗口下载
      window.open(res.data, '_blank')
      ElMessage.success('模板下载成功')
    } else {
      ElMessage.warning(res.msg || '下载失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '下载模板失败')
  } finally {
    downloadLoading.value = false
  }
}

// 文件选择
const handleFileChange = (file: UploadFile) => {
  // 验证文件类型
  const isExcel = file.name?.endsWith('.xlsx') || file.name?.endsWith('.xls')
  if (!isExcel) {
    ElMessage.error('只支持 .xlsx 或 .xls 格式的Excel文件')
    uploadRef.value?.clearFiles()
    return
  }

  // 验证文件大小（10MB）
  const isLt10M = file.size! / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB')
    uploadRef.value?.clearFiles()
    return
  }

  uploadFile.value = file.raw as File
}

// 文件数量超出
const handleExceed = () => {
  ElMessage.warning('一次只能上传一个文件')
}

// 移除文件
const handleRemoveFile = () => {
  uploadFile.value = null
  uploadRef.value?.clearFiles()
}

// 开始导入
const handleImport = async () => {
  if (!uploadFile.value) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }

  importing.value = true
  try {
    const res = await importContracts(uploadFile.value)
    if (res.code === 200) {
      importResult.value = res.data
      currentStep.value = 2
      
      if (res.data.failureCount === 0) {
        ElMessage.success(`导入成功！共导入 ${res.data.successCount} 条记录`)
      } else {
        ElMessage.warning(
          `导入完成！成功 ${res.data.successCount} 条，失败 ${res.data.failureCount} 条`
        )
      }
    } else {
      ElMessage.error(res.msg || '导入失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '导入失败，请检查文件格式')
  } finally {
    importing.value = false
  }
}

// 导出错误信息
const exportErrors = () => {
  if (!importResult.value?.errors) return

  // 生成CSV内容
  let csvContent = '行号,错误信息\n'
  importResult.value.errors.forEach(error => {
    csvContent += `${error.rowNum},"${error.errorMsg}"\n`
  })

  // 创建Blob并下载
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `导入错误_${new Date().getTime()}.csv`
  link.click()
  URL.revokeObjectURL(link.href)

  ElMessage.success('错误信息已导出')
}

// 重新导入
const handleRetry = () => {
  currentStep.value = 1
  uploadFile.value = null
  uploadRef.value?.clearFiles()
  importResult.value = null
}

// 查看导入的合同
const handleViewContracts = () => {
  emit('success')
  handleClose()
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  // 重置状态
  setTimeout(() => {
    currentStep.value = 0
    uploadFile.value = null
    uploadRef.value?.clearFiles()
    importResult.value = null
  }, 300)
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / 1024 / 1024).toFixed(2) + ' MB'
}
</script>

<style scoped>
.import-container {
  padding: 10px 0;
}

.step-content {
  min-height: 300px;
}

.upload-area {
  padding: 60px 20px;
  text-align: center;
}

.upload-demo :deep(.el-upload-dragger) {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  transition: all 0.3s;
}

.upload-demo :deep(.el-upload-dragger:hover) {
  border-color: #409eff;
}

.result-summary :deep(.el-result__title) {
  margin-top: 10px;
}

.error-table {
  font-size: 13px;
}

.error-table :deep(.el-table__row:hover) {
  background-color: #fef0f0;
}
</style>
