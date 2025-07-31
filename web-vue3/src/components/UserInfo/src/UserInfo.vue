<template>
  <!-- 顶部个人中心 -->
  <el-dropdown :id="`${variables.namespace}-userInfo`" class="top-tool-hover" :class="prefixCls" trigger="click">
    <!-- 头像、昵称 -->
    <div class="flex items-center h-100%">
      <img v-if="userInfo&&userInfo.avatar" :src="$baseServer+userInfo.avatar" :alt="'用户'+userInfo?.userName+'头像'"
           class="w-[var(--logo-height)] h-[var(--logo-height)] rounded-full p-4px object-cover"/>
      <img v-else src="@/assets/imgs/avatar.jpg" :alt="'用户'+userInfo?.userName+'默认头像'"
           class="w-[var(--logo-height)] h-[var(--logo-height)] rounded-full p-4px object-cover"/>
      <span class="<lg:hidden text-14px pl-[5px] text-[var(--top-header-text-color)]">
        {{ userInfo?.name || userInfo?.nickName || userInfo?.userName }}
      </span>
    </div>
    <!-- 下拉菜单 -->
    <template #dropdown>
      <el-dropdown-menu>
        <!-- 个人中心 -->
        <el-dropdown-item @click="toUserPage()">
          <div class="flex items-center">
            <my-icon icon="el-icon-user"/>
            {{ t('router.personalCenter') }}
          </div>
        </el-dropdown-item>
        <!-- 锁屏 -->
        <el-dropdown-item divided @click="lockScreen">
          <div class="flex items-center">
            <my-icon icon="el-icon-lock"/>
            {{ t('lock.lockScreen') }}
          </div>
        </el-dropdown-item>
        <!-- 退出登录 -->
        <el-dropdown-item @click="loginOut">
          <div class="flex items-center">
            <my-icon icon="exit"/>
            {{ t('common.loginOut') }}
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <!-- 锁屏弹窗 -->
  <lock-dialog v-if="dialogVisible" v-model="dialogVisible"/>
  <teleport to="body">
    <transition name="fade-bottom" mode="out-in">
      <lock-page v-if="getIsLock"/>
    </transition>
  </teleport>
</template>

<script setup lang="ts">
import LockDialog from './components/LockDialog.vue'
import LockPage from './components/LockPage.vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useDesign } from '@/hooks/web/useDesign'
import { useLockStore } from '@/store/modules/lock'
import { useUserStore } from '@/store/modules/user'
import { userRoute } from '@/router/constant-routes'
import { MyIcon } from '@/components/MyIcon'

const {t} = useI18n()
const {getPrefixCls, variables} = useDesign()
const prefixCls = getPrefixCls('user-info')

// 用户信息
const userStore = useUserStore()
const userInfo: any = computed(() => userStore.getUserInfo)
// 锁屏状态
const getIsLock = computed(() => useLockStore().getLockInfo?.isLock ?? false)
// 锁定弹窗
const dialogVisible = ref<boolean>(false)
const lockScreen = () => { dialogVisible.value = true }
// 退出登录
const loginOut = () => { userStore.logoutConfirm() }
// 个人中心
const {push} = useRouter()
const toUserPage = () => { push(userRoute.path) }
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
