<template>
  <div class="navbar" ref="navbarTopLeft">
    <!-- 显示隐藏侧边栏按钮 -->
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>
    <div class="left-menu">
      <scroll-horizontal v-if="leftMenuWidth" :container-width="leftMenuWidth" :container-height="50">
        <el-menu :style="{'--theme-color': themeColor}"
                 class="top-left-bar"
                 :default-active="activeMenu"
                 :background-color="variables.menuBg"
                 :text-color="variables.menuText"
                 :active-text-color="themeColor"
                 :unique-opened="false"
                 :collapse-transition="false"
                 mode="horizontal"
                 menu-trigger="hover"
        >
          <template v-for="item in permission_routes" v-if="item.hidden!==undefined&&!item.hidden">
            <template
                v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
              <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path,item.path)">
                <el-menu-item :index="resolvePath(onlyOneChild.path,item.path)">
                  <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)" :title="onlyOneChild.meta.title"/>
                </el-menu-item>
              </app-link>
            </template>
            <el-menu-item v-else :index="resolvePath(item.path,item.path)" popper-append-to-body>
              <template slot="title">
                <item :icon="item.meta.icon" :title="item.meta.title"/>
              </template>
            </el-menu-item>
          </template>
        </el-menu>
      </scroll-horizontal>
    </div>
    <!-- 顶部右侧菜单 -->
    <div class="right-menu">
      <header-user-setting/>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import Hamburger from '@/components/Hamburger'
import ErrorLog from '@/components/ErrorLog'
import Screenfull from '@/components/Screenfull'
import Search from '@/components/HeaderSearch'
import HeaderUserSetting from '@/layout/components/HeaderUserSetting';
import ScrollHorizontal from "@/components/ScrollHorizontal/index.vue";
import variables from '@/styles/variables.scss'
import Item from "@/layout/components/Sidebar/Item.vue";
import path from 'path'
import {isExternal} from "@/utils/validate";
import AppLink from "@/layout/components/Sidebar/Link.vue";

export default {
  components: {
    AppLink,
    Item,
    ScrollHorizontal,
    HeaderUserSetting,
    Hamburger,
    ErrorLog,
    Screenfull,
    Search
  },
  data() {
    this.onlyOneChild = null
    return {
      // 个人用户修改密码
      editPassword: false,
      // 顶部左侧菜单宽度
      leftMenuWidth: null,
    }
  },
  mounted() {
    this.leftMenuWidth = this.$refs.navbarTopLeft.offsetWidth - 190
  },
  computed: {
    ...mapGetters([
      'permission_routes',
      'sidebar',
      'device'
    ]),
    themeColor() {
      return this.$store.state.settings.theme
    },
    activeMenu() {
      const route = this.$route
      const {meta, path} = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    variables() {
      return variables
    }
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      // 子菜单数量
      const realChildren = children.filter(o => !o.hidden)
      // 只有一个子菜单
      if (realChildren.length === 1) {
        this.onlyOneChild = realChildren[0]
        console.log(666, this.onlyOneChild)
        return true
      }
      // 没有子菜单，显示父级信息
      if (realChildren.length === 0) {
        this.onlyOneChild = {...parent, path: '', noShowingChildren: true}
        console.log(555, this.onlyOneChild)
        return true
      }
      return false
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    resolvePath(routePath, basePath) {
      console.log(111)
      console.log('routePath', routePath, 'basePath', basePath)
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(basePath)) {
        return basePath
      }
      console.log('path.resolve', path.resolve(basePath, routePath))
      return path.resolve(basePath, routePath)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "~@/styles/variables.scss";

.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  // 顶部一级菜单
  .left-menu {
    margin: 0px 22px;
    float: left;

    .top-left-bar.el-menu {
      display: inline-flex;
      border-bottom: 0px;
      vertical-align: middle;

      .el-menu-item, .el-submenu__title {
        height: 40px !important;
        line-height: 40px !important;
        margin: 5px 2px;
        padding: 0px 12px;
        border-radius: 5px;

        &:hover {
          background-color: #{$subMenuHover} !important;
        }

        &:active {
          background-color: #{$subMenuHover} !important;
        }

        .svg-icon {
          margin-right: 10px;
        }
      }

      .is-active {
        background-color: #{$subMenuActive} !important;
        border-bottom: 0px;
      }
    }
  }

  // 顶部用户中心菜单
  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }
  }
}
</style>
