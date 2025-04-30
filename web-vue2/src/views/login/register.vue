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
                <!--标题-->
                <p class="login-card-description">用户注册</p>
                <!--注册表单-->
                <el-form ref="regForm" :model="regForm" :rules="loginRules"
                         autocomplete="off" label-position="left">
                  <el-form-item prop="userName">
                    <el-input ref="userName" prefix-icon="el-icon-user" tabindex="1"
                              v-model="regForm.userName" placeholder="请输入用户名"
                              name="userName" type="text" autocomplete="off"/>
                  </el-form-item>
                  <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
                    <el-form-item prop="password">
                      <el-input ref="password" prefix-icon="el-icon-lock" tabindex="2"
                                v-model="regForm.password" type="password" show-password
                                placeholder="请输入密码" name="password" autocomplete="off"
                                @keyup.native="checkCapslock" @blur="capsTooltip = false"/>
                    </el-form-item>
                  </el-tooltip>
                  <el-tooltip v-model="capsTooltip" content="大写已打开" placement="right" manual>
                    <el-form-item prop="rePassword">
                      <el-input ref="rePassword" prefix-icon="el-icon-lock" tabindex="3"
                                v-model="regForm.rePassword" type="password" show-password
                                placeholder="请再次输入密码确认" name="rePassword" autocomplete="off"
                                @keyup.native="checkCapslock" @blur="capsTooltip = false"/>
                    </el-form-item>
                  </el-tooltip>
                  <el-form-item prop="yzm">
                    <el-input ref="yzm" prefix-icon="el-icon-help" tabindex="4"
                              v-model="regForm.yzm" class="yzmInput" placeholder="请输入验证码"
                              name="yzm" type="text" autocomplete="off"/>
                    <img class="yzmImg" :src="regForm.codeBaseImage" @click="loadCaptcha">
                  </el-form-item>
                  <!-- 选填项 -->
                  <el-collapse style="margin-bottom:20px;padding-bottom:0px!important;">
                    <el-collapse-item title="选填项" name="1">
                      <el-form-item prop="orgId">
                        <select-tree v-model="regForm.orgId" tabindex="5"
                                     empty-text="请选择" empty-value="-1" style="width:100%;"
                                     :props="{children: 'children', label: 'label'}" :data="orgList"/>
                      </el-form-item>
                      <el-form-item prop="sex"
                                    style="text-align:left;border:1px solid #00000020;border-radius:.25rem;">
                        <span style="color:#c0c4cc;margin: auto 15px auto 10px;">
                          <el-icon class="el-icon-male"></el-icon>
                          <span style="margin-left:6px;">性别</span>
                        </span>
                        <el-radio-group v-model="regForm.sex" tabindex="6">
                          <el-radio label="0">保密</el-radio>
                          <el-radio label="1">男</el-radio>
                          <el-radio label="2">女</el-radio>
                        </el-radio-group>
                      </el-form-item>
                      <el-form-item prop="nickName">
                        <el-input prefix-icon="el-icon-user-solid" type="text" tabindex="7"
                                  autocomplete="off" v-model="regForm.nickName" placeholder="请输入昵称"/>
                      </el-form-item>
                      <el-form-item prop="introduce">
                        <el-input type="textarea" autocomplete="off" show-word-limit tabindex="8"
                                  maxlength="50" v-model="regForm.introduce" placeholder="个人简介"/>
                      </el-form-item>
                      <el-form-item prop="name">
                        <el-input prefix-icon="el-icon-user" type="text" autocomplete="off" tabindex="9"
                                  v-model="regForm.name" placeholder="姓名"/>
                      </el-form-item>
                      <el-form-item prop="phone">
                        <el-input prefix-icon="el-icon-mobile-phone" type="text" tabindex="10"
                                  autocomplete="off" v-model="regForm.phone" placeholder="手机号"/>
                      </el-form-item>
                    </el-collapse-item>
                  </el-collapse>

                  <el-button :loading="loading" type="primary" style="width:100%;"
                             @click.native.prevent="handleRegister">
                    点击注册
                  </el-button>
                </el-form>
                <p class="login-card-footer-text">
                  <span style="color: #666;">注册即代表同意</span>
                  <a target="_blank" href="/protocol.html">《用户协议》</a>
                  <a target="_blank" href="/protocol.html">《隐私协议》</a>
                </p>
                <p class="login-card-footer-text">
                  <router-link to="login" class="text-reset">返回登录</router-link>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import {encryptRSA} from '@/utils/jsencrypt-util'
