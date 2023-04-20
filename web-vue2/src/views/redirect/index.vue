<!--
路由跳转中间页
使用方法 - 举例：
    跳转到'/news/index'页，带参数newsId
    this.$router.push('redirect/news/index?newsId=value', {query: {newsId: 'value'}})
-->
<script>
export default {
  created() {
    const {params, query} = this.$route
    console.log(params, query)
    const {path} = params
    this.$router.replace({path: '/' + path, query})
  },
  render: function (h) {
    // 避免警告消息
    return h()
  }
}
</script>
