/**
 * 导入工具parseTime, formatTime，在main.js中，设置filter
 */
export {parseTime, formatTime} from '@/utils'

/**
 * 更多filter，在这里添加
 */

// 数字格式化，每千位加逗号（10000 => "10,000"）
export function toThousandFilter(num) {
  return (+num || 0).toString().replace(/^-?\d+/g, m => m.replace(/(?=(?!\b)(\d{3})+$)/g, ','))
}
