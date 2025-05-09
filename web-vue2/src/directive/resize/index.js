/**
 * 监听DOM元素宽高变化的指令（已注册全局）
 * 使用方法：
 *        <div v-resize="handleResize"></div>
 *        监听方法：handleResize(newWidth, newHeight)
 */
export default {
  bind(el, binding) {
    // 创建一个 ResizeObserver 实例
    const resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        // 当元素大小发生变化时，调用绑定的函数并传递新的高度
        binding.value(entry.contentRect.width, entry.contentRect.height);
      }
    })
    // 将 ResizeObserver 实例绑定到元素上，以便之后可以断开连接
    el.resizeObserver = resizeObserver;
    // 开始监听元素大小变化
    resizeObserver.observe(el);
  },
  unbind(el) {
    // 停止监听元素大小变化
    el.resizeObserver.disconnect();
  }
}
