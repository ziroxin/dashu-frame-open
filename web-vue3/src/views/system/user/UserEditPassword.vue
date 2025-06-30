<!--
 * 用户修改密码
 * @Author: ziro
 * @Date: 2023/01/04 08:37:40
 -->
<template>
  <!--修改密码-->
  <el-dialog title="修改密码" :visible.sync="innerVisible" width="600px"
             :close-on-click-modal="false" :show-close="showCloseBtn"
             :close-on-press-escape="false">
    <div v-if="info.length>0" style="text-align: center;font-size: 1em;color: red;margin: -10px auto 20px auto;">
      {{ this.info }}
    </div>
    <div class="password-container">
      <el-form ref="editPassword" :model="temp"
               :rules="passwordRules" class="login-form">
        <el-tooltip v-model="capsTooltip1" content="大写已打开" placement="right" manual v-if="!isDefaultPassword">
          <el-form-item prop="oldPassword">
            <span class="svg-container"><svg-icon icon-class="password"/></span>
            <el-input :type="oldPasswordType" ref="oldPassword" name="oldPassword" v-model="temp.oldPassword"
                      placeholder="请输入原密码" tabindex="1" autocomplete="off"
                      @keyup.native="checkCapslock1" @blur="capsTooltip1=false"/>
            <span class="show-pwd" @click="showPwd('old')">
                <svg-icon :icon-class="oldPasswordType==='password'?'eye':'eye-open'"/>
              </span>
          </el-form-item>
        </el-tooltip>

        <el-tooltip v-model="capsTooltip2" content="大写已打开" placement="right" manual>
          <el-form-item prop="pwd1">
            <span class="svg-container"><svg-icon icon-class="password"/></span>
            <el-input :type="pwd1Type" ref="pwd1" name="pwd1" v-model="temp.pwd1"
                      placeholder="请输入新密码" tabindex="2" autocomplete="off"
                      @keyup.native="checkCapslock2" @blur="capsTooltip2=false"/>
            <span class="show-pwd" @click="showPwd('pwd1')">
                <svg-icon :icon-class="pwd1Type==='password'?'eye':'eye-open'"/>
              </span>
          </el-form-item>
        </el-tooltip>

        <el-tooltip v-model="capsTooltip3" content="大写已打开" placement="right" manual>
          <el-form-item prop="pwd2">
            <span class="svg-container"><svg-icon icon-class="password"/></span>
            <el-input :type="pwd2Type" ref="pwd2" name="pwd2" v-model="temp.pwd2"
                      placeholder="请再次输入新密码确认" tabindex="3" autocomplete="off"
                      @keyup.native="checkCapslock3" @blur="capsTooltip3=false"/>
            <span class="show-pwd" @click="showPwd('pwd2')">
              <svg-icon :icon-class="pwd2Type==='password'?'eye':'eye-open'"></svg-icon>
            </span>
          </el-form-item>
        </el-tooltip>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="savePassword">保存</el-button>
        <el-button @click="innerVisible=false" v-if="showCloseBtn">取消</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import request from '@/utils/request';
import myMixDialog from '@/utils/my-mix-dialog'

export default {
  name: 'UserEditPassword',
  mixins: [myMixDialog],
  props: {
    // 提示信息
    info: {type: String, default: ''},
    // 用户id（为空时，则默认修改当前登录用户的密码）
    userId: {type: String, default: ''},
    // 是否显示关闭按钮
    showCloseBtn: {type: Boolean, default: false},
    // 是否是默认密码（默认密码无需输入旧密码）
    isDefaultPassword: {type: Boolean, default: false}
  },
  data() {
    return {
      temp: {},
      oldPasswordType: 'password',
      pwd1Type: 'password',
      pwd2Type: 'password',
      capsTooltip1: false,
      capsTooltip2: false,
      capsTooltip3: false,
      passwordRules: {
        oldPassword: [{required: true, message: '旧密码不能为空', trigger: 'blur'}],
        pwd1: [{required: true, message: '新密码不能为空', trigger: 'blur'}],
        pwd2: [{required: true, message: '确认密码不能为空', trigger: 'blur'}]
      }
    }
  },
  methods: {
    showPwd(name) {
      if (name === 'old') {
        this.oldPasswordType = (this.oldPasswordType === 'password' ? '' : 'password')
        this.$nextTick(() => {
          this.$refs.oldPassword.focus()
        })
      } else if (name === 'pwd1') {
        this.pwd1Type = (this.pwd1Type === 'password' ? '' : 'password')
        this.$nextTick(() => {
          this.$refs.pwd1.focus()
        })
      } else if (name === 'pwd2') {
        this.pwd2Type = (this.pwd2Type === 'password' ? '' : 'password')
        this.$nextTick(() => {
          this.$refs.pwd2.focus()
        })
      }
    },
    checkCapslock1(e) {
      const {key} = e
      this.capsTooltip1 = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    checkCapslock2(e) {
      const {key} = e
      this.capsTooltip2 = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    checkCapslock3(e) {
      const {key} = e
      this.capsTooltip3 = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    // 保存密码
    savePassword() {
      this.$refs.editPassword.validate(valid => {
        if (valid) {
          if (this.temp.pwd1 === this.temp.pwd2) {
            if (this.temp.pwd1 === this.temp.oldPassword && !this.isDefaultPassword) {
              this.$message({type: 'error', message: '新密码不能和旧密码一样！'})
              return;
            }
            this.temp.userId = this.userId
            this.temp.password = this.temp.pwd1
            const data = {...this.temp, isDefaultPassword: this.isDefaultPassword}
            request({url: '/user/edit/password', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '密码修改成功！下次登录请使用新密码'})
              sessionStorage.setItem(this.$storageKeys.isDefaultPassword, false)
              sessionStorage.setItem(this.$storageKeys.isInvalidPassword, false)
              this.innerVisible = false
            })
          } else {
            this.$message({type: 'error', message: '两次输入的密码不一致！'})
          }
        }
      })
    }
  }
}
</script>
<style lang="scss">
/* reset element-ui css */
.password-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      height: 47px;
    }
  }

  .el-form-item {
    border: 1px solid #DCDFE6;
    background: #FFFFFF;
    border-radius: 5px;
    color: #606266;
  }
}
</style>
<style lang="scss" scoped>
.password-container {
  min-height: 100%;
  width: 100%;
  overflow: hidden;

  .svg-container {
    padding: 6px 5px 6px 15px;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    cursor: pointer;
    user-select: none;
  }
}
</style>
