import Cookies from 'js-cookie'

// token信息
const TokenKey = 'Admin-Token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

// token过期时间
const TokenValidTimeKey = 'Admin-Token-Valid-Time'

export function getTokenValidTime() {
  return Cookies.get(TokenValidTimeKey)
}

export function setTokenValidTime(validTime) {
  return Cookies.set(TokenValidTimeKey, validTime)
}
