<template>
  <section :class="layout==='vertical'?'app-main':'app-main-hor'">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view :key="key"/>
      </keep-alive>
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
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
.app-main {
  min-height: calc(100vh - 65px);
  width: 100%;
  position: relative;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

/*--开启多标签--*/
.hasTagsView {
  .app-main {
    min-height: calc(100vh - 106px);
  }

  .fixed-header + .app-main {
    /*--固定头部--*/
    padding-top: 91px;
  }
}

.app-main-hor {
  min-height: calc(100vh - 115px);
  width: 100%;
  position: relative;

  .app-container {
    margin: 55px 15px 15px 15px !important;
  }
}

.fixed-header-hor + .app-main-hor {
  padding-top: 57px;
}

/*--开启多标签--*/
.hasTagsViewHor {
  .app-main-hor {
    min-height: calc(100vh - 156px);
  }

  .fixed-header-hor + .app-main-hor {
    /*--固定头部--*/
    padding-top: 98px;
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
