<template>
  <div class="fade-in">
    <!-- 页面头部 -->
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-[20px] font-bold text-gray-800">合同管理</h2>
        <p class="text-gray-500 text-xs mt-1">
          当前共 <span class="text-primary font-bold">{{ contracts.length }}</span> 份合同
        </p>
      </div>
      
      <div class="flex space-x-3">
        <el-button 
          v-if="currentRole === 'ADMIN'" 
          type="primary" 
          plain
          :icon="Document">
          导出Excel
        </el-button>
        <el-button 
          v-if="currentRole === 'ADMIN'" 
          type="warning"
          plain
          @click="showImportModal = true"
          :icon="Upload">
          批量导入
        </el-button>
        <el-button 
          v-if="currentRole === 'ADMIN'" 
          @click="showEntryModal = true"
          :icon="Upload">
          录入初始合同
        </el-button>
        <el-button 
          v-if="currentRole === 'USER' || currentRole === 'UNIT_ADMIN'"
          type="primary"
          @click="openNewContractModal"
          :icon="Plus">
          新建合同
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <ContractSearchForm 
      :show-unit-filter="currentRole === 'ADMIN'"
      @search="handleSearch" />

    <!-- 表格 -->
    <ContractTable 
      :contracts="contracts"
      :is-admin="currentRole === 'ADMIN'"
      :show-unit-column="currentRole === 'ADMIN'"
      @view-detail="viewDetail"
      @confirm="confirmContract"
      @return="openReturnModal"
      @audit="openAuditModal"
      @sign="openSignModal"
      @payment="openPaymentModal"
      @edit="openEditModal" />

    <!-- 分页 -->
    <div class="mt-4 flex justify-end">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="loadContracts"
        @current-change="loadContracts" />
    </div>

    <!-- 各类弹窗 -->
    <ContractEntryDialog 
      v-model="showEntryModal"
      @submit="handleEntrySubmit" />

    <NewContractDialog 
      v-model="showNewContractModal"
      :form="newContractForm"
      @update:form="(updated) => Object.assign(newContractForm, updated)"
      @preview="handlePreviewContract" />

    <ContractPreviewDialog 
      v-model="showPreviewModal"
      :form="newContractForm"
      @submit="handleCreateContract" />

    <ContractDetailDialog 
      v-model="detailDialogVisible"
      :contract="currentContract"
      @save="handleSaveDetail"
      @void-request="openVoidRequestModal"
      @confirm="confirmContract"
      @return="openReturnModal"
      @audit="openAuditModal"
      @payment="openPaymentModal"
      @edit="openEditModal" />

    <ReturnDialog 
      v-model="showReturnModal"
      @confirm="confirmReturn" />

    <PaymentDialog 
      v-model="showPaymentModal"
      :contract="currentActionContract"
      @submit="submitPayment" />

    <AuditDialog 
      v-model="showAuditModal"
      :contract="currentActionContract"
      @reject="rejectPayment"
      @approve="approvePayment" />

    <SignDialog 
      v-model="showSignModal"
      :contract="currentActionContract"
      @confirm="confirmSignature" />

    <VoidRequestDialog 
      v-model="showVoidRequestModal"
      :contract="currentContract"
      @submit="submitVoidRequest" />

    <ContractImportDialog
      v-model="showImportModal"
      @success="handleImportSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { Plus, Upload, Document } from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'
import { getContractInitialData, createContract, getContractList, getContractDetail, updateContract, terminateContract, signContract as apiSignContract , resubmitContract as apiResubmitContract, confirmContract as apiConfirmContract, rejectContract as apiRejectContract, submitPayment as apiSubmitPayment, auditPayment as apiAuditPayment } from '@/api/contract'
import { importCylindersFromExcel } from '@/api/cylinder'
import ContractSearchForm from './contract/ContractSearchForm.vue'
import ContractTable from './contract/ContractTable.vue'
import ContractEntryDialog from './contract/ContractEntryDialog.vue'
import NewContractDialog from './contract/NewContractDialog.vue'
import ContractPreviewDialog from './contract/ContractPreviewDialog.vue'
import ContractDetailDialog from './contract/ContractDetailDialog.vue'
import ReturnDialog from './contract/ReturnDialog.vue'
import PaymentDialog from './contract/PaymentDialog.vue'
import AuditDialog from './contract/AuditDialog.vue'
import SignDialog from './contract/SignDialog.vue'
import VoidRequestDialog from './contract/VoidRequestDialog.vue'
import ContractImportDialog from './contract/ContractImportDialog.vue'

