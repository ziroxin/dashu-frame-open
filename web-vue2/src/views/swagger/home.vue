<template>
  <div class="home" :style="{height: ($windowHeight-45) + 'px'}">
    <div class="title" @click="back">
      返回后台首页
    </div>
    <iframe :src="htmlPageURL" frameborder="0" style="overflow:hidden;"
            width="100%" height="100%"></iframe>
  </div>
</template>
<script>
export default {
  data() {
    return {
      htmlPageURL: '',
      bServer: this.$baseServer
    }
  },
  created() {
    if (this.bServer.indexOf('http') >= 0) {
      this.htmlPageURL = 'swagger/index.html#/home?burl=' + this.bServer + '/'
    } else {
      this.htmlPageURL = 'swagger/index.html#/home?burl=' + location.origin + this.bServer + '/'
    }
  },
  methods: {
    back() {
      this.$router.push({path: '/'})
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
    top: 10px;
    right: 300px;
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