import defaultSettings from '@/settings'
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys';

const {
  title,
  showSettings,
  tagsView,
  fixedHeader,
  sidebarLogo,
  layout,
  theme
} = localStorage.getItem(storageKeys.themeSetting) ? JSON.parse(localStorage.getItem(storageKeys.themeSetting)) : defaultSettings

const state = {
  theme: theme,
  title: title,
  showSettings: showSettings,
  tagsView: tagsView,
  fixedHeader: fixedHeader,
  sidebarLogo: sidebarLogo,
  layout: layout
}

const mutations = {
  CHANGE_SETTING: (state, {key, value}) => {
    if (defaultSettings.showSettings) {
      // 是否禁用主题设置，未禁用则改变配置并保存
      if (state.hasOwnProperty(key)) {
        state[key] = value
        // settings的cookie永远有效
        localStorage.setItem(storageKeys.themeSetting, JSON.stringify(state))
        // 保存用户参数到数据库
        const data = {themeJson: JSON.stringify(state)}
        request({
          url: '/userTheme/zUserTheme/updateByUser', method: 'post', data, headers: {skipRepeatSubmitCheck: true}
        }).then((response) => {
          console.log('保存用户配置成功！')
        })
      }
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

