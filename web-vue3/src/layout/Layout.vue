<template>
  <section :class="[prefixCls,`${prefixCls}__${layoutType}`,'w-[100%] h-[100%] relative']">
    <div v-if="mobile&&!collapse" @click="handleClickOutside"
         class="absolute top-0 left-0 w-full h-full opacity-30 z-99 bg-[var(--el-color-black)]"></div>
    <render-layout :layout-type="layoutType"/>
    <backtop/>
    <setting v-if="!hideSetting"/>
  </section>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { Backtop } from '@/components/Backtop'
import { Setting } from '@/components/Setting'
import RenderLayout from './components/RenderLayout.vue'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('layout')
const appStore = useAppStore()

// 是否是移动端
const mobile = computed(() => appStore.getMobile)
// 是否隐藏设置选项
const hideSetting = computed(() => import.meta.env.VITE_HIDE_GLOBAL_SETTING === 'true')
// 布局类型
const layoutType = computed(() => appStore.getLayout)

// 菜单折叠
const collapse = computed(() => appStore.getCollapse)
const handleClickOutside = () => { appStore.setCollapse(true) }
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-layout';
.@{prefix-cls} {
  background-color: var(--app-content-bg-color);
}
</style>
