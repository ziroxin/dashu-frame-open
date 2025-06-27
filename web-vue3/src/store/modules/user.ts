import { defineStore } from 'pinia'
import { store } from '../index'
import { UserType } from '@/api/login/types'
import { ElMessageBox } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { loginApi, logoutApi } from '@/api/login'
import { useTagsViewStore } from './tagsView'
import router from '@/router'
import Cookies from 'js-cookie'
import storageKeys from '@/utils/storage-keys'
import { setToken, setTokenValidTime } from '@/utils/auth'

interface UserState {
  userInfo?: UserType
  token: string
  tokenValidTime: Date,
  roleRouters?: string[] | AppCustomRouteRecordRaw[]
  permissions: any[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => {
    return {
      // 用户信息
      userInfo: undefined,
      // token
      token: '',
      // token有效时间
      tokenValidTime: new Date(),
      // 角色路由
      roleRouters: undefined,
      // 权限
      permissions: []
    }
  },
  getters: {
    getUserInfo(): UserType | undefined {
      return this.userInfo
    },
    getToken(): string {
      return Cookies.get(storageKeys.c_token)
    },
    getTokenValidTime(): Date {
      return Cookies.get(storageKeys.c_tokenValidTime)
    },
    getRoleRouters(): string[] | AppCustomRouteRecordRaw[] | undefined {
      return this.roleRouters
    },
    getPermissions(): any[] {
      return this.permissions
    }
  },
  actions: {
    setToken(token: string) {
      this.token = token
    },
    setUserInfo(userInfo?: UserType) {
      this.userInfo = userInfo
    },
    setRoleRouters(roleRouters: string[] | AppCustomRouteRecordRaw[]) {
      this.roleRouters = roleRouters
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
      this.setToken('')
      this.setUserInfo(undefined)
      this.setRoleRouters([])
      router.replace('/login')
    },
    logout() {
      this.reset()
    },
    // 登录
    login(userInfo: any): Promise<void> {
      return new Promise((resolve, reject) => {
        loginApi(userInfo).then(response => {
          const {data} = response
          setToken(data.accessToken, new Date(data.accessTokenValidTime))
          setTokenValidTime(new Date(data.accessTokenValidTime))
          this.setToken(data.accessToken)
          // 是默认密码
          sessionStorage.setItem(storageKeys.s_isDefaultPassword, data.defaultPassword)
          sessionStorage.setItem(storageKeys.s_isInvalidPassword, data.invalidPassword)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    refreshToken() {
      // TODO: refresh token
      console.log('refresh token!!!!')
    }
  },
  persist: true
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
