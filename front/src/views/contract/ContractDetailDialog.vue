<template>
  <el-dialog
    v-model="visible"
    title="合同详情"
    width="900px"
    destroy-on-close
    append-to-body
    class="preview-dialog"
    @update:model-value="handleClose"
    @open="loadDetail">
    
    <div v-loading="loading" class="preview-shell">
      <!-- Status Banner -->
      <div v-if="detail" class="status-banner mb-4 p-3 rounded flex justify-between items-center" 
           :class="statusClass(detail.contractStatus)">
        <div class="font-bold flex items-center">
           <i class="fa-solid fa-circle-info mr-2"></i>
           当前状态：{{ detail.contractStatusText }}
        </div>
          <div v-if="detail.rejectReason && (detail.contractStatus === 2 || detail.contractStatus === 1)" class="text-sm">
           <span class="font-bold">退回原因：</span> {{ detail.rejectReason }}
        </div>
      </div>

      <div v-if="detail" class="preview-paper">
        <h1 class="contract-title">气瓶安全监管软件服务合同</h1>
        
        <div class="contract-header">
          <div class="header-row">
            <span>合同编号：{{ detail.contractNo || detail.code }}</span>
            <span>签订日期：{{ formatDate(detail.createTime) }}</span>
          </div>
          <div class="header-row">
            <span>有效期：{{ getYearDiff(detail.startDate, detail.endDate) }} 年</span>
            <span>（{{ detail.startDate }} 至 {{ detail.endDate }}）</span>
          </div>
        </div>

        <div class="section-title">甲方（申请单位）信息</div>
        <div class="info-grid">
          <div class="label">单位名称：</div>
          <div class="value">{{ detail.unitName }}</div>
          
          <div class="label">社会信用代码：</div>
          <div class="value">{{ detail.socialCreditCode  }}</div>
          
          <div class="label">单位地址：</div>
          <div class="value full">{{ detail.unitAddress  }}</div>
          
          <div class="label">负责人：</div>
          <div class="value">{{ detail.unitPrincipalName || '-' }}</div>

          <div class="label">联系电话：</div>
          <div class="value">{{ detail.unitPrincipalPhone || '-' }}</div>
        </div>

        <div class="section-title">乙方（瓶安保）信息</div>
        <div class="info-grid">
          <div class="label">公司名称：</div>
          <div class="value">{{ detail.compName || detail.partyBName || '瓶安保科技有限公司' }}</div>
          
          <div class="label">统一信用代码：</div>
          <div class="value">{{ "91110000MA00000000" }}</div>
           <div class="label">公司地址：</div>
          <div class="value full">{{ "北京市西城区长椿街1号" }}</div>
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
              <tr>
                <td>{{ detail.cylinderLines[0].cylinderTypeName || '-' }}</td>
                <td>{{ detail.cylinderLines[0].cylinderQty || '-' }}</td>
                <td>{{ detail.cylinderLines[0].unitPrice || '-' }}</td>
                <td>{{ getYearDiff(detail.startDate, detail.endDate) }}</td>
                <td>{{ detail.finalAmount || detail.amount }}</td>
              </tr>
              <tr>
                <td colspan="4" style="text-align: right; font-weight: bold;">合计：</td>
                <td style="font-weight: bold;">¥ {{ detail.finalAmount || detail.amount }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- Payment Info Section -->
        <div v-if="detail.paymentTime" class="mt-8 pt-4 border-t border-dashed border-gray-300">
             <div class="section-title">缴费信息</div>
             <div class="grid grid-cols-2 gap-4 text-sm">
                 <div><span class="text-gray-500">支付方式：</span>{{ detail.paymentMethod }}</div>
                 <div><span class="text-gray-500">支付时间：</span>{{ detail.paymentTime }}</div>
                 <div class="col-span-2">
                     <span class="text-gray-500 block mb-1">支付凭证：</span>
                     <a v-if="detail.paymentVoucherUrl" :href="detail.paymentVoucherUrl" target="_blank" class="text-primary hover:underline">
                         <i class="fa-solid fa-paperclip"></i> 查看凭证
                     </a>
                     <span v-else class="text-gray-400">无凭证</span>
                 </div>
             </div>
        </div>

      </div>
      <div v-else class="text-center py-20 text-gray-400">
         数据加载中...
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer flex justify-between w-full px-4">
        <div class="left-actions">
          <el-button @click="handleClose">关闭</el-button>
          
          <!-- 气瓶列表查看按钮 - 跳转到气瓶管理页面 -->
          <el-button type="primary" plain icon="List" @click="viewCylinderList">
            查看气瓶列表
          </el-button>
        </div>
        
        <div class="actions">
            <!-- Admin Actions -->
            <template v-if="isAdmin">
              <!-- 状态1: 待确认 -->
              <el-button v-if="detail?.contractStatus === 1" type="danger" plain @click="emit('return', detail)">退回</el-button>
              <el-button v-if="detail?.contractStatus === 1" type="success" @click="emit('confirm', detail)">确认合同</el-button>
                
              <!-- 状态4: 待审核缴费 -->
              <el-button v-if="detail?.contractStatus === 4" type="warning" @click="emit('audit', detail)">审核缴费</el-button>
                
              <!-- 状态5: 待签章 -->
              <el-button v-if="detail?.contractStatus === 5" type="primary" plain @click="downloadPdf">导出签章PDF</el-button>
            </template>
            
            <!-- User Actions -->
            <template v-else>
              <!-- 状态2: 已退回 -->
              <el-button v-if="detail?.contractStatus === 2" type="primary" @click="emit('edit', detail)">修改并重新提交</el-button>
                
              <!-- 状态3: 待缴费 -->
              <el-button v-if="detail?.contractStatus === 3" type="primary" @click="emit('payment', detail)">去缴费</el-button>
                
              <!-- 状态6: 已生效 -->
              <el-button v-if="detail?.contractStatus === 6" type="primary" plain @click="downloadPdf">下载合同PDF</el-button>
            </template>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getContractDetail, downloadContractPdf } from '@/api/contract'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const userStore = useUserStore()