const userStore = useUserStore()
const { currentRole } = storeToRefs(userStore)

const currentPage = ref(1)
const pageSize = ref(10)
const showEntryModal = ref(false)
const showNewContractModal = ref(false)
const detailDialogVisible = ref(false)
const showVoidRequestModal = ref(false)
const showPreviewModal = ref(false)
const showPaymentModal = ref(false)
const showAuditModal = ref(false)
const showSignModal = ref(false)
const showReturnModal = ref(false)
const showImportModal = ref(false)

const currentContract = ref<any>(null)
const currentActionContract = ref<any>(null)
  const unit = ref('')
const searchKeyword = ref('')
const loading = ref(false)
const total = ref(0)

const newContractForm = reactive({
  unitId: null as any, // 单位ID
  contractNo: '',
  type: '', // 气瓶类型名称（用于前端显示和查询）
  cylinderTypeId: null as any, // 气瓶类型ID（后端需要）
  cylinderTypeName: '', // 气瓶类型名称（预览显示）
  quantity: 100,
  unitPrice: 100, // 单价
  years: 1,
  startDate: '',
  remark: '', // 备注
  cylinderFile: null as any, // 气瓶清单文件
  unitName: '',
  unitCode: '',
  unitAddress: '',
  unitTypes: '',
  unitPerson: '',
  unitPhone: '',
  unitBank: '',
  unitAccount: '',
  compName: '',
  compCode: '',
  compAddress: '',
  compPerson: '',
  compPhone: '',
  compBank: '',
  compAccount: ''
})

const contracts = ref<any[]>([])

