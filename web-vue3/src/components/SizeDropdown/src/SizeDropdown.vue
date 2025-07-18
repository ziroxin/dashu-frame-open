<template>
  <el-dropdown :class="prefixCls" trigger="click" @command="setCurrentSize">
    <div class="flex items-center h-100%">
      <my-icon :size="18" icon="vi-mdi:format-size" :color="color" class="cursor-pointer"/>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-for="item in sizeMap" :key="item" :command="item">
          {{ t(`size.${item}`) }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { useI18n } from '@/hooks/web/useI18n'
import { propTypes } from '@/utils/propTypes'
import { useDesign } from '@/hooks/web/useDesign'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('size-dropdown')

defineProps({color: propTypes.string.def('')})

const appStore = useAppStore()
const sizeMap = computed(() => appStore.sizeMap)
const setCurrentSize = (size) => { appStore.setCurrentSize(size) }
</script>
