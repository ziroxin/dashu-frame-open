/**
 * Tree转化为List（遍历所有层级）
 * @param tree 树
 * @param childrenName 子节点字段名，默认：children
 */
export const treeToList = <T = any>(tree: any, childrenName: string = 'children'): T => {
  const result: any = [...tree]
  for (let i = 0; i < result.length; i++) {
    if (result[i][childrenName]) {
      result.splice(i + 1, 0, ...result[i][childrenName])
    }
  }
  return result
}

/**
 * 递归过滤树结构
 * @param tree 树
 * @param currentPath 当前路径（判断=currentPath的节点或子节点不为空的节点保留）
 * @param childrenName 子节点字段名，默认：children
 */
export const filterTreeByCondition = <T = any>(tree: T[], currentPath, childrenName: string = 'children'): T[] => {
  return tree.map((node: any) => ({...node})).filter((node) => {
    if (node[childrenName]) {
      node[childrenName] = filterTreeByCondition(node[childrenName], currentPath)
      return node.path === currentPath || node[childrenName].length
    }
    return node.path === currentPath
  })
}
