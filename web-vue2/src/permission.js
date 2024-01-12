import router from './router'
import store from './store'
import {Message} from 'element-ui'
// 进度条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
// 从Cookie中读取token
import {getToken} from '@/utils/auth'
import getPageTitle from '@/utils/get-page-title'
import {isWhiteList} from '@/router/white-list'
import {saveLastedRoutes} from '@/utils/lasted-routes'

// 进度条配置
NProgress.configure({showSpinner: false})

router.beforeEach(async (to, from, next) => {
  // 进度条
  NProgress.start()

  // 设置标题
  document.title = getPageTitle(to.meta.title)

  // 单独判断login
  if (to.path === '/login') {
    if (getToken()) {
      // 登录后，跳转到首页
      next({path: '/'})
      NProgress.done()
    }
  }
  // 路由白名单，直接跳转（不需检测token）
  if (isWhiteList(to.path)) {
    saveLastedRoutes(to.path)
    next()
    NProgress.done()
  } else {
    // 判断用户是否登录（有token代表登录）
    const hasToken = getToken()

    if (hasToken) {
      // 登录后，跳转到原来打开的页面
      // 跳转前，先判断：store里是否有角色信息
      const hasRouters = store.getters.perrouters && store.getters.perrouters.length > 0
      if (hasRouters) {
        saveLastedRoutes(to.path)
        next()
      } else {
        try {
          // 查询角色信息
          const {perrouters} = await store.dispatch('user/getInfo')
          if (perrouters && perrouters.length > 0) {
            // 根据角色，生成可访问权限（路由）
            const accessRoutes = await store.dispatch('permission/generateRoutes', perrouters)
            // 动态加载路由
            router.addRoutes(accessRoutes)
            // 加载路由完成，跳转
            // replace: true 不缓存，每次页面都刷新
            next({...to, replace: true})
          } else {
            await store.dispatch('user/resetToken')
            next(`/login?redirect=${to.path}`)
            Message.error('抱歉，您没有权限！')
            NProgress.done()
          }
        } catch (error) {
          // 出现错误，删除token，重新登录
          await store.dispatch('user/resetToken')
          // Message.error(error || 'Has Error')
          Message.error({message: error || 'Has Error'})
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    } else {
      // 无token
      // 无权限，跳到登录页
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

// 路由加载完成
router.afterEach(() => {
  // 关闭进度条
  NProgress.done()
})
