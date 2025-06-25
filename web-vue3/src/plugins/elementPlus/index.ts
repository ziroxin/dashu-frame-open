import type { App } from 'vue'

// 全部引入element-plus
import ElementPlus from 'element-plus'

// 常用组件全局引入（如ElScrollbar，不然一些下拉项样式有问题），其他按需引入
import { ElLoading, ElScrollbar } from 'element-plus'

const plugins = [ElLoading]
const components = [ElScrollbar]

/**
 * 配置 [全部引入 / 按需引入] element-plus
 * @param app Vue实例
 * @param type ='all'时，表示全部引入；否则按需引入
 */
export const setupElementPlus = (app: App<Element>, type: string) => {
  // 首先引入样式：为了开发环境启动更快，一次性引入所有样式（生产环境按需引入，详见vite.config.ts）
  if (import.meta.env.VITE_USE_ALL_ELEMENT_PLUS_STYLE === 'true') {
    import('element-plus/dist/index.css')
  }
  if (type === 'all') {
    // 方式一：全局引入所有组件
    app.use(ElementPlus)
    return
  } else {
    // 方式二：按需引入组件（这里只引入部分必须的组件，其他组件在页面内按需引入）
    plugins.forEach((plugin) => {
      app.use(plugin)
    })
    components.forEach((component) => {
      app.component(component.name!, component)
    })
  }
}
