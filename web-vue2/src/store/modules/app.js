import storageKeys from '@/utils/storage-keys';

const state = {
  sidebar: {
    opened: sessionStorage.getItem(storageKeys.sidebarStatus) ? !!+sessionStorage.getItem(storageKeys.sidebarStatus) : true,
    withoutAnimation: false
  },
  device: 'desktop',
  size: sessionStorage.getItem(storageKeys.size) || 'medium'
}
console.log(state, 999)

const mutations = {
  TOGGLE_SIDEBAR: state => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
    if (state.sidebar.opened) {
      sessionStorage.setItem(storageKeys.sidebarStatus, 1)
    } else {
      sessionStorage.setItem(storageKeys.sidebarStatus, 0)
    }
  },
  CLOSE_SIDEBAR: (state, withoutAnimation) => {
    sessionStorage.setItem(storageKeys.sidebarStatus, 0)
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  },
  SET_SIZE: (state, size) => {
    state.size = size
    sessionStorage.setItem(storageKeys.size, size)
  }
}

const actions = {
  toggleSideBar({commit}) {
    commit('TOGGLE_SIDEBAR')
  },
  closeSideBar({commit}, {withoutAnimation}) {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice({commit}, device) {
    commit('TOGGLE_DEVICE', device)
  },
  setSize({commit}, size) {
    commit('SET_SIZE', size)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
