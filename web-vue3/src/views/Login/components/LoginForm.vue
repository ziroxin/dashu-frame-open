<template>
  <el-form ref="loginFormRef" :model="loginForm" label-position="top" hide-required-asterisk size="large"
           class="dark:(border-1 border-[var(--el-border-color)] border-solid)">
    <el-row>
      <el-col :span="24">
        <el-form-item>
          <h2 class="text-2xl font-bold text-center w-[100%]" v-text="t('login.login')"></h2>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.username')" prop="userName"
                      :rules="[{required: true, message: '用户名不能为空！'}]">
          <el-input v-model="loginForm.userName" :prefix-icon="iconMap.user"
                    :placeholder="t('login.usernamePlaceholder')" style="width: 100%"/>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.password')" prop="password"
                      :rules="[{required: true, message: '密码不能为空！'}]">
          <el-input type="password" v-model="loginForm.password" @keydown.enter.stop="signIn"
                    :prefix-icon="iconMap.password" show-password
                    :placeholder="t('login.passwordPlaceholder')" style="width: 100%"/>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.code')" prop="yzm"
                      :rules="[{required: true, message: '验证码不能为空！'}]">
          <div class="flex justify-between items-center w-[100%]">
            <el-input ref="yzm" v-model="loginForm.yzm" @keydown.enter.stop="signIn"
                      :prefix-icon="iconMap.yzm"
                      class="w-[50%]" :placeholder="t('login.codePlaceholder')"/>
            <img :src="loginForm.codeBaseImage" @click="loadCaptcha"/>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item>
          <div class="flex justify-between items-center w-[100%]">
            <el-checkbox v-model="loginForm.rememberMe" :label="t('login.remember')"/>
            <el-link type="primary" underline="never" v-text="t('login.forgetPassword')"/>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item class="w-[100%]">
          <base-button :loading="isLoading" type="primary" class="w-[100%]" @click="signIn">
            {{ t('login.login') }}
          </base-button>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-divider content-position="center" :content="t('login.otherLogin')"/>
        <div class="flex justify-between w-[100%]">
          <my-icon v-for="item in ['github-filled', 'wechat-filled','alipay-circle-filled','weibo-circle-filled']"
                   :icon="'vi-ant-design:'+item" :key="item"
                   class="cursor-pointer ant-icon" :size="30" color="#999" hoverColor="var(--el-color-primary)"/>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>
<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import request from '@/utils/request'
import { useIcon } from '@/hooks/web/useIcon'
import { encryptRSA } from '@/utils/jsencrypt-util'
import storageKeys from '@/utils/storage-keys'
import { loginApi } from '@/api/login'
import { setToken } from '@/utils/auth'
// 国际化
const {t} = useI18n()

// 登录表单数据
const isLoading = ref(false)
const loginFormRef = ref()
const loginForm = ref({
  userName: '', password: '', yzm: '', codeUuid: '', codeBaseImage: '', rememberMe: false
})
// 页面初始化
onMounted(() => {
  // 记住密码
  if (localStorage.getItem(storageKeys.l_rememberMeData)) {
    const userData = JSON.parse(localStorage.getItem(storageKeys.l_rememberMeData) as string)
    loginForm.value.userName = userData.userName
    loginForm.value.password = userData.password
    loginForm.value.rememberMe = true
  }
  // 加载验证码
  loadCaptcha()
})

// 验证码
const loadCaptcha = () => {
  request({url: '/captcha/get', method: 'get'}).then((response) => {
    const {data} = response
    loginForm.value.codeUuid = data.codeUuid
    loginForm.value.codeBaseImage = data.codeBaseImage
  })
}

const {replace, currentRoute} = useRouter()
// 登录
const signIn = () => {
  loginFormRef.value.validate((isValid) => {
    if (isValid) {
      isLoading.value = true
      const data: any = {...loginForm.value}
      // 加密传输设置为true，并对用户名密码加密（不设置或设置false，默认为不加密传输）
      data.isEncrypt = true
      data.userName = encryptRSA(data.userName)
      data.password = encryptRSA(data.password)
      data.codeBaseImage = ''// 验证码base64图片不传递
      loginApi(data).then(response => {
        // 登录成功，处理登录逻辑
        const {data} = response
        setToken(data.accessToken, new Date(data.accessTokenValidTime))
        // 是否默认密码
        sessionStorage.setItem(storageKeys.s_isDefaultPassword, data.defaultPassword)
        // 密码是否过期
        sessionStorage.setItem(storageKeys.s_isInvalidPassword, data.invalidPassword)
        // 是否记住我
        if (loginForm.value.rememberMe) {
          localStorage.setItem(storageKeys.l_rememberMeData,
              JSON.stringify({userName: loginForm.value.userName, password: loginForm.value.password}))
        } else {
          localStorage.removeItem(storageKeys.l_rememberMeData)
        }
        // 跳转页面
        replace({path: currentRoute.value?.query?.redirect as string || '/'})
        isLoading.value = false
        location.reload()
      }).catch(err => {
        console.log('login error!', err)
        isLoading.value = false
        loadCaptcha()
      })
    }
  })
}

// 输入框图标
const iconMap = {
  user: useIcon({icon: 'user'}),
  password: useIcon({icon: 'lock'}),
  yzm: useIcon({icon: 'yzm', size: 20, color: '#777'})
}
</script>

