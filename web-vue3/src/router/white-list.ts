import { loginRoute, oauth2Routes } from '@/router/constant-routes'

/**
 * 单页白名单列表
 * 不走权限验证、不在Layout中展示的页面，如登录页、注册页等
 *
 * 注意：使用 /.* 可以模糊匹配目录
 */
const whiteList = [loginRoute.path, ...oauth2Routes.map(o => o.path),
  '/register', '/swagger/home', '/generator/form']

// 路由是否在白名单中
export function isWhiteList(path) {
  // 匹配完整路径
  if (whiteList.indexOf(path) !== -1) {
    return true
  }
  // 匹配 .* 路径
  const patterns = whiteList.filter(o => o.indexOf('/.*') !== -1)
  for (let i = 0; i < patterns.length; i++) {
    if (new RegExp(patterns[i]).test(path)) {
      return true
    }
  }
  return false
}
