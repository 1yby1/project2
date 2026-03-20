import request from '@/utils/request'

/**
 * 气瓶登记DTO
 */
export interface CylinderRegisterDto {
  contractId: number
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
  cylinderStatus?: number
  location?: string
  remark?: string
}

/**
 * 气瓶查询DTO
 */
export interface CylinderQueryDto {
  pageNum: number
  pageSize: number
  contractId?: number
  cylinderTypeId?: number
  cylinderNo?: string
  rfidTag?: string
  cylinderStatus?: number
  inspectionStatus?: string // 'dueSoon' | 'overdue' | 'normal'
}

/**
 * 气瓶状态更新DTO
 */
export interface CylinderStatusUpdateDto {
  registerId: number
  cylinderStatus: number
  remark?: string
}

/**
 * 气瓶检验DTO
 */
export interface CylinderInspectDto {
  registerId: number
  inspectDate: string
  inspectResult: string
  inspectOrg?: string
  nextInspectDate: string
  remark?: string
}

/**
 * 导入结果
 */
export interface ImportResult {
  successCount: number
  failureCount: number
  errors: Array<{
    rowNum: number
    errorMsg: string
  }>
}

/**
 * 登记气瓶
 */
export const registerCylinder = (data: CylinderRegisterDto) => {
  return request.post('/cylinder/register/add', data)
}

/**
 * 批量登记气瓶
 */
export const batchRegisterCylinder = (data: CylinderRegisterDto[]) => {
  return request.post('/cylinder/register/batch', data)
}

/**
 * 分页查询气瓶登记列表
 */
export const queryCylinderList = (data: CylinderQueryDto) => {
  return request.post('/cylinder/register/list', data)
}

/**
 * 获取气瓶登记详情
 */
export const getCylinderDetail = (registerId: number) => {
  return request.get(`/cylinder/register/detail/${registerId}`)
}

/**
 * 根据气瓶编号查询详情
 */
export const getCylinderDetailByNo = (cylinderNo: string) => {
  return request.get(`/cylinder/register/detail/by-no/${cylinderNo}`)
}

/**
 * 更新气瓶状态
 */
export const updateCylinderStatus = (data: CylinderStatusUpdateDto) => {
  return request.put('/cylinder/register/status', data)
}

/**
 * 记录气瓶检验
 */
export const recordInspection = (data: CylinderInspectDto) => {
  return request.post('/cylinder/register/inspect', data)
}

/**
 * 查询即将到期的气瓶
 */
export const getInspectDueSoon = (days: number = 30) => {
  return request.get('/cylinder/register/inspect/due-soon', { params: { days } })
}

/**
 * 查询已过期的气瓶
 */
export const getInspectOverdue = () => {
  return request.get('/cylinder/register/inspect/overdue')
}

/**
 * 查询指定合同下的所有气瓶
 */
export const getCylindersByContract = (contractId: number) => {
  return request.get(`/cylinder/register/contract/${contractId}`)
}

/**
 * 下载气瓶导入模板
 */
export const downloadCylinderTemplate = () => {
  return request.get('/cylinder/register/template/download')
}

/**
 * 从 Excel 批量导入气瓶
 */
export const importCylindersFromExcel = (formData: FormData) => {
  return request.post('/cylinder/register/import/excel', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
