/**
 * 复制功能指令（已注册全局）
 * 使用方法：
 *    <button type="button"
 *        v-clipboard:copy="'待复制的内容'"<!--必填，支持data的变量-->
 *        v-clipboard:success="onCopy"<!--非必填，复制成功后的事件，不绑定则使用默认的-->
 *        v-clipboard:error="onError"<!--非必填，复制失败后的事件，不绑定则使用默认的-->
 *        >Copy!</button>
 *
 * 参考源码仓库：https://github.com/Inndy/vue-clipboard2
 */
const Clipboard = require('clipboard')
if (!Clipboard) {
  throw new Error('没有clipboard组件，请先安装：npm install `clipboard`')
}
import Vue from 'vue'

export default {
  bind(el, binding) {
    // 绑定默认复制成功事件
    el._v_clipboard_success = clipboardSuccess
    // 绑定默认复制失败事件
    el._v_clipboard_error = clipboardError
    // 支持自定义
    if (binding.arg === 'success') {
      el._v_clipboard_success = binding.value
    } else if (binding.arg === 'error') {
      el._v_clipboard_error = binding.value
    } else {
      // 复制功能
      const clipboard = new Clipboard(el, {
        text() {
          return binding.value
        },
        action() {
          return binding.arg === 'cut' ? 'cut' : 'copy'
        }
      })
      clipboard.on('success', e => {
        const callback = el._v_clipboard_success
        callback && callback(e) // eslint-disable-line
      })
      clipboard.on('error', e => {
        const callback = el._v_clipboard_error
        callback && callback(e) // eslint-disable-line
      })
      el._v_clipboard = clipboard
    }
  },
  update(el, binding) {
    if (binding.arg === 'success') {
      el._v_clipboard_success = binding.value
    } else if (binding.arg === 'error') {
      el._v_clipboard_error = binding.value
    } else {
      el._v_clipboard.text = function () {
        return binding.value
      }
      el._v_clipboard.action = function () {
        return binding.arg === 'cut' ? 'cut' : 'copy'
      }
    }
  },
  unbind(el, binding) {
    if (binding.arg === 'success') {
      delete el._v_clipboard_success
    } else if (binding.arg === 'error') {
      delete el._v_clipboard_error
    } else {
      el._v_clipboard.destroy()
      delete el._v_clipboard
    }
  }
}

function clipboardSuccess() {
  Vue.prototype.$message({message: '复制成功！', type: 'success', duration: 1500})
}

function clipboardError() {
  Vue.prototype.$message({message: '复制失败', type: 'error'})
}
