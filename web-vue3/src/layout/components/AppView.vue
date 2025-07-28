<template>
  <section :id="`${variables.namespace}-app-container`"
           :class="['box-border w-full bg-[var(--app-content-bg-color)] dark:bg-[var(--el-bg-color)] pb-0']">
    <!-- 最小高度，根据不同条件设置 -->
    <router-view :class="'!min-h-['+contentHeight+'] :root{--app-content-height:'+contentHeight+'}'">
      <template #default="{Component,route}">
        <keep-alive :include="getCaches">
          <component :is="Component" :key="route.fullPath"/>
        </keep-alive>
      </template>
    </router-view>
  </section>
  <Footer v-if="footer"/>
</template>
<script setup lang="ts">
import { useTagsViewStore } from '@/store/modules/tagsView'
import { useAppStore } from '@/store/modules/app'
import { Footer } from '@/components/Footer'
import { useDesign } from '@/hooks/web/useDesign'

const {variables} = useDesign()
const appStore = useAppStore()
const tagsView = computed(() => appStore.getTagsView)
const footer = computed(() => appStore.getFooter)
const getCaches = computed(() => useTagsViewStore().getCachedViews)

// 计算内容区域高度
const contentHeight = computed(() => {
  let result = ''
  if (footer.value && tagsView.value) {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height) - var(--tags-view-height) - var(--app-footer-height))'
  } else if (footer.value && !tagsView.value) {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height) - var(--app-footer-height))'
  } else if (!footer.value && tagsView.value) {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height) - var(--tags-view-height))'
  } else {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height))'
  }
  document.documentElement.style.setProperty('--app-content-height', result)
  return result
})
</script>


