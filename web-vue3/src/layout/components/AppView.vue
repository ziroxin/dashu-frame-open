<template>
  <section :class="['box-border w-full bg-[var(--app-content-bg-color)] dark:bg-[var(--el-bg-color)] pb-0']">
    <!-- 最小高度，根据不同条件设置 -->
    <router-view :class="[{'!min-h-[calc(100vh-2*var(--app-content-margin)-var(--top-tool-height)-var(--tags-view-height)-var(--app-footer-height))]':footer&&tagsView,
                   '!min-h-[calc(100vh-2*var(--app-content-margin)-var(--top-tool-height)-var(--app-footer-height))]':footer&&!tagsView,
                   '!min-h-[calc(100vh-2*var(--app-content-margin)-var(--top-tool-height)-var(--tags-view-height))]':!footer&&tagsView,
                   '!min-h-[calc(100vh-2*var(--app-content-margin)-var(--top-tool-height))]':!footer&&!tagsView}]">
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

const appStore = useAppStore()
const tagsView = computed(() => appStore.getTagsView)
const footer = computed(() => appStore.getFooter)
const getCaches = computed(() => useTagsViewStore().getCachedViews)
</script>


