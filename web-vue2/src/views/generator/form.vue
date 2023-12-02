<template>
  <div class="app-container">
    <iframe :src="htmlPageURL" frameborder="0" width="100%"
            :height="$windowHeight-150"></iframe>
  </div>
</template>
<script>
export default {
  data() {
    return {
      htmlPageURL: 'form-generator/index.html'
    }
  },
  created() {
    if (this.$route.query.openType === 'new') {
      this.htmlPageURL = 'form-generator/index.html#/?openType=new'
    }
    if (this.$route.query.fid) {
      this.htmlPageURL = 'form-generator/index.html#/?fid=' + this.$route.query.fid
    }
  }
}
</script>