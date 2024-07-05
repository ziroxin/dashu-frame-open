import {getMessageCount} from '@/api/message'

const state = {
  count: 0,// 消息总数
  unreadCount: 0,// 未读消息数
  permissionUnreadJson: {},// 模块未读消息数（json格式）
}

const mutations = {
  REFRESH_MESSAGE_COUNT(state, data) {
    state.count = data.count || 0
    state.unreadCount = data.unreadCount || 0
    state.permissionUnreadJson = JSON.parse(data.permissionUnreadJson) || {}
  }
}

const actions = {
  refreshMessageCount({commit}) {
    getMessageCount().then(response => {
      commit('REFRESH_MESSAGE_COUNT', response.data)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}