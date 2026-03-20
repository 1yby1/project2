import request from '@/utils/request'

// 单位管理相关接口
export function getUnitList(params: any) {
  return request({
    url: '/unit/list',
    method: 'get',
    params
  })
}

export function deleteUnit(id: number) {
  return request({
    url: `/unit/${id}`,
    method: 'delete'
  })
}

export function updateUnit(data: any) {
  return request({
    url: '/unit/update',
    method: 'put',
    data
  })
}

export function createUnit(data: any) {
  return request({
    url: '/unit/add',
    method: 'post',
    data
  })
}
