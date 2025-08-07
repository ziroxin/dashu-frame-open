export const filterBreadcrumb = (routeList: AppRouteRecordRaw[]): AppRouteRecordRaw[] => {
  const res: AppRouteRecordRaw[] = []

  for (const route of routeList) {
    const meta = route?.meta
    if (meta && meta.hidden) {
      continue
    }

    const data: AppRouteRecordRaw = meta && !meta.alwaysShow && route.children?.length === 1
      ? {...route.children[0]} : {...route}

    if (data.children) {
      data.children = filterBreadcrumb(data.children)
    }

    if (data) {
      res.push(data)
    }
  }
  return res
}
