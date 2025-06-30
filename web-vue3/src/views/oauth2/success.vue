<template>
  <div class="success-panel">
    <h1>统一认证中心 · 授权成功</h1>
    <div class="success-info">统一认证成功，正在跳转，请稍等...</div>
  </div>
</template>
<script>
import {setToken, setTokenValidTime} from '@/utils/auth';

export default {
  name: 'oauth2Success',
  created() {
    const params = {loginId: this.$route.query.loginId}
    this.$request({url: '/oauth2/client/login/check', method: 'get', params}).then((response) => {
      const {data} = response
      let errArr = data.successMsg.split('|');
      if (errArr && errArr.length === 2 && errArr[0] === 'error') {
        this.$router.push({path: '/oauth2/error', query: {err: errArr[1]}})
      } else {
        // 写入登录状态（同/store/modules/user.js:login）
        setToken(data.accessToken, new Date(data.accessTokenValidTime))
        setTokenValidTime(new Date(data.accessTokenValidTime))
        this.$store.commit('user/SET_TOKEN', data.accessToken)
        sessionStorage.setItem(this.$storageKeys.isDefaultPassword, data.defaultPassword)
        sessionStorage.setItem(this.$storageKeys.isInvalidPassword, data.invalidPassword)
        // 跳转
        this.$router.push({path: this.redirect || '/', query: this.otherQuery})
      }
    })
  }
}
</script>
<style scoped lang="scss">
.success-panel {
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

  .success-info, h2 {
    width: 100%;
    text-align: center;
  }

  .success-info {
    width: 50%;
    line-height: 40px;
    border-radius: 10px;
    background: #2C7EEA;
    font-size: 18px;
    color: #ffffff;
    margin-top: 22px;
  }
}
</style>