<template>
  <div :id="prefixCls" :class="prefixCls" class="flex w-full relative bg-[#fff] dark:bg-[var(--el-bg-color)]">
    <span :class="`${prefixCls}__tool ${prefixCls}__tool--first`"
          class="w-[var(--tags-view-height)] h-[var(--tags-view-height)] flex items-center justify-center cursor-pointer"
          @click="move(-200)">
      <my-icon icon="vi-ep:d-arrow-left" color="var(--el-text-color-placeholder)"
               :hover-color="isDark?'#fff':'var(--el-color-black)'"/>
    </span>
    <div class="overflow-hidden flex-1">
      <el-scrollbar ref="scrollbarRef" class="h-full" @scroll="scroll">
        <div class="flex h-full">
          <context-menu :ref="itemRefs.set"
                        :schema="[{
                          icon: 'vi-ant-design:sync-outlined',label: t('tagsView.reloadCtxBtn'),
                          disabled: selectedTag?.fullPath !== item.fullPath,
                          command: () => {refreshSelectedTag(item)}
                        },{
                          icon: 'vi-ant-design:close-outlined',label: t('tagsView.closeTabCtxBtn'),
                          disabled: !!(!!visitedViews?.length&&selectedTag?.meta.affix),
                          command: () => {closeSelectedTag(item)}
                        },{
                          divided: true,icon: 'vi-ant-design:vertical-right-outlined',
                          label: t('tagsView.closeLeftTabCtxBtn'),
                          disabled:!!visitedViews?.length&&(item.fullPath===visitedViews[0].fullPath||selectedTag?.fullPath!==item.fullPath),
                          command: () => {closeLeftTags()}
                        },{
                          icon: 'vi-ant-design:vertical-left-outlined',label: t('tagsView.closeRightTabCtxBtn'),
                          disabled:!!visitedViews?.length&&(item.fullPath===visitedViews[visitedViews.length-1].fullPath||selectedTag?.fullPath!==item.fullPath),
                          command: () => {closeRightTags()}
                        },{
                          divided: true,icon: 'vi-ant-design:tag-outlined',label: t('tagsView.closeOtherCtxBtn'),
                          disabled: selectedTag?.fullPath !== item.fullPath,
                          command: () => {closeOthersTags()}
                        },{
                          icon: 'vi-ant-design:line-outlined',label: t('tagsView.closeAllCtxBtn'),
                          command: () => {closeAllTags()}
                        }]"
                        :class="[`${prefixCls}__item`,item?.meta?.affix?`${prefixCls}__item--affix`:'',{'is-active': isActive(item)}]"
                        v-for="item in visitedViews" :key="item.fullPath"
                        :tag-item="item" @visible-change="visibleChange">
            <div>
              <router-link :ref="tagLinksRefs.set" :to="{ ...item }" custom #default="{ navigate }">
                <div @click="navigate" class="h-full flex justify-center items-center whitespace-nowrap pl-15px">
                  <my-icon v-if="canShowIcon(item)" :icon="item?.matched?.[1]?.meta?.icon||item?.meta?.icon"
                           :size="12" class="mr-5px"/>
                  {{ t(item?.meta?.title as string) }}
                  <my-icon :class="`${prefixCls}__item--close`" icon="vi-ant-design:close-outlined"
                           :size="12" @click.prevent.stop="closeSelectedTag(item)"/>
                </div>
              </router-link>
            </div>
          </context-menu>
        </div>
      </el-scrollbar>
    </div>
    <span :class="`${prefixCls}__tool`"
          class="w-[var(--tags-view-height)] h-[var(--tags-view-height)] flex items-center justify-center cursor-pointer"
          @click="move(200)">
      <my-icon icon="vi-ep:d-arrow-right" color="var(--el-text-color-placeholder)"
               :hover-color="isDark ? '#fff' : 'var(--top-header-text-color)'"/>
    </span>
    <span :class="`${prefixCls}__tool`"
          class="w-[var(--tags-view-height)] h-[var(--tags-view-height)] flex items-center justify-center cursor-pointer"
          @click="refreshSelectedTag(selectedTag)">
      <my-icon icon="vi-ant-design:reload-outlined" color="var(--el-text-color-placeholder)"
               :hover-color="isDark ? '#fff' : 'var(--top-header-text-color)'"/>
    </span>
    <context-menu trigger="click" :schema="[{
          icon: 'vi-ant-design:sync-outlined',label: t('tagsView.reloadCtxBtn'),
          command: () => {refreshSelectedTag(selectedTag)}
        },{
          icon: 'vi-ant-design:close-outlined',label: t('tagsView.closeTabCtxBtn'),
          disabled: !!(!!visitedViews?.length && selectedTag?.meta.affix),
          command: () => {closeSelectedTag(selectedTag!)}
        },{
          divided: true,icon: 'vi-ant-design:vertical-right-outlined',label: t('tagsView.closeLeftTabCtxBtn'),
          disabled: !!visitedViews?.length && selectedTag?.fullPath === visitedViews[0].fullPath,
          command: () => {closeLeftTags()}
        },{
          icon: 'vi-ant-design:vertical-left-outlined',label: t('tagsView.closeRightTabCtxBtn'),
          disabled:!!visitedViews?.length&&selectedTag?.fullPath===visitedViews[visitedViews.length-1].fullPath,
          command: () => {closeRightTags()}
        },{
          divided: true,icon: 'vi-ant-design:tag-outlined',label: t('tagsView.closeOtherCtxBtn'),
          command: () => {closeOthersTags()}
        },{
          icon: 'vi-ant-design:line-outlined',label: t('tagsView.closeAllCtxBtn'),
          command: () => {closeAllTags()}
        }]">
      <span :class="`${prefixCls}__tool`"
            class="w-[var(--tags-view-height)] h-[var(--tags-view-height)] flex items-center justify-center cursor-pointer block">
        <my-icon icon="vi-ant-design:setting-outlined" color="var(--el-text-color-placeholder)"
                 :hover-color="isDark ? '#fff' : 'var(--top-header-text-color)'"/>
      </span>
    </context-menu>
  </div>
