import variables from '@/styles/element-variables.scss'
import defaultSettings from '@/settings'
import Cookies from "js-cookie";

const {title, showSettings, tagsView, fixedHeader, sidebarLogo} = Cookies.get('settings') ? JSON.parse(Cookies.get('settings')) : defaultSettings

const state = {
  theme: variables.theme,
  title: title,
  showSettings: showSettings,
  tagsView: tagsView,
  fixedHeader: fixedHeader,
  sidebarLogo: sidebarLogo
}

const mutations = {
  CHANGE_SETTING: (state, {key, value}) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
      Cookies.set('settings', JSON.stringify(state))
    }
  }
}

const actions = {
  // 修改设置参数
  changeSetting({commit}, data) {
    commit('CHANGE_SETTING', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

