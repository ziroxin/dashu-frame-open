<template>
  <div class="app-container documentation-container">
    <div class="mydocument">
      <h2 style="width: 100%;">本框架相关</h2>
      <a target="_blank" href="https://gitee.com/ziro/dashu-frame-open">
        Gitee仓库
      </a>
      <a target="_blank" href="http://docs.java119.cn/">
        中文文档
      </a>
      <a target="_blank" href="http://yanshi.java119.cn/">
        演示网站
      </a>
    </div>
    <el-divider></el-divider>
    <h2 style="width: 100%;margin-left: 20px;">使用技术栈相关文档</h2>
    <a class="document-btn" target="_blank" href="https://github.com/PanJiaChen/vue-element-admin/">
      vue-element-admin仓库Github
    </a>
    <a class="document-btn" target="_blank" href="https://panjiachen.gitee.io/vue-element-admin-site/zh/">
      vue-element-admin文档
    </a>
    <a class="document-btn" target="_blank" href="https://element.eleme.cn/#/zh-CN/component/installation">
      element-ui文档
    </a>
    <a class="document-btn" target="_blank" href="https://v2.cn.vuejs.org/">
      vue2官方文档
    </a>
    <a class="document-btn" target="_blank" href="https://www.runoob.com/vue2/vue-tutorial.html">
      vue2-菜鸟文档
    </a>

    <dropdown-menu class="document-btn" :items="articleList" title="其他文章"/>
  </div>
</template>

<script>
import DropdownMenu from '@/components/Share/DropdownMenu'

export default {
  name: 'Documentation',
  components: {DropdownMenu},
  data() {
    return {
      articleList: [
        {title: '基础篇', href: 'https://juejin.im/post/59097cd7a22b9d0065fb61d2'},
        {title: '登录权限篇', href: 'https://juejin.im/post/591aa14f570c35006961acac'},
        {title: '实战篇', href: 'https://juejin.im/post/593121aa0ce4630057f70d35'},
        {title: 'vue-admin-template 篇', href: 'https://juejin.im/post/595b4d776fb9a06bbe7dba56'},
        {title: 'v4.0 篇', href: 'https://juejin.im/post/5c92ff94f265da6128275a85'},
        {title: '自行封装 component', href: 'https://segmentfault.com/a/1190000009090836'},
        {title: '优雅的使用 icon', href: 'https://juejin.im/post/59bb864b5188257e7a427c09'},
        {title: 'webpack4（上）', href: 'https://juejin.im/post/59bb864b5188257e7a427c09'},
        {title: 'webpack4（下）', href: 'https://juejin.im/post/5b5d6d6f6fb9a04fea58aabc'}
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.documentation-container {
  margin: 50px;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;

  .document-btn {
    flex-shrink: 0;
    display: block;
    cursor: pointer;
    background: black;
    color: white;
    height: 60px;
    padding: 0 16px;
    margin: 16px;
    line-height: 60px;
    font-size: 20px;
    text-align: center;
    border-radius: 5px;
  }

  .mydocument {
    margin-bottom: 20px;
    margin-left: 20px;
    line-height: 40px;

    a {
      margin: 30px 30px 30px 0px;
      color: black;
      padding: 15px 30px;
      border: 1px solid black;
      border-radius: 5px;

      &:hover {
        color: #FFFFFF;
        background-color: black;
      }
    }
  }
}
</style>
