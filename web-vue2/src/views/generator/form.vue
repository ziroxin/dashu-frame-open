<template>
  <div class="app-container">
    <iframe :src="htmlPageURL" frameborder="0" width="100%"
            :height="$windowHeight-150"></iframe>
  </div>
</template>
<script>
import {mapGetters} from 'vuex'

export default {
  data() {
    return {
      htmlPageURL: 'form-generator/index.html',
    }
  },
  computed: {
    ...mapGetters(['sidebar'])
  },
  created() {
    if (this.sidebar.opened) {
      // 关闭左侧菜单
      this.$store.dispatch('app/toggleSideBar')
    }
    if (this.$route.query.openType === 'new') {
      this.htmlPageURL = 'form-generator/index.html#/?openType=new'
    }
    if (this.$route.query.fid) {
      this.htmlPageURL = 'form-generator/index.html#/?fid=' + this.$route.query.fid
    }
  },
  beforeDestroy() {
    if (!this.sidebar.opened) {
      // 恢复左侧菜单
      this.$store.dispatch('app/toggleSideBar')
    }
  }
}
</script>