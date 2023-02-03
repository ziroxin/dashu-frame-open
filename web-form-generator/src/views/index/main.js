import Vue from 'vue'
import App from './App.vue'
import router from '@/router'
import '@/styles/index.scss'
import '@/icons'
import Tinymce from '@/components/tinymce/index.vue'
import ElKey from '@/components/elkey/index.vue'
import request from '@/utils/request'

Vue.component('tinymce', Tinymce)
Vue.component('el-key', ElKey)

Object.assign(Vue.prototype, {
  $baseServer: process.env.VUE_APP_BASE_API,
  $request: request
})

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
