<template>
  <div class="home">
    <div class="title" @click="back">&lt; 返回后台</div>
    <iframe :src="htmlPageURL" frameborder="0" width="100%" height="100%" class="overflow-hidden h-100vh"></iframe>
  </div>
</template>
<script>
import { getLastedRoutes } from '@/utils/lasted-routes'

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
<style lang="less" scoped>
.home {
  overflow: hidden;
  background: #FFFFFF;

  .title {
    position: absolute;
    bottom: 10px;
    left: 10px;
    text-align: center;
    font-size: 14px;
    padding: 10px 20px;
    opacity: 0.7;
    background-color: #FE4C4C;
    color: #FFFFFF;
    border-radius: 5px;

    &:hover {
      cursor: pointer;
      background-color: #FE4C4C;
      color: #FFFFFF;
      opacity: 1;
    }
  }
}
</style>