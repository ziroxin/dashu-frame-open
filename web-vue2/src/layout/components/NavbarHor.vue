<template>
  <div class="navbar">
    <!-- logo -->
    <div class="hor-logo-div">
      <router-link key="collapse" class="hor-logo-link" to="/">
        <img v-if="logo" :src="logo" class="hor-logo">
        <h1 class="hor-title">{{ settings.title }} </h1>
      </router-link>
    </div>
    <!-- 顶部菜单 -->
    <topbar />
    <!-- 头像菜单 -->
    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <!--PC菜单-->
        <search id="header-search" class="right-menu-item" />
        <error-log class="errLog-container right-menu-item hover-effect" />
        <screenfull id="screenfull" class="right-menu-item hover-effect" />
        <!--<el-tooltip content="全局文字大小配置" effect="dark" placement="bottom">
                  <size-select id="size-select" class="right-menu-item hover-effect"/>
                </el-tooltip>-->
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img v-if="avatar!=null" :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">
          <img v-else src="@/assets/images/avatar.png" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
            <el-dropdown-item>
              <i class="el-icon-document" />
              文档
            </el-dropdown-item>
          </a>
          <a @click="editPassword=true">
            <el-dropdown-item>
              <i class="el-icon-key" />
              修改密码
            </el-dropdown-item>
          </a>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">
              <svg-icon icon-class="exit" />
              退出
            </span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!-- 修改密码弹窗 -->
    <user-edit-password v-model="editPassword" :show-close-btn="true" />
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import Hamburger from '@/components/Hamburger'
import ErrorLog from '@/components/ErrorLog'
import Screenfull from '@/components/Screenfull'
// import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import UserEditPassword from '@/views/system/user/UserEditPassword';
import variables from '@/styles/variables.scss';
import Topbar from './Topbar/index'

export default {
  components: {
    UserEditPassword,
    Hamburger,
    ErrorLog,
    Screenfull,
    // SizeSelect,
    Search,
    Topbar
  },
  data() {
    return {
      // 个人用户修改密码
      editPassword: false,
      logo: 'https://wpimg.wallstcn.com/69a1c46c-eb1c-4b46-8bd4-e9e686ef5251.png'
    }
  },
  computed: {
    ...mapGetters([
      'permission_routes',
      'sidebar',
      'avatar',
      'device',
      'settings'
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
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
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

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
