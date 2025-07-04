import type { RouteLocationNormalized, Router, RouteRecordNormalized, RouteRecordRaw } from 'vue-router'
import { createRouter, createWebHashHistory } from 'vue-router'
import { isUrl } from '@/utils/is'
import { cloneDeep, omit } from 'lodash-es'
import ErrorComponent from '@/views/Error/ErrorComponent.vue'

// 顶级菜单使用：Layout组件
export const Layout = () => import('@/layout/Layout.vue')

const modules = import.meta.glob('../views/**/*.{vue,tsx}')
// 模块 - 动态导入
const convertToComponent = (componentName, childrenLength = 0) => {
  // 空/有子路由，直接返回
  if (!componentName || childrenLength > 0) {
    return () => new Promise((resolve) => { resolve({name: 'ParentLayout'}) })
  }
  // 加载其他路由
  if (componentName === 'Layout') {
    return Layout // 返回Layout组件
  }
  // 转换组件
  const view = componentName.startsWith('/') ? componentName.substring(1) : componentName
  const componentModule = modules[`../views/${view}.vue`] || modules[`../views/${view}.tsx`]
  if (componentModule) {
    return componentModule
  } else {
    console.error('路由加载出错了，组件不存在！')
    return ErrorComponent
  }
}

export const getRawRoute = (route: RouteLocationNormalized): RouteLocationNormalized => {
  if (!route) return route
  const {matched, ...opt} = route
  return {
    ...opt,
    matched: (matched
      ? matched.map((item) => ({meta: item.meta, name: item.name, path: item.path}))
      : undefined) as RouteRecordNormalized[]
  }
}

/**
 * 通过后台api数据，生成动态路由 - 非隐藏路由
 * @param routes 后台api数据
 * @param isTop 是否顶级路由
 */
export const generateRoutesByServer = (routes: Array<any>, isTop: boolean): AppRouteRecordRaw[] => {
  const res: AppRouteRecordRaw[] = []
  // 遍历路由
  routes.forEach(route => {
    // 是否禁用，是否显示
    if (route.permissionIsEnabled && route.permissionIsShow) {
      // 遍历子路由
      let thisChild: AppRouteRecordRaw[] = []
      if (route.children) {
        thisChild = generateRoutesByServer(route.children, false)
      }
      // 普通路由属性
      let temp: AppRouteRecordRaw = {
        path: (thisChild.length > 0 ? '/Parent' : '') + route.permissionRouter,
        // 顶部路由加Top-前缀，防止与children中name冲突
        name: (isTop ? 'Top-' : '') + route.permissionName || '',
        // 顶级路由默认使用Layout；非顶级路由，若有子路由，则使用ParentLayout；若无子路由，则使用该组件
        component: isTop ? Layout : convertToComponent(route.permissionComponent, thisChild.length),
        meta: {
          hidden: !route.permissionIsShow,
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
              name: route.permissionName || '',
              component: convertToComponent(route.permissionComponent, 0),
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
  })
  return res
}

/**
 * 通过后台api数据，生成动态路由 - 隐藏路由
 * @param routes 后台api数据
 */
export const generateRoutes4HiddenByServer = (routes: Array<any>): AppRouteRecordRaw[] => {
  const res: AppRouteRecordRaw[] = []
  // 遍历路由
  routes.forEach(route => {
    // 是否禁用
    if (route.permissionIsEnabled) {
      // 是否显示
      if (!route.permissionIsShow) {
        // 普通路由属性
        const temp: AppRouteRecordRaw = {
          path: route.permissionRouter,
          // 父级name加Hidden-前缀，防止与children中name冲突
          name: 'Hidden-' + route.permissionName || '',
          // 顶级路由默认使用Layout；非顶级路由，若有子路由，则使用ParentLayout；若无子路由，则使用该组件
          component: Layout,
          meta: {hidden: !route.permissionIsShow},
          children: [{
            path: route.permissionRouter,
            name: route.permissionName || '',
            component: convertToComponent(route.permissionComponent, 0),
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
      }
      // 遍历子路由
      if (route.children) {
        res.push(...generateRoutes4HiddenByServer(route.children))
      }
    }
  })
  return res
}

export const pathResolve = (parentPath: string, path: string) => {
  if (isUrl(path)) return path
  if (parentPath.includes(path) && path) return path
  const resolveParentPath = parentPath ? parentPath : '/'
  const childPath = path ? (path.startsWith('/') ? path : `/${path}`) : ''
  return `${resolveParentPath}${childPath}`.replace(/\/\//g, '/').trim()
}

// 路由降级
export const flatMultiLevelRoutes = (routes: AppRouteRecordRaw[]) => {
  const modules: AppRouteRecordRaw[] = cloneDeep(routes)
  for (let index = 0; index < modules.length; index++) {
    const route = modules[index]
    if (!isMultipleRoute(route)) {
      continue
    }
    promoteRouteLevel(route)
  }
  return modules
}

// 层级是否大于2
const isMultipleRoute = (route: AppRouteRecordRaw) => {
  if (!route || !Reflect.has(route, 'children') || !route.children?.length) {
    return false
  }

  const children = route.children

  let flag = false
  for (let index = 0; index < children.length; index++) {
    const child = children[index]
    if (child.children?.length) {
      flag = true
      break
    }
  }
  return flag
}

// 生成二级路由
const promoteRouteLevel = (route: AppRouteRecordRaw) => {
  let router: Router | null = createRouter({
    routes: [route as RouteRecordRaw],
    history: createWebHashHistory()
  })

  const routes = router.getRoutes()
  addToChildren(routes, route.children || [], route)
  router = null

  route.children = route.children?.map((item) => omit(item, 'children'))
}

// 添加所有子菜单
const addToChildren = (
  routes: RouteRecordNormalized[],
  children: AppRouteRecordRaw[],
  routeModule: AppRouteRecordRaw
) => {
  for (let index = 0; index < children.length; index++) {
    const child = children[index]
    const route = routes.find((item) => item.name === child.name)
    if (!route) {
      continue
    }
    routeModule.children = routeModule.children || []
    if (!routeModule.children.find((item) => item.name === route.name)) {
      routeModule.children?.push(route as unknown as AppRouteRecordRaw)
    }
    if (child.children?.length) {
      addToChildren(routes, child.children, routeModule)
    }
  }
}
