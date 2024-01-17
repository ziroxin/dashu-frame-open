const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  // token值
  token: state => state.user.token,
  // 格式：{UserJwtToken: 'token值'}
  headerToken: state => state.user.headerToken,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  introduction: state => state.user.introduction,
  // 用户路由
  perRouters: state => state.user.perRouters,
  // 用户权限
  permissions: state => state.user.permissions,
  // 组装好的路由
  permission_routes: state => state.permission.routes,
  errorLogs: state => state.errorLog.logs,
  // 设置
  settings: state => state.settings,
  // 数据字典
  dict: state => state.dict.dict,
}
export default getters
