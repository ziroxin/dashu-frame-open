import router from './router'
import { useTitle } from '@/hooks/web/useTitle'
import { useNProgress } from '@/hooks/web/useNProgress'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { usePageLoading } from '@/hooks/web/usePageLoading'
import { useUserStoreWithOut } from '@/store/modules/user'
import { getToken } from '@/utils/auth'
import { isWhiteList } from '@/router/white-list'
import { saveLastedRoutes } from '@/utils/lasted-routes'
import request from '@/utils/request'

const {start, done} = useNProgress()
const {loadStart, loadDone} = usePageLoading()

router.beforeEach(async (to, from, next) => {
  // 开启进度条
  start()
  loadStart()

  // 单独判断login
  if (to.path === '/login') {
    if (getToken()) {
      // 已登录的话，直接进入首页
      next({path: '/'})
    }
  }
  // 路由白名单，直接跳转（不需检测token）
  if (isWhiteList(to.path)) {
    saveLastedRoutes(to.path)
    next()
  } else {
    // 出现错误跳转到登录页
    const errorToLogin = `/login?redirect=${to.path}`
    // 判断用户是否登录（有token代表登录）
    const hasToken = getToken()
    if (hasToken) {
      // 登录后，跳转到原来打开的页面
      const permissionStore = usePermissionStoreWithOut()
      const hasRoutes = permissionStore.getRoutes && permissionStore.getRoutes.length > 0
      // 跳转前，先判断：store里是否有角色相关信息（路由等）
      if (hasRoutes) {
        saveLastedRoutes(to.path)
        next()
      } else {
        // 查询登录用户信息、角色信息、路由信息、权限信息
        const {data} = await request({url: '/permission/user/all', method: 'get'})
        if (data) {
          // 查询成功，组装路由
          const {permissions, perRouters} = data
          if (perRouters?.length > 0) {
            const userStore = useUserStoreWithOut()
            // 保存用户登录信息
            userStore.setUserData(data.user, perRouters, permissions)
            // 生成用户路由
            await permissionStore.generateRoutes(perRouters)
            // 加载路由完成，跳转
            const redirect = decodeURIComponent((from.query.redirect || to.path) as string)
            next(to.path === redirect ? {...to, replace: true} : {path: redirect})
          }
        }
        // 未正常跳转，跳转到登录页
        next(errorToLogin)
      }
    } else {
      // 无token
      // 无权限，跳到登录页
      next(errorToLogin)
    }
  }
})

router.afterEach((to) => {
  useTitle(to?.meta?.title as string) // 更新 html 的 title
  done() // 结束Progress
  loadDone()
})