</template>

<script setup lang="ts">
import type { RouteLocationNormalizedLoaded, RouterLinkProps } from 'vue-router'
import { usePermissionStore } from '@/store/modules/permission'
import { useTagsViewStore } from '@/store/modules/tagsView'
import { useAppStore } from '@/store/modules/app'
import { useI18n } from '@/hooks/web/useI18n'
import { filterAffixTags } from './helper'
import { ContextMenu, ContextMenuExpose } from '@/components/ContextMenu'
import { useDesign } from '@/hooks/web/useDesign'
import { useTemplateRefsList } from '@vueuse/core'
import { useScrollTo } from '@/hooks/event/useScrollTo'
import { useTagsView } from '@/hooks/web/useTagsView'
import { cloneDeep } from 'lodash-es'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('tags-view')


const {currentRoute, push} = useRouter()
const tagsViewStore = useTagsViewStore()
const appStore = useAppStore()
const permissionStore = usePermissionStore()

// 是否显示标签图标
const tagsViewIcon = computed(() => appStore.getTagsViewIcon)
// 是否暗黑模式
const isDark = computed(() => appStore.getIsDark)
// 路由
const routes = permissionStore.getRoutes
// 已打开过的标签
const visitedViews = computed(() => tagsViewStore.getVisitedViews)
// 当前选中的标签
const selectedTag = computed(() => tagsViewStore.getSelectedTag)

// 初始化tag
const affixTagArr = ref<RouteLocationNormalizedLoaded[]>([])
const initTags = () => {
  affixTagArr.value = filterAffixTags(unref(routes))
  for (const tag of unref(affixTagArr)) {
    if (tag.name) {
      tagsViewStore.addVisitedView(cloneDeep(tag))
    }
  }
}
// 页面加载时，初始化标签
onMounted(() => {
  initTags()
  addTags()
})
// 监听路由变化，切换标签
watch(() => currentRoute.value, () => {
  addTags()
  moveToCurrentTag()
})

