import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
import store from '@/store'
import {getToken, getTokenRefreshInterval, getTokenValidTime} from '@/utils/auth'
import {isWhiteList} from '@/router/white-list'

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
        // 给每个请求头，加上TOKEN：UserJwtToken
        config.headers['UserJwtToken'] = hasToken
      }
    }
    // 2、防止鼠标连击造成的数据重复提交
    if (config.method === 'post' || config.method === 'put' || config.method === 'delete') {
      requestCheckRepeatSubmit(config)
    }
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
      const o1 = JSON.parse(sessionStorage.getItem('oldReqObj'))
      // 2.同一请求，且间隔时间小于 repeatSubmitTime 则视为重复提交
      if (o1.data === reqObj.data && o1.url === reqObj.url && (reqObj.time - o1.time) < repeatSubmitTime) {
        return Promise.reject(new Error('数据正在处理，请勿重复提交'))
      }
    } catch (e) {
    }
    // 保存本次请求对象到sessionStorage
    sessionStorage.setItem('oldReqObj', JSON.stringify(reqObj))
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
      if (res.code === '40001' || res.code === '40002' || res.code === '40003' || res.code === '40004' || res.code === '401') {
        // 自定义异常1：未正常登录！40001=用户名或者密码错误;40002=无效的TOKEN;40003=用户未登录;40004=用户已禁用;401=无权限;
        MessageBox.confirm(res.message, '登录错误',
          {confirmButtonText: '刷新', cancelButtonText: '取消', type: 'error', center: true}
        ).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      } else {
        let errMsg, logErr;
        if (res.code === '500') {
          // 自定义异常2：服务器端异常（一般是bug）
          errMsg = res.message || '服务器端出错';
          logErr = '服务端出错';
        } else if (res.code === '400' || res.code === '403' || res.code === '405') {
          // 自定义异常3：客户端异常（一般是bug）
          errMsg = res.message || '客户端出错';
          logErr = '客户端出错';
        } else if (res.code === '429') {
          // 请求过于频繁或恶意攻击
          errMsg = res.message || '请求过于频繁';
          logErr = '请求过于频繁，疑似恶意请求';
        } else {
          // 未知异常
          errMsg = res.message || '未知异常';
          logErr = '未知异常';
        }
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
