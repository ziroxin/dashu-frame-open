import { defineStore } from 'pinia'
import { store } from '../index'
import { ElMessageBox } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { logoutApi } from '@/api/login'
import { useTagsViewStore } from './tagsView'
import router from '@/router'

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
      const tagsViewStore = useTagsViewStore()
      tagsViewStore.delAllViews()
      this.userInfo = undefined
      this.perRoutes = []
      this.permissions = []
      router.replace('/login')
    },
    logout() {
      this.reset()
    }
  },
  persist: true
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
