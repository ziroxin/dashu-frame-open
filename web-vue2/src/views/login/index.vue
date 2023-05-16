<template>
  <div class="loginBody" :style="'background-image: url(\''+loginBg[loginBgIndex]+'\');'">
    <main class="d-flex align-items-center min-vh-100 py-3 py-md-0">
      <div class="container">
        <div class="card login-card">
          <div class="row no-gutters">
            <div class="col-md-5">
              <img :src="loginBg[loginBgIndex]" alt="login" class="login-card-img">
              <div class="toggle-login-bg">
                <i class="el-icon-arrow-left" @click="toggleLoginBg(2)" />
                <i class="el-icon-arrow-right" @click="toggleLoginBg(1)" />
              </div>
            </div>
            <div class="col-md-7">
              <div class="card-body login-container">
                <div class="brand-wrapper">
                  <!--logo-->
                  <img :src="loginBg[loginBgIndex]" alt="logo" class="logo">
                </div>
                <!--标题-->
                <p class="login-card-description">{{ title }}</p>
                <!--登录表单-->
                <el-form ref="loginForm" :model="loginForm" :rules="loginRules"
                         autocomplete="on" label-position="left"
                >
                  <el-form-item prop="username">
                    <span class="svg-container"><svg-icon icon-class="user" /></span>
                    <el-input ref="username" v-model="loginForm.userName" placeholder="请输入用户名"
                              name="userName" type="text" tabindex="1" autocomplete="on"
                    />
                  </el-form-item>
                  <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
                    <el-form-item prop="password">
                      <span class="svg-container"><svg-icon icon-class="password" /></span>
                      <el-input :key="passwordType" ref="password" v-model="loginForm.password" :type="passwordType"
                                placeholder="请输入密码" name="password" tabindex="2" autocomplete="on"
                                @keyup.native="checkCapslock" @blur="capsTooltip = false"
                                @keyup.enter.native="handleLogin"
                      />
                      <span class="show-pwd" @click="showPwd">
                        <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
                      </span>
                    </el-form-item>
                  </el-tooltip>

                  <el-form-item prop="yzm">
                    <span class="svg-container"><svg-icon icon-class="example" /></span>
                    <el-input ref="yzm" v-model="loginForm.yzm" class="yzmInput" placeholder="请输入验证码"
                              name="yzm" type="text" tabindex="3" autocomplete="off"
                              @keyup.enter.native="handleLogin"
                    />
                    <img class="yzmImg" :src="loginForm.codeBaseImage" @click="loadCaptacha">
                  </el-form-item>

                  <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;"
                             @click.native.prevent="handleLogin"
                  >登 录
                  </el-button>
                </el-form>

                <a href="#!" class="forgot-password-link">忘记密码?</a>
                <a href="#!" class="forgot-password-link thirdparty-button" @click="showDialog=true">第三方登录</a>
                <p class="login-card-footer-text">还没有账号? <a href="#!" class="text-reset">立即注册</a></p>
                <nav class="login-card-footer-nav">
                  <a href="#!">Terms of use.</a>
                  <a href="#!">Privacy policy</a>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <el-dialog title="第三方登录" :visible.sync="showDialog"
               :append-to-body="true" top="30vh"
    >
      <social-sign />
    </el-dialog>
  </div>
</template>

<script>
import SocialSign from './components/SocialSignin'
import {mapState} from 'vuex'
import request from '@/utils/request';

export default {
  name: 'Login',
  computed: {
    ...mapState({
      title: state => state.settings.title
    })
  },
  components: {
    SocialSign
  },
  data() {
    return {
      loginBg: [
        require('@/assets/images/loginbg.jpg'),
        require('@/assets/images/loginbg1.jpg'),
        require('@/assets/images/loginbg2.jpg')
      ],
      loginBgIndex: 0,
      loginForm: {
        userName: 'admin',
        password: 'qwer@123',
        yzm: '',
        codeUuid: '',
        codeBaseImage: ''
      },
      loginRules: {
        userName: [{required: true, message: '用户名不能为空', trigger: 'blur'}],
        password: [{required: true, message: '密码不能为空', trigger: 'blur'}],
        yzm: [{required: true, message: '验证码不能为空', trigger: 'blur'}]
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {}
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  mounted() {
    if (this.loginForm.userName === '') {
      this.$refs.username.focus()
    } else if (this.loginForm.password === '') {
      this.$refs.password.focus()
    }
    // 加载验证码handleLogin
    this.loadCaptacha()
  },
  methods: {
    checkCapslock(e) {
      const {key} = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    toggleLoginBg(idx) {
      if (idx === 1) {
        this.loginBgIndex = this.loginBgIndex >= (this.loginBg.length - 1) ? 0 : this.loginBgIndex + 1
      } else {
        this.loginBgIndex = this.loginBgIndex === 0 ? this.loginBg.length - 1 : this.loginBgIndex - 1
      }
    },
    showPwd() {
      this.passwordType = this.passwordType === 'password' ? '' : 'password'
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({path: this.redirect || '/', query: this.otherQuery})
              this.loading = false
            })
            .catch(() => {
              console.log('login error!')
              this.loadCaptacha()
              this.loading = false
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    },
    //验证码
    loadCaptacha() {
      request({
        url: '/captcha/get', method: 'get'
      }).then((response) => {
        const {data} = response
        this.loginForm.codeUuid = data.codeUuid;
        this.loginForm.codeBaseImage = data.codeBaseImage;
      })
    }
  }
}
</script>
<style scoped>
@import '~@/assets/css/bootstrap.min.css';
@import '~@/assets/css/login.css';
</style>
<style lang="scss">
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      height: 47px;
      color: #495057;
    }
  }

  .el-form-item {
    border: 1px solid #d5dae2;
    border-radius: .25rem;
    color: #495057;
    text-align: left;

    &:hover {
      border-color: #80bdff;
      outline: 0;
      box-shadow: 0 0 0 .2rem rgba(0, 123, 255, .25)
    }
  }
}
</style>

<style lang="scss" scoped>
a:hover {
  text-decoration: underline;
}

.login-container {
  min-height: 100%;
  width: 100%;
  overflow: hidden;

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: #889aa4;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: #889aa4;
    cursor: pointer;
    user-select: none;
  }

  @media only screen and (max-width: 470px) {
    .thirdparty-button {
      display: none;
    }
  }

  .yzmInput {
    width: 40%;
  }

  .yzmImg {
    height: 39px;
    margin: 5px 0px;
    padding-left: 7px;
    float: right;
    cursor: pointer;
    border-left: 1px solid #d5dae2;
  }

  .thirdparty-button {
    color: #00afff;
    margin-left: 1.5em;
  }
}
</style>
