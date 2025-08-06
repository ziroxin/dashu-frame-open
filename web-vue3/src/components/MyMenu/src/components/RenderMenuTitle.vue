<template>
  <my-icon v-if="meta?.icon" :icon="meta.icon"/>
  <span class="overflow-hidden overflow-ellipsis whitespace-nowrap">{{ t(meta?.title) || '标题未配置' }}</span>
  <span v-if="badge&&badge>0" class="menu-badge">{{ badge }}</span>
</template>

<script setup>
import { useI18n } from '@/hooks/web/useI18n'
import { useMessageStoreWithOut } from '@/store/modules/message'

const {t} = useI18n()
const {meta, name} = defineProps({
  meta: {type: Object, required: true},
  name: {type: String, required: true}
})
const badge = computed(() => useMessageStoreWithOut().getMenuUnreadCount(name))
</script>

<style lang="less" scoped>
.menu-badge {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 5%;
  background: #f56c6c;
  color: #fff;
  border-radius: 100%;
  width: 18px;
  height: 18px;
  line-height: 18px;
  font-size: 12px;
  text-align: center;
}
</style>
