import { defineStore } from 'pinia'
import { store } from '../index'
import { ElMessageBox } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { logoutApi } from '@/api/login'
import { useTagsViewStoreWithOut } from './tagsView'
import router, { resetRouter } from '@/router'
import { removeToken } from '@/utils/auth'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { loginRoute } from '@/router/constant-routes'
import { storageClear4Logout } from '@/utils/storage-keys'

// 定义用户信息类型
interface UserType {
  name?: string,
  avatar?: string,
  introduction?: string
}

interface UserState {
  userInfo?: UserType
  perRoutes?: any[]
  permissions?: any[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => {
    return {
      // 当前用户信息
      userInfo: undefined,
      // 当前角色拥有的所有路由
      perRoutes: [],
      // 当前用户拥有的所有权限标记
      permissions: []
    }
  },
  getters: {
    getUserInfo(): UserType | undefined {
      return this.userInfo
    },
    getPerRoutes(): any[] | undefined {
      return this.perRoutes
    },
    getPermissions(): any[] | undefined {
      return this.permissions
    }
  },
  actions: {
    setUserData(userInfo: UserType, perRoutes: any[], permissions: any[]) {
      this.userInfo = userInfo
      this.perRoutes = perRoutes
      this.permissions = permissions
    },
    logoutConfirm() {
      const {t} = useI18n()
      ElMessageBox.confirm(t('common.loginOutMessage'), t('common.reminder'), {
        confirmButtonText: t('common.ok'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }).then(async () => {
        const res = await logoutApi()
        if (res) {
          this.reset()
        }
      })
    },
    reset() {
      console.log('logout reset')
      // 删除token
      removeToken()
      // 重置用户信息
      this.userInfo = undefined
      this.perRoutes = []
      this.permissions = []
      // 重置路由信息
      usePermissionStoreWithOut().resetPermission()
      resetRouter()
      // 删除所有页面缓存和tag
      useTagsViewStoreWithOut().delAllViews()
      // 清理Cookie、localStorage、sessionStorage中的相关存储
      storageClear4Logout()
      // 跳转到登录页面
      router.replace(loginRoute.path)
    },
    // 统一退出方法
    logout() {
      console.log('logout')
      return new Promise((resolve) => {
        logoutApi().then(() => {
          console.log('logout success')
          this.reset() // 重置所有登录相关信息
          resolve(true)
        })
      })
    }
  }
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
