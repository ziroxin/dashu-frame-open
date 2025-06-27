import request from '@/utils/request'

export function loginApi(data: any) {
  return request({
    url: '/login/login',
    method: 'post',
    data
  })
}

export function logoutApi() {
  return request({
    url: '/login/logout',
    method: 'get'
  })
}