<template>
  <div :class="prefixCls"
       class="h-[100%] relative lt-xl:bg-[var(--login-bg-color)] lt-sm:px-10px lt-xl:px-10px lt-md:px-10px">
    <el-scrollbar class="h-full">
      <div class="relative flex mx-auto min-h-100vh">
        <div :class="`${prefixCls}__left flex-1 bg-gray-500 bg-opacity-20 relative p-30px lt-xl:hidden`">
          <div class="flex items-center relative text-white">
            <img src="@/assets/imgs/logo.png" alt="" class="w-48px h-48px mr-10px"/>
            <span class="text-20px font-bold">{{ appStore.getTitle }}</span>
          </div>
          <div class="flex justify-center items-center h-[calc(100%-60px)]">
            <transition-group appear tag="div" enter-active-class="animate__animated animate__bounceInLeft">
              <img src="@/assets/svgs/login-box-bg.svg" key="1" alt="" class="w-350px"/>
              <div class="text-3xl text-white" key="2">欢迎使用本系统</div>
              <div class="mt-5 font-normal text-white text-14px" key="3">开箱即用的中后台管理系统</div>
            </transition-group>
          </div>
        </div>
        <div class="flex-1 p-30px lt-sm:p-10px dark:bg-[var(--login-bg-color)] relative">
          <div class="flex justify-between items-center text-white at-2xl:justify-end at-xl:justify-end">
            <div class="flex items-center at-2xl:hidden at-xl:hidden">
              <img src="@/assets/imgs/logo.png" alt="" class="w-48px h-48px mr-10px"/>
              <span class="text-20px font-bold">{{ appStore.getTitle }}</span>
            </div>
            <div class="flex justify-end items-center space-x-10px">
              <theme-switch/>
              <locale-dropdown class="lt-xl:text-white dark:text-white"/>
            </div>
          </div>
          <transition appear enter-active-class="animate__animated animate__bounceInRight">
            <div
                class="h-full flex items-center m-auto w-[100%] at-2xl:max-w-500px at-xl:max-w-500px at-md:max-w-500px at-lg:max-w-500px">
              <login-form class="p-20px h-auto m-auto lt-xl:rounded-3xl lt-xl:light:bg-white"/>
            </div>
          </transition>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>
<script setup lang="ts">
import { LoginForm } from './components'
import { ThemeSwitch } from '@/components/ThemeSwitch'
import { LocaleDropdown } from '@/components/LocaleDropdown'
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'
import { ElScrollbar } from 'element-plus'

const {getPrefixCls} = useDesign()

const prefixCls = getPrefixCls('login')

const appStore = useAppStore()
</script>
<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-login';

.@{prefix-cls} {
  overflow: auto;

  &__left {
    background-color: var(--login-bg-color);
  }
}
</style>
