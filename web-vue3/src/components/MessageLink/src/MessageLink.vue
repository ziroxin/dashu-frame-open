<template>
  <router-link to="/system/message" :class="prefixCls"
               class="top-tool-hover color-[var(--el-text-color-regular)]">
    <el-badge :value="unreadCount" :hidden="!unreadCount||unreadCount<=0" class="mt-5px">
      <my-icon icon="el-icon-message" :size="20"/>
    </el-badge>
  </router-link>
</template>

<script setup lang="ts">
import { useDesign } from '@/hooks/web/useDesign'
import { useMessageStore } from '@/store/modules/message'

const prefixCls = useDesign().getPrefixCls('message-link')

const messageStore = useMessageStore()
// 刷新消息数量
messageStore.refreshAllMessageCount()
// 获取未读消息数量
const unreadCount = computed(() => messageStore.getUnreadCount)
</script>