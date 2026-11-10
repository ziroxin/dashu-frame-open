import request from '@/utils/request'

/**
 * 执行 SQL 查询
 */
export function executeQueryApi(data: {
  sql: string
  databaseName?: string
  maxRows?: number
  timeout?: number
}) {
  return request({
    url: '/db/query/execute',
    method: 'post',
    data
  })
}

/**
 * 获取数据库列表
 */
export function getDatabasesApi() {
  return request({
    url: '/db/query/databases',
    method: 'get'
  })
}

/**
 * 获取表列表
 */
export function getTablesApi(databaseName?: string) {
  return request({
    url: '/db/query/tables',
    method: 'get',
    params: { databaseName }
  })
}

/**
 * 获取表结构信息
 */
export function getTableStructureApi(databaseName: string, tableName: string) {
  return request({
    url: '/db/query/table/structure',
    method: 'get',
    params: { databaseName, tableName }
  })
}

/**
 * 导出查询结果为 CSV
 */
export function exportToCsvApi(data: {
  sql: string
  databaseName?: string
  maxRows?: number
  timeout?: number
}) {
  return request({
    url: '/db/query/export/csv',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

/**
 * 获取模块状态（功能是否启用 / 是否允许写 / 当前用户是否开发管理员）
 */
export function getStatusApi() {
  return request({
    url: '/db/query/status',
    method: 'get'
  })
}

/**
 * 预览写 SQL（返回受影响行数、原始行备份、一次性token）
 */
export function previewWriteApi(data: {
  sql: string
  databaseName?: string
  maxBackupRows?: number
}) {
  return request({
    url: '/db/query/write/preview',
    method: 'post',
    data
  })
}

/**
 * 执行写 SQL（二次确认）
 */
export function executeWriteApi(data: {
  token: string
  sql: string
  databaseName?: string
}) {
  return request({
    url: '/db/query/write/execute',
    method: 'post',
    data
  })
}
