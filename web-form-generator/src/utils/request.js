import axios from 'axios'
import qs from 'qs'
import {getToken} from "@/utils/auth";

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

    // 给每个请求头，加上TOKEN：z_jwt_token
    config.headers['z_jwt_token'] = getToken()

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
    return res
  }, error => {
    console.log('err' + error) // for debug
    return Promise.reject(error)
  }
)

export default service
