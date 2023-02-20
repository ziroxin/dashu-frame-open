<template>
  <!-- 横向布局 -->
  <div v-if="'horizontal' === layout" class="app-wrapper">
    <div :class="{hasTagsViewHor:needTagsView}">
      <!--顶部-->
      <div :class="{'fixed-header-hor':fixedHeader}">
        <!--顶部导航栏-->
        <navbar-hor />
        <!--标签-->
        <tags-view v-if="needTagsView" />
        <!-- 面包屑 -->
        <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />
      </div>
      <!--主区域-->
      <app-main />
    </div>
    <!--右侧面板-->
    <right-panel v-if="showSettings">
      <settings />
    </right-panel>
    <user-edit-password v-model="editPassword" :info="editPasswordInfo" />
  </div>
  <!-- 纵向布局 -->
  <div v-else :class="classObj" class="app-wrapper">
    <!--手机端显示蒙版-->
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <!--左侧菜单-->
    <sidebar class="sidebar-container" />
    <!--右侧-->
    <div :class="{hasTagsView:needTagsView}" class="main-container">
      <!--顶部-->
      <div :class="{'fixed-header':fixedHeader}">
        <!--顶部导航栏-->
        <navbar />
        <!--标签-->
        <tags-view v-if="needTagsView" />
      </div>
      <!--主区域-->
      <app-main />
      <!--右侧面板-->
      <right-panel v-if="showSettings">
        <settings />
      </right-panel>
    </div>
    <user-edit-password v-model="editPassword" :info="editPasswordInfo" />
  </div>
</template>

<script>
import RightPanel from '@/components/RightPanel'
import {AppMain, Navbar, Settings, Sidebar, TagsView} from './components'
import ResizeMixin from './mixin/ResizeHandler'
import {mapState} from 'vuex'
import Cookies from 'js-cookie';
import UserEditPassword from '@/views/system/user/UserEditPassword';
import NavbarHor from '@/layout/components/NavbarHor';
import Breadcrumb from '@/components/Breadcrumb'

export default {
  name: 'Layout',
  components: {
    NavbarHor,
    UserEditPassword,
    AppMain,
    Navbar,
    RightPanel,
    Settings,
    Sidebar,
    TagsView,
    Breadcrumb
  },
  mixins: [ResizeMixin],
  data() {
    return {
      // 是否默认密码
      editPassword: false,
      editPasswordInfo: ''
    }
  },
  mounted() {
    console.log(this.device)
    this.editPassword = false
    if (Cookies.get('isDefaultPassword') === 'true') {
      this.editPassword = true
      this.editPasswordInfo = '检测到您当前的密码，是系统默认密码，请及时修改！！！'
    }
    if (Cookies.get('isInvalidPassword') === 'true') {
      this.editPassword = true
      this.editPasswordInfo = '检测到您的密码已失效，请修改！！！'
    }
  },
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      showSettings: state => state.settings.showSettings,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader,
      layout: state => state.settings.layout
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', {withoutAnimation: false})
    }
  }
}
</script>
<style lang="scss" scoped>
@import "~@/styles/mixin.scss";
@import "~@/styles/variables.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$sideBarWidth});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px)
}

.mobile .fixed-header {
  width: 100%;
}
</style>

<style lang="scss" scoped>
.fixed-header-hor {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100%);
}

.breadcrumb-container {
  float: left;
  background-color: #fff;
  width: calc(100%);
  border-top: solid 1px #e6e6e6;
  border-bottom: solid 1px #e6e6e6;
  height: 40px !important;
  line-height: 40px !important;
  padding-left: 20px !important;
  margin-left: 0px !important;
}
</style>
