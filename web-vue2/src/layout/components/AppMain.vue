<template>
  <section :class="layout==='topMenu'?'app-main-hor':layout==='topLeftMenu'?'app-main-all':'app-main'">
    <!-- 面包屑 -->
    <breadcrumb v-if="layout==='topLeftMenu'" id="breadcrumb-container" class="breadcrumb-container"/>
    <!--主内容区-->
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view :key="key"/>
      </keep-alive>
    </transition>
  </section>
</template>

<script>
import Breadcrumb from "@/components/Breadcrumb/index.vue";

export default {
  name: 'AppMain',
  components: {Breadcrumb},
  props: ['layout'],
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
// ========================左侧菜单模式（默认）==============================
// if 不开启多标签
.app-main {
  min-height: calc(100vh - 65px);
  width: 100%;
  position: relative;
}

.fixed-header + .app-main {
  padding-top: 50px; // 固定头部
}

// else 开启多标签
.hasTagsView {
  .app-main {
    min-height: calc(100vh - 106px);
  }

  .fixed-header + .app-main {
    padding-top: 91px; // 固定头部
  }
}
</style>

<style lang="scss" scoped>
// ========================顶部菜单模式==============================
// if 不开启多标签
.app-main-hor {
  min-height: calc(100vh - 115px);
  width: 100%;
  position: relative;

  .app-container {
    margin-top: 55px !important;// 面包屑单独一行，占位因此修改margin
  }
}

.fixed-header-hor + .app-main-hor {
  padding-top: 57px; // 固定头部
}

// else 开启多标签
.hasTagsViewHor {
  .app-main-hor {
    min-height: calc(100vh - 156px);
  }

  .fixed-header-hor + .app-main-hor {
    padding-top: 98px; // 固定头部
  }
}
</style>

<style lang="scss" scoped>
// ========================综合模式==============================
// if 不开启多标签
.app-main-all {
  min-height: calc(100vh - 65px);
  width: 100%;
  position: relative;

  .breadcrumb-container {
    background-color: #fff;
    width: calc(100%);
    border-top: solid 1px #e6e6e6;
    border-bottom: solid 1px #e6e6e6;
    height: 40px !important;
    line-height: 40px !important;
    padding-left: 20px !important;
    margin-left: 0px !important;
  }
}

.fixed-header-all + .app-main-all {
  padding-top: 50px; // 固定头部
}

// else 开启多标签
.hasTagsViewAll {
  .app-main-all {
    min-height: calc(100vh - 106px);
  }

  .fixed-header-all + .app-main-all {
    padding-top: 91px; // 固定头部
  }
}
</style>

<style lang="scss">
// 修复el-dialog弹窗样式bug
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
