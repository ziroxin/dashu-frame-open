<template>
  <div class="flex m-[var(--app-content-margin)]" v-loading="isLoading">
    <!-- 左侧个人信息 -->
    <el-card class="flex-[3] mr-15px">
      <template #header><span class="text-16px font-700">个人信息</span></template>
      <div class="flex items-center justify-center">
        <el-image :src="$baseServer+temp.avatar" class="w-[150px] h-[150px] rounded-full"
                  :preview-src-list="[$baseServer+temp.avatar]"/>
      </div>
      <el-divider/>
      <div class="flex justify-between items-center">
        <div>用户名：</div>
        <div>{{ temp.userName }}</div>
      </div>
      <el-divider/>
      <div class="flex justify-between items-center">
        <div>性别：</div>
        <div>{{ temp.sex === '0' ? '未知' : temp.sex === '1' ? '男' : '女' }}</div>
      </div>
      <el-divider/>
      <div class="flex justify-between items-center">
        <div>昵称：</div>
        <div>{{ temp.nickName }}</div>
      </div>
      <el-divider/>
      <div class="flex justify-between items-center">
        <div>简介：</div>
        <div>{{ temp.introduce }}</div>
      </div>
      <el-divider/>
      <div class="flex justify-between items-center">
        <div>姓名：</div>
        <div>{{ temp.name }}</div>
      </div>
      <el-divider/>
      <div class="flex justify-between items-center">
        <div>手机号：</div>
        <div>{{ temp.phone }}</div>
      </div>
      <el-divider/>
    </el-card>
    <!-- 右侧基本资料 -->
    <el-card class="flex-[7]">
      <template #header><span class="text-16px font-700">基本资料</span></template>
      <el-tabs v-model="activeTabName">
        <!-- 用户基本信息 -->
        <el-tab-pane name="user">
          <template #label>
            <div class="p-10px flex items-center justify-center">
              <my-icon icon="el-icon-user-solid" class="mr-5px"/>
              用户基本信息
            </div>
          </template>
          <el-form ref="userDataForm" :model="temp" :rules="rules"
                   label-width="80px" class="m-[20px_10px]">
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
              <div class="flex items-end">
                <image-avatar v-model="temp.avatar" name="avatar"/>
                <el-tag type="info" class="m-10px">提示：点击左侧头像图片可修改</el-tag>
              </div>
            </el-form-item>
            <el-form-item label="姓名：" prop="name">
              <el-input v-model="temp.name" placeholder="请输入姓名"/>
            </el-form-item>
            <el-form-item label="手机号：" prop="phone">
              <el-input v-model="temp.phone" placeholder="请输入手机号"/>
            </el-form-item>
          </el-form>
          <div class="m-[30px_10px] pl-80px">
            <base-button type="primary" @click="submitJudgment" icon="el-icon-edit">保存个人资料</base-button>
          </div>
        </el-tab-pane>
        <!-- 绑定Oauth2信息 -->
        <el-tab-pane label="绑定Oauth2信息" name="oauthBind">
          <div v-if="this.temp.oauthBind" class="w-500px text-center m-[50px_auto]">
            <span class="color-#00a226 mr-20px">已绑定</span>
            <base-button type="danger" icon="el-icon-delete" @click="unBindOauthUser">解绑</base-button>
          </div>
          <div v-else class="w-500px text-center color-#D7000F m-[50px_auto]">
            抱歉！您还没有绑定其他 “Oauth2平台” 账号！
          </div>
        </el-tab-pane>
        <!-- 绑定微信账号信息 -->
        <el-tab-pane label="绑定微信账号信息" name="wechatBind">
          <div v-if="this.temp.wechatBind" class="w-500px text-center m-[50px_auto]">
            <span class="color-#00a226 mr-20px">已绑定</span>
            <base-button type="danger" icon="el-icon-delete" @click="unBindWechat">解绑</base-button>
          </div>
          <div v-else class="w-500px text-center color-#D7000F m-[50px_auto]">未绑定</div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>
<script>
import ImageAvatar from '@/components/Upload/ImageAvatar.vue'
import request from '@/utils/request'
import { MyIcon } from '@/components/MyIcon'

export default {
  components: {MyIcon, ImageAvatar},
  data() {
    return {
      isLoading: false,
      temp: {},
      rules: {
        userName: [{required: true, message: '请填写用户名', trigger: 'blur'}],
        password: [{required: true, message: '请填写密码', trigger: 'blur'}],
        roleId: [{required: true, message: '请给用户选择角色', trigger: 'blur'}],
        phone: [{required: false, pattern: /^1[3456789]\d{9}$/, message: '手机号格式不正确'}]
      },
      activeTabName: 'user'
    }
  },
  created() {
    this.isLoading = true
    this.loadCurrentUser()
  },
  methods: {
    loadCurrentUser() {
      // 加载个人用户信息
      request({url: '/user/getCurrentUser', method: 'get'}).then((response) => {
        const {data} = response
        if (data) {
          this.temp = data
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
          request({url: '/user/saveCurrentUser', method: 'post', data: this.temp}).then((response) => {
            this.$message({type: 'success', message: '保存个人资料成功！'})
            this.loadCurrentUser()
            // 更新store中的用户资料
            this.$store.dispatch('user/getInfo')
          })
        }
      })
    },
    // 解绑
    unBindOauthUser() {
      this.$confirm('确定要解绑【Oauth2平台】账号吗?', '取消绑定确认', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.isLoading = true
        request({url: '/oauth2/client/login/userUnbind', method: 'get'}).then((response) => {
          this.$message({type: 'success', message: '解绑成功！'})
          this.loadCurrentUser()
        })
      })
    },
    unBindWechat() {
      this.$confirm('确定要解绑微信吗?', '取消绑定确认', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.isLoading = true
        request({url: '/applet/wechat2user/zUserWechat/userUnbind', method: 'get'}).then((response) => {
          this.$message({type: 'success', message: '解绑成功！'})
          this.loadCurrentUser()
        })
      })
    }
  }
}
</script>
<style scoped lang="less">
.app-container {
  .title {
    font-size: 20px;
    font-weight: bold;
  }
}
</style>