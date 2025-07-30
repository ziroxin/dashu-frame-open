<template>
  <div class="app-container">
    <div class="mb-15px p-15px bg-blue-1 b-rd-10px text-14px color-#666 lh-30px">
      引导页：用户首次进入时，一步一步，引导用户操作。
      <div>
        GitHub仓库：
        <a href="https://github.com/kamranahmedse/driver.js" target="_blank">driver.js</a>
      </div>
      <div>
        文档地址：
        <a href="https://driverjs.com/" target="_blank">官方文档</a>
      </div>
    </div>
    <base-button type="primary" icon="el-icon-question" @click="guideStart">显示引导页</base-button>
  </div>
</template>
<script setup lang="ts">
import { useGuide } from '@/hooks/web/useGuide'

const {drive} = useGuide()
const guideStart = () => { drive() }
</script>
