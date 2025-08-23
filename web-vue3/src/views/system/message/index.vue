<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-change="handleClick">
      <el-tab-pane :label="'全部(' + count + ')'" name="first">
        <message-list :msg-status="msgStatus" @refresh="refreshCounts" :key="'first'+keyIndex"/>
      </el-tab-pane>
      <el-tab-pane :label="'未读(' + unreadCount + ')'" name="second">
        <message-list :msg-status="msgStatus" @refresh="refreshCounts" :key="'second'+keyIndex"/>
      </el-tab-pane>
      <el-tab-pane :label="'已读(' + (count-unreadCount) + ')'" name="third">
        <message-list :msg-status="msgStatus" @refresh="refreshCounts" :key="'third'+keyIndex"/>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup lang="ts">
import MessageList from '@/views/system/message/message-list'
import { useMessageStore } from '@/store/modules/message'

const messageStore = useMessageStore()
// 当前tab
const activeName = ref('first')
// 消息状态（0:未读 1:已读 '':全部）
const msgStatus = ref('')
const keyIndex = ref(0)
// 消息总数
const count = computed(() => messageStore.getCount)
// 未读消息数
const unreadCount = computed(() => messageStore.getUnreadCount)

// 点击tab切换消息状态
const handleClick = (tabName) => {
  msgStatus.value = tabName === 'third' ? '1' : tabName === 'second' ? '0' : ''
  keyIndex.value++
}
// 刷新消息数量
const refreshCounts = () => {
  messageStore.refreshAllMessageCount()
}
</script>