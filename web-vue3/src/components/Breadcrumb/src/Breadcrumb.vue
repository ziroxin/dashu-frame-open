<template>
  <el-breadcrumb :id="`${variables.namespace}-breadcrumb`" separator="/"
                 :class="[prefixCls, 'flex items-center h-full ml-10']">
    <transition-group appear enter-active-class="animate__animated animate__fadeInRight">
      <el-breadcrumb-item v-for="v in breadcrumbList" :key="v.name" :to="{path:toPath(v)}">
        <template v-if="v.meta?.icon && showIcon">
          <my-icon :icon="v.meta.icon" class="mr-[5px]"/>
        </template>
        {{ t(v.meta.title || '') }}
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { usePermissionStore } from '@/store/modules/permission'
import { filterBreadcrumb } from './helper'
import { filter, treeToList } from '@/utils/tree'
import { useI18n } from '@/hooks/web/useI18n'
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const {t} = useI18n()
const {getPrefixCls, variables} = useDesign()
const prefixCls = getPrefixCls('breadcrumb')

const showIcon = computed(() => useAppStore().getBreadcrumbIcon)

// 加载路由数据
const menuRoutes = computed(() => { return filterBreadcrumb(usePermissionStore().getRoutes)})
// 解析面包屑列表
const breadcrumbList = ref<AppRouteRecordRaw[]>([])
const getBreadcrumb = (route) => {
  const currentPath = route.matched.slice(-1)[0].path
  const treeRoutes = filter<AppRouteRecordRaw>(unref(menuRoutes), node => node.path === currentPath)
  breadcrumbList.value = treeToList(treeRoutes)
}

// 监听路由变化
const {currentRoute} = useRouter()
watch(currentRoute, (route) => {
  if (!route.path.startsWith('/redirect/')) {
    getBreadcrumb(route) // 初始化面包屑路由数据
  }
}, {immediate: true})


// 解析跳转地址
const toPath = (v: AppRouteRecordRaw) => {
  return !v.redirect || v.redirect === 'noRedirect' ? '' : v.path
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{elNamespace}-breadcrumb';

.@{prefix-cls} {
  :deep(&__item) {
    display: flex;
    .@{prefix-cls}__inner {
      display: flex;
      align-items: center;
      color: var(--top-header-text-color);

      &:hover {
        color: var(--el-color-primary);
      }
    }
  }

  :deep(&__item):not(:last-child) {
    .@{prefix-cls}__inner {
      color: var(--top-header-text-color);

      &:hover {
        color: var(--el-color-primary);
      }
    }
  }

  :deep(&__item):last-child {
    .@{prefix-cls}__inner {
      color: var(--el-text-color-placeholder);

      &:hover {
        color: var(--el-text-color-placeholder);
      }
    }
  }
}
</style>
