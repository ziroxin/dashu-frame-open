<template>
  <div class="flex justify-evenly items-center my-50px">
    <base-button class="mr-50px" type="primary" @click="oauth2HandleClick">Oauth2统一认证登录</base-button>
    <base-button class="mr-50px" color="#24da70" text-color="#fff" icon="wechat"
                 @click="wechatHandleClick">WeChat
    </base-button>
    <base-button class="mr-50px" color="#6BA2D6" text-color="#fff" icon="qq"
                 @click="tencentHandleClick">QQ
    </base-button>
  </div>
</template>
<script>
import request from '@/utils/request'

export default {
  name: 'SocialSignin',
  methods: {
    wechatHandleClick() {
      this.$message({type: 'success', message: '努力开发中...'})
    },
    tencentHandleClick() {
      this.$message({type: 'success', message: '努力开发中...'})
    },
    oauth2HandleClick() {
      request({
        url: '/oauth2/client/login/getOauthAuthorizationUrl', method: 'get', params: {state: 'myState'}
      }).then((response) => {
        const {data} = response
        location.href = data
      })
    }
  }
}
</script>
