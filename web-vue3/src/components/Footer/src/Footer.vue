<template>
  <div :class="prefixCls"
       class="text-center text-[var(--el-text-color-placeholder)] bg-[var(--app-content-bg-color)] h-[var(--app-footer-height)] leading-[var(--app-footer-height)] dark:bg-[var(--el-bg-color)] overflow-hidden">
    Copyright ©2022-2032 {{ title }}
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('footer')
const title = computed(() => useAppStore().getTitle)
</script>

