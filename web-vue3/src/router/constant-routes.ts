import { Layout } from '@/utils/router-helper'

/**
 * 登录页
 */
export const loginRoute = {
  path: '/login',
  name: 'Login',
  title: '登录页',
  component: () => import('@/views/Login/Login.vue'),
  meta: {hidden: true, title: '登录页', noTagsView: true}
}

/**
 * 首页
 */
export const homeRoute = {
  path: '/dashboard/index',
  name: 'Dashboard',
  component: Layout,
  meta: {},
  children: [{
    path: '',
    name: 'DashboardIndex',
    component: () => import('@/views/Dashboard/Index.vue'),
    meta: {
      title: '首页',
      icon: 'vi-ant-design:dashboard-filled',
      alwaysShow: true,
      noCache: true,
      affix: true
    }
  }]
}

/**
 * oauth2 client 相关页面 - 路由配置
 */
export const oauth2Routes = [
  {
    path: '/oauth2/error',
    name: 'Oauth2Error',
    component: () => import('@/views/oauth2/error.vue'),
    meta: {hidden: true}
  }, {
    path: '/oauth2/success',
    name: 'Oauth2Success',
    component: () => import('@/views/oauth2/success.vue'),
    meta: {hidden: true}
  }, {
    path: '/oauth2/bind',
    name: 'Oauth2Bind',
    component: () => import('@/views/oauth2/bind.vue'),
    meta: {hidden: true}
  }
]

/**
 * 其他静态页面 - 路由配置
 */
export const otherRoutes = [
  // swagger 静态页
  {
    path: '/swagger/home',
    name: 'Knife4jSwaggerHome',
    component: () => import('@/views/swagger/home.vue'),
    meta: {hidden: true}
  },
  // 在线表单代码生成器 静态页
  {
    path: '/generator/form',
    name: 'GeneratorForm',
    component: () => import('@/views/generator/form.vue'),
    meta: {hidden: true}
  },
  // 用户个人中心（登录可用）
  {
    path: '/system/user/MyUser',
    name: 'MyUser',
    component: Layout,
    meta: {hidden: true},
    children: [{
      path: '/system/user/MyUser',
      name: 'MyUserIndex',
      component: () => import('@/views/system/user/MyUser.vue'),
      meta: {title: '个人中心'}
    }]
  },
  // 消息中心（登录可用）
  {
    path: '/system/message',
    name: 'SystemMessage',
    component: Layout,
    meta: {hidden: true},
    children: [{
      path: '/system/message',
      name: 'SystemMessageIndex',
      component: () => import('@/views/system/message/index.vue'),
      meta: {title: '消息中心'}
    }]
  }
]