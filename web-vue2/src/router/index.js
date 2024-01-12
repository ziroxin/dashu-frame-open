import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/layout'

// 开启路由
Vue.use(Router)

/**
 * 路由说明文档：https://panjiachen.gitee.io/vue-element-admin-site/zh/guide/essentials/router-and-nav.html
 * 下面是静态路由
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [{
      path: '/redirect/:path(.*)',
      component: () => import('@/views/redirect/index')
    }]
  }, {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  }, {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  }, {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  }, {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  }, {
    path: '/',
    redirect: '/dashboard/index',
  }, {
    path: '/oauth2/error',
    component: () => import('@/views/oauth2/error'),
    hidden: true
  }, {
    path: '/oauth2/success',
    component: () => import('@/views/oauth2/success'),
    hidden: true
  }, {
    path: '/knife4jVue3/dashuSwagger/home',
    component: () => import('@/views/swagger/home'),
    hidden: true
  }, {
    path: '/generator/form',
    component: () => import('@/views/generator/form'),
    hidden: true
  }
]

// 错误页跳转404
export const errorRoute = {
  path: '*',
  redirect: '/404',
  hidden: true
}

// 重置路由
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

// 初始化路由（动态路由，src/permission.js里动态addRoutes）
// 文档（see: https://panjiachen.gitee.io/vue-element-admin-site/zh/guide/essentials/router-and-nav.html）
const createRouter = () => new Router({
  // mode: 'history', // 默认使用：hashHistory，如果需要browserHistory则开启
  scrollBehavior: () => ({y: 0}),
  routes: constantRoutes
})

const router = createRouter()

export default router
