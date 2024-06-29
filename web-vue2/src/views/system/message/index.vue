<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane :label="'全部(' + count + ')'" name="first">
        <message-list :msg-status="msgStatus"
                      @unread-count-change="unreadCount-=1"
                      @all-read="getCounts"
                      :key="'first'+keyIndex"></message-list>
      </el-tab-pane>
      <el-tab-pane :label="'未读(' + unreadCount + ')'" name="second">
        <message-list :msg-status="msgStatus"
                      @unread-count-change="unreadCount-=1"
                      @all-read="getCounts"
                      :key="'second'+keyIndex"></message-list>
      </el-tab-pane>
      <el-tab-pane :label="'已读(' + (count-unreadCount) + ')'" name="third">
        <message-list :msg-status="msgStatus"
                      @unread-count-change="unreadCount-=1"
                      :key="'third'+keyIndex"></message-list>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import MessageList from "@/views/system/message/message-list.vue";

export default {
  components: {MessageList},
  data() {
    return {
      activeName: 'first',
      msgStatus: '',// 0:未读 1:已读 '':全部
      keyIndex: 0,
      // 总数
      count: 0,
      unreadCount: 0,
    }
  },
  mounted() {
    this.getCounts()
  },
  methods: {
    handleClick(tab, event) {
      this.msgStatus = tab.name === 'third' ? '1' : tab.name === 'second' ? '0' : ''
      this.keyIndex++
    },
    // 获取消息总数和未读数
    getCounts() {
      this.$request({url: '/message/zMessage/counts', method: 'get'}).then((response) => {
        this.count = response.data.count
        this.unreadCount = response.data.unreadCount
      })
    }
  }
}
</script>