<template>
  <div class="bind-panel">
    <h1>统一认证中心 · 绑定用户</h1>
    <div class="bind-info">首次登录，请先绑定您的用户！</div>
    <div class="bind-form">
      <el-form ref="loginForm" class="login-form"
               :model="loginForm" :rules="loginRules">
        <el-form-item prop="username">
          <el-input ref="username" v-model="loginForm.userName" prefix-icon="el-icon-user"
                    placeholder="请输入用户名" name="userName" autocomplete="off"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input ref="password" v-model="loginForm.password" prefix-icon="el-icon-lock"
                    show-password placeholder="请输入密码" name="password" autocomplete="off"/>
        </el-form-item>

        <el-form-item prop="yzm">
          <el-row :span="24">
            <el-col :span="14">
              <el-input ref="yzm" v-model="loginForm.yzm" class="yzmInput" prefix-icon="el-icon-key"
                        placeholder="请输入验证码" name="yzm" autocomplete="off"/>
            </el-col>
            <el-col :span="10">
              <img class="yzmImg" :src="loginForm.codeBaseImage" @click="loadCaptacha">
            </el-col>
          </el-row>
        </el-form-item>
        <el-button :loading="loading" type="primary" style="width: 300px;"
                   @click.native.prevent="loginUserBind">绑 定
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
import {encryptRSA} from "@/utils/jsencrypt-util";
import {setToken, setTokenValidTime} from "@/utils/auth";
import Cookies from 'js-cookie';

export default {
  data() {
    return {
      loginForm: {
        userName: '',
        password: '',
        codeBaseImage: '',
        yzm: '',
        loginId: this.$route.query.loginId
      },
      loginRules: {
        userName: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}],
        yzm: [{required: true, message: '请输入验证码', trigger: 'blur'}]
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false
    }
  },
  mounted() {
    this.loadCaptacha()
  },
  methods: {
    checkCapslock(e) {
      const {key} = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      this.passwordType = this.passwordType === 'password' ? '' : 'password'
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    loadCaptacha() {
      this.$request({url: '/captcha/get', method: 'get'})
          .then(response => {
            this.loginForm.codeUuid = response.data.codeUuid
            this.loginForm.codeBaseImage = response.data.codeBaseImage
          })
    },
    loginUserBind() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          let data = {...this.loginForm}
          data.codeBaseImage = null;
          data.isEncrypt = true
          data.userName = encryptRSA(this.loginForm.userName)
          data.password = encryptRSA(this.loginForm.password)
          this.$request({url: '/oauth2/client/login/userBind', method: 'post', data}).then(response => {
            const {data} = response
            let errArr = data.successMsg.split('|');
            if (errArr && errArr.length === 2 && errArr[0] === 'error') {
              // 绑定失败
              this.$router.push({path: '/oauth2/error', query: {err: errArr[1]}})
            } else {
              // 绑定成功，并登录成功！
              this.$message({type: 'success', message: '绑定成功！正在为您跳转，请稍等...'})
              // 写入登录状态（同/store/modules/user.js:login）
              setToken(data.accessToken, new Date(data.accessTokenValidTime))
              setTokenValidTime(new Date(data.accessTokenValidTime))
              this.$store.commit('user/SET_TOKEN', data.accessToken)
              Cookies.set('isDefaultPassword', data.defaultPassword)
              Cookies.set('isInvalidPassword', data.invalidPassword)
              // 跳转
              this.$router.push({path: this.redirect || '/', query: this.otherQuery})
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.bind-panel {
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

  .bind-info, h2 {
    width: 100%;
    text-align: center;
  }

  .bind-info {
    width: 60%;
    line-height: 40px;
    border-radius: 10px;
    background: #2C7EEA;
    font-size: 18px;
    color: #ffffff;
    margin-top: 22px;
  }

  .bind-form {
    width: 60%;
    margin: 30px auto;
    background-color: #FFFFFF;
    border-radius: 20px;

    .login-form {
      width: 300px;
      margin: 30px auto;
    }

    .yzmImg {
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      height: 35px;
      margin-left: 10px;
    }
  }
}
</style>