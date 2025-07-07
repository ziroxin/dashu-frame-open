interface HasOneShowingChild {
  childCount?: number
  onlyOneChild?: AppRouteRecordRaw
}

export const hasOneShowingChild = (children: AppRouteRecordRaw[] = []): HasOneShowingChild => {
  const showingChildren = children?.filter((v) => v.meta && !v.meta.hidden)
  // 没有可展示的子路由
  if (!showingChildren || !showingChildren.length) {
    return {childCount: 0}
  }
  // 只有一个子路由
  if (showingChildren.length === 1) {
    return {childCount: 1, onlyOneChild: showingChildren[0]}
  }
  // 多个子路由
  return {childCount: showingChildren.length}
}
