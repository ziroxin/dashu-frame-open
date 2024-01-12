/**
 * 最新访问路由列表
 * 功能：保存/获取，最近访问的 n 个路由列表
 */

// 保存的key
const lastedRoutesKey = "LastedRoutes@";
// 最多保存几个路由，默认10
const saveLength = 10;


/**
 * 保存最近访问的n个路由
 */
export function saveLastedRoutes(path) {
  let lastedRoutes = getLastedRoutes()
  if (lastedRoutes.length > 0 && path === lastedRoutes[0]) {
    return // 最新路由重复（一般是刷新页面），不再添加
  }
  lastedRoutes.unshift(path)// 将新的路由信息插入到数组开头
  if (lastedRoutes.length > saveLength) {
    lastedRoutes.pop()// 删除数组末尾的元素
  }
  localStorage.setItem(lastedRoutesKey, JSON.stringify(lastedRoutes))
}

/**
 * 查询最近保存的路由列表
 */
export function getLastedRoutes() {
  let lastedRoutes = localStorage.getItem(lastedRoutesKey);
  if (lastedRoutes) {
    try {
      lastedRoutes = JSON.parse(lastedRoutes);
    } catch (e) {
      lastedRoutes = []
    }
  }
  return lastedRoutes || []
}