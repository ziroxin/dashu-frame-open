/** 生成uuid */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

/**
 * 时间格式化
 * @param {(Object|string|number)} 时间
 * @param {string} 格式
 * @returns {string | null}
 */
export function parseTime(time, cFormat) {
  if (arguments.length === 0 || !time) {
    return null
  }
  const format = cFormat || 'yyyy-MM-dd HH:mm:ss' // 修改默认格式为 'yyyy-MM-dd HH:mm:ss'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string')) {
      if ((/^[0-9]+$/.test(time))) {
        // 支持时间戳（例：1548221490638）
        time = parseInt(time)
      } else {
        // 苹果safari浏览器支持（参考文档：https://stackoverflow.com/questions/4310953/invalid-date-in-safari）
        time = time.replace(new RegExp(/-/gm), '/')
      }
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    yyyy: date.getFullYear(),
    MM: (date.getMonth() + 1).toString().padStart(2, '0'), // 月份从0开始，所以需要加1
    M: (date.getMonth() + 1),
    dd: date.getDate().toString().padStart(2, '0'),
    d: date.getDate(),
    HH: date.getHours().toString().padStart(2, '0'),
    H: date.getHours(),
    mm: date.getMinutes().toString().padStart(2, '0'),
    m: date.getMinutes(),
    ss: date.getSeconds().toString().padStart(2, '0'),
    s: date.getSeconds(),
    SSS: date.getMilliseconds().toString().padStart(3, '0'),
    S: date.getMilliseconds(),
    ww: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()],
    w: ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
  }
  const time_str = format.replace(/(yyyy|MM|M|dd|d|HH|H|mm|m|ss|s|SSS|S|ww|w)/g, (key) => {
    return formatObj[key] || ''
  })
  return time_str
}
