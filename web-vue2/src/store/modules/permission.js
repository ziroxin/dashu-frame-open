import Layout from '@/layout'
import {errorRoute} from '@/router';

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
      if (route.permissionIsEnabled && route.permissionIsShow) {
        // 遍历子路由
        let thisChild;
        if (route.children) {
          thisChild = filterAsyncRoutes(route.children, false)
        }
        // 普通路由属性
        let temp = {
          hidden: !route.permissionIsShow,
          path: route.permissionRouter,
          component: isTop ? Layout : convertComponent(route.permissionComponent),
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
        // 是否有子路由
        if (thisChild && thisChild.length > 0) {
          // 有子路由（配置子路由属性）
          temp = {
            ...temp,
            redirect: route.noRedirect,
            alwaysShow: true,
            children: thisChild
          }
        } else {
          if (isTop) {
            // 无子路由，但是顶级路由（特殊处理）
            temp = {
              ...temp,
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
          }
        }
        res.push(temp)
      }
    }
  )
  return res
}

/**
 * 迭代（递归）循环出动态路由 - 专门处理隐藏路由
 */
export function filterAsyncRoutes4Hidden(routers, isTop) {
  const res = []
  // 遍历路由
  routers.forEach(route => {
      // 是否禁用
      if (route.permissionIsEnabled) {
        if (!route.permissionIsShow) {
          // 普通路由属性
          let temp = {
            hidden: !route.permissionIsShow,
            path: route.permissionRouter,
            // component: isTop ? Layout : convertComponent(route.permissionComponent),
            component: Layout,
            children: [
              {
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
            ]
          }
          res.push(temp)
        }
        // 遍历子路由
        if (route.children) {
          res.push(...filterAsyncRoutes4Hidden(route.children, false))
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
      // 动态生成菜单（排除隐藏路由）
      let accessedRoutes = filterAsyncRoutes(routers, true)
      // 单独处理隐藏路由
      accessedRoutes.push(...filterAsyncRoutes4Hidden(routers, true))
      // 404路由
      accessedRoutes.concat(errorRoute)
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
