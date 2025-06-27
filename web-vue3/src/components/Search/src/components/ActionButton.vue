<template>
  <el-button v-if="showSearch" type="primary" :loading="searchLoading" @click="onSearch">
    <my-icon icon="vi-ep:search"/>
    {{ t('common.query') }}
  </el-button>
  <el-button v-if="showReset" :loading="resetLoading" plain @click="onReset">
    <my-icon icon="vi-ep:refresh-right"/>
    {{ t('common.reset') }}
  </el-button>
  <el-button v-if="showExpand" text @click="onExpand">
    <my-icon :icon=" visible ? 'vi-ep:arrow-up' : 'vi-ep:arrow-down'"/>
    {{ t(visible ? 'common.shrink' : 'common.expand') }}
  </el-button>
</template>

<script setup lang="ts">
import { propTypes } from '@/utils/propTypes'
import { useI18n } from '@/hooks/web/useI18n'

const emit = defineEmits(['search', 'reset', 'expand'])

const {t} = useI18n()

defineProps({
  showSearch: propTypes.bool.def(true),
  showReset: propTypes.bool.def(true),
  showExpand: propTypes.bool.def(false),
  visible: propTypes.bool.def(true),
  searchLoading: propTypes.bool.def(false),
  resetLoading: propTypes.bool.def(false)
})

const onSearch = () => {
  emit('search')
}

const onReset = () => {
  emit('reset')
}

const onExpand = () => {
  emit('expand')
}
</script>
