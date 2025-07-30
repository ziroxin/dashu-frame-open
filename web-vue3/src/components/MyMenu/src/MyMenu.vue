<template>
  <div :id="prefixCls"
       :class="[`${prefixCls} ${prefixCls}__${menuMode}`,'h-[100%] overflow-hidden flex-col bg-[var(--left-menu-bg-color)]',
                {'w-[var(--left-menu-min-width)]':collapse&&layout!=='cutMenu',
                 'w-[var(--left-menu-max-width)]':!collapse&&layout!=='cutMenu'}]">
    <el-scrollbar v-if="layout!=='top'">
      <el-menu :default-active="activeMenu"
               :mode="menuMode"
               :collapse="(layout==='top'||layout==='cutMenu')?false:collapse"
               :unique-opened="layout==='top'?false:uniqueOpened"
               background-color="var(--left-menu-bg-color)"
               :text-color="collapse?'#333':'var(--left-menu-text-color)'"
               active-text-color="var(--left-menu-text-active-color)"
               :popper-class="`${prefixCls}-popper--${menuMode}`"
               @select="handleMenuSelect">
        <render-menu-item :menu-mode="menuMode"
                          v-for="item in routeList" :route-data="item" :key="item.name"/>
      </el-menu>
    </el-scrollbar>
    <el-menu :default-active="activeMenu"
             :mode="menuMode"
             :collapse="(layout==='top'||layout==='cutMenu')?false:collapse"
             :unique-opened="layout==='top'?false:uniqueOpened"
             background-color="var(--left-menu-bg-color)"
             :text-color="collapse?'#333':'var(--left-menu-text-color)'"
             active-text-color="var(--left-menu-text-active-color)"
             :popper-class="`${prefixCls}-popper--${menuMode}`"
             @select="handleMenuSelect">
      <render-menu-item :menu-mode="menuMode"
                        v-for="item in routeList" :route-data="item" :key="item.name"/>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { usePermissionStore } from '@/store/modules/permission'
import RenderMenuItem from './components/RenderMenuItem.vue'
import { isUrl } from '@/utils/is'
import { useDesign } from '@/hooks/web/useDesign'
import { hasOneShowingChild } from './helper'

const prefixCls = useDesign().getPrefixCls('menu')
const appStore = useAppStore()
const permissionStore = usePermissionStore()
const {push, currentRoute} = useRouter()

// 参数：菜单选中方法（非必填，若传入，优先调用该方法）
const {menuSelect} = defineProps({
  menuSelect: {type: Function as PropType<(index: string) => void>, default: undefined}
})

// layout布局：'classic'=经典左右布局 | 'topLeft'=顶部左侧布局 | 'top'=顶部菜单布局 | 'cutMenu'=分栏菜单布局
const layout: any = computed(() => appStore.getLayout)
// 菜单模式：'vertical'=垂直菜单 | 'horizontal'=水平菜单
const menuMode = computed(() => ['classic', 'topLeft', 'cutMenu'].includes(unref(layout)) ? 'vertical' : 'horizontal')
// 路由列表
const routeList = computed(() => {
  // 路由列表（分栏模式时特殊处理）
  const list = unref(layout) === 'cutMenu' ? permissionStore.getMenuTabRoutes : permissionStore.getRoutes
  const result: any[] = []
  list.forEach((v: any) => {
    if (!v.meta?.hidden) {
      const item = {...v}
      item.meta = item.meta ?? {}
      const {childCount, onlyOneChild} = hasOneShowingChild(item.children)
      item.childCount = childCount
      item.onlyOneChild = onlyOneChild
      result.push(item)
    }
  })
  return result
})

// 菜单展开/收起状态
const collapse = computed(() => appStore.getCollapse)
// 是否只保持一个子菜单的展开
const uniqueOpened = computed(() => appStore.getUniqueOpened)
// 当前激活菜单
const activeMenu = computed(() => currentRoute.value.meta.activeMenu || currentRoute.value.path)

