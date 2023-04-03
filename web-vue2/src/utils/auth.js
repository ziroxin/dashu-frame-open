import Cookies from 'js-cookie'

// token信息
const TokenKey = 'Admin-Token'
// token过期时间
const TokenValidTimeKey = 'Admin-Token-Valid-Time'
// 用户关闭浏览器保持登录状态：默认关闭 false;开启 true
// （若开启，则用户登录状态在：token到期后，或者用户手动清理cookie，才失效）
const IsTokenRemeber = false

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token, validTime) {
  if (IsTokenRemeber) {
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
  if (IsTokenRemeber) {
    return Cookies.set(TokenValidTimeKey, validTime, {expires: validTime})
  }
  return Cookies.set(TokenValidTimeKey, validTime)
}
