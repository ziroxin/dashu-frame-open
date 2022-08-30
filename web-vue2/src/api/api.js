import request from '@/utils/request'

// 获取api列表
export function getApiList() {
  return request({
    url: '/api/listGroupApi',
    method: 'get'
  })
}

// 获取资源列表
export function permissionTreeList() {
  return request({
    url: '/permission/tree/list',
    method: 'get'
  })
}

// 自动扫描api
export function scanApi() {
  return request({
    url: 'api/saveScanApi',
    method: 'get'
  })
}

// 清除无效的API
export function clearApi() {
  return request({
    url: 'api/clearApi',
    method: 'get'
  })
}

// 获取资源对应的api
export function getApiListByPermissionId(params) {
  return request({
    url: 'permission/api/getApiListByPermissionId',
    method: 'get',
    params
  })
}

// 保存资源和API关联关系
export function savePermissionApi(data) {
  return request({
    url: 'permission/api/savePermissionApi',
    method: 'post',
    data
  })
}

// 删除分组信息
export function saveApiGroup(data) {
  return request({
    url: 'api/group/add',
    method: 'post',
    data
  })
}

// 删除分组信息
export function deleteApiGroup(params) {
  return request({
    url: 'api/group/delete',
    method: 'post',
    params
  })
}

