<template>
  <div>
    <h1>支付宝回调</h1>
    <div v-if="isSuccess==='success'" class="pay-success">支付成功！</div>
    <div v-else class="pay-fail">支付失败！</div>
    <el-button @click="$router.replace('/demo/trade')">返回</el-button>
  </div>
</template>
<script>
export default {
  name: "AlipayReturn",
  data() {
    return {
      isSuccess: this.$route.query.isSuccess,
    }
  }
}
</script>
<style scoped>
.pay-success {
  color: green;
  font-size: 20px;
}

.pay-fail {
  color: red;
  font-size: 20px;
}
</style>
