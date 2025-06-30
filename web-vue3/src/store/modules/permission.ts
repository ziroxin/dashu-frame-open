import { defineStore } from 'pinia'
import { store } from '../index'
import { generateRoutes4HiddenByServer, generateRoutesByServer } from '@/utils/router-helper'
import router, { errorRoute } from '@/router'

export interface PermissionState {
  routes: AppRouteRecordRaw[]
  menuTabRoutes: AppRouteRecordRaw[]
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    // 全部路由
    routes: [],
    // 子路由（只在分栏布局时使用）
    menuTabRoutes: []
  }),
  getters: {
    getRoutes(): AppRouteRecordRaw[] {
      return this.routes
    },
    getMenuTabRoutes(): AppRouteRecordRaw[] {
      return this.menuTabRoutes
    }
  },
  actions: {
    generateRoutes(perRoutes: any[]): Promise<void> {
      return new Promise((resolve) => {
        // 加载动态路由（非隐藏路由）
        const accessedRoutes: AppRouteRecordRaw[] = generateRoutesByServer(perRoutes as any, true)
        // 单独加载隐藏路由
        accessedRoutes.push(...generateRoutes4HiddenByServer(perRoutes as any))
        // 加载404路由（必须放在最后）
        accessedRoutes.push(errorRoute)

        // 将 accessedRoutes 挂在到 router 上
        accessedRoutes.forEach((route) => {
          if (route.name) {
            router.hasRoute(route.name) && router.removeRoute(route.name)
          }
        })
        this.routes = [...accessedRoutes]
        resolve()
      })
    },
    setMenuTabRoutes(routes: AppRouteRecordRaw[]): void {
      this.menuTabRoutes = routes
    },
    resetPermission() {
      this.routes = []
      this.menuTabRoutes = []
    }
  },
  persist: [
    {pick: ['routes'], storage: localStorage},
    {pick: ['menuTabRoutes'], storage: localStorage}
  ]
})

export const usePermissionStoreWithOut = () => {
  return usePermissionStore(store)
}
