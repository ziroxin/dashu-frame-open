import {getInfo, login, logout, refreshToken} from '@/api/user'
import {getToken, getTokenHeader, removeToken, setToken, setTokenValidTime} from '@/utils/auth'
import {resetRouter} from '@/router'
import storageKeys from '@/utils/storage-keys';

const state = {
  token: getToken(),
  headerToken: getTokenHeader(),
  name: '',
  avatar: '',
  introduction: '',
  perRouters: [],
  permissions: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
    state.headerToken = getTokenHeader()
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_PERROUTERS: (state, perRouters) => {
    state.perRouters = perRouters
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  }
}

const actions = {
  // 登录
  login({commit}, userInfo) {
    return new Promise((resolve, reject) => {
      login(userInfo).then(response => {
        const {data} = response
        setToken(data.accessToken, new Date(data.accessTokenValidTime))
        setTokenValidTime(new Date(data.accessTokenValidTime))
        commit('SET_TOKEN', data.accessToken)
        // 是默认密码
        sessionStorage.setItem(storageKeys.isDefaultPassword, data.defaultPassword)
        sessionStorage.setItem(storageKeys.isInvalidPassword, data.invalidPassword)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  // 加载用户信息
  getInfo({commit, state}) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        const {data} = response
        if (!data) {
          reject('验证失败，请重新登录！')
        }
        const {permissions, perRouters} = data
        const {nickName, avatar, introduce} = data.user

        commit('SET_PERMISSIONS', permissions)
        commit('SET_PERROUTERS', perRouters)
        commit('SET_NAME', nickName)
        commit('SET_AVATAR', avatar)
        commit('SET_INTRODUCTION', introduce)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  // 退出登录
  logout({commit, state, dispatch}) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        removeToken()
        commit('SET_TOKEN', '')
        commit('SET_PERROUTERS', [])
        commit('SET_PERMISSIONS', [])
        resetRouter()

        // 重置标签缓存
        dispatch('tagsView/delAllViews', null, {root: true})

        // 清空导航缓存
        sessionStorage.clear()

        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  // 清除Token
  resetToken({commit}) {
    return new Promise(resolve => {
      removeToken()
      commit('SET_TOKEN', '')
      commit('SET_PERROUTERS', [])
      commit('SET_PERMISSIONS', [])
      resolve()
    })
  },
  // 刷新Token
  refreshToken({commit}) {
    return new Promise(resolve => {
      refreshToken().then(response => {
        const {data} = response
        setToken(data.accessToken, new Date(data.accessTokenValidTime))
        setTokenValidTime(new Date(data.accessTokenValidTime))
        commit('SET_TOKEN', data.accessToken)
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
