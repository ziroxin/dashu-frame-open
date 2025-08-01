<template>
  <div class="app-container flex justify-center items-center">
    <div class="text-14px color-#666">水印标题：</div>
    <el-input v-model="title" clearable class="w-300px! mr-10px"/>
    <base-button type="primary" icon="el-icon-check" @click="setWatermarkClick">
      {{ t('watermarkDemo.createdWatermark') }}
    </base-button>
    <base-button type="danger" icon="el-icon-close" @click="clear">
      {{ t('watermarkDemo.clearWatermark') }}
    </base-button>
    <base-button type="warning" icon="reset" @click="resetWatermarkClick">
      {{ t('watermarkDemo.resetWatermark') }}
    </base-button>
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
const {setWatermark, clear} = useWatermark()
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
