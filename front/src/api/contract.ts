import request from '@/utils/request'

/**
 * 获取气瓶类型列表
 */
export function getCylinderTypeList() {
  return request({
    url: '/cylinder/typeList',
    method: 'get'
  })
}

/**
 * 获取单位列表
 */
export function getUnitList() {
  return request({
    url: '/unit/simplelist',
    method: 'get'
  })
}

/**
 * 获取新建合同初始化数据
 */
export function getContractInitialData() {
  return request({
    url: '/contract/init',
    method: 'get'
  })
}

/**
 * 创建合同
 */
export function createContract(data: any) {
  return request({
    url: '/contract/create',
    method: 'post',
    data
  })
}

/**
 * 更新合同
 */
export function updateContract(data: any) {
  return request({
    url: '/contract/update',
    method: 'put',
    data
  })
}

/**
 * 分页查询合同列表
 */
export function getContractList(data: any) {
  return request({
    url: '/contract/list',
    method: 'post',
    data
  })
}

/**
 * 获取合同详情
 */
export function getContractDetail(contractId: number) {
  return request({
    url: `/contract/detail/${contractId}`,
    method: 'get',
    
  })
}

/**
 * 终止合同
 */
export function terminateContract(data: any) {
  return request({
    url: '/contract/terminate',
    method: 'post',
    data
  })
}

/**
 * 下载合同PDF
 */
export function downloadContractPdf(contractId: number) {
  return request({
    url: `/contract/download/${contractId}`,
    method: 'get',
    responseType: 'json'  // 后端返回文件URL，前端通过URL下载
  })
}

/**
 * 拒绝/退回合同
 */
export function rejectContract(data: any) {
  return request({
    url: '/contract/reject',
    method: 'post',
    data
  })
}

/**
 * 确认合同
 */
export function confirmContract(data: any) {
  return request({
    url: '/contract/confirm',
    method: 'post',
    data
  })
}

/**
 * 重新提交合同
 */
export function resubmitContract(data: any) {
  return request({
    url: '/contract/resubmit',
    method: 'post',
    data
  })
}

/**
 * 提交缴费信息
 */
export function submitPayment(data: any) {
  return request({
    url: '/contract/payment/submit',
    method: 'post',
    data
  })
}

/**
 * 审核缴费
 */
export function auditPayment(data: any) {
  return request({
    url: '/contract/payment/audit',
    method: 'post',
    data
  })
}
export function signContract(data: any) {
  return request({
    url: '/contract/sign',
    method: 'post',
    data
  })
}
/**
 * 上传合同扫描件
 */
export function uploadScanFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/contract/upload/scan',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量导入合同
 */
export function importContracts(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/contract/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 下载导入模板
 */
export function downloadTemplate() {
  return request({
    url: '/contract/template/download',
    method: 'get'
  })
}

// 获取即将到期的合同
export function getExpiringContracts(unitId?: number) {
  return request({
    url: '/contract/expiring',
    method: 'get',
    params: unitId ? { unitId } : undefined
  })
}

export function getDashboardStats() {
  return request({
    url: '/contract/dashboard/stats',
    method: 'get'
  })
}
