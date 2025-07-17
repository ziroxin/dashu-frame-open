import 'vue/jsx'

// 引入windi css
import '@/plugins/unocss'
// 导入全局的svg图标
import '@/plugins/svgIcon'
// 初始化多语言
import { setupI18n } from '@/plugins/vueI18n'
// 引入状态管理
import { setupStore } from '@/store'
// 引入全局自定义组件（常用自定义组件全局引入，无需在每个vue文件中单独引入）
import { setupGlobCom } from '@/components'
// 引入element-plus
import { setupElementPlus } from '@/plugins/elementPlus'
// 引入全局样式
import '@/styles/index.less'
// 引入动画
import '@/plugins/animate.css'
// 路由初始化
import { setupRouter } from './router'
// 注册全局指令（例如：v-permission）
import { setupDirectives } from './directives'
// 注册全局变量
import { setupGlobalProperties } from '@/utils/global-properties'
// 注册数据字典
import { setupDictList } from '@/utils/dict-utils'
// 引入App组件
import { createApp } from 'vue'
import App from './App.vue'
import './permission'

// 创建实例
const setupAll = async () => {
  const app = createApp(App)
  // 国际化
  await setupI18n(app)
  // 状态管理
  setupStore(app)
  // 全局组件
  setupGlobCom(app)
  // 引入element-plus（引入方式：type==='all'全部引入;否则按需引入）
  setupElementPlus(app, 'all')
  // 路由
  setupRouter(app)
  // 注册全局指令（例如：v-permission）
  setupDirectives(app)
  // 注册全局变量
  setupGlobalProperties(app)
  // 注册数据字典
  setupDictList()
  // 挂载
  app.mount('#app')
}

setupAll()
