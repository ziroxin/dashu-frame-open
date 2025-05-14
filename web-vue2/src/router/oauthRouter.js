/**
 * oauth2 client 相关页面 - 路由配置
 */
export default [
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
  }
]