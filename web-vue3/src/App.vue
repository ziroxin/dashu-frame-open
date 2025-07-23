<template>
  <config-global :size="currentSize">
    <router-view :class="greyMode ? `${prefixCls}-grey-mode` : ''"/>
  </config-global>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { ConfigGlobal } from '@/components/ConfigGlobal'
import { useDesign } from '@/hooks/web/useDesign'

// 灰度模式判断
const prefixCls = useDesign().getPrefixCls('app')
const appStore = useAppStore()
const greyMode = computed(() => appStore.getGreyMode)
// 初始化主题
appStore.initTheme()
</script>

<style lang="less">
@prefix-cls: ~'@{adminNamespace}-app';
.size {
  width: 100%;
  height: 100%;
}

html,
body {
  padding: 0 !important;
  margin: 0;
  overflow: hidden;
  .size;
  #app {
    .size;
  }
}

.@{prefix-cls}-grey-mode {
  filter: grayscale(100%);
}
</style>
