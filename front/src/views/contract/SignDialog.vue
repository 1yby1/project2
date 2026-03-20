<template>
  <el-dialog
    v-model="visible"
    title="电子签名及盖章"
    width="900px"
    destroy-on-close
    append-to-body
    @update:model-value="handleClose">
    
    <!-- 合同预览区域 -->
    <div class="contract-preview-shell">
      <div v-if="contract" class="preview-paper">
        <h1 class="contract-title">气瓶安全监管软件服务合同</h1>
        
        <div class="contract-header">
          <div class="header-row">
            <span>合同编号：{{ contract.contractNo || contract.code }}</span>
            <span>签订日期：{{ formatDate(new Date()) }}</span>
          </div>
          <div class="header-row">
            <span>有效期：{{ getYearDiff(contract.startDate, contract.endDate) }} 年</span>
            <span>（{{ contract.startDate }} 至 {{ contract.endDate }}）</span>
          </div>
        </div>

        <div class="section-title">甲方（申请单位）信息</div>
        <div class="info-grid">
          <div class="label">单位名称：</div>
          <div class="value">{{ contract.unitName }}</div>
          
          <div class="label">社会信用代码：</div>
          <div class="value">{{ contract.socialCreditCode }}</div>
          
          <div class="label">单位地址：</div>
          <div class="value full">{{ contract.unitAddress }}</div>
          
          <div class="label">负责人：</div>
          <div class="value">{{ contract.unitPrincipalName || '-' }}</div>

          <div class="label">联系电话：</div>
          <div class="value">{{ contract.unitPrincipalPhone || '-' }}</div>
        </div>

        <div class="section-title">乙方（瓶安保）信息</div>
        <div class="info-grid">
          <div class="label">公司名称：</div>
          <div class="value">瓶安保科技有限公司</div>
          
          <div class="label">统一信用代码：</div>
          <div class="value">91110000MA00000000</div>
          
          <div class="label">公司地址：</div>
          <div class="value full">北京市西城区长椿街1号</div>
        </div>

        <div class="contract-body">
          <h4>第一条：服务内容及范围</h4>
          <table class="fee-table">
            <thead>
              <tr>
                <th>气瓶类型</th>
                <th>数量（只）</th>
                <th>服务单价</th>
                <th>年份</th>
                <th>小计（元）</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="contract.cylinderLines && contract.cylinderLines.length">
                <td>{{ contract.cylinderLines[0].cylinderTypeName || '-' }}</td>
                <td>{{ contract.cylinderLines[0].cylinderQty || '-' }}</td>
                <td>¥{{ contract.cylinderLines[0].unitPrice || '-' }}</td>
                <td>{{ getYearDiff(contract.startDate, contract.endDate) }}</td>
                <td>¥{{ contract.finalAmount || contract.amount }}</td>
              </tr>
              <tr>
                <td colspan="4" style="text-align: right; font-weight: bold;">合计金额：</td>
                <td style="font-weight: bold;">¥ {{ contract.finalAmount || contract.amount }}</td>
              </tr>
            </tbody>
          </table>

          <h4 style="margin-top: 20px;">第二条：服务费用及支付方式</h4>
          <p>甲方同意按本合同约定向乙方支付服务费用。支付方式为线下转账/在线支付，具体由甲方选择。</p>

          <h4>第三条：合同有效期</h4>
          <p>本合同自 {{ contract.startDate }} 至 {{ contract.endDate }} 止。</p>

          <h4>第四条：其他条款</h4>
          <p>本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p>
        </div>

        <!-- 盖章区域提示 -->
        <div class="seal-area mt-8 p-4 border-2 border-dashed border-red-300 rounded text-center">
          <p class="text-sm text-gray-600 mb-2">请确认合同信息无误，点击"确认签署"进行盖章</p>
          <p class="text-xs text-gray-500">盖章完成后，合同状态将更新为"已生效"</p>
        </div>
      </div>
      <div v-else class="text-center py-20 text-gray-400">
        合同信息加载中...
      </div>
    </div>

    <!-- 签名及盖章区域 -->
    <div class="signature-section-wrapper" v-if="contract">
      <div class="signature-block">
        <div class="block-title">📝 电子签名</div>
        <div class="block-content">
          <div v-if="!signatureData" class="status-box pending">
            <span class="status-icon">⊙</span>
            <span class="status-text">待签名</span>
          </div>
          <div v-else class="status-box completed">
            <span class="status-icon">✓</span>
            <span class="status-text">已签名</span>
          </div>
          <el-button 
            v-if="!signatureData"
            type="primary"
            @click="openSignPlatform"
            class="sign-btn">
            点击进行电子签名
          </el-button>
          <div v-else class="sign-details">
            <p>签署ID: {{ signatureData.signId }}</p>
            <p>签署时间: {{ signatureData.timestamp }}</p>
            <p>签署人: {{ signatureData.signer }}</p>
          </div>
        </div>
      </div>

      <!-- 手写签名区域 -->
      <div class="signature-block">
        <div class="block-title">✍️ 手写签名</div>
        <div class="block-content">
          <div v-if="!signatureImage" class="signature-area">
            <canvas 
              ref="canvasRef"
              class="signature-canvas"
              @mousedown="startDrawing"
              @mousemove="draw"
              @mouseup="stopDrawing"
              @mouseleave="stopDrawing"
              @touchstart="handleTouchStart"
              @touchmove="handleTouchMove"
              @touchend="stopDrawing">
            </canvas>
            <div class="canvas-tools">
              <el-button size="small" @click="clearCanvas">清空</el-button>
              <el-button size="small" type="primary" @click="saveSignature">确认签名</el-button>
            </div>
          </div>
          <div v-else class="signature-preview">
            <img :src="signatureImage" alt="签名" />
            <el-button size="small" @click="resetSignature">重新签名</el-button>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button 
        type="default" 
        plain
        :loading="isExporting"
        @click="handleExportPdf">
        导出PDF
      </el-button>
      <el-button 
        type="primary"
        :loading="isSigning"
        :disabled="!signatureImage"
        @click="handleOnlineSign">
        提交签名并生效
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { downloadContractPdf } from '@/api/contract'

