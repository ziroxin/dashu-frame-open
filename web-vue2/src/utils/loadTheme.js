// 根据条件，加载不同的css文件
export function loadTheme(themeColor) {
  if (themeColor === '#F5222D') {
    // 枫叶
    return import('@/assets/theme-fengye/index.css')
  } else if (themeColor === '#FA541C') {
    // 暖阳
    return import('@/assets/theme-nuanyang/index.css')
  } else if (themeColor === '#FAAD14') {
    // 柠檬
    return import('@/assets/theme-ningmeng/index.css')
  } else if (themeColor === '#13C2C2') {
    // 霁青
    return import('@/assets/theme-jiqing/index.css')
  } else if (themeColor === '#52C41A') {
    // 森绿
    return import('@/assets/theme-senlv/index.css')
  } else if (themeColor === '#722ED1') {
    // 葡萄
    return import('@/assets/theme-putao/index.css')
  } else {
    // 默认
    return import('@/assets/theme-default/index.css')
  }
}
