import { cloneDeep } from 'lodash-es'
import { reactive } from 'vue'

export type TabMapTypes = { [key: string]: string[] }

export const tabPathMap = reactive<TabMapTypes>({})

export const initTabMap = (routeList: AppRouteRecordRaw[]) => {
  for (const v of routeList) {
    if (v.meta && !v.meta.hidden && v.path) {
      tabPathMap[v.path] = []
    }
  }
}

export const filterMenusPath = (routes: AppRouteRecordRaw[], allRoutes: AppRouteRecordRaw[]): AppRouteRecordRaw[] => {
  const res: AppRouteRecordRaw[] = []
  for (const v of routes) {
    let data: Nullable<AppRouteRecordRaw> = null
    if (v.meta && !v.meta.hidden && v.path) {
      data = cloneDeep(v)
      if (v.children && data) {
        data.children = filterMenusPath(v.children, allRoutes)
      }
      if (data) {
        res.push(data)
      }
      if (v.path && Reflect.has(tabPathMap, v.path)) {
        // 遍历出所有子路由，写入到tabPathMap中
        tabPathMap[v.path].push(...filterAllChildrenPath(v))
      }
    }
  }
  return res
}

// 遍历出所有子路由，写入到tabPathMap中
const filterAllChildrenPath = (route) => {
  const res = []
  if (route.children) {
    for (const v of route.children) {
      if (v.meta && !v.meta.hidden && v.path) {
        res.push(v.path)
        res.push(...filterAllChildrenPath(v))
      }
    }
  }
  return res
}
