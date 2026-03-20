import request from '@/utils/request'

// 用户信息相关接口
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateUserInfo(data: any) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

export function getUnitInfo(unitId: number) {
  return request({
    url: `/unit/${unitId}`,
    method: 'get'
  })
}

export function changePassword(data: any) {
  return request({
    url: '/user/password',
    method: 'post',
    data
  })
}

export function updatePhone(data: any) {
  return request({
    url: '/user/phone',
    method: 'put',
    data
  })
}

export function updateEmail(data: any) {
  return request({
    url: '/user/email',
    method: 'put',
    data
  })
}


