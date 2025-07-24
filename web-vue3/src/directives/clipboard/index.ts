/**
 * 复制功能指令（已注册全局），在任意html元素上可使用v-clipboard指令，实现复制功能
 * 使用方法：
 *    <base-button type="button"
 *          v-clipboard:copy="'待复制的内容'" <!--必填(copy/cut二选一)，复制内容-->
 *          v-clipboard:cut="'待剪切的内容'" <!--必填(copy/cut二选一)，剪切内容-->
 *          v-clipboard:success="onCopy"<!--非必填，复制成功后的事件，不绑定则使用默认事件-->
 *          v-clipboard:error="onError"<!--非必填，复制失败后的事件，不绑定则使用默认事件-->
 *          >点击赋值</base-button>
 *
 * 参考源码仓库：https://github.com/Inndy/vue-clipboard2
 */
import { ElMessage } from 'element-plus'
import VueClipboard from 'clipboard'
// 检测是否安装了clipboard组件
if (!VueClipboard) {
  throw new Error('没有clipboard组件，请先安装：pnpm install `clipboard`')
}

export default {
  mounted(el, binding) {
    // 初始化复制成功/失败事件
    el._v_clipboard_success = () => {ElMessage({message: '复制成功！', type: 'success'})}
    el._v_clipboard_error = () => {ElMessage({message: '复制失败！', type: 'error'})}
    // 若用户传入事件，则覆盖默认事件
    if (binding.arg === 'success') {
      el._v_clipboard_success = binding.value
    } else if (binding.arg === 'error') {
      el._v_clipboard_error = binding.value
    } else {
      // 初始化剪切板
      const clipboard = new VueClipboard(el, {
        text: () => binding.value,
        action: () => binding.arg === 'cut' ? 'cut' : 'copy'
      })
      // 绑定复制成功事件
      clipboard.on('success', e => {
        el._v_clipboard_success && el._v_clipboard_success(e)
      })
      // 绑定复制失败事件
      clipboard.on('error', e => {
        el._v_clipboard_error && el._v_clipboard_error(e)
      })
      // 绑定剪切板对象
      el._v_clipboard = clipboard
    }
  },
  beforeUnmount(el, binding) {
    if (el._v_clipboard) {
      el._v_clipboard.destroy()
      delete el._v_clipboard_success
    }
    if (el._v_clipboard_success) delete el._v_clipboard_success
    if (el._v_clipboard_error) delete el._v_clipboard_error
  }
}
