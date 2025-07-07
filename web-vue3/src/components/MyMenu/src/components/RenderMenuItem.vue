<template>
  <div>
    <template v-for="item in routeListNew" :key="item.name">
      <el-menu-item v-if="!item.alwaysShow && item.childCount<=1" :index="item.onlyOneChild?.path||item.path">
        <render-menu-title :meta="item.onlyOneChild?.meta||item.meta"/>
      </el-menu-item>
      <el-sub-menu v-else :index="item.path" teleported
                   :popper-class="menuMode==='vertical'?`${prefixCls}-popper--vertical`:''">
        <template #title>
          <render-menu-title :meta="item.meta"/>
        </template>
        <template #default>
          <render-menu-item :menu-mode="menuMode" :route-list="item.children!"/>
        </template>
      </el-sub-menu>
    </template>
  </div>
</template>

<script setup lang="ts">
import { hasOneShowingChild } from '../helper'
import { useDesign } from '@/hooks/web/useDesign'
import RenderMenuTitle from './RenderMenuTitle.vue'

const prefixCls = useDesign().getPrefixCls('submenu')

// 参数1：routeList 路由列表（遍历处理）
// 参数2：menuMode 菜单模式（vertical/horizontal）
const {routeList, menuMode} = defineProps({
  routeList: {type: Array, default: () => []},
  menuMode: String
})

const routeListNew = computed(() => {
  const result: any[] = []
  routeList.forEach((v: any) => {
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
</script>
