<template>
  <div :class="classObj" class="app-wrapper">
    <!--手机端显示蒙版-->
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <!--左侧菜单-->
    <sidebar class="sidebar-container"/>
    <!--右侧-->
    <div :class="{hasTagsView:needTagsView}" class="main-container">
      <!--顶部-->
      <div :class="{'fixed-header':fixedHeader}">
        <!--顶部导航栏-->
        <navbar/>
        <!--标签-->
        <tags-view v-if="needTagsView"/>
      </div>
      <!--主区域-->
      <app-main/>
      <!--右侧面板-->
      <right-panel v-if="showSettings">
        <settings/>
      </right-panel>
    </div>
    <user-edit-password :is-show="isDefaultPassword" :info="editPasswordInfo"></user-edit-password>
  </div>
</template>

<script>
import RightPanel from '@/components/RightPanel'
import {AppMain, Navbar, Settings, Sidebar, TagsView} from './components'
import ResizeMixin from './mixin/ResizeHandler'
import {mapState} from 'vuex'
import Cookies from "js-cookie";
import UserEditPassword from "@/views/system/user/UserEditPassword";

export default {
  name: 'Layout',
  components: {
    UserEditPassword,
    AppMain,
    Navbar,
    RightPanel,
    Settings,
    Sidebar,
    TagsView
  },
  data() {
    return {
      // 是否默认密码
      editPassword: false,
      editPasswordInfo: '',
    }
  },
  mixins: [ResizeMixin],
  mounted() {
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
      fixedHeader: state => state.settings.fixedHeader
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
    },
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