// 菜单选中（点击）方法
const handleMenuSelect = (index: string) => {
  if (menuSelect) {
    // 父级传入菜单选中function，则优先调用父级
    menuSelect(index)
  } else {
    if (isUrl(index)) {
      // 外链跳转
      window.open(index)
    } else {
      // 内部跳转
      push(index)
    }
  }
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-menu';

.@{prefix-cls} {
  position: relative;
  transition: width var(--transition-time-02);

  :deep(.@{elNamespace}-menu) {
    width: 100% !important;
    border-right: none;

    // 设置选中时子标题的颜色
    .is-active {
      & > .@{elNamespace}-sub-menu__title {
        color: var(--left-menu-text-active-color) !important;
      }
    }

    // 设置子菜单悬停的高亮和背景色
    .@{elNamespace}-sub-menu__title,
    .@{elNamespace}-menu-item {
      &:hover {
        color: var(--left-menu-text-active-color) !important;
        background-color: var(--left-menu-bg-active-color) !important;
      }
    }

    // 设置选中时的高亮背景和高亮颜色
    .@{elNamespace}-menu-item.is-active {
      color: var(--left-menu-text-active-color) !important;
      background-color: var(--left-menu-bg-active-color) !important;
      &:hover {
        background-color: var(--left-menu-bg-active-color) !important;
      }
    }

    .@{elNamespace}-menu-item.is-active {
      position: relative;
    }

    // 设置子菜单样式
    .@{elNamespace}-menu {
      background-color: var(--left-sub-menu-bg-color) !important;
      padding: 1px 0 !important;

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

  // 折叠时的最小宽度
  :deep(.@{elNamespace}-menu--collapse) {
    width: var(--left-menu-min-width);

    & > .is-active,
    & > .is-active > .@{elNamespace}-sub-menu__title {
      position: relative;
      background-color: var(--left-menu-collapse-bg-active-color) !important;
    }
  }

  // 折叠动画的时候，就需要把文字给隐藏掉
  :deep(.horizontal-collapse-transition) {
    .@{prefix-cls}__title {
      display: none;
    }
  }

  // 水平菜单
  &__horizontal {
    height: calc(~'var(--top-tool-height)') !important;
    :deep(.@{elNamespace}-menu--horizontal) {
      height: calc(~'var(--top-tool-height)');
      border-bottom: none;
      // 重新设置底部高亮颜色
      & > .@{elNamespace}-sub-menu.is-active {
        .@{elNamespace}-sub-menu__title {
          border-bottom-color: var(--el-color-primary) !important;
        }
      }
      .@{elNamespace}-menu-item.is-active {
        position: relative;
        &::after {
          display: none !important;
        }
      }
      .@{prefix-cls}__title {
        /* stylelint-disable-next-line */
        max-height: calc(~'var(--top-tool-height) - 2px') !important;
        /* stylelint-disable-next-line */
        line-height: calc(~'var(--top-tool-height) - 2px');
      }
      // 更多按钮样式
      .@{elNamespace}-sub-menu__hide-arrow {
        .@{elNamespace}-sub-menu__title {
          padding-right: 20px !important;
        }
      }
    }
  }
}
</style>

<style lang="less">
@prefix-cls: ~'@{adminNamespace}-menu-popper';

.@{prefix-cls}--vertical,
.@{prefix-cls}--horizontal {
  // 设置选中时子标题的颜色
  .is-active {
    & > .el-sub-menu__title {
      color: var(--left-menu-text-active-color) !important;
    }
  }

  // 设置子菜单悬停的高亮和背景色
  .el-sub-menu__title,
  .el-menu-item {
    &:hover {
      color: var(--left-menu-text-active-color) !important;
      background-color: var(--left-menu-bg-color) !important;
    }
  }

  // 设置选中时的高亮背景
  .el-menu-item.is-active {
    position: relative;
    background-color: var(--left-menu-bg-active-color) !important;

    &:hover {
      background-color: var(--left-menu-bg-active-color) !important;
    }
  }
}

@submenu-prefix-cls: ~'@{adminNamespace}-submenu-popper';

// 设置 菜单折叠后，子菜单的样式
.@{submenu-prefix-cls}--vertical {
  max-height: 100%;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
    background-color: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgb(144 147 153 / 30%);
    border-radius: 4px;
  }

  // 设置选中时子标题的颜色
  .is-active {
    & > .@{elNamespace}-sub-menu__title {
      color: var(--left-menu-text-active-color) !important;
      background-color: var(--left-menu-bg-active-color) !important;
    }
  }

  // 设置选中时的高亮背景和高亮颜色
  .@{elNamespace}-menu-item.is-active {
    color: var(--left-menu-text-active-color) !important;
    background-color: var(--left-menu-bg-active-color) !important;
    &:hover {
      background-color: var(--left-menu-bg-active-color) !important;
    }
  }

  // 设置子菜单样式
  .@{elNamespace}-menu {
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

// top模式：水平菜单样式
.@{elNamespace}-popper {
  .@{elNamespace}-menu--horizontal {
    .@{elNamespace}-menu {
      // 设置选中时的高亮背景和高亮颜色
      .@{elNamespace}-sub-menu__title,
      .@{elNamespace}-menu-item {
        height: var(--left-menu-item-height);
        border-radius: 10px !important;
        margin: 3px 5px !important;
        > :first-child {
          margin-left: -5px;
        }
        &:hover {
          color: var(--left-menu-text-active-color) !important;
          background-color: var(--left-menu-bg-active-color) !important;
        }
        &.is-active {
          color: var(--left-menu-text-active-color) !important;
          background-color: var(--left-menu-bg-active-color) !important;
        }
      }

      // 设置选中时子标题的颜色
      .is-active {
        & > .@{elNamespace}-sub-menu__title {
          color: var(--left-menu-text-active-color) !important;
          background-color: var(--left-menu-bg-active-color) !important;
        }
      }
    }
  }
}
</style>
