<template>
  <el-form ref="regFormRef" :model="regForm" label-position="top" hide-required-asterisk v-loading="isLoading"
           size="large" class="dark:(border-1 border-[var(--el-border-color)] border-solid)">
    <el-row>
      <el-col :span="24">
        <el-form-item>
          <h2 class="text-2xl font-bold text-center w-[100%]" v-text="t('login.registerTitle')"></h2>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.userNameLabel')" prop="userName"
                      :rules="[{required: true, message: '用户名不能为空！'}]">
          <el-input v-model="regForm.userName" class="w-full" :placeholder="t('login.userNamePlaceholder')">
            <template #prefix>
              <my-icon icon="user"/>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.passwordLabel')" prop="password"
                      :rules="[{required: true, message: '密码不能为空！'}]">
          <el-input type="password" v-model="regForm.password" class="w-full"
                    show-password :placeholder="t('login.passwordPlaceholder')">
            <template #prefix>
              <my-icon icon="lock"/>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.rePasswordLabel')" prop="rePassword"
                      :rules="[{required: true, message: '确认密码不能为空！'},
                               {validator: (r, v, c) => {v!==regForm.password?c(new Error('两次输入密码不一致!')):c()}}]">
          <el-input type="password" v-model="regForm.rePassword" class="w-full"
                    show-password :placeholder="t('login.rePasswordPlaceholder')">
            <template #prefix>
              <my-icon icon="lock"/>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.yzmLabel')" prop="yzm" :rules="[{required: true, message: '验证码不能为空！'}]">
          <div class="flex justify-between items-center w-[100%]">
            <el-input ref="yzm" v-model="regForm.yzm" class="w-[50%]"
                      :placeholder="t('login.yzmPlaceholder')">
              <template #prefix>
                <my-icon icon="yzm" :size="20" color="#777"/>
              </template>
            </el-input>
            <img :src="regForm.codeBaseImage" @click="loadCaptcha"/>
          </div>
        </el-form-item>
      </el-col>

      <el-col :span="24">
        <!-- 选填项 -->
        <el-collapse class="mb-20px pb-0!">
          <el-collapse-item title="选填项" name="1">
            <el-form-item prop="orgId">
              <el-cascader v-model="regForm.orgId" placeholder="请选择组织机构" class="w-full"
                           :options="orgList" clearable filterable
                           :props="{value:'value',label:'label',checkStrictly:true,emitPath:false}">
                <template #prefix>
                  <my-icon icon="org"/>
                </template>
              </el-cascader>
            </el-form-item>
            <el-form-item prop="sex" class="text-left b-1px b-solid b-#00000020 b-rd-[0.25rem]">
              <div class="mx-10px flex items-center">
                <my-icon icon="el-icon-male"/>
                <span class="ml-6px">性别：</span>
              </div>
              <el-radio-group v-model="regForm.sex">
                <el-radio value="0">保密</el-radio>
                <el-radio value="1">男</el-radio>
                <el-radio value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="nickName">
              <el-input v-model="regForm.nickName" placeholder="请输入昵称">
                <template #prefix>
                  <my-icon icon="el-icon-user-solid"/>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="introduce">
              <el-input type="textarea" show-word-limit :rows="4"
                        maxlength="50" v-model="regForm.introduce" placeholder="个人简介"/>
            </el-form-item>
            <el-form-item prop="name">
              <el-input v-model="regForm.name" placeholder="姓名">
                <template #prefix>
                  <my-icon icon="el-icon-user"/>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="phone" :rules="[{pattern: /^1[3456789]\d{9}$/, message: '手机号格式不正确'}]">
              <el-input v-model="regForm.phone" placeholder="手机号">
                <template #prefix>
                  <my-icon icon="el-icon-mobile-phone"/>
                </template>
              </el-input>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </el-col>

      <el-col :span="24">
        <div class="flex justify-center items-center mb-10px">
          <el-checkbox v-model="regForm.agree" label="同意" size="large"/>
          <el-link target="_blank" type="primary" underline="always" class="mx-5px"
                   href="https://yanshi.java119.cn/protocol.html">用户协议
          </el-link>
          <el-link target="_blank" type="primary" underline="always" class="mx-5px"
                   href="https://yanshi.java119.cn/protocol.html">隐私协议
          </el-link>
        </div>
      </el-col>

      <el-col :span="24">
        <el-form-item class="w-[100%]">
          <base-button :loading="isLoading" type="primary" class="w-[100%]" @click="signUp">
            {{ t('login.registerBtn') }}
          </base-button>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <div class="flex justify-center items-center">
          <div class="flex items-center mr-20px">
            <div class="color-#666 text-14px">还没有账号?</div>
            <base-button link type="primary" @click="toLogin">{{ t('login.loginBtn') }}</base-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>
<script setup lang="ts">
import request from '@/utils/request'
import { useI18n } from '@/hooks/web/useI18n'
import { BaseButton } from '@/components/BaseButton'
import { MyIcon } from '@/components/MyIcon'
import { ElMessage } from 'element-plus'
import { encryptRSA } from '@/utils/jsencrypt-util'
// 国际化
const {t} = useI18n()
// 登录表单数据
const isLoading = ref(false)
const regFormRef = ref()
const regForm = ref({
  userName: '', password: '', rePassword: '', sex: '0', yzm: '', codeUuid: '', codeBaseImage: ''
})
// 组织架构
const orgList = ref([])
const loadOrgList = () => {
  request({url: '/register/org/list', method: 'get'}).then((rep) => { orgList.value = rep.data })
}
// 验证码
const loadCaptcha = () => {
  request({url: '/captcha/get', method: 'get'}).then((response) => {
    const {data} = response
    regForm.value.codeUuid = data.codeUuid
    regForm.value.codeBaseImage = data.codeBaseImage
  })
}
// 页面初始化
onMounted(() => {
  loadCaptcha()
  loadOrgList()
})

// 注册方法
const signUp = () => {
  regFormRef.value.validate(valid => {
    if (valid) {
      if (!regForm.value.agree) {
        ElMessage({message: '请先勾选同意用户协议、隐私协议！', type: 'error', grouping: true})
        return
      }
      isLoading.value = true
      const data = {...regForm.value}
      // 加密传输设置为true，并对用户名密码加密（不设置或设置false，默认为不加密传输）
      data.isEncrypt = true
      data.userName = encryptRSA(regForm.value.userName)
      data.password = encryptRSA(regForm.value.password)
      request({url: 'register/new', method: 'post', data}).then((rep) => {
        ElMessage({message: '注册成功！', type: 'success', grouping: true})
        toLogin()
        isLoading.value = false
      }).catch((err) => {
        console.log(err)
        isLoading.value = false
        loadCaptcha()
      })
    }
  })
}

// 去登录页
const emit = defineEmits(['toLogin'])
const toLogin = () => { emit('toLogin') }

</script>

