<template>
  <div class="drawer-container">
    <div>
      <div class="drawer-item">
        <span>主题颜色</span>
        <div style="height: 30px;">
          <ul class="themeSelector">
            <el-tooltip effect="dark" :content="item.title" placement="top"
                        v-for="(item, index) in themeList" :key="index">
              <li class="themeS" @click="themeChange(item.color)" :style="'background-color:'+item.color">
                <i v-if="theme === item.color" class="el-icon-check"/>
              </li>
            </el-tooltip>
          </ul>
        </div>
      </div>
      <!--      <div class="drawer-item">
              <span>自定义</span>
              <theme-picker style="float: right;height: 26px;margin: -3px 8px 0 0;" @change="themeChange"/>
            </div>-->

      <el-divider></el-divider>

      <div class="drawer-item">
        <span>导航模式</span>
        <ul>
          <li :class="layout === 'leftMenu'?'active':''" @click="layout = 'leftMenu'">
            <svg-icon icon-class="left-menu"></svg-icon>
            <br/><span>左侧菜单</span>
          </li>
          <li :class="layout === 'topMenu'?'active':''" @click="layout = 'topMenu'">
            <svg-icon icon-class="top-menu"></svg-icon>
            <br/><span>顶部菜单</span>
          </li>
          <li :class="layout === 'topLeftMenu'?'active':''" @click="layout = 'topLeftMenu'">
            <svg-icon icon-class="top-left-menu"></svg-icon>
            <br/><span>综合布局</span>
          </li>
        </ul>
      </div>

      <el-divider></el-divider>

      <div class="drawer-item">
        <span>开启多标签</span>
        <el-switch v-model="tagsView" class="drawer-switch"/>
      </div>

      <div class="drawer-item">
        <span>固定头部</span>
        <el-switch v-model="fixedHeader" class="drawer-switch"/>
      </div>

      <div class="drawer-item">
        <span>显示 Logo</span>
        <el-switch v-model="sidebarLogo" class="drawer-switch"/>
      </div>

      <div class="drawer-item">
        <span>
          是否显示设置按钮
          <el-tooltip effect="dark" content="【设置】按钮隐藏后，可在【个人中心】重新打开显示" placement="top">
            <i class="el-icon-question" style="font-size: 20px;color: #f5222d;"></i>
          </el-tooltip>
        </span>
        <el-switch v-model="showSettings" class="drawer-switch"/>
      </div>

      <el-divider></el-divider>

      <div class="drawer-item" style="text-align: center;">
        <el-button type="primary" plain icon="el-icon-refresh" @click="resetMyTheme">重置默认配置</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import ThemePicker from '@/components/ThemePicker'
import defaultSettings from '@/settings'

export default {
  components: {ThemePicker},
  data() {
    return {
      themeList: [
        {title: '深空', color: '#4080FF'},
        {title: '枫叶', color: '#F5222D'},
        {title: '暖阳', color: '#FA541C'},
        {title: '柠檬', color: '#FAAD14'},
        {title: '霁青', color: '#13C2C2'},
        {title: '森绿', color: '#52C41A'},
        {title: '葡萄', color: '#722ED1'},
      ],
    }
  },
  computed: {
    fixedHeader: {
      get() {
        return this.$store.state.settings.fixedHeader
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'fixedHeader',
          value: val
        })
      }
    },
    tagsView: {
      get() {
        return this.$store.state.settings.tagsView
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'tagsView',
          value: val
        })
        this.refreshPage()
      }
    },
    layout: {
      get() {
        return this.$store.state.settings.layout
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'layout',
          value: val
        })
        this.refreshPage()
      }
    },
    sidebarLogo: {
      get() {
        return this.$store.state.settings.sidebarLogo
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'sidebarLogo',
          value: val
        })
      }
    },
    showSettings: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
        this.refreshPage()
      }
    },
    theme() {
      return this.$store.state.settings.theme
    }
  },
  methods: {
    themeChange(val) {
      this.$store.dispatch('settings/changeSetting', {
        key: 'theme',
        value: val
      })
      this.refreshPage()
    },
    refreshPage() {
      this.$loading({
        lock: true, spinner: 'el-icon-loading', background: 'rgba(0, 0, 0, 1)', fullscreen: true,
        text: '正在应用样式，请稍等...'
      });
      setTimeout(() => {
        window.location.reload()
      }, 1000)
    },
    resetMyTheme() {
      this.$confirm('确定要重置样式吗？', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', zIndex: 40001
      }).then(() => {
        // 重置
        for (const key in defaultSettings) {
          if (defaultSettings.hasOwnProperty(key)) {
            this.$store.dispatch('settings/changeSetting', {
              key: key, value: defaultSettings[key]
            })
          }
        }
        // 刷新页面
        this.refreshPage()
      })
    }
  }
}
</script>
<style>
.el-tooltip__popper {
  z-index: 99999 !important;
}
</style>
<style lang="scss" scoped>
.drawer-container {
  padding: 24px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;

  .drawer-title {
    margin-bottom: 12px;
    color: rgba(0, 0, 0, .85);
    font-size: 14px;
    line-height: 22px;
  }

  .drawer-item {
    color: rgba(0, 0, 0, .65);
    font-size: 14px;
    padding: 12px 0;

    .themeSelector {
      float: left;

      li.themeS {
        width: 26px;
        height: 26px;
        border-radius: 50%;
        color: #fff;
        float: left;
        margin: 5px 2px;
      }
    }

    ul {
      width: 100%;
      padding: 0px;

      li.active {
        border-radius: 8px;
        background-color: rgba(0, 0, 0, .05);
        color: #00afff;
      }

      li {
        display: inline-block;
        text-align: center;
        list-style: none;
        font-size: 10px;
        width: 31%;
        cursor: pointer;
        padding: 5px 0px;
        margin: 0 1%;

        .svg-icon {
          width: 45px;
          height: 45px;
        }

        &:hover {
          border-radius: 8px;
          background-color: rgba(0, 0, 0, .1);
          color: #00afff;
        }
      }
    }
  }

  .drawer-switch {
    float: right
  }
}
</style>
