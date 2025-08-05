<template>
  <div class="app-container">
    <h2 class="w-full text-center lh-200px!">测试水印功能</h2>
    <div class="w-full flex justify-center items-center">
      <div class="text-14px color-#666">水印标题：</div>
      <el-input v-model="title" clearable class="w-300px! mr-10px"/>
      <base-button type="primary" icon="el-icon-check" @click="setWatermarkClick">创建水印</base-button>
      <base-button type="danger" icon="el-icon-close" @click="clearWatermark">清除水印</base-button>
      <base-button type="warning" icon="reset" @click="resetWatermarkClick">重置水印标题</base-button>
    </div>
    <h2 class="w-full text-center lh-100px!">
      <el-tag type="primary" size="large" effect="plain">
        说明：本页面仅用于测试！ 系统水印在 “主题配置” 中进行修改！！！
      </el-tag>
    </h2>

  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { useWatermark } from '@/hooks/web/useWatermark'
import { useAppStore } from '@/store/modules/app'
import { ElMessage } from 'element-plus'
// 国际化
const {t} = useI18n()
// 水印（默认用标题）
const title = ref(useAppStore().getTitle)
const {setWatermark, clearWatermark} = useWatermark()
// 设置水印
const setWatermarkClick = () => {
  if (!title.value) {
    ElMessage({message: '水印标题不能为空！', type: 'error', grouping: true})
    return
  }
  setWatermark(title.value)
}
// 重置水印
const resetWatermarkClick = () => {title.value = useAppStore().getTitle}
</script>
