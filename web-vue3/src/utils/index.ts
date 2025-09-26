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

/** Object 转 字符串（单引号） */
export const objToStr = (obj: any): string => {
  // 1 非object类型直接返回
  if (typeof obj !== 'object') {
    return obj
  }
  // 2 定义处理不同类型数据的方法
  const replacer = (key: string, value: any): string => {
    // number/boolean直接返回
    if (typeof value === 'number' || typeof value === 'boolean') {
      return value.toString()
    }
    // 数组，处理每一个内部值
    if (Array.isArray(value)) {
      return `[${value.map(item => replacer(key, item)).join(', ')}]`
    }
    // 其他object，迭代转str
    if (value && typeof value === 'object') {
      return `${objToStr(value)}`
    }
    // 其他类型，如string等，加单引号返回
    return `'${value}'`
  }
  // 3 单独处理数组
  if (Array.isArray(obj)) {
    return `[${obj.map(item => replacer('', item)).join(', ')}]`
  }
  // 4 处理object
  // 4.1 含特殊字符的key，加单引号
  const specialCharsRegex = /[^a-zA-Z0-9_]/
  const processKey = (key: string): string => (specialCharsRegex.test(key) ? `'${key}'` : `${key}`)
  // 4.2 组装结果集
  const result: string[] = []
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      const newKey = processKey(key)
      const newValue = replacer(key, obj[key])
      result.push(`${newKey}:${newValue}`)
    }
  }
  return `{${result.join(', ')}}`
}

/** 字符串 转 object（优化JSON.parse） */
export const strToObj = (str: string): any => {
  try {
    // 使用正则表达式将单引号替换为双引号
    let jsonString = str.replace(/'/g, '"').replace(/\b(\d+)\b(?=\s*:)/g, '"$1"')
    // 给key加双引号
    jsonString = jsonString.replace(/(\w+):/g, '"$1":')
    // 解析 JSON 字符串为 JavaScript 对象
    return JSON.parse(jsonString)
  } catch (e) {
    return str
  }
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