import {imageAdeskVertical} from "@/utils/image-data-util";
import Cookies from 'js-cookie';
import SelectTree from "@/components/SelectTree/index.vue";

export default {
  name: 'Login',
  components: {SelectTree},
  data() {
    return {
      loginBg: [
        require('@/assets/images/loginbg.jpg'),
        require('@/assets/images/loginbg1.jpg'),
        require('@/assets/images/loginbg2.jpg')
      ],
      loginBgIndex: 0,
      regForm: {
        userName: '',
        password: '',
        rePassword: '',
        sex: '0',
        yzm: '',
        codeUuid: '',
        codeBaseImage: ''
      },
      loginRules: {
        userName: [{required: true, message: '用户名不能为空', trigger: 'blur'}],
        password: [{required: true, message: '密码不能为空', trigger: 'blur'}],
        rePassword: [
          {required: true, message: '确认密码不能为空', trigger: 'blur'},
          {
            validator: (rule, value, callback) => {
              value !== this.regForm.password ? callback(new Error('两次输入密码不一致!')) : callback()
            }, trigger: 'blur'
          }
        ],
        yzm: [{required: true, message: '验证码不能为空', trigger: 'blur'}],
        phone: [{pattern: /^1(3|4|5|7|8|9)\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}]
      },
      capsTooltip: false,
      loading: false,
      intervalIndex: 0,
      autoRefresh: false,
      // 组织机构列表
      orgList: []
    }
  },
  mounted() {
    this.$refs.userName.focus()
    // 加载验证码
    this.loadCaptcha()
    // 开启定时刷新
    this.autoRefreshLoginBg()
    // 动态壁纸 - 远程图库
    this.loadRemoteWallpaper()
    // 加载组织机构
    this.loadOrgList()
  },
  destroyed() {
    clearInterval(this.intervalIndex)
  },
  methods: {
    loadOrgList() {
      this.$request({url: '/register/org/list', method: 'get'}).then((rep) => {
        this.orgList = rep.data;
      })
    },
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
    handleRegister() {
      this.$refs.regForm.validate(valid => {
        if (valid) {
          this.loading = true
          let data = {...this.regForm}
          // 加密传输设置为true，并对用户名密码加密（不设置或设置false，默认为不加密传输）
          data.isEncrypt = true
          data.userName = encryptRSA(this.regForm.userName)
          data.password = encryptRSA(this.regForm.password)
          this.$request({url: 'register/new', method: 'post', data}).then((rep) => {
            this.$message({type: 'success', message: '注册成功！'})
            this.$router.push({path: 'login'})
            this.loading = false
          }).catch((err) => {
            console.log(err)
            this.loading = false
            this.loadCaptcha()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    //验证码
    loadCaptcha() {
      this.$request({url: '/captcha/get', method: 'get'}).then((rep) => {
        this.regForm.codeUuid = rep.data.codeUuid;
        this.regForm.codeBaseImage = rep.data.codeBaseImage;
      })
    },
    // 动态壁纸
    loadRemoteWallpaper(forceFlush) {
      try {
        let skip = 0
        if (forceFlush) {
          // 强制刷新（换一批）
          Cookies.remove('loginBgData')
          skip = parseInt(Cookies.get('loginBgDataSkip')) + 1 || 0
        }
        let loginBgData = Cookies.get('loginBgData')
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
              Cookies.set('loginBgData', JSON.stringify(this.loginBg))
              Cookies.set('loginBgDataSkip', skip)
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

  ::v-deep .el-collapse-item__content {
    padding-bottom: 0px !important;
  }

  .yzmInput {
    width: 50%;
    margin-top: 2px;
    float: left;
  }

  .yzmImg {
    height: 39px;
    padding-left: 10px;
    float: right;
    cursor: pointer;
    border-left: 1px solid #d5dae2;
  }
}
</style>
