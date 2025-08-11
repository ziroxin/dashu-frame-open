<template>
  <el-form ref="loginFormRef" :model="loginForm" label-position="top" hide-required-asterisk size="large"
           class="dark:(border-1 border-[var(--el-border-color)] border-solid)">
    <el-row>
      <el-col :span="24">
        <el-form-item>
          <h2 class="text-2xl font-bold text-center w-[100%]" v-text="t('login.loginTitle')"></h2>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.userNameLabel')" prop="userName"
                      :rules="[{required: true, message: '用户名不能为空！'}]">
          <el-input v-model="loginForm.userName" class="w-full" :placeholder="t('login.userNamePlaceholder')">
            <template #prefix>
              <my-icon icon="user"/>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.passwordLabel')" prop="password"
                      :rules="[{required: true, message: '密码不能为空！'}]">
          <el-input type="password" v-model="loginForm.password" @keydown.enter.stop="signIn" class="w-full"
                    show-password :placeholder="t('login.passwordPlaceholder')">
            <template #prefix>
              <my-icon icon="lock"/>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item :label="t('login.yzmLabel')" prop="yzm" :rules="[{required: true, message: '验证码不能为空！'}]">
          <div class="flex justify-between items-center w-[100%]">
            <el-input ref="yzm" v-model="loginForm.yzm" @keydown.enter.stop="signIn" class="w-[50%]"
                      :placeholder="t('login.yzmPlaceholder')">
              <template #prefix>
                <my-icon icon="yzm" :size="20" color="#777"/>
              </template>
            </el-input>
            <img :src="loginForm.codeBaseImage" @click="loadCaptcha"/>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item>
          <div class="flex justify-between items-center w-[100%]">
            <el-checkbox v-model="loginForm.rememberMe" :label="t('login.rememberChb')"/>
            <el-link type="primary" underline="never" v-text="t('login.forgetPasswordLink')"/>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item class="w-[100%]">
          <base-button :loading="isLoading" type="primary" class="w-[100%]" @click="signIn">
            {{ t('login.loginBtn') }}
          </base-button>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <div class="flex justify-center items-center mb-10px">
          <el-link target="_blank" href="./protocol.html">《用户协议》</el-link>
          <el-link target="_blank" href="./protocol.html">《隐私协议》</el-link>
        </div>
      </el-col>
      <el-col :span="24">
        <div class="flex justify-center items-center">
          <div class="color-#666 text-14px flex items-center mr-20px">
            <div>还没有账号?</div>
            <base-button link type="primary" @click="toRegister">{{ t('login.registerBtn') }}</base-button>
          </div>
          <base-button link type="primary" @click="otherLoginVisible=true">{{ t('login.otherLoginBtn') }}</base-button>
        </div>
        <el-dialog title="其他方式登录" v-model="otherLoginVisible" draggable>
          <social-signin/>
        </el-dialog>
      </el-col>
    </el-row>
  </el-form>
</template>
<script setup lang="ts">
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys'
import { useI18n } from '@/hooks/web/useI18n'
import { encryptRSA } from '@/utils/jsencrypt-util'
import { loginApi } from '@/api/login'
import { setToken } from '@/utils/auth'
import { useAppStore } from '@/store/modules/app'
import { BaseButton } from '@/components/BaseButton'
import { MyIcon } from '@/components/MyIcon'
import { SocialSignin } from './'
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
        // 如果“隐藏主题配置”不是true，则登录成功后，加载用户自己的主题
        if (import.meta.env.VITE_HIDE_GLOBAL_SETTING !== 'true') {
          const params = {themeType: 'vue3'}
          request({url: '/userTheme/zUserTheme/getByUser', method: 'get', params}).then((response) => {
            const {data} = response
            if (data) {
              // 加载用户自己的主题
              useAppStore().loadTheme(JSON.parse(data))
            }
            // 跳转页面
            loginSuccess()
          })
        } else {
          // 跳转页面
          loginSuccess()
        }
      }).catch(err => {
        console.log('login error!', err)
        isLoading.value = false
        loadCaptcha()
      })
    }
  })
}

// 登录成功，跳转页面
const loginSuccess = () => {
  replace({path: currentRoute.value?.query?.redirect as string || '/'})
}

// 注册
const emit = defineEmits(['toRegister'])
const toRegister = () => { emit('toRegister') }
// 其他方式登录
const otherLoginVisible = ref(false)
</script>

