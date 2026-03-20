import request from '@/utils/request'

// 续约策略相关接口
export function getAllStrategies() {
  return request({
    url: '/renewal/strategies',
    method: 'get'
  })
}

export function getStrategyById(id: number) {
  return request({
    url: `/renewal/strategies/${id}`,
    method: 'get'
  })
}

export function createStrategy(data: any) {
  return request({
    url: '/renewal/strategies',
    method: 'post',
    data
  })
}

export function updateStrategy(id: number, data: any) {
  return request({
    url: `/renewal/strategies/${id}`,
    method: 'put',
    data
  })
}

export function deleteStrategy(id: number) {
  return request({
    url: `/renewal/strategies/${id}`,
    method: 'delete'
  })
}

// 续约案件相关接口
export function getRenewalCases(params: any) {
  return request({
    url: '/renewal/cases',
    method: 'get',
    params
  })
}

export function getCaseById(id: number) {
  return request({
    url: `/renewal/cases/${id}`,
    method: 'get'
  })
}

export function createCase(data: any, contractId?: number) {
  return request({
    url: '/renewal/cases',
    method: 'post',
    data,
    params: contractId ? { contractId } : undefined
  })
}

export function updateCase(id: number, data: any) {
  return request({
    url: `/renewal/cases/${id}`,
    method: 'put',
    data
  })
}

export function updateCaseStatus(id: number, status: number) {
  return request({
    url: `/renewal/cases/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function deleteCase(id: number) {
  return request({
    url: `/renewal/cases/${id}`,
    method: 'delete'
  })
}
