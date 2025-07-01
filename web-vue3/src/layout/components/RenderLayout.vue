<template>
  <div>
    <!-- 经典布局 -->
    <div v-if="layoutType === 'classic'">
      <div :class="['absolute top-0 left-0 h-full layout-border__right',{'!fixed z-3000':mobile}]">
        <Logo v-if="logo" style="transition: all var(--transition-time-02);"
              :class="['bg-[var(--left-menu-bg-color)] relative',
                      {'!pl-0':mobile&&collapse,
                      'w-[var(--left-menu-min-width)]':collapse,
                      'w-[var(--left-menu-max-width)]':!collapse}]"/>
        <Menu :class="[{'!h-[calc(100%-var(--logo-height))]':logo}]"/>
      </div>
      <div style="transition: all var(--transition-time-02);"
           :class="[prefixCls+'-content','absolute top-0 h-[100%]',
                   {'w-[calc(100%-var(--left-menu-min-width))] left-[var(--left-menu-min-width)]':collapse&&!mobile,
                   'w-[calc(100%-var(--left-menu-max-width))] left-[var(--left-menu-max-width)]':!collapse&&!mobile,
                   'fixed !w-full !left-0':mobile }]">
        <el-scrollbar :v-loading="pageLoading"
                      :class="[prefixCls+'-content-scrollbar',
                              {'!h-[calc(100%-var(--top-tool-height)-var(--tags-view-height))] mt-[calc(var(--top-tool-height)+var(--tags-view-height))]': fixedHeader }]">
          <div style="transition: all var(--transition-time-02);"
               :class="[{'fixed top-0 left-0 z-10':fixedHeader,
                        'w-[calc(100%-var(--left-menu-min-width))] !left-[var(--left-menu-min-width)]':collapse&&fixedHeader&&!mobile,
                        'w-[calc(100%-var(--left-menu-max-width))] !left-[var(--left-menu-max-width)]':!collapse&&fixedHeader&&!mobile,
                        '!w-full !left-0': mobile}]">
            <tool-header :class="['bg-[var(--top-header-bg-color)]',{'layout-border__bottom':!tagsView}]"/>
            <tags-view v-if="tagsView" class="layout-border__bottom layout-border__top"/>
          </div>
          <app-view/>
        </el-scrollbar>
      </div>
    </div>
    <!-- 顶部左侧布局 -->
    <div v-else-if="layoutType === 'topLeft'">
      <div
          class="flex items-center bg-[var(--top-header-bg-color)] relative layout-border__bottom dark:bg-[var(--el-bg-color)]">
        <Logo v-if="logo" class="custom-hover"/>
        <tool-header class="flex-1"/>
      </div>
      <div class="absolute top-[var(--logo-height)+1px] left-0 w-full h-[calc(100%-1px-var(--logo-height))] flex">
        <Menu class="!h-full relative layout-border__right"/>
        <div style="transition: all var(--transition-time-02);"
             :class="[prefixCls+'-content','h-[100%]',
                    {'w-[calc(100%-var(--left-menu-min-width))] left-[var(--left-menu-min-width)]':collapse,
                    'w-[calc(100%-var(--left-menu-max-width))] left-[var(--left-menu-max-width)]':!collapse}]">
          <el-scrollbar :v-loading="pageLoading"
                        :class="[prefixCls + '-content-scrollbar', { '!h-[calc(100%-var(--tags-view-height))] mt-[calc(var(--tags-view-height))]': fixedHeader && tagsView }]">
            <tags-view v-if="tagsView"
                       :class="['layout-border__bottom absolute',
                              {'!fixed top-0 left-0 z-10':fixedHeader,
                              'w-[calc(100%-var(--left-menu-min-width))] !left-[var(--left-menu-min-width)] mt-[calc(var(--logo-height)+1px)]':collapse&&fixedHeader,
                              'w-[calc(100%-var(--left-menu-max-width))] !left-[var(--left-menu-max-width)] mt-[calc(var(--logo-height)+1px)]':!collapse&&fixedHeader}]"
                       style="transition: width var(--transition-time-02), left var(--transition-time-02);"/>
            <app-view/>
          </el-scrollbar>
        </div>
      </div>
    </div>
    <!-- 顶部布局 -->
    <div v-else-if="layoutType === 'top'">
      <div :class="['flex items-center justify-between bg-[var(--top-header-bg-color)] relative',
                   {'layout-border__bottom':!tagsView}]">
        <Logo v-if="logo" class="custom-hover"/>
        <Menu class="flex-1 px-10px h-[var(--top-tool-height)]"/>
        <tool-header/>
      </div>
      <div :class="[prefixCls+'-content','w-full',{'h-[calc(100%-var(--top-tool-height))]':!fixedHeader,
                    'h-[calc(100%-var(--tags-view-height)-var(--top-tool-height))]':fixedHeader}]">
        <el-scrollbar :v-loading="pageLoading"
                      :class="[prefixCls+'-content-scrollbar',
                      {'mt-[var(--tags-view-height)] !pb-[calc(var(--tags-view-height)+var(--app-footer-height))]':fixedHeader,
                      'pb-[var(--app-footer-height)]': !fixedHeader}]">
          <tags-view v-if="tagsView"
                     :class="['relative layout-border__bottom layout-border__top',
                     {'!fixed top-0 left-0 z-10':fixedHeader,
                     'w-[calc(100%-var(--left-menu-min-width))] !left-[var(--left-menu-min-width)] mt-[var(--logo-height)]':collapse&&fixedHeader,
                     'w-[calc(100%-var(--left-menu-max-width))] !left-[var(--left-menu-max-width)] mt-[var(--logo-height)]':!collapse&&fixedHeader}]"
                     style="transition: width var(--transition-time-02), left var(--transition-time-02);"/>
          <app-view/>
        </el-scrollbar>
      </div>
    </div>
    <!-- 分栏菜单布局 -->
    <div v-else-if="layoutType === 'cutMenu'">
      <div class="flex items-center bg-[var(--top-header-bg-color)] relative layout-border__bottom">
        <Logo v-if="logo" class="custom-hover !pr-15px"/>
        <tool-header class="flex-1"/>
      </div>
      <div class="absolute top-[var(--logo-height)] left-0 w-[calc(100%-2px)] h-[calc(100%-var(--logo-height))] flex">
        <tab-menu/>
        <div :class="[prefixCls+'-content','h-[100%]',
                      {'w-[calc(100%-var(--tab-menu-min-width))] left-[var(--tab-menu-min-width)]':collapse&&!fixedMenu,
                      'w-[calc(100%-var(--tab-menu-max-width))] left-[var(--tab-menu-max-width)]':!collapse&&!fixedMenu,
                      'w-[calc(100%-var(--tab-menu-min-width)-var(--left-menu-max-width))] ml-[var(--left-menu-max-width)]':collapse&&fixedMenu,
                      'w-[calc(100%-var(--tab-menu-max-width)-var(--left-menu-max-width))] ml-[var(--left-menu-max-width)]':!collapse&&fixedMenu}]"
             style="transition: all var(--transition-time-02);">
          <el-scrollbar :v-loading="pageLoading"
                        :class="[prefixCls+'-content-scrollbar',
                        {'!h-[calc(100%-var(--tags-view-height))] mt-[calc(var(--tags-view-height))]':fixedHeader&&tagsView}]">
            <tags-view v-if="tagsView"
                       :class="['relative layout-border__bottom layout-border__top',
                               {'!fixed top-0 left-0 z-10':fixedHeader,
                               'w-[calc(100%-var(--tab-menu-min-width))] !left-[var(--tab-menu-min-width)] mt-[var(--logo-height)]':collapse&&fixedHeader,
                               'w-[calc(100%-var(--tab-menu-max-width))] !left-[var(--tab-menu-max-width)] mt-[var(--logo-height)]':!collapse&&fixedHeader,
                               '!fixed top-0 !left-[var(--tab-menu-min-width)+var(--left-menu-max-width)] z-10':fixedHeader&&fixedMenu,
                               'w-[calc(100%-var(--tab-menu-min-width)-var(--left-menu-max-width))] !left-[var(--tab-menu-min-width)+var(--left-menu-max-width)] mt-[var(--logo-height)]':collapse&&fixedHeader&&fixedMenu,
                               'w-[calc(100%-var(--tab-menu-max-width)-var(--left-menu-max-width))] !left-[var(--tab-menu-max-width)+var(--left-menu-max-width)] mt-[var(--logo-height)]':!collapse&&fixedHeader&&fixedMenu}]"
                       style="transition: width var(--transition-time-02), left var(--transition-time-02);"/>
            <app-view/>
          </el-scrollbar>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { Menu } from '@/components/Menu'
import { TabMenu } from '@/components/TabMenu'
import { TagsView } from '@/components/TagsView'
import { Logo } from '@/components/Logo'
import AppView from './AppView.vue'
import ToolHeader from './ToolHeader.vue'
import { useDesign } from '@/hooks/web/useDesign'

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
</script>

<style scoped>
/* 这里放置您的样式 */
</style>