interface Props {
  modelValue: boolean
  contract?: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'confirm': []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isSigning = ref(false)
const isExporting = ref(false)

// Canvas手写签名相关
const canvasRef = ref<HTMLCanvasElement | null>(null)
const isDrawing = ref(false)
const signatureImage = ref<string>('')
let ctx: CanvasRenderingContext2D | null = null

// 初始化canvas
onMounted(() => {
  nextTick(() => {
    initCanvas()
  })
})

const initCanvas = () => {
  if (canvasRef.value) {
    const canvas = canvasRef.value
    canvas.width = 600
    canvas.height = 200
    ctx = canvas.getContext('2d')
    if (ctx) {
      ctx.strokeStyle = '#000'
      ctx.lineWidth = 2
      ctx.lineCap = 'round'
      ctx.lineJoin = 'round'
      // 设置白色背景
      ctx.fillStyle = '#fff'
      ctx.fillRect(0, 0, canvas.width, canvas.height)
    }
  }
}

// 鼠标绘制
const startDrawing = (e: MouseEvent) => {
  if (!ctx || !canvasRef.value) return
  isDrawing.value = true
  const rect = canvasRef.value.getBoundingClientRect()
  const scaleX = canvasRef.value.width / rect.width
  const scaleY = canvasRef.value.height / rect.height
  const x = (e.clientX - rect.left) * scaleX
  const y = (e.clientY - rect.top) * scaleY
  ctx.beginPath()
  ctx.moveTo(x, y)
}

const draw = (e: MouseEvent) => {
  if (!isDrawing.value || !ctx || !canvasRef.value) return
  const rect = canvasRef.value.getBoundingClientRect()
  const scaleX = canvasRef.value.width / rect.width
  const scaleY = canvasRef.value.height / rect.height
  const x = (e.clientX - rect.left) * scaleX
  const y = (e.clientY - rect.top) * scaleY
  ctx.lineTo(x, y)
  ctx.stroke()
}

const stopDrawing = () => {
  isDrawing.value = false
}

// 触摸绘制（移动端支持）
const handleTouchStart = (e: TouchEvent) => {
  if (!ctx || !canvasRef.value) return
  e.preventDefault()
  isDrawing.value = true
  const rect = canvasRef.value.getBoundingClientRect()
  const scaleX = canvasRef.value.width / rect.width
  const scaleY = canvasRef.value.height / rect.height
  const touch = e.touches[0]
  const x = (touch.clientX - rect.left) * scaleX
  const y = (touch.clientY - rect.top) * scaleY
  ctx.beginPath()
  ctx.moveTo(x, y)
}

const handleTouchMove = (e: TouchEvent) => {
  if (!isDrawing.value || !ctx || !canvasRef.value) return
  e.preventDefault()
  const rect = canvasRef.value.getBoundingClientRect()
  const scaleX = canvasRef.value.width / rect.width
  const scaleY = canvasRef.value.height / rect.height
  const touch = e.touches[0]
  const x = (touch.clientX - rect.left) * scaleX
  const y = (touch.clientY - rect.top) * scaleY
  ctx.lineTo(x, y)
  ctx.stroke()
}

// 清空画布
const clearCanvas = () => {
  if (!ctx || !canvasRef.value) return
  ctx.fillStyle = '#fff'
  ctx.fillRect(0, 0, canvasRef.value.width, canvasRef.value.height)
}

// 保存签名
const saveSignature = () => {
  if (!canvasRef.value) return
  
  // 检查是否有签名内容
  const imageData = ctx?.getImageData(0, 0, canvasRef.value.width, canvasRef.value.height)
  if (imageData) {
    const pixels = imageData.data
    let hasDrawing = false
    for (let i = 0; i < pixels.length; i += 4) {
      if (pixels[i] !== 255 || pixels[i + 1] !== 255 || pixels[i + 2] !== 255) {
        hasDrawing = true
        break
      }
    }
    
    if (!hasDrawing) {
      ElMessage.warning('请先进行签名')
      return
    }
  }
  
  signatureImage.value = canvasRef.value.toDataURL('image/png')
  ElMessage.success('签名已保存')
}

// 重新签名
const resetSignature = () => {
  signatureImage.value = ''
  nextTick(() => {
    initCanvas()
  })
}

const handleClose = () => {
  emit('update:modelValue', false)
}

// 线上签名并提交（签名完成后直接生效）
const handleOnlineSign = async () => {
  try {
    const contractId = props.contract?.contractId || props.contract?.id
    if (!contractId) {
      ElMessage.error('合同ID不存在')
      return
    }

    if (!signatureImage.value) {
      ElMessage.warning('请先完成手写签名')
      return
    }

    isSigning.value = true

    // TODO: 将签名图片上传到后端并更新合同状态为已生效(6)
    // const formData = new FormData()
    // formData.append('contractId', contractId)
    // formData.append('signature', signatureImage.value)
    // const response = await apiSubmitSignature(formData)
    // if (response.code === 200) {
    //   ElMessage.success('签名提交成功，合同已生效')
    //   visible.value = false
    //   emit('confirm')
    // }
    
    // 临时模拟提交
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('签名提交成功，合同已生效')
    visible.value = false
    emit('confirm')
    
  } catch (error: any) {
    ElMessage.error(error.message || '签名提交失败')
  } finally {
    isSigning.value = false
  }
}

// 导出PDF（可选功能）
const handleExportPdf = async () => {
  try {
    const contractId = props.contract?.contractId || props.contract?.id
    if (!contractId) {
      ElMessage.error('合同ID不存在')
      return
    }

    isExporting.value = true

    const res = await downloadContractPdf(contractId)
    if (res.code === 200 && res.data) {
      // 后端返回文件URL，直接打开下载
      window.open(res.data, '_blank')
      ElMessage.success('PDF导出成功')
    } else {
      ElMessage.warning(res.msg || 'PDF导出失败')
    }
    
  } catch (error: any) {
    ElMessage.error(error.message || 'PDF导出失败')
  } finally {
    isExporting.value = false
  }
}

const formatDate = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getCurrentDateTime = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const getYearDiff = (start: string, end: string) => {
  if (!start || !end) return 0
  return new Date(end).getFullYear() - new Date(start).getFullYear()
}
</script>

<style scoped>
.contract-preview-shell {
  background: #f0f2f5;
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.preview-paper {
  background: white;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.contract-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  font-family: 'SimSun', serif;
}

.contract-header {
  display: flex;
  justify-content: space-between;
  border-bottom: 2px solid #333;
  padding-bottom: 10px;
  margin-bottom: 20px;
  font-family: 'SimHei', sans-serif;
}

.header-row {
  font-size: 14px;
}

.section-title {
  background: #eee;
  padding: 5px 10px;
  font-weight: bold;
  margin: 20px 0 10px 0;
  border-left: 4px solid #409EFF;
}

.info-grid {
  display: grid;
  grid-template-columns: 100px 1fr 100px 1fr;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 14px;
}

.info-grid .label {
  color: #666;
  text-align: right;
}

.info-grid .value {
  border-bottom: 1px solid #ddd;
  padding-left: 5px;
  min-height: 20px;
}

.info-grid .value.full {
  grid-column: span 3;
}

.contract-body h4 {
  font-weight: bold;
  margin: 15px 0 5px 0;
}

.contract-body p {
  text-indent: 2em;
  margin: 5px 0;
  line-height: 1.6;
  font-size: 14px;
}

.fee-table {
  width: 100%;
  border-collapse: collapse;
  margin: 10px 0;
}

.fee-table th, 
.fee-table td {
  border: 1px solid #333;
  padding: 8px;
  text-align: center;
  font-size: 14px;
}

.fee-table th {
  background: #f5f5f5;
  font-weight: bold;
}

.seal-area {
  background: #fef3f3;
}

.signature-section-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin: 20px 0;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

.signature-block {
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.block-title {
  background: #f0f2f5;
  padding: 10px 15px;
  font-weight: bold;
  font-size: 14px;
  border-bottom: 1px solid #ddd;
}

.block-content {
  padding: 15px;
}

.status-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 12px;
  font-weight: 500;
}

.status-box.pending {
  background: #fef0f0;
  color: #f56c6c;
}

.status-box.completed {
  background: #f0f9ff;
  color: #409eff;
}

.status-icon {
  font-size: 18px;
  font-weight: bold;
}

.status-text {
  font-size: 14px;
}

.sign-btn {
  width: 100%;
  margin-top: 10px;
}

.sign-details {
  font-size: 12px;
  line-height: 1.8;
  color: #666;
}

.sign-details p {
  margin: 5px 0;
}

/* 手写签名样式 */
.signature-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.signature-canvas {
  border: 2px dashed #409eff;
  border-radius: 8px;
  cursor: crosshair;
  background: #fff;
  display: block;
  width: 100%;
  touch-action: none;
}

.canvas-tools {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.signature-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.signature-preview img {
  max-width: 100%;
  height: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: white;
  padding: 10px;
}

.seal-info {
  font-size: 13px;
  line-height: 2;
  color: #333;
  margin-bottom: 15px;
}

.seal-info p {
  margin: 8px 0;
}

.seal-info strong {
  color: #000;
}

.seal-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: #fff;
  border: 1px dashed #ddd;
  border-radius: 4px;
}

.seal-circle {
  width: 80px;
  height: 80px;
  border: 2px solid #e41e1e;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #e41e1e;
  font-size: 18px;
  transform: rotate(-45deg);
}
</style>
