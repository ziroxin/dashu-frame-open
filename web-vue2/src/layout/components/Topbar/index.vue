<template>
  <el-menu class="top-bar"
           :default-active="activeMenu"
           :background-color="variables.menuBgHor"
           :text-color="variables.menuTextHor"
           :unique-opened="false"
           :active-text-color="variables.menuActiveTextHor"
           :collapse-transition="false"
           mode="horizontal"
           menu-trigger="hover"
  >
    <topbar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path" />
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
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    variables() {
      return variables
    }
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
  }
}
</style>
