<template>
  <el-dialog
    v-model="dialogVisible"
    title="批量导入气瓶"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-steps :active="currentStep" finish-status="success" align-center>
      <el-step title="下载模板" />
      <el-step title="上传文件" />
      <el-step title="导入结果" />
    </el-steps>

    <div class="step-content">
      <!-- 步骤1: 下载模板 -->
      <div v-if="currentStep === 0" class="step-1">
        <el-alert
          title="操作说明"
          type="info"
          :closable="false"
          show-icon
        >
          <p>1. 请先下载Excel模板，模板中包含详细的填写说明</p>
          <p>2. 按照模板格式填写气瓶信息（气瓶编号为必填项）</p>
          <p>3. 填写完成后，返回此页面上传文件</p>
        </el-alert>

        <div class="download-section">
          <el-button
            type="primary"
            icon="Download"
            :loading="downloading"
            @click="downloadTemplate"
          >
            下载导入模板
          </el-button>
        </div>

        <el-divider />

        <div class="cylinder-type-info" v-if="cylinderTypeId">
          <el-descriptions title="合同气瓶类型信息" :column="2" border>
            <el-descriptions-item label="气瓶类型ID">
              {{ cylinderTypeId }}
            </el-descriptions-item>
            <el-descriptions-item label="合同ID">
              {{ contractId }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 步骤2: 上传文件 -->
      <div v-if="currentStep === 1" class="step-2">
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          :file-list="fileList"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处,或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 xlsx/xls 文件,且不超过 5MB
            </div>
          </template>
        </el-upload>

        <el-alert
          v-if="uploadError"
          :title="uploadError"
          type="error"
          :closable="false"
          show-icon
          class="mt-4"
        />
      </div>

      <!-- 步骤3: 导入结果 -->
      <div v-if="currentStep === 2" class="step-3">
        <el-result
          :icon="importResult.failureCount === 0 ? 'success' : 'warning'"
          :title="getResultTitle()"
        >
          <template #sub-title>
            <div>
              <p>成功导入: {{ importResult.successCount }} 条</p>
              <p v-if="importResult.failureCount > 0">
                失败: {{ importResult.failureCount }} 条
              </p>
            </div>
          </template>
          <template #extra>
            <el-button type="primary" @click="handleClose">完成</el-button>
            <el-button v-if="importResult.failureCount > 0" @click="currentStep = 1">
              重新上传
            </el-button>
          </template>
        </el-result>

        <!-- 错误详情表格 -->
        <div v-if="importResult.errors && importResult.errors.length > 0" class="error-details">
          <el-divider content-position="left">错误详情</el-divider>
          <el-table :data="importResult.errors" border stripe max-height="300">
            <el-table-column prop="rowNum" label="行号" width="80" align="center" />
            <el-table-column prop="errorMsg" label="错误信息" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button v-if="currentStep > 0 && currentStep < 2" @click="currentStep--">
          上一步
        </el-button>
        <el-button
          v-if="currentStep === 0"
          type="primary"
          @click="currentStep++"
        >
          下一步
        </el-button>
        <el-button
          v-if="currentStep === 1"
          type="primary"
          :loading="uploading"
          :disabled="fileList.length === 0"
          @click="handleUpload"
        >
          开始导入
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, UploadFile, UploadFiles, UploadInstance } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

interface Props {
  contractId: number
  cylinderTypeId: number
}

interface ImportResult {
  successCount: number
  failureCount: number
  errors: Array<{
    rowNum: number
    errorMsg: string
  }>
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'success'): void
  (e: 'close'): void
}>()

const dialogVisible = ref(true)
const currentStep = ref(0)
const downloading = ref(false)
const uploading = ref(false)
const uploadError = ref('')
const fileList = ref<UploadFile[]>([])
const uploadRef = ref<UploadInstance>()

const importResult = reactive<ImportResult>({
  successCount: 0,
  failureCount: 0,
  errors: []
})

// 下载模板
const downloadTemplate = async () => {
  downloading.value = true
  try {
    const response = await request.get('/cylinder/register/template/download')
    if (response.data.code === 200 && response.data.data) {
      // 创建下载链接
      const link = document.createElement('a')
      link.href = response.data.data
      link.download = '气瓶导入模板.xlsx'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      ElMessage.success('模板下载成功')
    } else {
      ElMessage.error(response.data.message || '下载失败')
    }
  } catch (error: unknown) {
    console.error('下载模板失败:', error)
    ElMessage.error((error as Error).message || '下载模板失败')
  } finally {
    downloading.value = false
  }
}

// 文件选择变化
const handleFileChange = (file: UploadFile, files: UploadFiles) => {
  uploadError.value = ''
  
  // 检查文件大小
  if (file.size && file.size > 5 * 1024 * 1024) {
    uploadError.value = '文件大小不能超过 5MB'
    files.pop()
    return
  }
  
  // 检查文件格式
  const fileName = file.name
  if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
    uploadError.value = '只支持 .xlsx 和 .xls 格式的文件'
    files.pop()
    return
  }
  
  fileList.value = files
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

// 上传文件
const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  uploading.value = true
  uploadError.value = ''

  try {
    const formData = new FormData()
    formData.append('file', fileList.value[0].raw as File)
    formData.append('contractId', String(props.contractId))
    formData.append('cylinderTypeId', String(props.cylinderTypeId))

    const response = await request.post('/cylinder/register/import/excel', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 200) {
      const result = response.data.data
      importResult.successCount = result.successCount || 0
      importResult.failureCount = result.failureCount || 0
      importResult.errors = result.errors || []
      
      currentStep.value = 2
      
      if (importResult.failureCount === 0) {
        ElMessage.success(`成功导入 ${importResult.successCount} 条气瓶数据`)
        emit('success')
      } else {
        ElMessage.warning(
          `导入完成: 成功 ${importResult.successCount} 条, 失败 ${importResult.failureCount} 条`
        )
      }
    } else {
      uploadError.value = response.data.message || '导入失败'
      ElMessage.error(uploadError.value)
    }
  } catch (error: unknown) {
    console.error('上传失败:', error)
    uploadError.value = (error as Error).message || '上传失败,请稍后重试'
    ElMessage.error(uploadError.value)
  } finally {
    uploading.value = false
  }
}

// 获取结果标题
const getResultTitle = () => {
  if (importResult.failureCount === 0) {
    return '导入成功!'
  } else {
    return '导入完成,部分数据失败'
  }
}

// 关闭对话框
const handleClose = () => {
  emit('close')
}
</script>

<style scoped lang="scss">
.step-content {
  margin: 30px 0;
  min-height: 300px;
}

.step-1 {
  .download-section {
    text-align: center;
    margin: 30px 0;
  }

  .cylinder-type-info {
    margin-top: 20px;
  }
}

.step-2 {
  .upload-demo {
    :deep(.el-upload-dragger) {
      padding: 40px;
    }
  }

  .mt-4 {
    margin-top: 16px;
  }
}

.step-3 {
  .error-details {
    margin-top: 20px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
