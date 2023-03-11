import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
import store from '@/store'
import {getToken, getTokenValidTime} from '@/utils/auth'
import qs from 'qs'
import {isWhiteList} from '@/utils/white-list'

// 创建axios
const service = axios.create({
  // 设置baseURL（url = base url + request url）
  baseURL: process.env.VUE_APP_BASE_API,
  // 跨域请求时，携带cookie
  // withCredentials: true,
  // 请求超时时间：20s
  timeout: 20000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 发送请求前操作
    if (store.getters.token) {
      const hasToken = getToken()
      if (hasToken) {
        if (config.url !== '/login/refresh/token' && !isWhiteList(config.url)) {
          // 判断token的有效期
          let tokenValidTime = getTokenValidTime();
          if (new Date().getTime() < new Date(tokenValidTime).getTime() &&
            (new Date().getTime() + (10 * 60 * 1000)) > new Date(tokenValidTime).getTime()) {
            // token失效前10分钟，刷新token
            store.dispatch('user/refreshToken')
          }
        }
        // 给每个请求头，加上TOKEN：UserJwtToken
        config.headers['UserJwtToken'] = hasToken
      }
    }
    if (config.method === 'get') {
      // 若是是get请求，且params是数组类型如arr=[1,2]，则转换成arr=1&arr=2
      config.paramsSerializer = function (params) {
        return qs.stringify(params, {arrayFormat: 'repeat'})
      }
    }
    return config
  },
  error => {
    // 请求出错时操作
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  // 如果想获取完整http响应信息（如headers,status），可以直接返回response
  response => {
    const res = response.data
    // 成功
    if (res.code === '200') {
      return res
    } else {
      // 异常1：未正常登录！ 40001=用户名或者密码错误;40002=无效的TOKEN;40003=用户未登录;40004=用户已禁用;401=无权限;
      if (res.code === '40001' || res.code === '40002' || res.code === '40003' || res.code === '40004' || res.code === '401') {
        MessageBox.confirm(res.message, '登录失败', {
          confirmButtonText: '刷新',
          cancelButtonText: '取消',
          type: 'error',
          center: true
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      // 异常2：服务器端异常
      if (res.code === '500') {
        console.log('服务端出错(' + res.code + ')：' + res.message);
        Message({message: res.message, type: 'error', duration: 3 * 1000})
        return Promise.reject(new Error(res.message || 'Error'))
      }
      // 异常3：客户端异常
      if (res.code === '400' || res.code === '403' || res.code === '405') {
        console.log('客户端出错(' + res.code + ')：' + res.message);
        Message({message: res.message, type: 'error', duration: 3 * 1000})
      }
    }
  }, error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
