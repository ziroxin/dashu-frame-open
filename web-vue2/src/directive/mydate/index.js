/**
 * 自定义日期格式化指令（已注册全局）
 * 使用方法：
 *    span可以换成，除了表单之外的任意html标签
 *    <span v-mydate="需要格式化的日期值"></span>
 */

function formatDate(value) {
  try {
    let v = new Date(value)
    let now = new Date()
    let d = now.getTime() - v.getTime()
    if (d <= (1000 * 60 * 60 * 24)) {
      // 24小时内（今天 10:10; 昨天 23:23）
      if (now.getDay() === v.getDay()) {
        return '今天 ' + padLeft(v.getHours()) + ':' + padLeft(v.getMinutes());
      } else {
        return '昨天 ' + padLeft(v.getHours()) + ':' + padLeft(v.getMinutes());
      }
    } else {
      if (v.getFullYear() === now.getFullYear()) {
        // 本年度 (08/08 08:08)
        return padLeft(v.getMonth() + 1) + '/' + padLeft(v.getDate()) + ' ' + padLeft(v.getHours()) + ':' + padLeft(v.getMinutes());
      } else {
        // 正常格式 (2020/08/08 08:08)
        return padLeft(v.getFullYear()) + '/' + padLeft(v.getMonth() + 1) + '/' + padLeft(v.getDate()) + ' ' + padLeft(v.getHours()) + ':' + padLeft(v.getMinutes());
      }
    }
  } catch (e) {
  }
  return value
}

function padLeft(num) {
  // 单数前面加0
  if (parseInt(num) < 10) {
    num = '0' + num;
  }
  return num;
}

export default {
  inserted(el, binding) {
    el.innerHTML = formatDate(binding.value)
  },
  update(el, binding) {
    el.innerHTML = formatDate(binding.value)
  }
}
