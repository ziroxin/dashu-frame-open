<template>
  <!--修改密码-->
  <el-dialog title="修改密码" v-model="innerVisible" width="600px" :show-close="showCloseBtn" append-to-body
             :close-on-click-modal="false" :close-on-press-escape="false" @close="closeDialog">
    <div v-if="info" class="text-center text-1em color-red m-[-10px_auto_20px_auto]">{{ info }}</div>
    <div class="m-10px">
      <el-form ref="editPassword" :model="temp" :rules="passwordRules">
        <el-form-item prop="oldPassword" v-if="!isDefaultPassword">
          <el-input v-model="temp.oldPassword" class="w-full!"
                    type="password" showPassword placeholder="请输入原密码"/>
        </el-form-item>
        <el-form-item prop="pwd1">
          <input-password v-model="temp.pwd1" class="w-full!" placeholder="请输入新密码"/>
        </el-form-item>
        <el-form-item prop="pwd2">
          <input-password v-model="temp.pwd2" class="w-full!" placeholder="请再次输入新密码确认"/>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <base-button type="primary" icon="el-icon-check" @click="savePassword">保存</base-button>
        <base-button v-if="showCloseBtn" @click="closeDialog" icon="el-icon-close">取消</base-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { InputPassword } from '@/components/InputPassword'
import request from '@/utils/request'

export default {
  name: 'UserEditPassword',
  components: {InputPassword},
  props: {
    modelValue: {type: Boolean, default: false},
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
      passwordRules: {
        oldPassword: [{required: true, message: '旧密码不能为空', trigger: 'blur'}],
        pwd1: [{required: true, message: '新密码不能为空', trigger: 'blur'}],
        pwd2: [{required: true, message: '确认密码不能为空', trigger: 'blur'}]
      },
      innerVisible: false
    }
  },
  mounted() {
    this.innerVisible = this.modelValue
  },
  methods: {
    closeDialog() {
      this.innerVisible = false
      this.$emit('update:modelValue', false)
    },
    // 保存密码
    savePassword() {
      this.$refs.editPassword.validate(valid => {
        if (valid) {
          if (this.temp.pwd1 === this.temp.pwd2) {
            if (this.temp.pwd1 === this.temp.oldPassword && !this.isDefaultPassword) {
              this.$message({type: 'error', message: '新密码不能和旧密码一样！'})
              return
            }
            this.temp.userId = this.userId
            this.temp.password = this.temp.pwd1
            const data = {...this.temp, isDefaultPassword: this.isDefaultPassword}
            request({url: '/user/edit/password', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '密码修改成功！下次登录请使用新密码'})
              sessionStorage.setItem(this.$storageKeys.s_isDefaultPassword, false)
              sessionStorage.setItem(this.$storageKeys.s_isInvalidPassword, false)
              this.closeDialog()
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
