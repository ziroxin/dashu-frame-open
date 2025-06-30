<template>
  <div class="home" :style="{height: ($windowHeight-45) + 'px'}">
    <div class="title" @click="back">
      < 返回后台
    </div>
    <iframe :src="htmlPageURL" frameborder="0" style="overflow:hidden;"
            width="100%" height="100%"></iframe>
  </div>
</template>
<script>
import {getLastedRoutes} from '@/utils/lasted-routes'

export default {
  data() {
    return {
      htmlPageURL: 'form-generator/index.html',
      backRoute: '/generator'
    }
  },
  created() {
    if (this.$route.query.openType === 'new') {
      this.htmlPageURL = 'form-generator/index.html#/?openType=new'
    }
    if (this.$route.query.fid) {
      this.htmlPageURL = 'form-generator/index.html#/?fid=' + this.$route.query.fid
    }
    // 处理返回的路由
    const lastedRoutes = getLastedRoutes()
    if (lastedRoutes && lastedRoutes.length > 0) {
      this.backRoute = lastedRoutes.find(item => !item.startsWith(this.$route.path)) || '/generator'
    }
  },
  methods: {
    back() {
      this.$router.push({path: this.backRoute})
    }
  }
}
</script>
<style lang="scss" scoped>
.home {
  overflow: hidden;
  background: #FFFFFF;

  .title {
    position: absolute;
    bottom: 2%;
    left: 50px;
    width: 140px;
    height: 40px;
    line-height: 40px;
    text-align: center;
    font-size: 16px;
    background-color: #FE4C4C;
    color: #FFFFFF;
    border-radius: 5px;

    &:hover {
      cursor: pointer;
      background-color: #FFFFFF;
      color: #FE4C4C;
      border: 1px solid #FE4C4C;
    }
  }
}
</style>