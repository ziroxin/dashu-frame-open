<template>
  <div :class="prefixCls" class="fixed inset-0 flex h-screen w-screen bg-black items-center justify-center">
    <!-- 解锁按钮 -->
    <div :class="`${prefixCls}__unlock`" @click="handleShowForm(true)"
         class="absolute top-0 left-1/2 flex pt-5 h-16 items-center justify-center sm:text-md xl:text-xl text-white flex-col cursor-pointer transform translate-x-1/2">
      <my-icon icon="vi-ep:unlock" :size="28"/>
      <span>{{ t('lock.unlockBtn') }}</span>
    </div>
    <!-- 时钟 -->
    <div class="flex w-screen h-screen justify-center items-center">
      <div :class="`${prefixCls}__hour`" class="relative mr-5 md:mr-20 w-2/5 h-2/5 md:h-4/5">
        <span>{{ hour }}</span>
        <span class="meridiem absolute left-5 top-5 text-md xl:text-xl">{{ meridiem }}</span>
      </div>
      <div :class="`${prefixCls}__minute w-2/5 h-2/5 md:h-4/5 `">
        <span>{{ minute }}</span>
      </div>
    </div>
    <!-- 解锁表单 -->
    <transition name="fade-slide">
      <div :class="`${prefixCls}-entry`" v-show="formVisible">
        <div :class="`${prefixCls}-entry-content mt-[-100px]`">
          <!-- 头像、昵称 -->
          <div class="flex flex-col items-center">
            <img v-if="userInfo.avatar" :src="$baseServer+userInfo.avatar" alt="" class="w-70px h-70px rounded-[50%]"/>
            <img v-else src="@/assets/imgs/avatar.jpg" alt="" class="w-70px h-70px rounded-[50%]"/>
            <span class="text-14px my-20px text-[var(--logo-title-text-color)]">
              {{ userInfo?.name || userInfo?.nickName || userInfo?.userName }}
            </span>
          </div>
          <!-- 密码输入框 -->
          <el-input type="password" :placeholder="t('lock.lockPlaceholder')" class="enter-x"
                    v-model="password" @keydown.enter="unLock" ref="passwordInputRef"/>
          <!-- 错误提示 -->
          <span :class="`text-14px ${prefixCls}-entry__err-msg enter-x`" v-if="errMsg">{{ errMsg }}</span>
          <!-- 底部按钮 -->
          <div :class="`${prefixCls}-entry__footer enter-x mt-10px`">
            <base-button type="info" size="small" class="mt-2 mr-2 enter-x" link @click="handleShowForm(false)">
              {{ t('lock.lockBackBtn') }}
            </base-button>
            <base-button type="danger" class="mt-2" size="small" link @click="unLock()">
              {{ t('lock.lockEntrySystem') }}
            </base-button>
            <base-button type="primary" size="small" class="mt-2 mr-2 enter-x" link @click="goLogin">
              {{ t('lock.lockToLogin') }}
            </base-button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 底部 -->
    <div class="absolute bottom-5 w-full text-gray-300 xl:text-xl 2xl:text-3xl text-center enter-y">
      <!-- 时间 -->
      <div class="text-5xl mb-4 enter-x" v-show="formVisible">
        {{ hour }}:{{ minute }} <span class="text-3xl">{{ meridiem }}</span>
      </div>
      <!-- 日期 -->
      <div class="text-2xl">{{ year }}/{{ month }}/{{ day }} {{ week }}</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ElMessageBox } from 'element-plus'
import { useLockStore } from '@/store/modules/lock'
import { useI18n } from '@/hooks/web/useI18n'
import { useNow } from '@/hooks/web/useNow'
import { useDesign } from '@/hooks/web/useDesign'
import { useUserStoreWithOut } from '@/store/modules/user'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('lock-page')
const {hour, month, minute, meridiem, year, day, week} = useNow(true)
const lockStore = useLockStore()

// 加载用户信息
const userInfo: any = useUserStoreWithOut().getUserInfo

// 解锁表单显示状态
const formVisible = ref(false)
// 密码输入框
const passwordInputRef = ref()

// 显示/隐藏解锁表单
function handleShowForm(val = false) {
  formVisible.value = val
  if (val) {
    // 获取密码输入框焦点
    requestAnimationFrame(() => { passwordInputRef.value?.focus() })
  }
}

// 密码输入框
const password = ref('')
const errMsg = ref('')

// 解锁
async function unLock() {
  if (!password.value) {
    errMsg.value = '请输入锁屏密码'
  } else {
    const unlocked = await lockStore.unLock(password.value)
    errMsg.value = unlocked ? '' : '锁屏密码错误'
  }
}

// 返回登录（退出）
function goLogin() {
  ElMessageBox.confirm('确定要退出系统，返回登录页吗?', '退出登录提醒', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', appendTo: `.${prefixCls}-entry`
  }).then(() => {
    // 重置锁定信息
    lockStore.resetLockInfo()
    // 调用统一退出方法
    useUserStoreWithOut().logout()
  })
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-lock-page';

// Small screen / tablet
@screen-sm: 576px;

// Medium screen / desktop
@screen-md: 768px;

// Large screen / wide desktop
@screen-lg: 992px;

// Extra large screen / full hd
@screen-xl: 1200px;

// Extra extra large screen / large desktop
@screen-2xl: 1600px;

@error-color: #ed6f6f;

.@{prefix-cls} {
  z-index: 3000;

  &__unlock {
    transform: translate(-50%, 0);
  }

  &__hour,
  &__minute {
    display: flex;
    font-weight: 700;
    color: #bababa;
    background-color: #141313;
    border-radius: 30px;
    justify-content: center;
    align-items: center;

    @media screen and (max-width: @screen-md) {
      span:not(.meridiem) {
        font-size: 160px;
      }
    }

    @media screen and (min-width: @screen-md) {
      span:not(.meridiem) {
        font-size: 160px;
      }
    }

    @media screen and (max-width: @screen-sm) {
      span:not(.meridiem) {
        font-size: 90px;
      }
    }

    @media screen and (min-width: @screen-lg) {
      span:not(.meridiem) {
        font-size: 220px;
      }
    }

    @media screen and (min-width: @screen-xl) {
      span:not(.meridiem) {
        font-size: 260px;
      }
    }

    @media screen and (min-width: @screen-2xl) {
      span:not(.meridiem) {
        font-size: 320px;
      }
    }
  }

  &-entry {
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    width: 100%;
    height: 100%;
    background-color: rgb(0 0 0 / 50%);
    backdrop-filter: blur(8px);
    justify-content: center;
    align-items: center;

    &-content {
      width: 260px;
    }

    &__header {
      text-align: center;

      &-img {
        width: 70px;
        margin: 0 auto;
        border-radius: 50%;
      }

      &-name {
        margin-top: 5px;
        font-weight: 500;
        color: #bababa;
      }
    }

    &__err-msg {
      display: inline-block;
      margin-top: 10px;
      color: @error-color;
    }

    &__footer {
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>
