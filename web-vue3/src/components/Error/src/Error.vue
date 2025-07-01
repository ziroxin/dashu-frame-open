<template>
  <div class="flex justify-center">
    <div class="text-center mt-20px">
      <my-icon :icon="errorMap[errorType].icon" :size="380"/>
      <div class="mt-20px text-18px text-[var(--el-color-info)]">{{ errorMap[errorType].message }}</div>
      <div class="mt-30px">
        <base-button type="primary" @click="toBack" v-text="t('error.returnBack')"/>
        <base-button type="primary" @click="toHome" v-text="t('error.returnToHome')"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { homeRoute, loginRoute } from '@/router/constant-routes'
import { getLastedRoutes } from '@/utils/lasted-routes'

// 参数
const {errorType} = defineProps({
  errorType: {type: String, default: '404'}
})

// 错误信息定义
const {t} = useI18n()
const errorMap = {
  '401': {icon: 'svg-403', message: t('error.error401')},
  '403': {icon: 'svg-403', message: t('error.error403')},
  '404': {icon: 'svg-404', message: t('error.error404')},
  '500': {icon: 'svg-500', message: t('error.error500')}
}

const {push} = useRouter()
// 返回首页
const toHome = () => { push(homeRoute.path) }
// 返回上一页
const toBack = () => {
  push(getLastedRoutes().filter(path => path !== loginRoute.path && path !== '/404')[0] || homeRoute.path)
}
</script>
