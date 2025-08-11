import type { App } from 'vue'

// 引入element-plus
import ElementPlus from 'element-plus'

/**
 * 引入 element-plus
 * @param app Vue实例
 */
export const setupElementPlus = (app: App<Element>) => {
  import('element-plus/dist/index.css')
  app.use(ElementPlus)
}
