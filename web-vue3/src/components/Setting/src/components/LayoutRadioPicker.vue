<template>
  <div :class="prefixCls" class="flex flex-wrap space-x-14px">
    <!-- 经典布局（左右结构） -->
    <el-tooltip content="经典布局" :append-to="`${prefixCls}__classic`" :teleported="false" placement="top">
      <div :class="[`${prefixCls}__classic`,' relative w-56px h-48px cursor-pointer bg-gray-300',
                  {'is-acitve': layout === 'classic'}]" @click="appStore.setLayout('classic')"></div>
    </el-tooltip>
    <!-- 顶部左侧布局 -->
    <el-tooltip content="顶部左侧布局" :append-to="`${prefixCls}__classic`" :teleported="false" placement="top">
      <div :class="[`${prefixCls}__top-left`,'relative w-56px h-48px cursor-pointer bg-gray-300',
                  {'is-acitve': layout === 'topLeft'}]" @click="appStore.setLayout('topLeft')"></div>
    </el-tooltip>
    <!-- 顶部布局 -->
    <el-tooltip content="顶部布局" :append-to="`${prefixCls}__classic`" :teleported="false" placement="top">
      <div :class="[`${prefixCls}__top`,'relative w-56px h-48px cursor-pointer bg-gray-300',
                  {'is-acitve': layout === 'top'}]" @click="appStore.setLayout('top')"></div>
    </el-tooltip>
    <!-- 分栏菜单布局 -->
    <el-tooltip content="分栏菜单布局" :append-to="`${prefixCls}__classic`" :teleported="false" placement="top">
      <div :class="[`${prefixCls}__cut-menu`,'relative w-56px h-48px cursor-pointer bg-gray-300',
                  {'is-acitve': layout === 'cutMenu'}]" @click="appStore.setLayout('cutMenu')">
        <div class="absolute h-full w-[33%] top-0 left-[10%] bg-gray-200"></div>
      </div>
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('layout-radio-picker')
const appStore = useAppStore()
const layout = computed(() => appStore.getLayout)
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-layout-radio-picker';

.@{prefix-cls} {
  &__classic {
    border: 2px solid #e5e7eb;
    border-radius: 4px;
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 33%;
      height: 100%;
      background-color: #273352;
      border-radius: 4px 0 0 4px;
      content: '';
    }
    &::after {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 25%;
      background-color: #fff;
      border-radius: 4px 4px 0;
      content: '';
    }
  }
  &__top-left {
    border: 2px solid #e5e7eb;
    border-radius: 4px;
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 100%;
      height: 33%;
      background-color: #273352;
      border-radius: 4px 4px 0 0;
      content: '';
    }
    &::after {
      position: absolute;
      top: 0;
      left: 0;
      width: 33%;
      height: 100%;
      background-color: #fff;
      border-radius: 4px 0 0 4px;
      content: '';
    }
  }
  &__top {
    border: 2px solid #e5e7eb;
    border-radius: 4px;
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 100%;
      height: 33%;
      background-color: #273352;
      border-radius: 4px 4px 0 0;
      content: '';
    }
  }
  &__cut-menu {
    border: 2px solid #e5e7eb;
    border-radius: 4px;
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 100%;
      height: 33%;
      background-color: #273352;
      border-radius: 4px 4px 0 0;
      content: '';
    }
    &::after {
      position: absolute;
      top: 0;
      left: 0;
      width: 10%;
      height: 100%;
      background-color: #fff;
      border-radius: 4px 0 0 4px;
      content: '';
    }
  }
  .is-acitve {
    border-color: var(--el-color-primary);
  }
}
</style>
