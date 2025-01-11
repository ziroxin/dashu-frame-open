<template>
  <div class="loginBody" :style="'background-image: url(\''+loginBg[loginBgIndex]+'\');'">
    <main class="d-flex align-items-center min-vh-100 py-3 py-md-0">
      <div class="container">
        <div class="card login-card">
          <div class="row no-gutters">
            <div class="col-md-5">
              <img :src="loginBg[loginBgIndex]" @error="handleImageError"
                   alt="login" class="login-card-img">
              <div class="toggle-login-bg">
                <el-tooltip class="item" effect="dark" content="换一批" placement="top">
                  <i class="el-icon-refresh" @click="loadRemoteWallpaper(true)"/>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="上一张" placement="top">
                  <i class="el-icon-arrow-left" @click="toggleLoginBg(2)"/>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="下一张" placement="top">
                  <i class="el-icon-arrow-right" @click="toggleLoginBg(1)"/>
                </el-tooltip>
              </div>
            </div>
            <div class="col-md-7">
              <div class="card-body login-container">
                <!--logo-->
                <div class="brand-wrapper" v-if="logo">
                  <img :src="logo" alt="logo" class="logo">
                </div>
                <!--标题-->
                <p class="login-card-description">{{ title }}</p>
                <!--登录表单-->
                <el-form ref="loginForm" :model="loginForm" :rules="loginRules"
                         autocomplete="off" label-position="left"
                >
                  <el-form-item prop="userName">
                    <el-input ref="userName" prefix-icon="el-icon-user"
                              v-model="loginForm.userName" placeholder="请输入用户名"
                              name="userName" type="text" tabindex="1" autocomplete="off"
                    />
                  </el-form-item>
                  <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
                    <el-form-item prop="password">
                      <el-input ref="password" prefix-icon="el-icon-lock"
                                v-model="loginForm.password" type="password" show-password
                                placeholder="请输入密码" name="password" tabindex="2" autocomplete="off"
                                @keyup.native="checkCapslock" @blur="capsTooltip = false"
                                @keyup.enter.native="handleLogin"/>
                    </el-form-item>
                  </el-tooltip>

                  <el-form-item prop="yzm">
                    <el-input ref="yzm" prefix-icon="el-icon-help"
                              v-model="loginForm.yzm" class="yzmInput" placeholder="请输入验证码"
                              name="yzm" type="text" tabindex="3" autocomplete="off"
                              @keyup.enter.native="handleLogin"
                    />
                    <img class="yzmImg" :src="loginForm.codeBaseImage" @click="loadCaptcha">
                  </el-form-item>

                  <div class="remember-me">
                    <el-checkbox v-model="loginForm.rememberMe" label="记住密码">记住密码</el-checkbox>
                    <div><a href="#!" class="forgot-password-link">忘记密码？</a></div>
                  </div>

                  <el-button :loading="loading" type="primary" style="width:100%;"
                             @click.native.prevent="handleLogin">登 录
                  </el-button>
                </el-form>

                <p class="login-card-footer-text">
                  <span style="color: #666;">登录即代表同意</span>
                  <a target="_blank" href="/protocol.html">《用户协议》</a>
                  <a target="_blank" href="/protocol.html">《隐私协议》</a>
                </p>
                <p class="login-card-footer-text">
                  <template v-if="isRegisterOpen">
                    <span style="color: #666;">还没有账号？</span>
                    <router-link to="register" class="text-reset">立即注册</router-link>
                  </template>
                  <a class="forgot-password-link thirdparty-button" @click="showDialog=true">其他方式登录</a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <el-dialog title="第三方登录" :visible.sync="showDialog" :append-to-body="true" top="30vh">
      <social-sign/>
    </el-dialog>
  </div>
</template>

