import Layout from '@/layout'
import {errorRoute} from "@/router";

// 转换成组件
function convertComponent(component) {
  if (!component) {
    // 空直接返回
    return
  }
  if (component === 'Layout') {
    // 返回Layout
    return Layout
  } else {
    // 转换组件
    const view = component.indexOf('/') === 0 ? component.substring(1) : component

    if (process.env.NODE_ENV === 'development') {
      return resolve => require([`@/views/${view}`], resolve)
    } else {
      return () => import('@/views/' + view)
    }
  }
}

/**
 * 迭代（递归）循环出动态路由
 */
export function filterAsyncRoutes(routers, isTop) {
  const res = []
  // 遍历路由
  routers.forEach(route => {
      // 是否禁用
      if (route.permissionIsEnabled) {
        if (route.children) {
          // 包含子路由的父级路由
          const temp = {
            hidden: !route.permissionIsShow,
            path: route.permissionRouter,
            component: isTop ? Layout : convertComponent(route.permissionComponent),
            name: route.permissionName || '',
            redirect: route.noRedirect,
            meta: {
              title: route.permissionTitle,
              icon: route.permissionIcon || '',
              activeMenu: !route.permissionIsShow ? route.activeMenu : '',
              noCache: route.noCache,
              breadcrumb: route.breadcrumb,
              affix: route.affix
            },
            alwaysShow: true,
            // 迭代子路由
            children: filterAsyncRoutes(route.children, false)
          }
          res.push(temp)
        } else {
          if (isTop) {
            // 顶级路由
            const temp = {
              hidden: !route.permissionIsShow,
              path: route.permissionRouter,
              component: Layout,
              redirect: route.noRedirect,
              children: [{
                path: 'index',
                component: convertComponent(route.permissionComponent),
                name: route.permissionName || '',
                meta: {
                  title: route.permissionTitle,
                  icon: route.permissionIcon || '',
                  activeMenu: !route.permissionIsShow ? route.activeMenu : '',
                  noCache: route.noCache,
                  breadcrumb: route.breadcrumb,
                  affix: route.affix
                }
              }]
            }
            res.push(temp)
          } else {
            // 子路由（不能配置redirect）
            const temp = {
              hidden: !route.permissionIsShow,
              path: route.permissionRouter,
              component: convertComponent(route.permissionComponent),
              name: route.permissionName || '',
              meta: {
                title: route.permissionTitle,
                icon: route.permissionIcon || '',
                activeMenu: !route.permissionIsShow ? route.activeMenu : '',
                noCache: route.noCache,
                breadcrumb: route.breadcrumb,
                affix: route.affix
              }
            }
            res.push(temp)
          }
        }
      }
    }
  )
  return res
}

const state = {
  routes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  }
}

const actions = {
  generateRoutes({commit}, routers) {
    return new Promise(resolve => {
      // 动态生成菜单
      const accessedRoutes = filterAsyncRoutes(routers, true)
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes.concat(errorRoute))
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
