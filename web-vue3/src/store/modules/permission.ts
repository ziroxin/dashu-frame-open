import { defineStore } from 'pinia'
import { store } from '../index'
import { generateRoutes4HiddenByServer, generateRoutesByServer } from '@/utils/router-helper'
import router, { constantRoutes, errorRoute } from '@/router'
import storageKeys from '@/utils/storage-keys'
import type { RouteRecordRaw } from 'vue-router'

export interface PermissionState {
  routes: AppRouteRecordRaw[]
  menuTabRoutes: AppRouteRecordRaw[]
  routesLoaded: boolean
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    // 全部路由
    routes: [],
    // 子路由（只在分栏布局时使用）
    menuTabRoutes: [],
    // 路由是否已加载完成
    routesLoaded: false
  }),
  getters: {
    getRoutes(): AppRouteRecordRaw[] {
      return this.routes
    },
    getMenuTabRoutes(): AppRouteRecordRaw[] {
      return this.menuTabRoutes
    },
    getRoutesLoaded(): boolean {
      return this.routesLoaded
    }
  },
  actions: {
    generateRoutes(perRoutes: any[]) {
      return new Promise<void>((resolve) => {
        // 加载动态路由（非隐藏路由）
        const accessedRoutes: AppRouteRecordRaw[] = generateRoutesByServer(perRoutes as any, true)
        // 单独加载隐藏路由
        accessedRoutes.push(...generateRoutes4HiddenByServer(perRoutes as any))
        // 加载404路由（必须放在最后）
        accessedRoutes.push(errorRoute)

        // 绑定到router上
        accessedRoutes.forEach(route => {
          router.addRoute(route as RouteRecordRaw)
        })
        // 赋值到state上
        this.setRoutes(constantRoutes.concat(accessedRoutes))
        this.setRoutesLoaded(true)
        resolve()
      })
    },
    setRoutes(routes: AppRouteRecordRaw[]): void {
      this.routes = routes
    },
    setMenuTabRoutes(routes: AppRouteRecordRaw[]): void {
      this.menuTabRoutes = routes
    },
    setRoutesLoaded(loaded: boolean): void {
      this.routesLoaded = loaded
    },
    resetPermission() {
      this.routes = []
      this.menuTabRoutes = []
    }
  },
  persist: [{key: storageKeys.l_permissionStore, pick: ['routes', 'menuTabRoutes']}]
})

export const usePermissionStoreWithOut = () => {
  return usePermissionStore(store)
}
