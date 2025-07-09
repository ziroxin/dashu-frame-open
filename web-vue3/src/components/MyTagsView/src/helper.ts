import type { RouteLocationNormalizedLoaded } from 'vue-router'

export const filterAffixTags = (routes: AppRouteRecordRaw[]) => {
  let tags: RouteLocationNormalizedLoaded[] = []
  routes.forEach((route) => {
    const tagPath = route.path
    if (route.meta?.affix) {
      tags.push({...route, path: tagPath, fullPath: tagPath} as RouteLocationNormalizedLoaded)
    }
    if (route.children) {
      const tempTags: RouteLocationNormalizedLoaded[] = filterAffixTags(route.children)
      if (tempTags && tempTags.length > 0) {
        tags = [...tags, ...tempTags]
      }
    }
  })
  return tags
}
