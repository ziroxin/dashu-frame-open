<template>
  <div :id="prefixCls"
       :class="[`${prefixCls} ${prefixCls}__${menuMode}`,'h-[100%] overflow-hidden flex-col bg-[var(--left-menu-bg-color)]',
       {'w-[var(--left-menu-min-width)]': collapse && layout !== 'cutMenu',
       'w-[var(--left-menu-max-width)]': !collapse && layout !== 'cutMenu' }]">
    <el-scrollbar v-if="layout!=='top'">
      <el-menu :default-active="activeMenu"
               :mode="menuMode"
               :collapse="(layout==='top'||layout==='cutMenu')?false:collapse"
               :unique-opened="layout==='top'?false:uniqueOpened"
               background-color="var(--left-menu-bg-color)"
               text-color="var(--left-menu-text-color)"
               active-text-color="var(--left-menu-text-active-color)"
               :popper-class="`${prefixCls}-popper--${menuMode}`"
               @select="handleMenuSelect">
        <render-menu-item :menu-mode="menuMode" :route-list="routeList"/>
      </el-menu>
    </el-scrollbar>
    <el-menu v-else :default-active="activeMenu"
             :mode="menuMode"
             :collapse="(layout==='top'||layout==='cutMenu')?false:collapse"
             :unique-opened="layout==='top'?false:uniqueOpened"
             background-color="var(--left-menu-bg-color)"
             text-color="var(--left-menu-text-color)"
             active-text-color="var(--left-menu-text-active-color)"
             :popper-class="`${prefixCls}-popper--${menuMode}`"
             @select="handleMenuSelect">
      <render-menu-item :menu-mode="menuMode" :route-list="routeList"/>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { usePermissionStore } from '@/store/modules/permission'
import RenderMenuItem from './components/RenderMenuItem.vue'
import { useRouter } from 'vue-router'
import { isUrl } from '@/utils/is'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('menu')

const {menuSelect} = defineProps({
  menuSelect: {type: Function as PropType<(index: string) => void>, default: undefined}
})

const appStore = useAppStore()
const permissionStore = usePermissionStore()
const {push, currentRoute} = useRouter()

// 计算相关属性
const layout = computed(() => appStore.getLayout)
const menuMode = computed(() => ['classic', 'topLeft', 'cutMenu'].includes(unref(layout)) ? 'vertical' : 'horizontal')
const routeList = computed(() => unref(layout) === 'cutMenu' ? permissionStore.getMenuTabRoutes : permissionStore.getRoutes)
const collapse = computed(() => appStore.getCollapse)
const uniqueOpened = computed(() => appStore.getUniqueOpened)

const activeMenu = computed(() => {
  const {meta, path} = unref(currentRoute)
  return meta.activeMenu ? meta.activeMenu as string : path
})

// 菜单选中方法
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
        background-color: var(--left-menu-bg-color) !important;
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

    // 设置子菜单的背景颜色
    .@{elNamespace}-menu {
      .@{elNamespace}-sub-menu__title,
      .@{elNamespace}-menu-item:not(.is-active) {
        background-color: var(--left-menu-bg-light-color) !important;
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

// 设置子菜单溢出时滚动样式
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
}
</style>
