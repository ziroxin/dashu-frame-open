import { defineStore } from 'pinia'
import { store } from '../index'
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys'

export interface MessageState {
  count: number
  unreadCount: number
  permissionUnreadJson: object
}

export const useMessageStore = defineStore('message', {
  state: (): MessageState => ({
    // 消息总数
    count: 0,
    // 未读消息总数
    unreadCount: 0,
    // 模块维度消息树（json格式，用在菜单上显示未读消息）
    permissionUnreadJson: {}
  }),
  getters: {
    getCount(): number {return this.count},
    getUnreadCount(): number {return this.unreadCount}
  },
  actions: {
    setCount(count: number) { this.count = count },
    setUnreadCount(unreadCount: number) { this.unreadCount = unreadCount },
    setPermissionUnreadJson(permissionUnreadJson: object) { this.permissionUnreadJson = permissionUnreadJson },
    // 刷新消息数量
    refreshAllMessageCount() {
      request({url: '/message/zMessage/counts', method: 'get'}).then(response => {
        this.setCount(response.data.count || 0)
        this.setUnreadCount(response.data.unreadCount || 0)
        const unreadJson = JSON.parse(response.data.permissionUnreadJson) || {}
        this.setPermissionUnreadJson(unreadJson)
      })
    },
    getMenuUnreadCount(name: string): number {
      return Object.prototype.hasOwnProperty.call(this.permissionUnreadJson, name) ? this.permissionUnreadJson[name] : 0
    }
  },
  persist: {key: storageKeys.l_messageStore}
})

export const useMessageStoreWithOut = () => {
  return useMessageStore(store)
}