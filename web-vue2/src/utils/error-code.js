/**
 * 统一的错误码及错误信息
 */
export default {
  '40001': '用户名或者密码错误！',
  '40002': '无效的TOKEN！',
  '40003': '用户未登录！',
  '40004': '用户已禁用！',
  '400': '客户端请求错误！',
  '401': '无权限访问该资源！Unauthorized',
  '403': '您没有权限访问该资源！Forbidden',
  '404': '访问的资源不存在！',
  '429': '请求过于频繁，请稍后再试！',
  '500': '服务器内部错误！',
  'default': '未知异常！'
}

/**
 * 判断是否是未登录错误
 * @param code {string} 错误码
 * @returns {boolean} 是否是未登录错误
 */
export function notLoginError(code) {
  if (code === '40001' || code === '40002' || code === '40003' || code === '40004' || code === '401') {
    return true;
  }
  return false;
}