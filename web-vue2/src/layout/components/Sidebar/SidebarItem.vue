<template>
  <div v-if="item.hidden!==undefined&&!item.hidden"
       :class="{'first-div':!isNest&&this.$store.getters.sidebar.opened}">
    <template
        v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown':!isNest}">
          <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)"
                :title="onlyOneChild.meta.title" :permission-name="onlyOneChild.name"/>
        </el-menu-item>
      </app-link>
    </template>

    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <item v-if="item.meta" :icon="item.meta && item.meta.icon"
              :title="item.meta.title" :permission-name="item.name"/>
      </template>
      <sidebar-item
          v-for="child in item.children"
          :key="child.path"
          :is-nest="true"
          :item="child"
          :base-path="resolvePath(child.path)"
          class="nest-menu"
      />
    </el-submenu>
  </div>
</template>

<script>
import path from 'path'
import {isExternal} from '@/utils/validate'
import Item from './Item'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'

export default {
  name: 'SidebarItem',
  components: {Item, AppLink},
  mixins: [FixiOSBug],
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  data() {
    this.onlyOneChild = null
    return {}
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      const showingChildren = children.filter(item => {
        if (item.hidden && item.path !== 'index') {
          // 只有一个children，且path=index时，代表没有子菜单
          return false
        } else {
          // 否则是有子菜单
          this.onlyOneChild = item
          return true
        }
      })
      if (showingChildren.length === 1) {
        // 当有一个子路由时，直接返回
        return true
      } else if (showingChildren.length === 0) {
        // 如果没有子路由，则children是父级扩展内容
        this.onlyOneChild = {...parent, path: '', noShowingChildren: true}
        return true
      }
      return false
    },
    resolvePath(routePath) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(this.basePath)) {
        return this.basePath
      }
      return path.resolve(this.basePath, routePath)
    }
  }
}
</script>
