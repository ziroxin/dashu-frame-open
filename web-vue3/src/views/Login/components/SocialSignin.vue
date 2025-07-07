<template>
  <div class="social-signup-container">
    <el-button style="margin-right: 50px;" type="primary" @click="oauth2HandleClick">
      Oauth2统一认证登录
    </el-button>
    <div class="sign-btn" @click="wechatHandleClick('wechat')">
      <span class="wx-svg-container"><svg-icon icon-class="wechat" class="icon"/></span>
      WeChat
    </div>
    <div class="sign-btn" @click="tencentHandleClick('tencent')">
      <span class="qq-svg-container"><svg-icon icon-class="qq" class="icon"/></span>
      QQ
    </div>
  </div>
</template>
<script>

export default {
  name: 'SocialSignin',
  methods: {
    wechatHandleClick() {
      alert('ok')
    },
    tencentHandleClick() {
      alert('ok')
    },
    oauth2HandleClick() {
      this.$request({
        url: '/oauth2/client/login/getOauthAuthorizationUrl', method: 'get', params: {state: 'myState'}
      }).then((response) => {
        const {data} = response
        location.href = data;
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.social-signup-container {
  margin: 20px 0;
  text-align: center;

  .sign-btn {
    display: inline-block;
    cursor: pointer;
  }

  .icon {
    color: #fff;
    font-size: 20px;
    margin-top: 6px;
  }

  .wx-svg-container,
  .qq-svg-container {
    display: inline-block;
    width: 40px;
    height: 40px;
    line-height: 40px;
    text-align: center;
    padding-top: 1px;
    border-radius: 4px;
    margin-bottom: 20px;
    margin-right: 5px;
  }

  .wx-svg-container {
    background-color: #24da70;
  }

  .qq-svg-container {
    background-color: #6BA2D6;
    margin-left: 50px;
  }
}
</style>