// 加载合同列表
const loadContracts = async () => {
  try {
    loading.value = true
    const res = await getContractList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      unitName: unit.value
    })
    console.log(res)

    if (res.code === 200 && res.data) {
      contracts.value = res.data.records || []
      total.value = res.data.total || 0
    }

  } catch (error: any) {
    ElMessage.error(error.message || '加载合同列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = (searchForm: any) => {
  unit.value = searchForm.unit
  console.log('搜索关键字：', searchForm.unit, searchForm.keyword)
  searchKeyword.value = searchForm.keyword
  currentPage.value = 1
  loadContracts()
}

const viewDetail = async (contract: any) => {
  try {
    const res = await getContractDetail(contract.contractId)
    if (res.code === 200 && res.data) {
      currentContract.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取合同详情失败')
  }
}

const openNewContractModal = async () => {
  try {
    const loadingInstance = ElLoading.service({ text: '加载中...' })
  
    const res = await getContractInitialData()
    loadingInstance.close()
    console.log(res)
    if (res.code === 200 && res.data) {
      // 更新表单数据
      Object.assign(newContractForm, {
        unitId: JSON.parse(localStorage.getItem('user') || '{}').unitId || '',
        unitName: res.data.partyAInfo.unitName || '',
        unitCode: res.data.partyAInfo.socialCreditCode || '',
        unitAddress: res.data.partyAInfo.unitAddress || '',
        unitTypes: res.data.partyAInfo.businessScope || '',
        unitPerson: res.data.partyAInfo.principalName || '',
        unitPhone: res.data.partyAInfo.principalPhone || '',
        unitBank: res.data.partyAInfo.bankName || '',
        unitAccount: res.data.partyAInfo.bankAccount || '',
        compName: res.data.partyBInfo.companyName || '',
        compCode: res.data.partyBInfo.socialCreditCode || '',
        compAddress: res.data.partyBInfo.address || '',
        compPerson: res.data.partyBInfo.contactName || '',
        compPhone: res.data.partyBInfo.contactPhone || '',
        compBank: res.data.partyBInfo.bankName || '',
        compAccount: res.data.partyBInfo.bankAccount || '',
        contractNo: res.data.preGeneratedContractNo || '',
        type: '',
        cylinderTypeId: null,
        cylinderTypeName: '',
        quantity: 100,
        unitPrice: 100,
        years: 1,
        startDate: '',
        remark: ''
      })
      showNewContractModal.value = true
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取初始化数据失败')
  }
}

const handlePreviewContract = () => {
  if (!newContractForm.cylinderTypeId) {
    ElMessage.warning('请选择气瓶类型')
    return
  }
  showPreviewModal.value = true
}

const handleCreateContract = async () => {
  if (!newContractForm.startDate) {
    ElMessage.warning('请选择生效日期')
    return
  }
  
  if (!newContractForm.unitId) {
    ElMessage.warning('单位ID不能为空')
    return
  }
  
  if (!newContractForm.cylinderTypeId) {
    ElMessage.warning('请选择气瓶类型')
    return
  }
  
  try {
    const loadingInstance = ElLoading.service({ text: '提交中...' })
    
    // 计算到期日期
    const startDate = new Date(newContractForm.startDate)
    const endDate = new Date(startDate)
    endDate.setFullYear(endDate.getFullYear() + Number(newContractForm.years || 0))
    endDate.setDate(endDate.getDate() - 1)
    
    // 构建符合 DTO 结构的请求数据
    const res = await createContract({
      unitId: newContractForm.unitId,
      contractNo: newContractForm.contractNo,
      startDate: newContractForm.startDate,
      endDate: endDate.toISOString().split('T')[0],
      // 后端限制每个合同仅一条气瓶类型行
      cylinderLines: [
        {
          cylinderTypeId: newContractForm.cylinderTypeId,
          cylinderQty: newContractForm.quantity,
          unitPrice: newContractForm.unitPrice,
          years: newContractForm.years,
          discountRate: 1.0
        }
      ],
      remark: newContractForm.remark || ''
    })
    
    if (res.code === 200) {
      const contractId = res.data?.contractId || res.data?.id
      
      // 如果有气瓶文件，立即上传
      if (newContractForm.cylinderFile && contractId) {
        try {
          loadingInstance.setText('正在上传气瓶清单...')
          const formData = new FormData()
          formData.append('file', newContractForm.cylinderFile.raw as File)
          formData.append('contractId', String(contractId))
          formData.append('cylinderTypeId', String(newContractForm.cylinderTypeId))
          
          const uploadRes = await importCylindersFromExcel(formData)
          
          if (uploadRes.code === 200) {
            const result = uploadRes.data
            if (result.failureCount > 0) {
              ElMessage.warning(
                `合同提交成功，气瓶导入部分失败：成功 ${result.successCount} 条，失败 ${result.failureCount} 条`
              )
            } else {
              ElMessage.success(`合同及气瓶清单提交成功，共导入 ${result.successCount} 条气瓶数据`)
            }
          } else {
            ElMessage.warning('合同提交成功，但气瓶清单上传失败：' + uploadRes.data.message)
          }
        } catch (uploadError) {
          console.error('气瓶清单上传失败:', uploadError)
          ElMessage.warning('合同提交成功，但气瓶清单上传失败')
        }
      } else {
        ElMessage.success('合同申请已提交，等待管理员确认')
      }
      
      loadingInstance.close()
      showPreviewModal.value = false
      showNewContractModal.value = false
      // 刷新列表
      await loadContracts()
    } else {
      ElMessage.error(res.msg || '创建合同失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '创建合同失败')
  }
}

const handleEntrySubmit = (form: any, activeTab: string) => {
  if (activeTab === 'batch') {
    ElMessage.success('批量导入任务已提交后台处理')
    showEntryModal.value = false
    return
  }

  if (!form.unit || !form.code || !form.type) {
    ElMessage.warning('请填写完整合同信息')
    return
  }

  const newContract = {
    id: contracts.value.length + 1,
    code: form.code,
    unit: form.unit,
    type: form.type,
    quantity: form.quantity,
    amount: form.amount,
    startDate: form.startDate,
    endDate: form.endDate,
    status: '生效中',
    remarks: '线下录入'
  }
  contracts.value.unshift(newContract)
  ElMessage.success('合同录入成功')
  showEntryModal.value = false
}

const confirmContract = async (contract: any) => {
  try {
    const contractId = contract.contractId || contract.id
    console.log('确认合同ID:', contractId)
    await apiConfirmContract({
      contract_id: contractId,
      remark: '管理员通过确认'
    })
    ElMessage.success('合同已确认，等待用户缴费')
    detailDialogVisible.value = false
    loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const openReturnModal = (contract: any) => {
  currentActionContract.value = contract
  showReturnModal.value = true
}

const confirmReturn = async (reason: string) => {
  if (!reason) {
    ElMessage.warning('请填写退回原因')
    return
  }
  try {
    const contractId = currentActionContract.value?.contractId || currentActionContract.value?.id
    await apiRejectContract({
      contract_id: contractId,
      reject_reason: reason
    })
    ElMessage.success('合同申请已退回')
    showReturnModal.value = false
    detailDialogVisible.value = false
    loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const openPaymentModal = (contract: any) => {
  currentActionContract.value = contract
  console.log('打开缴费弹窗，合同：', contract)
  showPaymentModal.value = true
}

const submitPayment = async (data: any) => {
  // data = { paymentMethod, paymentAmount, paymentVoucherUrl, remark }
  try {
    const contractId = currentActionContract.value?.contractId || currentActionContract.value?.id
    await apiSubmitPayment({
      contract_id: contractId,
      ...data
    })
    ElMessage.success('缴费成功，等待管理员审核')
    showPaymentModal.value = false
    detailDialogVisible.value = false
    loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  }
}

const openAuditModal = (contract: any) => {
  currentActionContract.value = contract
  console.log('打开审核弹窗，合同：', contract)
  showAuditModal.value = true
}

const rejectPayment = async (remark: string) => {
  if (!remark) {
     ElMessage.warning('驳回必须填写审核意见')
     return
  }
  try 
  {
    const contractId = currentActionContract.value?.contractId || currentActionContract.value?.id
      await apiAuditPayment({
          contract_id: contractId,
          approved: false,
          aaudit_remark: remark
      })
      ElMessage.warning('缴费已驳回')
      showAuditModal.value = false
      detailDialogVisible.value = false
      loadContracts()
  } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
  }
}

const approvePayment = async (remark: string) => {
  try 
  {const contractId = currentActionContract.value?.contractId || currentActionContract.value?.id
      await apiAuditPayment({
          contract_id: contractId,
          
          approved: true,
          audit_remark: remark || '审核通过'
      })
      ElMessage.success('缴费审核通过')
      showAuditModal.value = false
      detailDialogVisible.value = false
      loadContracts()
  } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
  }
}

const openEditModal = async (contract: any) => {
  // 已退回的合同可以重新提交
  if (contract.contractStatus !== 2) {
    ElMessage.warning('只有已退回状态的合同才能重新提交')
    return
  }
  
  try {
    const loadingInstance = ElLoading.service({ text: '处理中...' })
    // 重新提交合同
    await apiResubmitContract({
      contract_id: contract.id
    })
    loadingInstance.close()
    
    ElMessage.success('合同已重新提交，等待管理员再次审核')
    detailDialogVisible.value = false
    loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '重新提交失败')
  }
}

const openSignModal = async (contract: any) => {
  try {
    const res = await getContractDetail(contract.contractId)
    console.log('签署合同详情：', res)
    if (res.data.contractStatus !== 5) {
      ElMessage.warning('只有已审核通过的合同才能签章')
      return
    }
    currentActionContract.value = res.data
    showSignModal.value = true
  } catch (error: any) {
    ElMessage.error(error.message || '签署失败')
    return
  }
  
  const res = await getContractDetail(contract.contractId)
}

const confirmSignature = async () => {
  try {
    const contractId = currentActionContract.value?.contractId || currentActionContract.value?.id
    if (!contractId) {
      ElMessage.error('合同ID不存在')
      return
    }

    // 调用签章确认接口（后端接口或前端直接状态更新）
    // 这里暂时以前端状态更新为例，实际生产环境应该调用后端接口
    await apiSignContract({
      contract_id: contractId
    })
    
    ElMessage.success('签章完成，合同已生效')
    showSignModal.value = false
    
    // 刷新列表以获取最新状态
    loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '签章失败')
  }
}

const handleSaveDetail = async () => {
  try {
    await updateContract({
      contractId: currentContract.value.id,
      ...currentContract.value
    })
    ElMessage.success('修改已保存')
    detailDialogVisible.value = false
    await loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

const openVoidRequestModal = () => {
  showVoidRequestModal.value = true
}

const submitVoidRequest = async (reason: string) => {
  if (!reason) {
    ElMessage.warning('请填写作废原因')
    return
  }
  try {
    const contractId = currentContract.value?.contractId || currentContract.value?.id
    await terminateContract({
      contractId: contractId,
      reason: reason
    })
    ElMessage.success('作废申请已提交，等待管理员审核')
    showVoidRequestModal.value = false
    detailDialogVisible.value = false
    await loadContracts()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  }
}

// 处理导入成功
const handleImportSuccess = () => {
  ElMessage.success('导入成功，已刷新列表')
  showImportModal.value = false
  loadContracts()
}

// 页面加载时获取数据
onMounted(() => {
  loadContracts()
})
</script>

<style scoped>
</style>
