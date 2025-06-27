import Cookies from 'js-cookie'
import storageKeys from '@/utils/storage-keys'

// token到期前多久，刷新token（默认：60分钟；每次请求api前检测request.js中）
// 注意：该时间必须小于后台配置的jwtToken有效期！！！
const TokenRefreshInterval = 60 * 60 * 1000
// 用户关闭浏览器保持登录状态：默认关闭 false;开启 true
// （若开启，则用户登录状态在：token到期后，或者用户手动清理cookie，才失效）
const IsTokenRemember = false

export function getToken() {
  return Cookies.get(storageKeys.c_token)
}

export function setToken(token, validTime) {
  if (IsTokenRemember) {
    if (validTime) {
      return Cookies.set(storageKeys.c_token, token, {expires: validTime})
    }
  }
  return Cookies.set(storageKeys.c_token, token)
}

export function getTokenHeader() {
  return {UserJwtToken: Cookies.get(storageKeys.c_token)}
}

export function removeToken() {
  return Cookies.remove(storageKeys.c_token)
}

export function getTokenValidTime() {
  return Cookies.get(storageKeys.c_tokenValidTime)
}

export function setTokenValidTime(validTime) {
  if (IsTokenRemember) {
    return Cookies.set(storageKeys.c_tokenValidTime, validTime, {expires: validTime})
  }
  return Cookies.set(storageKeys.c_tokenValidTime, validTime)
}

export function getTokenRefreshInterval() {
  return TokenRefreshInterval
}
