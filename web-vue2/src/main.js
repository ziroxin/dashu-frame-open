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
import resize from '@/directive/resize'
// 导入全局变量
import request from '@/utils/request'
// 初始化数据字典
import '@/utils/dict-install'
import {loadTheme} from "@/utils/load-theme";
import PATCH_ElOverlayAutoClose from '@/utils/el-overlay-auto-close';
import homeRouter from "@/router/homeRouter";

// 加载自定义主题
loadTheme(Cookies.get('settings') ? JSON.parse(Cookies.get('settings')).theme : '#4080FF')
// element-ui配置
Vue.use(Element, {
  // 默认大小
  size: Cookies.get('size') || 'medium'
  // 英文语言包，如果使用中文，无需设置，请删除
  //, locale: enLang
})
// 修复：el-dialog（开启点击遮罩关闭时）鼠标点击后，移出弹窗范围内，意外关闭的bug
Vue.use(PATCH_ElOverlayAutoClose);

// 注册全局过滤器filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})
// 注册全局指令directive
Vue.directive('permission', permission)
Vue.directive('mydate', mydate)
Vue.directive('waves', waves)
Vue.directive('clipboard', clipboard)
Vue.directive('resize', resize)

// window.innerHeight自适应（1是否显示多标签；2横向布局多出一行面包屑）
let windowHeight = window.innerHeight
windowHeight = store.getters.settings.tagsView ? windowHeight : windowHeight + 40
windowHeight = store.getters.settings.layout === 'topMenu' ? windowHeight - 50 : windowHeight
// 注册全局变量
Object.assign(Vue.prototype, {
  $baseServer: process.env.VUE_APP_BASE_API,
  $windowHeight: windowHeight,
  $request: request,
  $homeRouter: homeRouter
});

// 开发时不提示生产环境
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