const canShowIcon = (item: RouteLocationNormalizedLoaded) => {
  return (item?.matched?.[1]?.meta?.icon && unref(tagsViewIcon))
      || (item?.meta?.affix && unref(tagsViewIcon) && item?.meta?.icon)
}

// 所有右键菜单组件的元素
const itemRefs = useTemplateRefsList<ComponentRef<typeof ContextMenu & ContextMenuExpose>>()
// 右键菜单状态改变的时候
const visibleChange = (visible: boolean, tagItem: RouteLocationNormalizedLoaded) => {
  if (visible) {
    for (const v of unref(itemRefs)) {
      const elDropdownMenuRef = v.elDropdownMenuRef
      if (tagItem.fullPath !== v.tagItem.fullPath) {
        elDropdownMenuRef?.handleClose()
        tagsViewStore.setSelectedTag(tagItem)
      }
    }
  }
}

// 是否是当前tag
const isActive = (route: RouteLocationNormalizedLoaded): boolean => (route.path === unref(currentRoute).path)
// 新增tag
const addTags = () => {
  const {name} = unref(currentRoute)
  if (name) {
    tagsViewStore.setSelectedTag(unref(currentRoute))
    tagsViewStore.addView(unref(currentRoute))
  }
}

const {closeAll, closeLeft, closeRight, closeOther, closeCurrent, refreshPage} = useTagsView()
// 关闭选中的tag
const closeSelectedTag = (view: RouteLocationNormalizedLoaded) => {
  closeCurrent(view, () => {
    if (isActive(view)) {
      // 若关闭的是当前标签，则跳转至最后一个标签
      toLastView()
    }
  })
}
// 去最后一个
const toLastView = () => {
  const visitedViews = tagsViewStore.getVisitedViews
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    push(latestView)
  } else {
    // 最后一个为空，跳转到第一个路由，若全部关闭，则创建第一个路由并跳转
    if (unref(currentRoute).path === unref(routes)[0].path || unref(currentRoute).path === unref(routes)[0].redirect) {
      addTags()
      return
    }
    push(unref(routes)[0].path as string)
  }
}
// 关闭全部
const closeAllTags = () => { closeAll(() => { toLastView() }) }
// 关闭其它
const closeOthersTags = () => { closeOther() }
// 重新加载
const refreshSelectedTag = async (view?: RouteLocationNormalizedLoaded) => { refreshPage(view) }
// 关闭左侧
const closeLeftTags = () => { closeLeft() }
// 关闭右侧
const closeRightTags = () => { closeRight() }

// elscroll 实例
const scrollbarRef = ref<ComponentRef<typeof ElScrollbar>>()
// 保存滚动位置
const scrollLeftNumber = ref(0)
const scroll = ({scrollLeft}) => {
  scrollLeftNumber.value = scrollLeft as number
}
// 移动到某个位置
const move = (to: number) => {
  const wrap$ = unref(scrollbarRef)?.wrapRef
  const {start} = useScrollTo({
    el: wrap$!,
    position: 'scrollLeft',
    to: unref(scrollLeftNumber) + to,
    duration: 500
  })
  start()
}

