/**
 * 其他静态页面 - 路由配置
 */
export default [
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
  // 消息中心（登录可用）
  {
    path: '/system/message',
    component: () => import('@/layout'),
    hidden: true,
    children: [{
      path: '/system/message',
      component: () => import('@/views/system/message/index.vue'),
      meta: {title: '消息中心'}
    }]
  }
]