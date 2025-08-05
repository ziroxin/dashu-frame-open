/**
 * 包装类型判断函数
 * @param val 待判断的值
 * @param type 类型，如：Object、Array、String、Number等
 */
export const is = (val: unknown, type: string) => {
  return Object.prototype.toString.call(val) === `[object ${type}]`
}

/* 是否数字 */
export const isNumber = (val: unknown) => {
  return is(val, 'Number')
}

/* 是否字符串 */
export const isString = (val: unknown) => {
  return is(val, 'String')
}

/* 是否Url（判断外链） */
export const isUrl = (path: string): boolean => {
  try {
    new URL(path)
    return true
  } catch (_error) {
    return false
  }
}

