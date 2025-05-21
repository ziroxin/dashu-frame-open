import {storageKeyPrefix} from "@/settings";

/** 配置一个统一前缀，防止不同项目key值冲突 */
let key_prefix = storageKeyPrefix

/**
 * 统一管理: Cookie、localStorage、sessionStorage 的key值
 */
export default {
  // auth权限相关的key
  tokenKey: `${key_prefix}_admin_token`,
  tokenValidTimeKey: `${key_prefix}_admin_token_valid_time`,
  // 主题配置的key
  themeSetting: `${key_prefix}_settings`,
  // 侧边栏状态的key
  sidebarStatus: `${key_prefix}_sidebar_status`,
  // 系统整体大小（字体等：default、medium、small、mini）的key
  size: `${key_prefix}_size`,
  // 是否默认密码（true：是，false：否）默认密码用户登录强制修改的key
  isDefaultPassword: `${key_prefix}_is_default_password`,
  // 密码是否已过期（true：是，false：否）的key
  isInvalidPassword: `${key_prefix}_is_invalid_password`,
  // 登录页背景数据的key
  loginBgData: `${key_prefix}_login_bg_data`,
  loginBgDataSkip: `${key_prefix}_login_bg_data_skip`,
  // 记住密码相关，存储当前用户信息的key
  currentLoginUserData: `${key_prefix}_current_login_user_data`,
  // 简易postman页historyKeys
  historyKeys: `${key_prefix}_history_keys`,
  // request.js中存储的旧请求对象的key
  oldReqObj: `${key_prefix}_old_req_obj`,
  // 综合布局下，当前激活菜单项的key
  activeTopMenu: `${key_prefix}_active_top_menu`,
  // 支付相关数据缓存的key
  payData: `${key_prefix}_pay_data`,
  // 最近访问的路由记录的key
  lastedRoutesKey: `${key_prefix}_lasted_routes_key`,
}