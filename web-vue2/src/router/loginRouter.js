/**
 * 登录页面路由配置
 */
export default {
  path: '/login',
  name: 'login',
  title: '登录页',
  component: () => import('@/views/login/index'),
  hidden: true
}