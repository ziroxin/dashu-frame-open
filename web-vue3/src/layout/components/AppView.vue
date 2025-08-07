<template>
  <section :id="`${variables.namespace}-app-container`"
           :class="['box-border w-full bg-[var(--app-content-bg-color)] dark:bg-[var(--el-bg-color)] pb-0']">
    <!-- 最小高度，根据不同条件设置 -->
    <router-view :style="'min-height:'+contentHeight+' !important;'">
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
const {layoutType} = defineProps({layoutType: {type: String, default: ''}})
const appStore = useAppStore()
const tagsView = computed(() => appStore.getTagsView)
const footer = computed(() => appStore.getFooter)
const breadcrumb = computed(() => appStore.getBreadcrumb)
const getCaches = computed(() => useTagsViewStore().getCachedViews)

// 计算内容区域高度
const contentHeight = computed(() => {
  let result = ''
  let breadcrumHeight = ''
  if (layoutType === 'top' && breadcrumb.value) {
    // 顶部模式下，需单独处理面包屑高度
    breadcrumHeight = ' - var(--breadcrumb-height)'
  }
  if (footer.value && tagsView.value) {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height) - var(--tags-view-height) - var(--app-footer-height)' + breadcrumHeight + ')'
  } else if (footer.value && !tagsView.value) {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height) - var(--app-footer-height)' + breadcrumHeight + ')'
  } else if (!footer.value && tagsView.value) {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height) - var(--tags-view-height)' + breadcrumHeight + ')'
  } else {
    result = 'calc(100vh - 2 * var(--app-content-margin) - var(--top-tool-height)' + breadcrumHeight + ')'
  }
  document.documentElement.style.setProperty('--app-content-height', result)
  return result
})
</script>


