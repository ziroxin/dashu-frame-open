<template>
  <div class="errPage-container">
    <ErrorA />
    <ErrorB />
    <h3>请点击右上角的bug图标</h3>
    <aside>
      <a target="_blank" class="link-type" href="https://panjiachen.gitee.io/vue-element-admin-site/zh/guide/advanced/error.html#%E9%A1%B5%E9%9D%A2">
        错误处理说明文档
      </a>
    </aside>
    <a href="#">
      <img src="https://wpimg.wallstcn.com/360e4842-4db5-42d0-b078-f9a84a825546.gif">
    </a>
  </div>
</template>

<script>
import ErrorA from './components/ErrorTestA'
import ErrorB from './components/ErrorTestB'

export default {
  name: 'ErrorLog',
  components: { ErrorA, ErrorB }
}
</script>

<style scoped>
  .errPage-container {
    padding: 30px;
  }
</style>
