/**
 * 首页路由配置
 */
const homePath = '/dashboard/index'
const homeName = 'dashboard'
const homeTitle = '首页'

export default {
  path: homePath,
  name: homeName,
  title: homeTitle,
  component: () => import('@/layout'),
  hidden: false,
  redirect: 'noRedirect',
  children: [{
    path: homePath,
    name: homeName,
    component: () => import('@/views/dashboard/index'),
    meta: {title: homeTitle, activeMenu: '', affix: false, breadcrumb: true, icon: 'dashboard', noCache: false}
  }]
}