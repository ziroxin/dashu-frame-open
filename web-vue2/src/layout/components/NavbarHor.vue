<template>
  <div class="navbar" ref="navbar">
    <!-- logo -->
    <div class="hor-logo-div">
      <router-link v-if="showLogo" key="collapse" class="hor-logo-link" to="/">
        <img v-if="logo" :src="logo" class="hor-logo">
        <h1 class="hor-title">{{ settings.title }} </h1>
      </router-link>
    </div>
    <!-- 顶部菜单 -->
    <scroll-horizontal v-if="leftMenuWidth" :container-width="leftMenuWidth" :container-height="57"
                       style="float: left;margin:0px 30px;line-height: 17px;">
      <topbar/>
    </scroll-horizontal>
    <!-- 头像菜单 -->
    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <!--PC菜单-->
        <search id="header-search" class="right-menu-item"/>
        <error-log class="errLog-container right-menu-item hover-effect"/>
        <screenfull id="screenfull" class="right-menu-item hover-effect"/>
      </template>

      <message-link/>
      <header-user-setting/>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import ErrorLog from '@/components/ErrorLog'
import Screenfull from '@/components/Screenfull'
// import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import variables from '@/styles/variables.scss';
import Topbar from './Topbar/index'
import HeaderUserSetting from "@/layout/components/HeaderUserSetting";
import Logo from "@/layout/components/Sidebar/Logo.vue";
import ScrollHorizontal from "@/components/ScrollHorizontal/index.vue";
import MessageLink from "@/layout/components/MessageLink.vue";

export default {
  components: {
    MessageLink,
    ScrollHorizontal,
    Logo,
    HeaderUserSetting,
    ErrorLog,
    Screenfull,
    // SizeSelect,
    Search,
    Topbar
  },
  data() {
    return {
      // logo默认图片
      logo: require('@/assets/images/logo.png'),
      // 顶部左侧菜单宽度
      leftMenuWidth: null,
    }
  },
  mounted() {
    this.leftMenuWidth = this.showLogo ? this.$refs.navbar.offsetWidth - 440 : this.$refs.navbar.offsetWidth - 270
  },
  computed: {
    ...mapGetters([
      'permission_routes',
      'sidebar',
      'device',
      'settings'
    ]),
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
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
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
  }
}
</script>

<style lang="scss" scoped>
.el-menu.el-menu--horizontal {
  border-bottom: 0px !important;
}

.navbar {
  height: 57px;
  line-height: 57px;
  /*overflow: hidden;*/
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

  .hor-logo-div {
    float: left;

    .hor-logo-link {
      height: 100%;

      .hor-logo {
        width: 32px;
        height: 30px;
        vertical-align: middle;
        margin: 0px 12px 0px 20px;
      }

      .hor-title {
        display: inline-block;
        margin: 0;
        font-weight: 600;
        line-height: 50px;
        font-size: 14px;
        font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
        vertical-align: middle;
      }
    }
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 10px;
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
