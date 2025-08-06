<template>
  <el-menu-item v-if="!routeData.alwaysShow && routeData.childCount<=1"
                :index="routeData.onlyOneChild?.path||routeData.path">
    <render-menu-title :meta="routeData.onlyOneChild?.meta||routeData.meta" :name="routeData.name"/>
  </el-menu-item>
  <el-sub-menu v-else :index="routeData.path" teleported
               :popper-class="menuMode==='vertical'?`${prefixCls}-popper--vertical`:''">
    <template #title>
      <render-menu-title :meta="routeData.meta" :name="routeData.name"/>
    </template>
    <template #default>
      <render-menu-item :menu-mode="menuMode"
                        v-for="item in getRouteList(routeData.children)" :route-data="item" :key="item.name"/>
    </template>
  </el-sub-menu>
</template>

<script setup lang="ts">
import RenderMenuTitle from './RenderMenuTitle.vue'
import { useDesign } from '@/hooks/web/useDesign'
import { hasOneShowingChild } from '@/components/MyMenu/src/helper'

const prefixCls = useDesign().getPrefixCls('submenu')

// 参数1：routeData 路由项
// 参数2：menuMode 菜单模式（vertical/horizontal）
const {routeData, menuMode} = defineProps({
  routeData: {type: Object, default: () => {}},
  menuMode: String
})

// 处理路由列表
const getRouteList = (list: any[]) => {
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
}
</script>
