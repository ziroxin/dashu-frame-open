<template>
  <error v-if="showError" error-type="404"/>
</template>

<script setup lang="ts">
import { Error } from '@/components/Error'
import { getLastedRoutes } from '@/utils/lasted-routes'
import { homeRoute, loginRoute } from '@/router/constant-routes'

const showError = ref(false)
// 判断上一路由是否为登录页面，如果是，则直接跳转到主页
const lastedRoutes = [...getLastedRoutes()].filter(r => r !== '/404')
if (lastedRoutes && lastedRoutes.length > 0 && lastedRoutes[0] === loginRoute.path) {
  useRouter().push(homeRoute.path)
} else {
  showError.value = true
}
</script>