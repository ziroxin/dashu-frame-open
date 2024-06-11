import Cookies from 'js-cookie'

// token信息
const TokenKey = 'Admin-Token'
// token过期时间
const TokenValidTimeKey = 'Admin-Token-Valid-Time'
// token到期前多久，刷新token（默认：10分钟；每次请求api前检测request.js中）
// 注意：该时间必须小于后台配置的jwtToken有效期！！！
const TokenRefreshInterval = 10 * 60 * 1000
// 用户关闭浏览器保持登录状态：默认关闭 false;开启 true
// （若开启，则用户登录状态在：token到期后，或者用户手动清理cookie，才失效）
const IsTokenRemember = false

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token, validTime) {
  if (IsTokenRemember) {
    if (validTime) {
      return Cookies.set(TokenKey, token, {expires: validTime})
    }
  }
  return Cookies.set(TokenKey, token)
}

export function getTokenHeader() {
  return {UserJwtToken: Cookies.get(TokenKey)}
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getTokenValidTime() {
  return Cookies.get(TokenValidTimeKey)
}

export function setTokenValidTime(validTime) {
  if (IsTokenRemember) {
    return Cookies.set(TokenValidTimeKey, validTime, {expires: validTime})
  }
  return Cookies.set(TokenValidTimeKey, validTime)
}

export function getTokenRefreshInterval() {
  return TokenRefreshInterval
}
