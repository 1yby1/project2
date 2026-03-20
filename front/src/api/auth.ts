import request from '@/utils/request'

export function loginWithUserAndPassword(data: any) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}
export function loginWithPhoneAndPassword(data: any) {
  return request({
    url: '/auth/login/phone',
    method: 'post',
    data
  })
}
export function sendCode(data: any) {
  return request({
    url: '/auth/phone/code',
    method: 'post',
    data
  })
}
export function loginwithCode(data: any) {
    return request({
        url: '/auth/phone/login',
        method: 'post',
        data
    })
}
export function register(data: any) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}


