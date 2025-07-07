<template>
  <div>
    <my-icon v-if="meta?.icon" :icon="meta.icon"/>
    <span class="v-menu__title overflow-hidden overflow-ellipsis whitespace-nowrap">
      {{ t(meta?.title) || '标题未配置' }}
    </span>
  </div>
</template>

<script setup>
import { useI18n } from '@/hooks/web/useI18n'

const {t} = useI18n()
const {meta} = defineProps({meta: {type: Object, required: true}})
</script>
