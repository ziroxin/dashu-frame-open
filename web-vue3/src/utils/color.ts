/**
 * 判断是否 十六进制颜色值（输入形式可为 #fff000 #f00）
 * @param color 十六进制颜色值
 */
export const isHexColor = (color: string) => {
  const reg = /^#([0-9a-fA-F]{3}|[0-9a-fA-f]{6})$/
  return reg.test(color)
}

/**
 * RGB 颜色值转换为 十六进制颜色值（r, g, 和 b 需要在 [0, 255] 范围内）
 * @param r RED值
 * @param g GREEN值
 * @param b BLUE值
 */
export const rgbToHex = (r: number, g: number, b: number) => {
  // tslint:disable-next-line:no-bitwise
  const hex = ((r << 16) | (g << 8) | b).toString(16)
  return '#' + new Array(Math.abs(hex.length - 7)).join('0') + hex
}

/**
 * 将HEX颜色转换为RGB表示（例：#FFFFFF => RGB(255,255,255)）
 * @param hex 要转换的颜色
 * @param opacity 透明度
 */
export const hexToRGB = (hex: string, opacity?: number) => {
  let sHex = hex.toLowerCase()
  if (isHexColor(hex)) {
    if (sHex.length === 4) {
      let sColorNew = '#'
      for (let i = 1; i < 4; i += 1) {
        sColorNew += sHex.slice(i, i + 1).concat(sHex.slice(i, i + 1))
      }
      sHex = sColorNew
    }
    const sColorChange: number[] = []
    for (let i = 1; i < 7; i += 2) {
      sColorChange.push(parseInt('0x' + sHex.slice(i, i + 2)))
    }
    return opacity ? 'RGBA(' + sColorChange.join(',') + ',' + opacity + ')' : 'RGB(' + sColorChange.join(',') + ')'
  }
  return sHex
}

/**
 * 颜色是否深色
 * @param color 要判断的颜色
 */
export const colorIsDark = (color: string) => {
  if (!isHexColor(color)) return
  const [r, g, b] = hexToRGB(color)
    .replace(/(?:\(|\)|rgb|RGB)*/g, '').split(',').map((item) => Number(item))
  return r * 0.299 + g * 0.578 + b * 0.114 < 192
}

/**
 * 颜色变暗（#RRGGBB）
 * @param {string} color 要变暗的颜色
 * @param {number} amount 变暗的值，范围 0-100
 */
export const darken = (color: string, amount: number) => {
  color = color.indexOf('#') >= 0 ? color.substring(1, color.length) : color
  amount = Math.trunc((255 * amount) / 100)
  return `#${subtractLight(color.substring(0, 2), amount)}${subtractLight(color.substring(2, 4), amount)}${subtractLight(color.substring(4, 6), amount)}`
}

const subtractLight = (color: string, amount: number) => {
  const cc = parseInt(color, 16) - amount
  const c = cc < 0 ? 0 : cc
  return c.toString(16).length > 1 ? c.toString(16) : `0${c.toString(16)}`
}

/**
 * 颜色变亮（#RRGGBB）
 * @param {string} color 要变亮的颜色
 * @param {number} amount 变亮的值，范围 0-100
 */
export const lighten = (color: string, amount: number) => {
  color = color.indexOf('#') >= 0 ? color.substring(1, color.length) : color
  amount = Math.trunc((255 * amount) / 100)
  return `#${addLight(color.substring(0, 2), amount)}${addLight(color.substring(2, 4), amount)}${addLight(color.substring(4, 6), amount)}`
}

const addLight = (color: string, amount: number) => {
  const cc = parseInt(color, 16) + amount
  const c = cc > 255 ? 255 : cc
  return c.toString(16).length > 1 ? c.toString(16) : `0${c.toString(16)}`
}

/**
 * 混合两种颜色（#RRGGBB）
 *
 * @param {string} color1 颜色1 (#RRGGBB)
 * @param {string} color2 颜色2 (#RRGGBB)
 * @param {number} weight 混合权重，范围0-1之间，默认0.5（例如：weight=0.6；则color1=0.6；color2=1-0.6=0.4）
 */
export const mix = (color1: string, color2: string, weight: number = 0.5): string => {
  let color = '#'
  for (let i = 0; i <= 2; i++) {
    const c1 = parseInt(color1.substring(1 + i * 2, 3 + i * 2), 16)
    const c2 = parseInt(color2.substring(1 + i * 2, 3 + i * 2), 16)
    const c = Math.round(c1 * weight + c2 * (1 - weight))
    color += c.toString(16).padStart(2, '0')
  }
  return color
}
