/**
 * 最新访问路由列表
 * 功能：保存/获取，最近访问的 n 个路由列表
 */
import storageKeys from '@/utils/storage-keys'

// 最多保存几个路由，默认10
const saveLength = 10

/**
 * 保存最近访问的n个路由
 */
export function saveLastedRoutes(path) {
  const lastedRoutes = getLastedRoutes()
  if (lastedRoutes.length > 0 && path === lastedRoutes[0]) {
    return // 最新路由重复（一般是刷新页面），不再添加
  }
  lastedRoutes.unshift(path)// 将新的路由信息插入到数组开头
  if (lastedRoutes.length > saveLength) {
    lastedRoutes.pop()// 删除数组末尾的元素
  }
  localStorage.setItem(storageKeys.l_lastedRoutesKey, JSON.stringify(lastedRoutes))
}

/**
 * 查询最近保存的路由列表
 */
export function getLastedRoutes(): string[] {
  const lastedRoutes = localStorage.getItem(storageKeys.l_lastedRoutesKey)
  if (lastedRoutes) {
    try {
      return JSON.parse(lastedRoutes)
    } catch (e) {
      return []
    }
  }
  return []
}