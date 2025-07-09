<template>
  <el-dropdown class="custom-hover" :class="prefixCls" trigger="click">
    <div class="flex items-center">
      <img v-if="userInfo.avatar" :src="$baseServer+userInfo.avatar"
           class="w-[calc(var(--logo-height)-25px)] rounded-[50%]" alt=""/>
      <img v-else src="@/assets/imgs/avatar.jpg"
           class="w-[calc(var(--logo-height)-25px)] rounded-[50%]" alt=""/>
      <span class="<lg:hidden text-14px pl-[5px] text-[var(--top-header-text-color)]">
        {{ userInfo?.name || userInfo?.nickName || userInfo?.userName }}
      </span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>
          <div @click="toPage('/personal/personal-center')">{{ t('router.personalCenter') }}</div>
        </el-dropdown-item>
        <el-dropdown-item divided>
          <div @click="lockScreen">{{ t('lock.lockScreen') }}</div>
        </el-dropdown-item>
        <el-dropdown-item>
          <div @click="loginOut">{{ t('common.loginOut') }}</div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <lock-dialog v-if="dialogVisible" v-model="dialogVisible"/>
  <teleport to="body">
    <transition name="fade-bottom" mode="out-in">
      <lock-page v-if="getIsLock"/>
    </transition>
  </teleport>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { useDesign } from '@/hooks/web/useDesign'
import LockDialog from './components/LockDialog.vue'
import LockPage from './components/LockPage.vue'
import { useLockStore } from '@/store/modules/lock'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('user-info')

// 用户信息
const userStore = useUserStore()
const userInfo: any = computed(() => userStore.getUserInfo)
console.log(userStore.getUserInfo)
// 锁屏状态
const getIsLock = computed(() => useLockStore().getLockInfo?.isLock ?? false)
// 锁定弹窗
const dialogVisible = ref<boolean>(false)
const lockScreen = () => { dialogVisible.value = true }
// 退出登录
const loginOut = () => { userStore.logoutConfirm() }
// 个人中心
const {push} = useRouter()
const toPage = (path: string) => { push(path) }
</script>

<style scoped lang="less">
.fade-bottom-enter-active,
.fade-bottom-leave-active {
  transition: opacity 0.25s,
  transform 0.3s;
}

.fade-bottom-enter-from {
  opacity: 0;
  transform: translateY(-10%);
}

.fade-bottom-leave-to {
  opacity: 0;
  transform: translateY(10%);
}
</style>
