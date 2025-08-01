const domSymbol = Symbol('watermark-dom')

/** 使用水印 */
export function useWatermark(appendEl: HTMLElement | null = document.body) {
  let func: Fn = () => {}
  const id = domSymbol.toString()
  // 清除水印
  const clear = () => {
    const domId = document.getElementById(id)
    if (domId) {
      const el = appendEl
      el && el.removeChild(domId)
    }
    window.removeEventListener('resize', func)
  }
  // 创建水印
  const createWatermark = (str: string) => {
    clear()

    const can = document.createElement('canvas')
    can.width = 300
    can.height = 240

    const cans = can.getContext('2d')
    if (cans) {
      cans.rotate((-20 * Math.PI) / 120)
      cans.font = '15px Vedana'
      cans.fillStyle = 'rgba(0, 0, 0, 0.15)'
      cans.textAlign = 'left'
      cans.textBaseline = 'middle'
      cans.fillText(str, can.width / 20, can.height)
    }

    const div = document.createElement('div')
    div.id = id
    div.style.pointerEvents = 'none'
    div.style.top = '0px'
    div.style.left = '0px'
    div.style.position = 'absolute'
    div.style.zIndex = '100000000'
    div.style.width = document.documentElement.clientWidth + 'px'
    div.style.height = document.documentElement.clientHeight + 'px'
    div.style.background = 'url(' + can.toDataURL('image/png') + ') left top repeat'
    const el = appendEl
    el && el.appendChild(div)
    return id
  }
  // 设置水印
  const setWatermark = (str: string) => {
    createWatermark(str)
    func = () => { createWatermark(str) }
    window.addEventListener('resize', func)
  }
  // 导出方法：设置水印、清空水印
  return {setWatermark, clear}
}
