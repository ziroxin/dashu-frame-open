<template>
  <div class="error-panel">
    <h1>统一认证中心 · 授权失败</h1>
    <div class="error-info">{{ errorInfo }}</div>
    <div style="width: 100%; text-align: center;margin:30px auto;">
      <el-button @click="$router.go(-1)" type="primary" icon="el-icon-back"
                 plain>返回上一页
      </el-button>
      <el-button @click="$router.push({path: '/login'})"
                 type="danger" icon="el-icon-s-home" plain>
        返回登录页
      </el-button>
    </div>
  </div>
</template>
<script>
export default {
  name: 'oauth2Error',
  data() {
    return {
      errorInfo: '统一认证出现异常，请重试或联系管理员。'
    }
  },
  created() {
    if (this.$route.query.err) {
      this.errorInfo = this.$route.query.err;
    }
  }
}
</script>
<style scoped lang="scss">
.error-panel {
  margin-top: -80px;
  height: 100%;
  overflow: hidden;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  align-content: center;

  h1 {
    width: 100%;
    height: 80px;
    border-bottom: 1px dashed #BFBFBF;
    text-align: center;
  }

  .error-info, h2 {
    width: 100%;
    text-align: center;
  }

  .error-info {
    width: 50%;
    line-height: 40px;
    border-radius: 10px;
    background: #b92d54;
    font-size: 18px;
    color: #ffffff;
    margin-top: 22px;
    padding: 10px;
    text-align: left;
  }
}
</style>