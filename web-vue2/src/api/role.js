import request from '@/utils/request'

export function getRoleList() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function updateRole(data) {
  return request({
    url: `/role/update`,
    method: 'post',
    data
  })
}

export function addRole(data) {
  return request({
    url: '/role/add',
    method: 'post',
    data
  })
}

export function deleteRoles(data) {
  return request({
    url: '/role/delete',
    method: 'post',
    data
  })
}

export function copyRole(params) {
  return request({
    url: '/role/copy',
    method: 'post',
    params
  })
}

export function getPermissionList(params) {
  return request({
    url: '/permission/listForRole',
    method: 'get',
    params
  })
}

export function saveRolePermission(data) {
  return request({
    url: '/role/permission/saveRolePermission',
    method: 'post',
    data
  })
}
