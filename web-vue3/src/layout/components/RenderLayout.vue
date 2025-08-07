<template>
  <!-- 经典布局（A）  注意：手机端自动切换为“经典布局” -->
  <template v-if="layoutType === 'classic'">
    <!-- A-左侧 -->
    <div :class="['absolute top-0 left-0 h-full layout-border__right',{'!fixed z-1000':mobile}]">
      <div v-if="logo" style="transition: all var(--transition-time-02);"
           :class="['bg-[var(--left-menu-bg-color)] relative',
                        {'!pl-0':mobile&&collapse,'w-[var(--left-menu-min-width)]':collapse,'w-[var(--left-menu-max-width)]':!collapse}]">
        <my-logo/>
      </div>
      <my-menu :class="[{'!h-[calc(100%-var(--logo-height))]':logo}]"/>
    </div>
    <!-- A-右侧 -->
    <div style="transition: all var(--transition-time-02);"
         :class="[prefixCls+'-content','absolute top-0 h-[100%]',
                   {'w-[calc(100%-var(--left-menu-min-width))] left-[var(--left-menu-min-width)]':collapse&&!mobile,
                   'w-[calc(100%-var(--left-menu-max-width))] left-[var(--left-menu-max-width)]':!collapse&&!mobile,
                   'fixed !w-full !left-0':mobile}]">
      <el-scrollbar :v-loading="pageLoading"
                    :class="[prefixCls+'-content-scrollbar',
                              {'!h-[100%]':!fixedHeader,
                               '!h-[calc(100%-var(--top-tool-height))] mt-[calc(var(--top-tool-height))]': fixedHeader&&!tagsView,
                               '!h-[calc(100%-var(--top-tool-height)-var(--tags-view-height))] mt-[calc(var(--top-tool-height)+var(--tags-view-height))]':fixedHeader&&tagsView}]">
        <div style="transition: all var(--transition-time-02);"
             :class="[{'fixed !w-full !left-0 !top-0': mobile,
                       'fixed top-0 left-0 z-10':fixedHeader&&!mobile,
                       'w-[calc(100%-var(--left-menu-min-width))] !left-[var(--left-menu-min-width)]':collapse&&fixedHeader&&!mobile,
                       'w-[calc(100%-var(--left-menu-max-width))] !left-[var(--left-menu-max-width)]':!collapse&&fixedHeader&&!mobile}]">
          <tool-header :class="['bg-[var(--top-header-bg-color)]',{'layout-border__bottom':!tagsView}]"/>
          <my-tags-view v-if="tagsView" class="layout-border__bottom layout-border__top"/>
        </div>
        <app-view/>
      </el-scrollbar>
    </div>
  </template>
  <!-- 顶部左侧布局（B） -->
  <template v-else-if="layoutType === 'topLeft'">
    <!-- B-顶部工具栏 -->
    <div
        class="flex items-center bg-[var(--top-header-bg-color)] relative layout-border__bottom dark:bg-[var(--el-bg-color)]">
      <div v-if="logo" class="top-tool-hover">
        <my-logo/>
      </div>
      <tool-header class="flex-1"/>
    </div>
    <!-- B-下方内容区域 -->
    <div class="absolute top-[var(--logo-height)] left-0 w-full h-[calc(100%-var(--logo-height))] flex">
      <my-menu class="!h-full relative layout-border__right"/>
      <div :class="[prefixCls+'-content','h-[100%]',
                   {'w-[calc(100%-var(--left-menu-min-width))] left-[var(--left-menu-min-width)]':collapse,
                    'w-[calc(100%-var(--left-menu-max-width))] left-[var(--left-menu-max-width)]':!collapse}]"
           style="transition: all var(--transition-time-02);">
        <!-- 说明：fixedHeader 固定头部，指的是：固定/不固定标签页 -->
        <el-scrollbar :v-loading="pageLoading"
                      :class="[prefixCls + '-content-scrollbar',
                              {'!h-[100%]':!fixedHeader||!tagsView,
                               '!h-[calc(100%-var(--tags-view-height))] mt-[calc(var(--tags-view-height))]':fixedHeader&&tagsView}]">
          <my-tags-view v-if="tagsView"
                        :class="['layout-border__bottom absolute',
                              {'!fixed top-1px left-0 z-10 mt-[calc(var(--logo-height))]':fixedHeader,
                              'w-[calc(100%-var(--left-menu-min-width))] !left-[var(--left-menu-min-width)]':collapse&&fixedHeader,
                              'w-[calc(100%-var(--left-menu-max-width))] !left-[var(--left-menu-max-width)]':!collapse&&fixedHeader}]"
                        style="transition: width var(--transition-time-02), left var(--transition-time-02);"/>
          <app-view/>
        </el-scrollbar>
      </div>
    </div>
  </template>
  <!-- 顶部布局（C） -->
  <template v-else-if="layoutType === 'top'">
    <div :class="[`${prefixCls}-content`,'w-full absolute top-0 h-[100%]']">
      <el-scrollbar :v-loading="pageLoading"
                    :class="[`${prefixCls}-content-scrollbar`,
                            {'!h-[100%]':!fixedHeader,
                             '!h-[calc(100%-var(--top-tool-height))] mt-[calc(var(--top-tool-height))]': fixedHeader&&!tagsView&&!breadcrumb,
                             '!h-[calc(100%-var(--top-tool-height)-var(--tags-view-height))] mt-[calc(var(--top-tool-height)+var(--tags-view-height))]':fixedHeader&&tagsView&&!breadcrumb,
                             '!h-[calc(100%-var(--top-tool-height)-var(--breadcrumb-height))] mt-[calc(var(--top-tool-height)+var(--breadcrumb-height))]':fixedHeader&&!tagsView&&breadcrumb,
                             '!h-[calc(100%-var(--top-tool-height)-var(--tags-view-height)-var(--breadcrumb-height))] mt-[calc(var(--top-tool-height)+var(--tags-view-height)+var(--breadcrumb-height))]':fixedHeader&&tagsView&&breadcrumb,}]">
        <!-- C-顶部工具栏 -->
        <div :class="['flex items-center justify-between flex-wrap bg-[var(--top-header-bg-color)] layout-border__bottom',
                 {'!fixed top-0 left-0 w-full':fixedHeader}]">
          <!-- C-Logo -->
          <div v-if="logo" class="top-tool-hover">
            <my-logo/>
          </div>
          <!-- C-菜单 -->
          <my-menu class="flex-1 px-10px h-[var(--top-tool-height)]"/>
          <!-- C-全屏、尺寸、语言、用户 -->
          <tool-header/>
          <!-- C-标签页（第2行） -->
          <my-tags-view v-if="tagsView" class="w-full layout-border__top"
                        style="transition: width var(--transition-time-02), left var(--transition-time-02);"/>
          <!-- C-面包屑（第3行） -->
          <div v-if="breadcrumb"
               class="w-full h-[var(--breadcrumb-height)] b-t-1px b-t-solid b-t-[var(--el-border-color)]">
            <my-breadcrumb/>
          </div>
        </div>
        <!-- C-下方内容区域 -->
        <app-view :layout-type="layoutType"/>
      </el-scrollbar>
    </div>
  </template>
  <!-- 分栏菜单布局（D） -->
  <template v-else-if="layoutType === 'cutMenu'">
    <!-- D-顶部工具栏 -->
    <div class="flex items-center bg-[var(--top-header-bg-color)] relative layout-border__bottom">
      <div v-if="logo" class="top-tool-hover !pr-15px">
        <my-logo/>
      </div>
      <tool-header class="flex-1"/>
    </div>
    <!-- D-下方内容区域 -->
    <div class="absolute top-[var(--logo-height)] left-0 w-[calc(100%-2px)] h-[calc(100%-var(--logo-height))] flex">
      <!-- D-左侧一级菜单 -->
      <tab-menu/>
      <!-- D-右侧内容区域 -->
      <div :class="[prefixCls+'-content','h-[100%]',
                   {'w-[calc(100%-var(--tab-menu-min-width))] left-[var(--tab-menu-min-width)]':collapse&&!fixedMenu,
                    'w-[calc(100%-var(--tab-menu-max-width))] left-[var(--tab-menu-max-width)]':!collapse&&!fixedMenu,
                    'w-[calc(100%-var(--tab-menu-min-width)-var(--left-menu-max-width))] ml-[var(--left-menu-max-width)]':collapse&&fixedMenu,
                    'w-[calc(100%-var(--tab-menu-max-width)-var(--left-menu-max-width))] ml-[var(--left-menu-max-width)]':!collapse&&fixedMenu}]"
           style="transition: all var(--transition-time-02);">
        <!-- 说明：fixedHeader 固定头部，指的是：固定/不固定标签页 -->
        <el-scrollbar :v-loading="pageLoading"
                      :class="[prefixCls+'-content-scrollbar',
                              {'!h-[100%]':!fixedHeader||!tagsView,
                               '!h-[calc(100%-var(--tags-view-height))] mt-[calc(var(--tags-view-height))]':fixedHeader&&tagsView}]">
          <my-tags-view v-if="tagsView"
                        :class="['layout-border__bottom layout-border__top',
                                {'relative':!fixedHeader,
                                 '!fixed top-0 left-0 z-10 mt-[var(--logo-height)]':fixedHeader,
                                 'w-[calc(100%-var(--tab-menu-min-width))] !left-[var(--tab-menu-min-width)]':collapse&&!fixedMenu&&fixedHeader,
                                 'w-[calc(100%-var(--tab-menu-max-width))] !left-[var(--tab-menu-max-width)]':!collapse&&!fixedMenu&&fixedHeader,
                                 'w-[calc(100%-var(--tab-menu-min-width)-var(--left-menu-max-width))] !left-[var(--tab-menu-min-width)+var(--left-menu-max-width)]':collapse&&fixedMenu&&fixedHeader,
                                 'w-[calc(100%-var(--tab-menu-max-width)-var(--left-menu-max-width))] !left-[var(--tab-menu-max-width)+var(--left-menu-max-width)]':!collapse&&fixedMenu&&fixedHeader}]"
                        style="transition: width var(--transition-time-02), left var(--transition-time-02);"/>
          <app-view/>
        </el-scrollbar>
      </div>
    </div>
  </template>
</template>

<script setup lang="ts">
import AppView from './AppView.vue'
import ToolHeader from './ToolHeader.vue'
import { useAppStore } from '@/store/modules/app'
import { TabMenu } from '@/components/TabMenu'
import { MyTagsView } from '@/components/MyTagsView'
import { useDesign } from '@/hooks/web/useDesign'
import { MyLogo } from '@/components/MyLogo'
import { MyMenu } from '@/components/MyMenu'
import { MyBreadcrumb } from '@/components/MyBreadcrumb'

// 传参
const {layoutType} = defineProps({layoutType: {type: String, default: 'classic'}})

const {getPrefixCls} = useDesign()
const prefixCls = getPrefixCls('layout')

const appStore = useAppStore()
const pageLoading = computed(() => appStore.getPageLoading)
const tagsView = computed(() => appStore.getTagsView)
const collapse = computed(() => appStore.getCollapse)
const logo = computed(() => appStore.logo)
const fixedHeader = computed(() => appStore.getFixedHeader)
const mobile = computed(() => appStore.getMobile)
const fixedMenu = computed(() => appStore.getFixedMenu)
const breadcrumb = computed(() => appStore.getBreadcrumb)
</script>

<style scoped>
/* 这里放置您的样式 */
</style>
