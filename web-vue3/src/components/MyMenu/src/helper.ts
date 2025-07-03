import { findPath } from '@/utils/tree'

type OnlyOneChildType = AppRouteRecordRaw & { noShowingChildren?: boolean }

interface HasOneShowingChild {
  oneShowingChild?: boolean
  onlyOneChild?: OnlyOneChildType
}

export const getAllParentPath = <T = Recordable>(treeData: T[], path: string) => {
  const menuList = findPath(treeData, (n) => n.path === path) as AppRouteRecordRaw[]
  return (menuList || []).map((item) => item.path)
}

export const hasOneShowingChild = (children: AppRouteRecordRaw[] = [], parent: AppRouteRecordRaw): HasOneShowingChild => {
  const showingChildren = children?.filter((v) => v.meta && !v.meta.hidden)
  // 没有可展示的子路由
  if (!showingChildren || !showingChildren.length) {
    return {oneShowingChild: true, onlyOneChild: {...parent, path: '', noShowingChildren: true}}
  }
  // 只有一个子路由
  if (showingChildren.length === 1) {
    return {oneShowingChild: true, onlyOneChild: showingChildren[0]}
  }
  // 多个子路由
  return {oneShowingChild: false}
}
