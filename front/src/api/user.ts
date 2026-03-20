import request from '@/utils/request'

// 用户管理相关接口
export function getUserList(params: any) {
  return request({
    url: 'user/list',
    method: 'get',
    params
  })
}

export function updateUserStatus(id: number, status: number) {
  return request({
    url: `/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function deleteUser(id: number) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export function updateUser(data: any) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

export function createUser(data: any) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}
