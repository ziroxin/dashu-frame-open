/**
 * 权限指令
 * 使用方法：
 *    用于控制html标签的显示和隐藏
 *    v-permission可以加在任意标签，支持数组
 *    权限根据表字段：permission_name 来匹配
 *    <el-button v-permission="['admin']">按钮</el-button>
 */
import store from '@/store'

function checkPermission(el, binding) {
  const {value} = binding
  // 获取当前用户权限列表
  const permissions = store.getters && store.getters.permissions
  // 获取当前标签值
  if (value.length > 0) {
    const permissionKey = Array.isArray(value) ? value : [value]
    // 比较用户是否有权限
    const hasPermission = permissions.some(perm => {
      return permissionKey.includes(perm.permissionName)
    })
    // 无权限移除dom
    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

export default {
  inserted(el, binding) {
    checkPermission(el, binding)
  },
  update(el, binding) {
    checkPermission(el, binding)
  }
}
