<template>
  <!-- cutMenu - 分栏菜单模式 -->
  <div :id="`${variables.namespace}-menu`" v-click-outside="clickOut"
       :class="[prefixCls,'relative bg-[var(--left-menu-bg-color)] layout-border__right',
               {'w-[var(--tab-menu-max-width)]':!collapse,'w-[var(--tab-menu-min-width)]':collapse}]">
    <!-- 一级菜单 -->
    <el-scrollbar class="!h-[calc(100%-var(--tab-menu-collapse-height))]">
      <div>
        <div v-for="item in tabFirstRoutes" :key="item.path" @click="tabClick(item)"
             :class="[`${prefixCls}__item`,collapse?'b-rd-0!':'m-[1px_5px]! b-rd-6px!',
                     'text-center text-12px relative py-12px cursor-pointer',
                     {'is-active':item.path&&isActive(item.path)}]">
          <my-icon v-if="item.meta&&item.meta.icon" :icon="item.meta?.icon" :size="collapse?20:16"/>
          <p v-if="showTitle" class="break-words mt-5px px-2px">{{ t(item.meta?.title || '') }}</p>
        </div>
      </div>
    </el-scrollbar>
    <!-- 折叠按钮（显示/隐藏菜单名称） -->
    <div @click="setCollapse"
         :class="[`${prefixCls}--collapse`,'text-center h-[var(--tab-menu-collapse-height)] leading-[var(--tab-menu-collapse-height)] cursor-pointer']">
      <my-icon :icon="collapse?'vi-ep:d-arrow-right':'vi-ep:d-arrow-left'"/>
    </div>
    <!-- 子菜单 -->
    <my-menu :class="['!absolute top-1px z-1000 cut-menu',
                   {'!left-[var(--tab-menu-min-width)]':collapse,
                   '!left-[var(--tab-menu-max-width)]':!collapse,
                   '!w-[var(--left-menu-max-width)] border-r-1 border-r-solid border-[var(--el-border-color)]':showMenu||fixedMenu,
                   '!w-0':!showMenu&&!fixedMenu}]"
             style="transition: width var(--transition-time-02), left var(--transition-time-02);"
             @mouseleave="clickOut"/>
  </div>
</template>

<script setup lang="ts">
import { usePermissionStore } from '@/store/modules/permission'
import { useAppStore } from '@/store/modules/app'
import { useI18n } from '@/hooks/web/useI18n'
import { MyMenu } from '@/components/MyMenu'
import { cloneDeep } from 'lodash-es'
import { filterMenusPath, initTabMap, tabPathMap } from './helper'
import { useDesign } from '@/hooks/web/useDesign'
import { isUrl } from '@/utils/is'

const {t} = useI18n()
const {getPrefixCls, variables} = useDesign()
const prefixCls = getPrefixCls('tab-menu')

const {push, currentRoute} = useRouter()
const appStore = useAppStore()
const permissionStore = usePermissionStore()

// 展开/折叠菜单监听（显示/隐藏标题）
const showTitle = ref(true)
const collapse = computed(() => appStore.getCollapse)
watch(() => collapse.value, (collapse: boolean) => {
  if (collapse) {
    showTitle.value = false
  } else {
    // 展开时，延迟显示标题
    setTimeout(() => { showTitle.value = !collapse }, 200)
  }
}, {immediate: true})

// 处理菜单路由数据
const menuRoutes = computed(() => permissionStore.getRoutes)

watch(() => menuRoutes.value, (routeList) => {
  initTabMap(routeList)
  filterMenusPath(routeList, routeList)
}, {immediate: true, deep: true})

const tabFirstRoutes = computed(() =>
    cloneDeep(unref(menuRoutes).filter((v) => !v?.meta?.hidden)).map(v => {
      if (v.children && v.children.length === 1) {
        v.meta = {...v.meta, ...v.children[0].meta}
      }
      return v
    })
)

// 是否固定菜单（固定菜单，不自动隐藏）
const fixedMenu = computed(() => appStore.getFixedMenu)
// 是否隐藏菜单（配合fixedMenu使用）
const showMenu = ref(fixedMenu.value ? true : false)
// 当前激活的路由
const tabActive = ref('')
// 判断当前路由激活状态
const isActive = (currentPath: string) => {
  return tabPathMap[currentPath].includes(currentRoute.value.path)
}

onMounted(() => {
  if (fixedMenu.value) {
    // 若是固定菜单模式，默认加载第一个菜单的子菜单
    tabActive.value = currentRoute.value.path
    const f = tabFirstRoutes.value
        .find(v => (v.meta?.alwaysShow || (v?.children?.length && v?.children?.length > 1)) && v.path === tabActive.value)
    if (f && f.children) {
      permissionStore.setMenuTabRoutes(cloneDeep(f.children))
    }
  }
})

// 折叠/展开菜单，按钮点击
const setCollapse = () => {
  appStore.setCollapse(!collapse.value)
}
// 不是固定菜单模式时：点击菜单外部则隐藏菜单
const clickOut = () => {
  if (!fixedMenu.value) {
    showMenu.value = false
  }
}
// 点击一级菜单
const tabClick = (item: AppRouteRecordRaw) => {
  if (item.path) {
    if (isUrl(item.path)) {
      window.open(item.path)
      return
    }
    if (item.children && item.children.length > 1) {
      if (item.path === tabActive.value || !showMenu.value) {
        showMenu.value = !showMenu.value
      }
      if (showMenu.value) {
        permissionStore.setMenuTabRoutes(cloneDeep(item.children))
      }
    } else {
      push(item.path)
      permissionStore.setMenuTabRoutes([])
      showMenu.value = false
    }
    tabActive.value = item.path
  }
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-tab-menu';

.@{prefix-cls} {
  transition: all var(--transition-time-02);

  &__item {
    color: var(--left-menu-text-color);
    transition: all var(--transition-time-02);
    &:hover {
      color: var(--left-menu-text-active-color);
      background-color: var(--left-menu-bg-active-color);
    }
  }

  &--collapse {
    color: var(--left-menu-text-color);
    background-color: var(--left-menu-bg-light-color);
  }

  .is-active {
    color: var(--left-menu-text-active-color);
    background-color: var(--left-menu-bg-active-color);
  }
}

.cut-menu {
  // 设置子菜单样式
  :deep(.@{elNamespace}-menu) {
    .@{elNamespace}-sub-menu__title,
    .@{elNamespace}-menu-item {
      height: var(--left-menu-item-height);
      border-radius: 10px !important;
      margin: 3px 5px !important;
      > :first-child {
        margin-left: -5px;
      }
      &:hover {
        background-color: var(--left-menu-bg-active-color) !important;
      }
    }
  }
}
</style>
