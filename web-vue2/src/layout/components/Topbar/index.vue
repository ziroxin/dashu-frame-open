<template>
  <el-menu :style="{'--theme-color': themeColor}"
           class="top-bar"
           :default-active="activeMenu"
           :background-color="variables.menuBg"
           :text-color="variables.menuText"
           :active-text-color="themeColor"
           :unique-opened="false"
           :collapse-transition="false"
           mode="horizontal"
           menu-trigger="hover"
  >
    <topbar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path"/>
  </el-menu>
</template>

<script>
import {mapGetters} from 'vuex'
import variables from '@/styles/variables.scss'
import TopbarItem from './TopbarItem'

export default {
  components: {TopbarItem},
  computed: {
    ...mapGetters([
      'permission_routes',
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
    variables() {
      return variables
    },
    themeColor() {
      return this.$store.state.settings.theme
    },
  }
}
</script>
<style lang="scss" scoped>
.top-bar {
  margin-left: 20px;
  display: inline-block;
  width: calc(100% - 350px);

  .hor-menu {
    float: left;

    /*--图标样式--*/
    ::v-deep .el-submenu__icon-arrow {
      position: relative;
      top: unset;
      right: unset;
      margin-top: unset;
      margin-left: 5px;
      font-weight: bold !important;
    }

    ::v-deep .is-active > .el-submenu__title > .el-submenu__icon-arrow {
      color: var(--theme-color) !important;
    }

    ::v-deep .is-active, ::v-deep .is-opened {
      margin-bottom: -1px !important;
      border-bottom: 2px solid var(--theme-color) !important;
    }
  }
}
</style>
