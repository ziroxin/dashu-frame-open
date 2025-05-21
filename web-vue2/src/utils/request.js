import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
import store from '@/store'
import {getToken, getTokenRefreshInterval, getTokenValidTime} from '@/utils/auth'
import {isWhiteList} from '@/router/white-list'
import errorCode, {notLoginError} from '@/utils/error-code'
import {encryptRSA, isEncrypt} from "@/utils/jsencrypt-util";
import storageKeys from '@/utils/storage-keys';

// 创建axios
const service = axios.create({
  // 设置baseURL（url = base url + request url）
  baseURL: process.env.VUE_APP_BASE_API,
  // 跨域请求时，携带cookie
  // withCredentials: true,
  // 请求超时时间：120s
  timeout: 120000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 发送请求前，统一拦截操作
    // 1、处理TOKEN：（请求头添加token；请求前定时刷新token）
    if (store.getters.token) {
      const hasToken = getToken()
      if (hasToken) {
        if (config.url !== '/login/refresh/token' && !isWhiteList(config.url)) {
          // 判断token的有效期
          let tokenValidTime = getTokenValidTime();
          if (new Date().getTime() < new Date(tokenValidTime).getTime() &&
            (new Date().getTime() + getTokenRefreshInterval()) > new Date(tokenValidTime).getTime()) {
            // token失效前60分钟，刷新token
            store.dispatch('user/refreshToken')
          }
        }
        // 给每个请求头，加上TOKEN：UserJwtToken（key和后台api相对应，不要修改）
        config.headers['UserJwtToken'] = hasToken
      }
    }
    // 2、防止鼠标连击造成的数据重复提交
    const method = config.method.toLowerCase()
    if (method === 'post' || method === 'put' || method === 'delete') {
      requestCheckRepeatSubmit(config)
    }
    // 3、请求参数加密传输（data和params）
    requestCheckEncrypt(config)
    return config
  },
  error => {
    // 请求出错时操作
    console.log(error)
    return Promise.reject(error)
  }
)

// 防止数据重复提交处理
function requestCheckRepeatSubmit(config) {
  // 如果请求头中，带有 skipRepeatSubmitCheck = true，则跳过
  const skipRepeatSubmitCheck = (config.headers || {}).skipRepeatSubmitCheck
  if (!skipRepeatSubmitCheck) {
    // 间隔时间(ms)，小于此时间视为重复提交
    const repeatSubmitTime = 1000
    // 本次请求对象
    const reqObj = {
      url: config.url,
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      time: new Date().getTime()
    }
    // 处理重复提交逻辑
    try {
      // 1.尝试从sessionStorage中取出上一次请求对象
      const o1 = JSON.parse(sessionStorage.getItem(storageKeys.oldReqObj))
      // 2.同一请求，且间隔时间小于 repeatSubmitTime 则视为重复提交
      if (o1.data === reqObj.data && o1.url === reqObj.url && (reqObj.time - o1.time) < repeatSubmitTime) {
        return Promise.reject(new Error('数据正在处理，请勿重复提交'))
      }
    } catch (e) {
    }
    // 保存本次请求对象到sessionStorage
    sessionStorage.setItem(storageKeys.oldReqObj, JSON.stringify(reqObj))
  }
}

// 请求参数加密处理
function requestCheckEncrypt(config) {
  let isFormData = config.headers.getContentType() && config.headers.getContentType().toLowerCase().indexOf('multipart/form-data') >= 0
  if (isFormData) {
    return // form-data请求不加密，直接返回
  }
  if (!isEncrypt(config.url.split('?')[0])) {
    return // 全局配置不加密，或不加密白名单接口，直接返回
  }
  try {
    // 加密body中的data参数
    if (config.data) {
      if (Array.isArray(config.data)) {
        config.data = {encryptDataArray: encryptRSA(encodeURIComponent(JSON.stringify(config.data)))}
      } else {
        Object.keys(config.data).forEach(key => {
          const val = config.data[key]
          const type = Object.prototype.toString.call(val)
          if (val !== null && val !== undefined && val !== 'null' && val !== 'undefined' && val !== ''
            && type !== '[object File]' && type !== '[object Blob]') {
            // 通过前缀区分是字符串还是对象，用于后台解密时解析正确的数据格式
            config.data[key] = typeof val === 'object'
              ? encryptRSA('object_' + encodeURIComponent(JSON.stringify(val)))
              : encryptRSA('string_' + encodeURIComponent(String(val)))
          }
        })
      }
    }
    // 处理params，提取url中的参数
    if (config.url.indexOf('?') > -1) {
      config.params = config.params || {}
      let paramsArr = config.url.split('?')[1].split('&')
      paramsArr.forEach(item => {
        config.params[item.split('=')[0]] = item.split('=')[1]
      })
      config.url = config.url.split('?')[0]
    }
    // 加密params参数
    if (config.params) {
      Object.keys(config.params).forEach(key => {
        const val = config.params[key]
        if (val !== null && val !== undefined && val !== 'null' && val !== 'undefined' && val !== '') {
          config.params[key] = typeof val === 'object' ?
            encryptRSA(encodeURIComponent(JSON.stringify(val))) : encryptRSA(encodeURIComponent(String(val)))
        }
      })
    }
    // 配置请求加密标识（后台根据此标识 {isRequestEncrypt:true/false} 判断是否解密）
    if (config.data || config.params) {
      config.headers['isRequestEncrypt'] = 'true'
    }
  } catch (e) {
    console.log('加密失败:', e)
  }
}

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 1、如果响应是二进制数据,则直接返回
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      return response.data
    }
    // 2、否则,根据状态码判断
    const res = response.data
    if (res.code === '200') {
      // 正常
      return res
    } else {
      if (notLoginError(res.code)) {
        // 自定义异常1：未正常登录！40001=用户名或者密码错误;40002=无效的TOKEN;40003=用户未登录;40004=用户已禁用;401=无权限;
        MessageBox.confirm(res.message, '登录错误',
          {confirmButtonText: '刷新', cancelButtonText: '取消', type: 'error', center: true}
        ).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      } else {
        let logErr = errorCode[res.code] || errorCode['default']
        let errMsg = res.message || logErr
        console.log(logErr + '(' + res.code + ')：' + errMsg);
        Message({message: errMsg, type: 'error', duration: 3 * 1000})
        return Promise.reject(new Error(errMsg))
      }
    }
  }, error => {
    // 请求出错时操作
    console.log('err' + error)
    Message({message: error.message, type: 'error', duration: 5 * 1000})
    return Promise.reject(error)
  }
)

export default service
