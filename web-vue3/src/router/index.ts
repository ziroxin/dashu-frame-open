import type { RouteRecordRaw } from 'vue-router'
import { createRouter, createWebHashHistory } from 'vue-router'
import type { App } from 'vue'
import { Layout } from '@/utils/router-helper'
import { homeRoute, loginRoute, oauth2Routes, otherRoutes, userRoute } from '@/router/constant-routes'

export const constantRoutes: AppRouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    name: 'RedirectWrap',
    meta: {hidden: true, noTagsView: true},
    children: [{
      path: '/redirect/:path(.*)',
      name: 'Redirect',
      component: () => import('@/views/Redirect/Redirect'),
      meta: {}
    }]
  },
  loginRoute,// 登录页路由
  {
    path: '/401',
    name: 'Unauthorized',
    component: () => import('@/views/Error/401'),
    meta: {hidden: true, title: '401', noTagsView: true}
  }, {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Error/403'),
    meta: {hidden: true, title: '403', noTagsView: true}
  }, {
    path: '/404',
    name: 'NoFind',
    component: () => import('@/views/Error/404'),
    meta: {hidden: true, title: '404', noTagsView: true}
  }, {
    path: '/500',
    name: 'ServerError',
    component: () => import('@/views/Error/500'),
    meta: {hidden: true, title: '500', noTagsView: true}
  }, {
    path: '/',
    name: 'Root',
    redirect: '/dashboard/index',
    component: Layout,
    meta: {hidden: true}
  },
  homeRoute,
  userRoute,
  ...oauth2Routes,
  ...otherRoutes
]

// 错误页跳转404
export const errorRoute: AppRouteRecordRaw = {
  path: '/:path(.*)*',
  redirect: '/404',
  name: '404Page',
  meta: {hidden: true, breadcrumb: false}
}

// 重置路由（锁定屏幕、退出时使用）
export const resetRouter = (): void => {
  console.log('reset  router')
  router.getRoutes().forEach(({name}) => {
    if (name && !constantRoutes.map(i => i.name).includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

// 路由初始化（动态路由，在src/permission.ts里动态 addRoutes）
// 文档：@link https://element-plus-admin-doc.cn/guide/router.html
const router = createRouter({
  // 默认使用：hash 模式；若需 history 模式，改成 createWebHistory()，部署时 nginx 配合修改配置
  history: createWebHashHistory(),
  strict: true,
  // 配置默认加载的静态路由
  routes: constantRoutes as RouteRecordRaw[],
  scrollBehavior: () => ({left: 0, top: 0})
})

export default router

// 加载路由
export const setupRouter = (app: App<Element>) => {
  app.use(router)
}
