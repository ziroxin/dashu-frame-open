<template>
  <div :class="{'has-logo':showLogo}" :style="{'--theme-color': theme}">
    <logo v-if="showLogo" :collapse="isCollapse" style="border-bottom: 1px solid #e6e6e6;"/>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :text-color="variables.menuText"
          :active-text-color="theme"
          :unique-opened="false"
          :collapse-transition="false"
          mode="vertical"
      >
        <sidebar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path"/>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import Logo from '@/layout/components/Sidebar/Logo'
import SidebarItem from '@/layout/components/Sidebar/SidebarItem'
import variables from '@/styles/variables.scss'

export default {
  name: 'TopSidebar',
  props: {
    permission_routes: {
      type: Array,
      required: true
    }
  },
  components: {SidebarItem, Logo},
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    activeMenu() {
      const route = this.$route
      const {meta, path} = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    layout() {
      return this.$store.state.settings.layout
    },
    theme() {
      return this.$store.state.settings.theme
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep .is-active.el-submenu {
  .el-submenu__title, .el-submenu__title > i {
    color: var(--theme-color) !important;
  }
}

::v-deep .el-submenu:not(.is-active) {
  .el-submenu__title, .el-submenu__title > i {
    color: #4e5969 !important;
  }
}
</style>
