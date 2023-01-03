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
    <!--修改默认密码-->
    <el-dialog title="修改默认密码" :visible.sync="isDefaultPassword"
               :close-on-click-modal="false" :show-close="false"
               :close-on-press-escape="false">
      <div class="login-container">
        <el-form ref="resetPassword" :model="temp" class="login-form">
          <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
            <el-form-item prop="oldPassword">
              <span class="svg-container"><svg-icon icon-class="password"/></span>
              <el-input :type="oldPasswordType" ref="oldPassword" name="oldPassword" v-model="temp.oldPassword"
                        placeholder="请输入原密码" tabindex="1" autocomplete="off"
                        @keyup.native="checkCapslock" @blur="capsTooltip=false"/>
              <span class="show-pwd" @click="showPwd('old')">
                <svg-icon :icon-class="oldPasswordType==='password'?'eye':'eye-open'"/>
              </span>
            </el-form-item>
          </el-tooltip>

          <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
            <el-form-item prop="pwd1">
              <span class="svg-container"><svg-icon icon-class="password"/></span>
              <el-input :type="pwd1Type" ref="pwd1" name="pwd1" v-model="temp.pwd1"
                        placeholder="请输入新密码" tabindex="2" autocomplete="off"
                        @keyup.native="checkCapslock" @blur="capsTooltip=false"/>
              <span class="show-pwd" @click="showPwd('pwd1')">
                <svg-icon :icon-class="pwd1Type==='password'?'eye':'eye-open'"/>
              </span>
            </el-form-item>
          </el-tooltip>

          <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
            <el-form-item prop="pwd2">
              <span class="svg-container"><svg-icon icon-class="password"/></span>
              <el-input :type="pwd2Type" ref="pwd2" name="pwd2" v-model="temp.pwd2"
                        placeholder="请再次输入新密码确认" tabindex="3" autocomplete="off"
                        @keyup.native="checkCapslock" @blur="capsTooltip=false"/>
              <span class="show-pwd" @click="showPwd('pwd2')">
                <svg-icon :icon-class="pwd2Type==='password'?'eye':'eye-open'"/>
              </span>
            </el-form-item>
          </el-tooltip>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary">保存</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import RightPanel from '@/components/RightPanel'
import {AppMain, Navbar, Settings, Sidebar, TagsView} from './components'
import ResizeMixin from './mixin/ResizeHandler'
import {mapState} from 'vuex'
import Cookies from "js-cookie";

export default {
  name: 'Layout',
  components: {
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
      isDefaultPassword: false,
      temp: {},
      oldPasswordType: 'password',
      pwd1Type: 'password',
      pwd2Type: 'password',
      capsTooltip: false,
    }
  },
  mixins: [ResizeMixin],
  mounted() {
    if (Cookies.get("isDefaultPassword")) {
      this.isDefaultPassword = true// 默认密码，弹窗修改密码
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
    showPwd(name) {
      if (name == "old") {
        this.oldPasswordType = (this.oldPasswordType === 'password' ? '' : 'password')
        this.$nextTick(() => {
          this.$refs.oldPassword.focus()
        })
      } else if (name = 'pwd1') {
        this.pwd1Type = (this.pwd1Type === 'password' ? '' : 'password')
        this.$nextTick(() => {
          this.$refs.pwd1.focus()
        })
      } else if (name = 'pwd2') {
        this.pwd2Type = (this.pwd2Type === 'password' ? '' : 'password')
        this.$nextTick(() => {
          this.$refs.pwd2.focus()
        })
      }
    },
    checkCapslock(e) {
      const {key} = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
  }
}
</script>

<style lang="scss">
/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      height: 47px;
    }
  }

  .el-form-item {
    border: 1px solid #DCDFE6;
    background: #FFFFFF;
    border-radius: 5px;
    color: #606266;
  }
}
</style>
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

.login-container {
  min-height: 100%;
  width: 100%;
  overflow: hidden;

  .svg-container {
    padding: 6px 5px 6px 15px;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    cursor: pointer;
    user-select: none;
  }
}
</style>
