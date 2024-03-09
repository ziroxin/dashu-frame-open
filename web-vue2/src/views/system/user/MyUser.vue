<template>
  <div class="app-container" v-loading="isLoading">
    <el-divider>
      <div class="title">
        <el-icon class="el-icon-user-solid"></el-icon>
        用户基本信息
      </div>
    </el-divider>
    <el-form ref="userDataForm" :model="temp" :rules="rules" label-position="right"
             label-width="100px" style="width: 500px;margin: 50px auto;"
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
    <div style="width:500px;margin:0px auto 30px auto;padding-left:100px">
      <el-button type="primary" @click="submitJudgment" icon="el-icon-edit">保存个人资料</el-button>
    </div>


    <!-- 绑定Oauth2用户信息 -->
    <div style="margin-top:60px;">
      <el-divider>
        <div class="title">
          <el-icon class="el-icon-link"></el-icon>
          绑定 Oauth2 信息
        </div>
      </el-divider>
      <div v-if="this.temp.bind" style="width: 500px;text-align: center;margin: 50px auto;">
        <span style="color: #00a226;margin-right: 20px;">已绑定</span>
        <el-button type="danger" icon="el-icon-delete" @click="unBindOauthUser">解绑</el-button>
      </div>
      <div v-else style="width: 500px;text-align: center;color: #D7000F;margin: 50px auto;">
        抱歉！您还没有绑定其他 “ Oauth2平台 ” 账号！
      </div>
    </div>


    <!-- 主题设置配置 -->
    <div v-if="!showSettings && defaultSettings.showSettings" style="text-align: center;margin-top:60px;">
      <el-divider>
        <div class="title">
          <el-icon class="el-icon-setting"></el-icon>
          主题设置
        </div>
      </el-divider>
      <el-button type="danger" icon="el-icon-setting" style="margin-top: 10px;"
                 @click="showSettings=true">
        显示主题设置
      </el-button>
    </div>
  </div>
</template>
<script>
import ImageAvatar from "@/components/Upload/ImageAvatar.vue";
import SelectTree from "@/components/SelectTree/index.vue";
import defaultSettings from '@/settings'

export default {
  components: {SelectTree, ImageAvatar},
  computed: {
    showSettings: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    }
  },
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
      defaultSettings: defaultSettings
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
    // 显示设置按钮
    updateSettingsStatus() {

    },
    // 解绑
    unBindOauthUser() {
      this.$confirm('确定要解绑【Oauth2平台】账号吗?', '取消绑定确认', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.isLoading = true
        this.$request({
          url: '/oauth2/client/login/userUnbind', method: 'get'
        }).then((response) => {
          this.$message({type: 'success', message: '用户解绑成功！'})
          this.loadCurrentUser()
          this.isEdit = false
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
.app-container {
  .title {
    font-size: 20px;
    font-weight: bold;
  }
}
</style>