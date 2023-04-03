import {getInfo, login, logout, refreshToken} from '@/api/user'
import {getToken, removeToken, setToken, setTokenValidTime} from '@/utils/auth'
import {resetRouter} from '@/router'
import Cookies from 'js-cookie';

const state = {
  token: getToken(),
  name: '',
  avatar: '',
  introduction: '',
  perrouters: [],
  permissions: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
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
  SET_PERROUTERS: (state, perrouters) => {
    state.perrouters = perrouters
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
        commit('SET_TOKEN', data.accessToken)
        setToken(data.accessToken, new Date(data.accessTokenValidTime))
        setTokenValidTime(new Date(data.accessTokenValidTime))
        // 是默认密码
        Cookies.set('isDefaultPassword', data.defaultPassword)
        Cookies.set('isInvalidPassword', data.invalidPassword)
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
        const {permissions, perrouters} = data
        const {nickName, avatar, introduce} = data.user

        commit('SET_PERMISSIONS', permissions)
        commit('SET_PERROUTERS', perrouters)
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
        commit('SET_TOKEN', '')
        commit('SET_PERROUTERS', [])
        commit('SET_PERMISSIONS', [])
        removeToken()
        resetRouter()

        // 重置标签缓存
        dispatch('tagsView/delAllViews', null, {root: true})

        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  // 清除Token
  resetToken({commit}) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_PERROUTERS', [])
      commit('SET_PERMISSIONS', [])
      removeToken()
      resolve()
    })
  },
  // 刷新Token
  refreshToken({commit}) {
    return new Promise(resolve => {
      refreshToken().then(response => {
        const {data} = response
        commit('SET_TOKEN', data.accessToken)
        setToken(data.accessToken, new Date(data.accessTokenValidTime))
        setTokenValidTime(new Date(data.accessTokenValidTime))
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
