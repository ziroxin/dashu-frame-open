<template>
  <div class="app-container" v-loading="isLoading">
    <div style="text-align: center">
      <h2>个人信息</h2>
    </div>
    <el-divider></el-divider>
    <el-form ref="userDataForm" :model="temp" :rules="rules" label-position="right"
             label-width="100px" style="width: 500px;margin: auto;"
    >
      <el-form-item label="用户名：" prop="userName">
        <el-input v-model="temp.userName" placeholder="请输入用户名"/>
      </el-form-item>
      <el-form-item label="性别：" prop="sex">
        <el-radio v-model="temp.sex" label="0">未知</el-radio>
        <el-radio v-model="temp.sex" label="1">男</el-radio>
        <el-radio v-model="temp.sex" label="2">女</el-radio>
      </el-form-item>
      <el-form-item label="昵称：" prop="nickName">
        <el-input v-model="temp.nickName" placeholder="请输入昵称"/>
      </el-form-item>
      <el-form-item label="简介：" prop="introduce">
        <el-input v-model="temp.introduce" type="textarea" placeholder="请输入简介"/>
      </el-form-item>
      <el-form-item label="头像：" prop="avatar">
        <image-avatar v-model="temp.avatar" name="avatar"/>
      </el-form-item>
      <el-form-item label="姓名：" prop="name">
        <el-input v-model="temp.name" placeholder="请输入姓名"/>
      </el-form-item>
      <el-form-item label="手机号：" prop="phone">
        <el-input v-model="temp.phone" placeholder="请输入手机号"/>
      </el-form-item>
    </el-form>
    <el-divider></el-divider>
    <div style="text-align: center;">
      <el-button type="primary" @click="submitJudgment">保存个人资料</el-button>
    </div>
  </div>
</template>
<script>

import ImageAvatar from "@/components/Upload/ImageAvatar.vue";
import SelectTree from "@/components/SelectTree/index.vue";
import store from "@/store";

export default {
  components: {SelectTree, ImageAvatar},
  data() {
    return {
      isLoading: false,
      temp: {},
      rules: {
        userName: [{required: true, message: '请填写用户名', trigger: 'blur'}],
        password: [{required: true, message: '请填写密码', trigger: 'blur'}],
        roleId: [{required: true, message: '请给用户选择角色', trigger: 'blur'}],
        phone: [{
          required: false, trigger: 'blur', validator: (r, v, b) => {
            (v && !(/^(?:(?:\+|00)86)?1\d{10}$/.test(v))) ? b('手机号格式不正确') : b()
          }
        }]
      },
    }
  },
  created() {
    this.isLoading = true
    this.loadCurrentUser()
  },
  methods: {
    loadCurrentUser() {
      // 加载个人用户信息
      this.$request({url: '/user/getCurrentUser', method: 'get'})
          .then((response) => {
            const {data} = response
            if (data) {
              this.temp = data
              console.log(data)
              this.isLoading = false
            } else {
              this.$message({type: 'error', message: '获取当前用户信息失败！请刷新或重新登录重试'})
            }
          })
    },
    // 提交数据
    submitJudgment() {
      // 表单验证
      this.$refs['userDataForm'].validate((valid) => {
        if (valid) {
          this.$request({
            url: '/user/saveCurrentUser', method: 'post', data: this.temp
          }).then((response) => {
            this.$message({type: 'success', message: '保存个人资料成功！'})
            this.loadCurrentUser()
            // 更新store中的用户资料
            this.$store.dispatch('user/getInfo')
          })
        }
      })
    },
  }
}
</script>
<style scoped lang="scss">

</style>