const isAdmin = computed(() => userStore.currentRole === 'ADMIN')

const props = defineProps<{
  modelValue: boolean
  contract: any 
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'return': [contract: any]
  'confirm': [contract: any]
  'audit': [contract: any]
  'payment': [contract: any]
  'edit': [contract: any]
  'save': [contract: any]
  'void-request': []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const detail = ref<any>(null)
const loading = ref(false)

const loadDetail = async () => {
    if (!props.contract?.contractId && !props.contract?.id) return
    loading.value = true
    try {
        const contractId = props.contract?.contractId || props.contract?.id
        const res = await getContractDetail(contractId)
        if (res.code === 200) {
            detail.value = res.data
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleClose = () => emit('update:modelValue', false)

const downloadPdf = async () => {
    const contractId = detail.value?.contractId || detail.value?.id
    if (!contractId) return
    try {
        const res = await downloadContractPdf(contractId)
        if (res.code === 200) {
            window.open(res.data, '_blank')
        } else {
             ElMessage.warning(res.msg || '下载失败')
        }
    } catch(e) {
        ElMessage.error('下载出错')
    }
}

const formatDate = (str: string) => str ? str.split(' ')[0] : ''
const getYearDiff = (start: string, end: string) => {
    if(!start || !end) return 0
    return new Date(end).getFullYear() - new Date(start).getFullYear()
}

const statusClass = (status: number | string) => {
  const code = Number(status)
  switch(code) {
    case 2: return 'bg-red-50 text-red-600 border border-red-200'
    case 1: return 'bg-blue-50 text-blue-600 border border-blue-200'
    case 3: return 'bg-orange-50 text-orange-600 border-orange-200'
    case 4: return 'bg-yellow-50 text-yellow-600 border-yellow-200'
    case 5: return 'bg-blue-50 text-blue-600 border-blue-200'
    case 6: return 'bg-green-50 text-green-600 border-green-200'
    default: return 'bg-gray-50 text-gray-600'
  }
}

// 跳转到气瓶管理页面
const viewCylinderList = () => {
  const contractId = detail.value?.contractId || detail.value?.id
  if (contractId) {
    router.push({
      path: '/cylinder',
      query: { contractId: contractId }
    })
  } else {
    ElMessage.warning('未找到合同ID')
  }
}

watch(() => props.contract, () => {
    if (visible.value) loadDetail()
})
</script>

<style scoped>
.preview-shell {
  background: #f0f2f5;
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}
.preview-paper {
  background: white;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  min-height: 600px;
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
.dialog-footer {
  display: flex;
  justify-content: space-between;
}
.left-actions {
  display: flex;
  gap: 10px;
}
.contract-body h4 {
  font-weight: bold;
  margin: 15px 0 5px 0;
}
.contract-body p {
  text-indent: 2em;
  margin: 5px 0;
  line-height: 1.6;
}
.fee-table {
  width: 100%;
  border-collapse: collapse;
  margin: 10px 0;
}
.fee-table th, .fee-table td {
  border: 1px solid #333;
  padding: 8px;
  text-align: center;
}
</style>
