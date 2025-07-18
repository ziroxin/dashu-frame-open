<template>
  <!-- 顶部工具栏 -->
  <div :id="`${variables.namespace}-tool-header`"
       :class="[prefixCls,'h-[var(--top-tool-height)] relative px-[var(--top-tool-p-x)] flex items-center justify-between']">
    <!-- 左侧按钮 -->
    <div v-if="layout !== 'top'" class="h-full flex items-center">
      <!-- 展开/收起菜单按钮 -->
      <div v-if="hamburger&&layout!=='cutMenu'" :class="prefixClsCollapse" class="top-tool-hover" @click="toggleCollapse">
        <my-icon :size="18" color="var(--top-header-text-color)"
                 class="cursor-pointer"
                 :icon="collapse?'vi-ant-design:menu-unfold-outlined':'vi-ant-design:menu-fold-outlined'"/>
      </div>
      <!-- 面包屑 -->
      <Breadcrumb v-if="breadcrumb" class="<md:hidden"/>
    </div>
    <!-- 右侧按钮 -->
    <div class="h-full flex items-center">
      <!-- 全屏按钮 -->
      <div v-if="screenFull" :class="prefixClsScreenfull" class="top-tool-hover" @click="toggleFullscreen">
        <my-icon :size="18" color="var(--top-header-text-color)"
                 :icon="isFullscreen?'vi-zmdi:fullscreen-exit':'vi-zmdi:fullscreen'"/>
      </div>
      <!-- 字体大小切换按钮 -->
      <size-dropdown v-if="size" class="top-tool-hover" color="var(--top-header-text-color)"/>
      <!-- 语言切换按钮 -->
      <locale-dropdown v-if="locale" class="top-tool-hover" color="var(--top-header-text-color)"/>
      <!-- 用户信息按钮 -->
      <user-info/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { LocaleDropdown } from '@/components/LocaleDropdown'
import { SizeDropdown } from '@/components/SizeDropdown'
import { UserInfo } from '@/components/UserInfo'
import { Breadcrumb } from '@/components/Breadcrumb'
import { useFullscreen } from '@vueuse/core'
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const {getPrefixCls, variables} = useDesign()
const prefixCls = getPrefixCls('tool-header')
const prefixClsCollapse = getPrefixCls('collapse')
const prefixClsScreenfull = getPrefixCls('screenfull')

const appStore = useAppStore()
const breadcrumb = computed(() => appStore.getBreadcrumb)
const hamburger = computed(() => appStore.getHamburger)
const screenFull = computed(() => appStore.getScreenfull)
const size = computed(() => appStore.getSize)
const layout = computed(() => appStore.getLayout)
const locale = computed(() => appStore.getLocale)

// 菜单展开/收起
const collapse = computed(() => appStore.getCollapse)
const toggleCollapse = () => { appStore.setCollapse(!unref(collapse)) }
// 全屏/退出全屏
const {toggle, isFullscreen} = useFullscreen()
const toggleFullscreen = () => { toggle() }
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-tool-header';
.@{prefix-cls} {
  transition: left var(--transition-time-02);
}
</style>
