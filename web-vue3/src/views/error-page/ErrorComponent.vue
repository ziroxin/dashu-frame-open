<template>
  <div class="app-container">
    <div class="errinfo">
      错误提示：组件加载错误，请检查该路由的组件地址！
    </div>
  </div>
</template>
<script>
export default {
  name: 'ErrorComponent'
}
</script>
<style scoped lang="scss">
.app-container {
  .errinfo {
    text-align: center;
    font-size: 20px;
    color: #D7000F;
    margin: 50px auto;
  }
}
</style>