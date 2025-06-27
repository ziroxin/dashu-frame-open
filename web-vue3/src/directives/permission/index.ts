import { useUserStore } from "@/store/modules/user";

/**
 * 判断是否有权限
 * @param value 权限值
 */
const checkPermission = (el, binding) => {
  const {value} = binding
  if (value.length > 0) {
    // 获取当前用户权限列表
    const permissions = useUserStore().getPermissions
    // 获取当前标签值
    const permissionKey = Array.isArray(value) ? value : [value]
    // 比较用户是否有权限
    const hasPermission = permissions.some(perm => {
      return permissionKey.includes(perm.permissionName)
    })
    // 无权限移除dom
    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

export default {
  mounted: (el: Element, binding) => { checkPermission(el, binding) },
  updated: (el: Element, binding) => { checkPermission(el, binding) }
}
