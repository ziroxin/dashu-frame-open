<template>
  <div class="navbar">
    <!-- 显示隐藏侧边栏按钮 -->
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>
    <!-- 面包屑 -->
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container"/>
    <!-- 顶部右侧菜单 -->
    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <!--PC菜单-->
        <el-tooltip content="查询菜单" placement="top">
          <search id="header-search" class="right-menu-item"/>
        </el-tooltip>
        <error-log class="errLog-container right-menu-item hover-effect"/>
        <el-tooltip content="全屏/取消全屏" placement="top">
          <screenfull id="screenfull" class="right-menu-item hover-effect"/>
        </el-tooltip>
        <!--点击打开消息中心路由-->
        <el-tooltip content="消息中心" placement="bottom">
          <router-link to="/system/message" class="right-menu-item hover-effect">
            <i style="font-size: 28px;vertical-align: middle;" class="el-icon-message"/>
          </router-link>
        </el-tooltip>
      </template>

      <header-user-setting/>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import ErrorLog from '@/components/ErrorLog'
import Screenfull from '@/components/Screenfull'
import Search from '@/components/HeaderSearch'
import HeaderUserSetting from '@/layout/components/HeaderUserSetting';

export default {
  components: {
    HeaderUserSetting,
    Breadcrumb,
    Hamburger,
    ErrorLog,
    Screenfull,
    Search
  },
  data() {
    return {
      // 个人用户修改密码
      editPassword: false
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'device'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    }
  }
}
</script>

<style lang="scss" scoped>
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

  .breadcrumb-container {
    float: left;
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
