<!--
  路由跳转带参数中间页（在router/index.js中配置）
    注意：不可删除!!!
        目前用在标签页和面包屑页面。

    使用方法-举例：
        跳转到 '/news/index' 页，并带参数（直接url带参数，或者使用query参数，作用完全相同）
        this.$router.push({path:'redirect/news/index?newsId1=value', query: {newsId2: 'value'}})
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
