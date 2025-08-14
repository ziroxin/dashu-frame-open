/** 驼峰转下划线 */
export const humpToUnderline = (str: string): string => {
  return str.replace(/([A-Z])/g, '_$1').toLowerCase()
}

/** 下划线转驼峰 */
export const underlineToHump = (str: string): string => {
  if (!str) return ''
  return str.replace(/_(\w)/g, (_, letter: string) => letter.toUpperCase())
}

/** 首字母大写 */
export function firstUpperCase(str: string) {
  return str.toLowerCase().replace(/( |^)[a-z]/g, (L) => L.toUpperCase())
}

/** 驼峰转中划线 */
export const humpToDash = (str: string): string => {
  return str.replace(/([A-Z])/g, '-$1').toLowerCase()
}

/** 设置css变量 */
export const setCssVar = (prop: string, val: any, dom = document.documentElement) => {
  dom.style.setProperty(prop, val)
}

/** 获取css变量 */
export const getCssVar = (prop: string, dom = document.documentElement) => {
  return getComputedStyle(dom).getPropertyValue(prop)
}

/**
 * 查找数组对象的某个下标
 * @param {Array} ary 查找的数组
 * @param {Functon} fn 判断的方法
 */
export const findIndex = <T = Recordable>(ary: Array<T>, fn: Fn): number => {
  if (ary.findIndex) {
    return ary.findIndex(fn)
  }
  let index = -1
  ary.some((item: T, i: number, ary: Array<T>) => {
    const ret: T = fn(item, i, ary)
    if (ret) {
      index = i
      return ret
    }
  })
  return index
}

/** 去除字符串两端空格 */
export const trim = (str: string) => {
  return str.replace(/(^\s*)|(\s*$)/g, '')
}
