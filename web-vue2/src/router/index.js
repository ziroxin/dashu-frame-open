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
    path: '/register',
    component: () => import('@/views/login/register'),
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
  },
  // oauth2 client 相关页面
  {
    path: '/oauth2/error',
    component: () => import('@/views/oauth2/error'),
    hidden: true
  }, {
    path: '/oauth2/success',
    component: () => import('@/views/oauth2/success'),
    hidden: true
  }, {
    path: '/oauth2/bind',
    component: () => import('@/views/oauth2/bind'),
    hidden: true
  },
  // swagger 静态页
  {
    path: '/knife4jVue3/dashuSwagger/home',
    component: () => import('@/views/swagger/home'),
    hidden: true
  },
  // 在线表单代码生成器 静态页
  {
    path: '/generator/form',
    component: () => import('@/views/generator/form'),
    hidden: true
  },
  // 用户个人中心（登录可用）
  {
    path: '/system/user/MyUser',
    component: () => import('@/layout'),
    hidden: true,
    children: [{
      path: '/system/user/MyUser',
      component: () => import('@/views/system/user/MyUser.vue'),
      meta: {title: '个人中心'}
    }]
  },
  // 首页路由
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('@/layout'),
    hidden: false,
    redirect: 'noRedirect',
    children: [{
      path: 'index',
      name: 'dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: {title: '首页', activeMenu: '', affix: false, breadcrumb: true, icon: 'dashboard', noCache: false}
    }]
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