const tagLinksRefs = useTemplateRefsList<RouterLinkProps>()
// 滚动到选中的tag
const moveToCurrentTag = async () => {
  await nextTick()
  for (const v of unref(visitedViews)) {
    if (v.fullPath === unref(currentRoute).path) {
      moveToTarget(v)
      if (v.fullPath !== unref(currentRoute).fullPath) {
        tagsViewStore.updateVisitedView(unref(currentRoute))
      }
      break
    }
  }
}
const moveToTarget = (currentTag: RouteLocationNormalizedLoaded) => {
  const wrap$ = unref(scrollbarRef)?.wrapRef
  let firstTag: Nullable<RouterLinkProps> = null
  let lastTag: Nullable<RouterLinkProps> = null
  const tagList = unref(tagLinksRefs)
  // 查找第一个标签和最后一个标签
  if (tagList.length > 0) {
    firstTag = tagList[0]
    lastTag = tagList[tagList.length - 1]
  }
  if ((firstTag?.to as RouteLocationNormalizedLoaded).fullPath === currentTag.fullPath) {
    // 直接滚动到0的位置
    const {start} = useScrollTo({el: wrap$!, position: 'scrollLeft', to: 0, duration: 500})
    start()
  } else if ((lastTag?.to as RouteLocationNormalizedLoaded).fullPath === currentTag.fullPath) {
    // 滚动到最后的位置
    const {start} = useScrollTo({
      el: wrap$!, position: 'scrollLeft', to: wrap$!.scrollWidth - wrap$!.offsetWidth, duration: 500
    })
    start()
  } else {
    // 查找上一个和下一个表填
    const currentIndex: number = tagList.findIndex(
        (item) => (item?.to as RouteLocationNormalizedLoaded).fullPath === currentTag.fullPath
    )
    const tgsRefs = document.getElementsByClassName(`${prefixCls}__item`)
    const prevTag = tgsRefs[currentIndex - 1] as HTMLElement
    const nextTag = tgsRefs[currentIndex + 1] as HTMLElement
    // 下一个标签位置
    const afterNextTagOffsetLeft = nextTag.offsetLeft + nextTag.offsetWidth + 4
    // 上一个标签位置
    const beforePrevTagOffsetLeft = prevTag.offsetLeft - 4
    if (afterNextTagOffsetLeft > unref(scrollLeftNumber) + wrap$!.offsetWidth) {
      const {start} = useScrollTo({
        el: wrap$!, position: 'scrollLeft', to: afterNextTagOffsetLeft - wrap$!.offsetWidth, duration: 500
      })
      start()
    } else if (beforePrevTagOffsetLeft < unref(scrollLeftNumber)) {
      const {start} = useScrollTo({
        el: wrap$!, position: 'scrollLeft', to: beforePrevTagOffsetLeft, duration: 500
      })
      start()
    }
  }
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-tags-view';

.@{prefix-cls} {
  :deep(.@{elNamespace}-scrollbar__view) {
    height: 100%;
  }

  &__tool {
    position: relative;

    &::before {
      position: absolute;
      top: 1px;
      left: 0;
      width: 100%;
      height: calc(~'100% - 1px');
      border-left: 1px solid var(--el-border-color);
      content: '';
    }

    &--first {
      &::before {
        position: absolute;
        top: 1px;
        left: 0;
        width: 100%;
        height: calc(~'100% - 1px');
        border-right: 1px solid var(--el-border-color);
        border-left: none;
        content: '';
      }
    }
  }

  &__item {
    position: relative;
    top: 3px;
    height: calc(~'100% - 6px');
    padding-right: 25px;
    margin-left: 4px;
    font-size: 12px;
    cursor: pointer;
    border: 1px solid #d9d9d9;
    border-radius: 2px;

    &--close {
      position: absolute;
      top: 50%;
      right: 5px;
      display: none;
      transform: translate(0, -50%);
    }
    &:not(.@{prefix-cls}__item--affix):hover {
      .@{prefix-cls}__item--close {
        display: block;
      }
    }
  }

  &__item:not(.is-active) {
    &:hover {
      color: var(--el-color-primary);
    }
  }

  &__item.is-active {
    color: var(--el-color-white);
    background-color: var(--el-color-primary);
    border: 1px solid var(--el-color-primary);
    .@{prefix-cls}__item--close {
      :deep(svg) {
        color: var(--el-color-white) !important;
      }
    }
  }
}

.dark {
  .@{prefix-cls} {
    &__tool {
      &--first {
        &::after {
          display: none;
        }
      }
    }

    &__item {
      border: 1px solid var(--el-border-color);
    }

    &__item:not(.is-active) {
      &:hover {
        color: var(--el-color-primary);
      }
    }

    &__item.is-active {
      color: var(--el-color-white);
      background-color: var(--el-color-primary);
      border: 1px solid var(--el-color-primary);
      .@{prefix-cls}__item--close {
        :deep(svg) {
          color: var(--el-color-white) !important;
        }
      }
    }
  }
}
</style>
