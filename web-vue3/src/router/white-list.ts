/**
 * 单页白名单列表
 * 不走权限验证、不在Layout中展示的页面，如登录页、注册页等
 *
 * 注意：使用 /.* 可以模糊匹配目录
 */
const whiteList = ['/login', '/register', '/knife4jVue3/dashuSwagger/home', '/generator/form',
  '/oauth2/error', '/oauth2/success', '/oauth2/bind']

// 路由是否在白名单中
export function isWhiteList(path) {
  // 匹配完整路径
  if (whiteList.indexOf(path) !== -1) {
    return true
  }
  // 匹配 .* 路径
  const patts = whiteList.filter(o => o.indexOf('/.*') !== -1);
  for (let i = 0; i < patts.length; i++) {
    if (new RegExp(patts[i]).test(path)) {
      return true
    }
  }
  return false
}
