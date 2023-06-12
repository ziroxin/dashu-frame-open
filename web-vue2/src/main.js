import Vue from 'vue'

import Cookies from 'js-cookie'
// CSS重置工具，纠正错误和常见的浏览器的不一致性
import 'normalize.css/normalize.css'

import Element from 'element-ui'
// 如果使用中文语言包请默认支持，无需额外引入，请删除该依赖
// import enLang from 'element-ui/lib/locale/lang/en'
// 全局样式
import '@/styles/index.scss'

import App from './App'
import store from './store'
import router from './router'

import './icons'
import './permission'
import './utils/error-log'

// 导入全局过滤器filters
import * as filters from './filters'
// 导入全局指令directive
import permission from '@/directive/permission'
import mydate from '@/directive/mydate'
import waves from '@/directive/waves/index'
import clipboard from '@/directive/clipboard'
// 导入全局变量
import request from '@/utils/request'
import {getTokenHeader} from '@/utils/auth'
// 初始化数据字典
import '@/utils/dict-install'
import {loadTheme} from "@/utils/loadTheme";

// 加载自定义主题
loadTheme(Cookies.get('settings') ? JSON.parse(Cookies.get('settings')).theme : '#4080FF')

Vue.use(Element, {
  // 设置element-ui默认大小
  size: Cookies.get('size') || 'medium'
  // 英文语言包，如果使用中文，无需设置，请删除
  //, locale: enLang
})

// 注册全局过滤器filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})
// 注册全局指令directive
Vue.directive('permission', permission)
Vue.directive('mydate', mydate)
Vue.directive('waves', waves)
Vue.directive('clipboard', clipboard)
// 注册全局变量
Object.assign(Vue.prototype, {
  $baseServer: process.env.VUE_APP_BASE_API,
  $windowHeight: window.innerHeight,
  $request: request,
  $headerToken: getTokenHeader()
});

// 开发时不提示生产环境
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
