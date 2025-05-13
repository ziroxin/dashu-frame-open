<template>
  <div class="navbar" ref="navbarTopLeft" :style="{'--theme-color': themeColor}">
    <!-- 显示隐藏侧边栏按钮 -->
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>
    <div class="left-menu">
      <!-- 综合布局-顶部菜单 -->
      <scroll-horizontal id="menu-container1" v-if="leftMenuWidth" :container-width="leftMenuWidth" :container-height="50">
        <el-menu class="top-left-bar"
                 :default-active="activeTopMenu.name"
                 :background-color="variables.menuBg"
                 :text-color="variables.menuText"
                 :active-text-color="themeColor"
                 :unique-opened="false"
                 :collapse-transition="false"
                 mode="horizontal"
                 menu-trigger="hover"
        >
          <template v-for="item in permission_routes" v-if="item.hidden!==undefined&&!item.hidden">
            <!-- 无子菜单（跳页） -->
            <template
                v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
              <a v-if="onlyOneChild.meta" @click="loadLeftMenu(item,true)">
                <el-menu-item :index="item.name">
                  <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)"
                        :title="onlyOneChild.meta.title" :permission-name="onlyOneChild.name"/>
                </el-menu-item>
              </a>
            </template>
            <!-- 有子菜单（加载左侧菜单） -->
            <el-menu-item v-else :index="item.name" @click="loadLeftMenu(item)">
              <template slot="title">
                <item :icon="item.meta.icon" :title="item.meta.title" :permission-name="item.name"/>
              </template>
            </el-menu-item>
          </template>
        </el-menu>
      </scroll-horizontal>
    </div>
    <!-- 顶部右侧菜单 -->
    <div class="right-menu">
      <message-link/>
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
import MessageLink from "@/layout/components/MessageLink.vue";

export default {
  components: {
    MessageLink,
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
      // 当前路由
      activeTopMenu: ''
    }
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
    variables() {
      return variables
    }
  },
  mounted() {
    this.leftMenuWidth = this.$refs.navbarTopLeft.offsetWidth - 230
    this.firstOrRefreshLoad()
  },
  methods: {
    // 首次加载或刷新页面
    firstOrRefreshLoad() {
      if (sessionStorage.hasOwnProperty('activeTopMenu')) {
        // 刷新
        this.activeTopMenu = JSON.parse(sessionStorage.getItem('activeTopMenu'))
        const item = this.activeTopMenu;
        const isRouter = this.hasOneShowingChild(item.children, item)
            && (!this.onlyOneChild.children || this.onlyOneChild.noShowingChildren)
            && !item.alwaysShow;
        this.loadLeftMenu(item, isRouter)
      } else {
        // 取当前顶级路由（首次加载，判断选中项）
        const itemArr = this.permission_routes.filter(item => {
          if (!item.hidden && item.children !== undefined) {
            if (this.$route.fullPath.indexOf(item.path) >= 0) {
              // 顶级菜单判断
              return this.$route.path === this.resolvePath(item.children[0].path, item.path)
            } else {
              // 子菜单判断
              return this.checkChildrenMenu(item);
            }
          }
          return false;
        });
        if (itemArr.length > 0) {
          // 加载选中项
          const item = itemArr[0]
          const isRouter = this.hasOneShowingChild(item.children, item)
              && (!this.onlyOneChild.children || this.onlyOneChild.noShowingChildren)
              && !item.alwaysShow;
          this.loadLeftMenu(item, isRouter)
        } else {
          // 未取到，加载第一项
          this.permission_routes.forEach(item => {
            if (!item.hidden) {
              const isRouter = this.hasOneShowingChild(item.children, item)
                  && (!this.onlyOneChild.children || this.onlyOneChild.noShowingChildren)
                  && !item.alwaysShow;
              this.loadLeftMenu(item, isRouter)
              return
            }
          })
        }
      }
    },
    // 判断子菜单是否满足条件
    checkChildrenMenu(item) {
      const c1Arr = item.children.filter(c => {
        if (!c.children) {
          return !c.hidden && this.$route.path === this.resolvePath(c.path, item.path)
        } else {
          // 三级或更多菜单递归判断
          return this.checkChildrenMenu(c)
        }
      })
      return c1Arr.length > 0
    },
    // 加载左侧菜单
    loadLeftMenu(item, isRouter) {
      if (isRouter) {
        const path = this.resolvePath('index', item.path)
        if (path !== this.$route.path) {
          this.$router.push(path) // 跳转路由
        }
        // 无子菜单，加载自己作为左侧菜单
        if (item.children) {
          this.$emit('menuChanged', [item])
        }
      } else {
        // 有子菜单，加载子菜单
        for (let i = 0; i < this.permission_routes.length; i++) {
          const menuItem = this.permission_routes[i]
          if (menuItem.name === item.name) {
            // 回调父组件，加载左侧菜单
            this.$emit('menuChanged', menuItem.children)
          }
        }
      }
      sessionStorage.setItem('activeTopMenu', JSON.stringify(item))
      this.activeTopMenu = item
    },
    hasOneShowingChild(children = [], parent) {
      // 子菜单数量
      const realChildren = children.filter(o => !o.hidden && o.path === 'index')
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
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    resolvePath(routePath, basePath) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(basePath)) {
        return basePath
      }
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
      vertical-align: middle;

      .el-menu-item, .el-submenu__title {
        height: 40px !important;
        line-height: 40px !important;
        margin: 5px 2px;
        padding: 0px 12px;
        border-radius: 5px;
        border-bottom: 0px;

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
