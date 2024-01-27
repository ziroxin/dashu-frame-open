<template>
  <div v-if="item.hidden!==undefined&&!item.hidden" class="hor-menu">
    <template
        v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown':!isNest}">
          <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)" :title="onlyOneChild.meta.title"/>
        </el-menu-item>
      </app-link>
    </template>

    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <item v-if="item.meta" :icon="item.meta && item.meta.icon" :title="item.meta.title"/>
      </template>
      <topbar-item
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
import Item from '../Sidebar/Item'
import AppLink from '../Sidebar/Link'

export default {
  name: 'TopbarItem',
  components: {Item, AppLink},
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
      // 子菜单数量
      const realChildren = children.filter(o => !o.hidden)
      // 只有一个子菜单
      if (realChildren.length === 1) {
        this.onlyOneChild = realChildren[0]
        return true
      }
      // 没有子菜单，显示父级信息
      if (realChildren.length === 0) {
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
<style lang="scss" scoped>
@import "~@/styles/variables.scss";

.el-menu--horizontal {
  .el-menu {
    .el-menu-item, .el-submenu__title {
      height: 45px;
      line-height: 45px;
      margin: 3px 5px;
      border-radius: 10px;
    }

    .is-active {
      background-color: #{$subMenuActive} !important;
    }
  }
}

.hor-menu {
  .el-submenu {
    ::v-deep .el-submenu__title {
      &:hover {
        background-color: #{$subMenuActive} !important;
      }
    }
  }

  .submenu-title-noDropdown {
    &:hover {
      height: 58px;
      background-color: #{$subMenuActive} !important;
      border-bottom: 2px solid var(--theme-color) !important;
    }

    &.is-active {
      height: 58px;
    }
  }

  .svg-icon {
    margin: 0 10px 0px 5px !important;
  }
}

</style>