<script>
import SocialSign from './components/SocialSignin'
import {mapState} from 'vuex'
import request from '@/utils/request';
import {encryptRSA} from '@/utils/jsencrypt-util'
import {imageAdeskVertical} from "@/utils/image-data-util";
import Cookies from 'js-cookie';
import defaultSettings from '@/settings'

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
      logo: require('@/assets/images/logo.png'),
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
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {},
      intervalIndex: 0,
      autoRefresh: false,
      isRegisterOpen: false,// 是否允许注册
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
    if (localStorage.getItem('currentLoginUserData')) {
      const userData = JSON.parse(localStorage.getItem('currentLoginUserData'))
      this.loginForm.userName = userData.userName || ''
      this.loginForm.password = userData.password || ''
      this.loginForm.rememberMe = true
    }
    if (this.loginForm.userName === '') {
      this.$refs.userName.focus()
    } else if (this.loginForm.password === '') {
      this.$refs.password.focus()
    }
    // 加载验证码
    this.loadCaptcha()
    // 开启定时刷新
    this.autoRefreshLoginBg()
    // 动态壁纸 - 远程图库
    this.loadRemoteWallpaper()
    // 是否允许注册
    this.$request({url: '/register/config/reg', method: 'get'}).then((response) => {
      this.isRegisterOpen = response.data
    })
  },
  destroyed() {
    clearInterval(this.intervalIndex)
  },
  methods: {
    checkCapslock(e) {
      const {key} = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    toggleLoginBg(idx) {
      if (idx === 1) {
        // 下一张
        this.loginBgIndex = this.loginBgIndex >= (this.loginBg.length - 1) ? 0 : this.loginBgIndex + 1
      } else {
        // 上一张
        this.loginBgIndex = this.loginBgIndex === 0 ? this.loginBg.length - 1 : this.loginBgIndex - 1
      }
      if (this.autoRefresh) {
        this.autoRefreshLoginBg()
      }
    },
    autoRefreshLoginBg() {
      clearInterval(this.intervalIndex)
      this.intervalIndex = setInterval(() => {
        this.toggleLoginBg(1)
      }, 10000)
      this.autoRefresh = true
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
          let data = {...this.loginForm}
          // 加密传输设置为true，并对用户名密码加密（不设置或设置false，默认为不加密传输）
          data.isEncrypt = true
          data.userName = encryptRSA(this.loginForm.userName)
          data.password = encryptRSA(this.loginForm.password)
          this.$store.dispatch('user/login', data).then(() => {
            if (this.loginForm.rememberMe) {
              localStorage.setItem('currentLoginUserData', JSON.stringify({
                userName: this.loginForm.userName,
                password: this.loginForm.password
              }))
            } else {
              localStorage.removeItem('currentLoginUserData')
            }
            const routerMode = this.$router.mode;
            if (defaultSettings.showSettings) {
              // 登录成功，加载用户主题配置
              this.$request({url: '/userTheme/zUserTheme/getByUser', method: 'get'}).then((response) => {
                // 跳转
                if (routerMode === 'history') {
                  location.href = location.href.split('/login')[0] + (this.redirect || '/') +
                      (Object.keys(this.otherQuery).length === 0 ? '' : '?' + new URLSearchParams(this.otherQuery).toString())
                } else {
                  location.hash = (this.redirect || '/') +
                      (Object.keys(this.otherQuery).length === 0 ? '' : '?' + new URLSearchParams(this.otherQuery).toString())
                }
                this.loading = false
                // 刷新页面样式
                const {data} = response
                if (data) {
                  Cookies.set('settings', data, {expires: new Date('9999-12-31T23:59:59')})
                  if (routerMode !== 'history') location.reload()
                }
              })
            } else {
              // 主题设置已禁用，直接跳转
              if (routerMode === 'history') {
                location.href = location.href.split('/login')[0] + (this.redirect || '/') +
                    (Object.keys(this.otherQuery).length === 0 ? '' : '?' + new URLSearchParams(this.otherQuery).toString())
              } else {
                location.hash = (this.redirect || '/') +
                    (Object.keys(this.otherQuery).length === 0 ? '' : '?' + new URLSearchParams(this.otherQuery).toString())
              }
              this.loading = false
            }
          }).catch(() => {
            console.log('login error!')
            this.loadCaptcha()
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
    loadCaptcha() {
      request({url: '/captcha/get', method: 'get'}).then((response) => {
        const {data} = response
        this.loginForm.codeUuid = data.codeUuid;
        this.loginForm.codeBaseImage = data.codeBaseImage;
      })
    },
    // 动态壁纸
    loadRemoteWallpaper(forceFlush) {
      try {
        let skip = 0
        if (forceFlush) {
          // 强制刷新（换一批）
          localStorage.removeItem('loginBgData')
          skip = parseInt(localStorage.getItem('loginBgDataSkip')) + 1 || 0
        }
        let loginBgData = localStorage.getItem('loginBgData')
        if (loginBgData) {
          this.loginBg = JSON.parse(loginBgData)
        } else {
          // 动态加载背景图
          imageAdeskVertical(null, 'hot', 20, skip * 20).then(imgs => {
            if (imgs && imgs.length > 0) {
              this.loginBg = []
              for (let i = 0; i < imgs.length; i++) {
                this.loginBg.push(imgs[i].img)
              }
              localStorage.setItem('loginBgData', JSON.stringify(this.loginBg))
              localStorage.setItem('loginBgDataSkip', skip)
            }
          })
        }
      } catch (e) {
        console.log(e)
      }
    },
    handleImageError(event) {
      event.target.src = require('@/assets/images/loginbg.jpg')
    },
  }
}
</script>
<style scoped>
@import '~@/assets/css/bootstrap.min.css';
@import '~@/assets/css/login.css';
</style>
<style lang="scss" scoped>
a:hover {
  text-decoration: underline;
}

.login-container {
  min-height: 100%;
  width: 100%;
  overflow: hidden;

  ::v-deep .el-form-item__content > .el-input {
    display: inline-block;
    height: 47px !important;
    line-height: 47px !important;
    font-size: 16px;

    .el-input__inner {
      height: 47px !important;
      line-height: 47px !important;

    }
  }

  @media only screen and (max-width: 470px) {
    .thirdparty-button {
      display: none;
    }
  }

  .yzmInput {
    width: 50%;
    float: left;
  }

  .yzmImg {
    height: 40px;
    margin-top: 4px;
    padding-left: 8px;
    float: right;
    cursor: pointer;
    border-left: 1px solid #d5dae2;
  }

  .remember-me {
    margin: 0 5px 15px 0px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .thirdparty-button {
    color: #00afff;
    margin-left: 1.5em;
  }
}
</style>
