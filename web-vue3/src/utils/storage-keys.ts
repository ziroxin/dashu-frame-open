import Cookies from 'js-cookie'

/**
 * 统一管理: Cookie、localStorage、sessionStorage 的key值
 *
 * 配置统一前缀，防止不同项目key值冲突
 */
const key_prefix = import.meta.env.VITE_APP_KEY_PREFIX
Cookies.set('key_prefix', key_prefix)

/**
 * 统一管理: Cookie、localStorage、sessionStorage 的key值
 *
 * 注意：
 * 1. 请不要随意修改key值，以免造成其他功能异常
 * 2. Cookie以 c_ 开头，localStorage以 l_ 开头，sessionStorage以 s_ 开头
 */
const storageKeys = {
  key_prefix: key_prefix,
  // auth权限相关的key
  c_token: `${key_prefix}_admin_token`,
  c_tokenValidTime: `${key_prefix}_admin_token_valid_time`,
  // 是否默认密码（true：是，false：否）默认密码用户登录强制修改的key
  s_isDefaultPassword: `${key_prefix}_is_default_password`,
  // 密码是否已过期（true：是，false：否）的key
  s_isInvalidPassword: `${key_prefix}_is_invalid_password`,
  // request.js中存储的旧请求对象的key
  s_oldReqObj: `${key_prefix}_old_req_obj`,
  // 记住密码相关，存储当前用户信息的key
  l_rememberMeData: `${key_prefix}_remember_me_data`,
  // 最近访问的路由记录的key
  l_lastedRoutesKey: `${key_prefix}_lasted_routes_key`,
  // 国际化lang的key
  l_lang: `${key_prefix}_lang`,
  // app主题的store持久化key（主题配置）
  l_themeSetting: `${key_prefix}_app_theme_settings`,
  // permission的store持久化key
  l_permissionStore: `${key_prefix}_store_permission`,
  // 用户信息的store持久化key
  l_userStore: `${key_prefix}_store_user`,
  // lock锁屏的store持久化key
  l_lockStore: `${key_prefix}_store_lock`,
  // 数据字典持久化key
  l_dictList: `${key_prefix}_dicts_list`,
  // 侧边栏状态的key
  sidebarStatus: `${key_prefix}_sidebar_status`,
  // 系统整体大小（字体等：default、medium、small、mini）的key
  size: `${key_prefix}_size`,
  // 简易postman页historyKeys
  historyKeys: `${key_prefix}_history_keys`,
  // 综合布局下，当前激活菜单项的key
  activeTopMenu: `${key_prefix}_active_top_menu`,
  // 支付相关数据缓存的key
  payData: `${key_prefix}_pay_data`
}

export default storageKeys

/**
 * 退出时，清理所有相关的key
 */
export function storageClear4Logout() {
  console.log('clear storage')
  // cookie
  Cookies.remove(storageKeys.c_token)
  Cookies.remove(storageKeys.c_tokenValidTime)
  // sessionStorage
  sessionStorage.removeItem(storageKeys.s_isDefaultPassword)
  sessionStorage.removeItem(storageKeys.s_isInvalidPassword)
  sessionStorage.removeItem(storageKeys.s_oldReqObj)
  // localStorage
  localStorage.removeItem(storageKeys.l_permissionStore)
  localStorage.removeItem(storageKeys.l_userStore)
  localStorage.removeItem(storageKeys.l_lockStore)
}