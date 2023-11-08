import defaultSettings from '@/settings'
import Cookies from 'js-cookie';
import request from '@/utils/request'

const {
  title,
  showSettings,
  tagsView,
  fixedHeader,
  sidebarLogo,
  layout,
  theme
} = Cookies.get('settings') ? JSON.parse(Cookies.get('settings')) : defaultSettings

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
    if (state.hasOwnProperty(key)) {
      state[key] = value
      // settings的cookie永远有效
      Cookies.set('settings', JSON.stringify(state), {expires: new Date('9999-12-31T23:59:59')})
      // 保存用户参数到数据库
      const data = {themeJson: JSON.stringify(state)}
      request({
        url: '/userTheme/zUserTheme/updateByUser', method: 'post', data
      }).then((response) => {
        console.log('保存用户配置成功！')
      })
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

