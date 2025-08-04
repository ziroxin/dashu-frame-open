<template>
  <div :class="prefixCls" class="text-center text-12px color-#909399 h-[var(--app-footer-height)] overflow-hidden">
    Copyright ©2022-2032 {{ title }}
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('footer')
const title = computed(() => useAppStore().getTitle)
</script>

