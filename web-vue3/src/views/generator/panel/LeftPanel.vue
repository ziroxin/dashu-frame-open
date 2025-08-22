<template>
  <div v-for="row in componentList" :key="row.name" class="b-b-1px b-b-dashed b-b-#bbb mb-15px">
    <div class="left-title">
      <my-icon :icon="row.icon" class="mr-5px"/>
      {{ row.name }}
    </div>
    <div :sort="false" class="grid gap-5px w-full grid-cols-[1fr_1fr] mt-10px mb-15px">
      <template v-for="item in row.list" :key="item.__key">
        <div class="left-component-item" @click="addToCenter(item)">
          <my-icon v-if="item.__icon" :icon="item.__icon" class="mr-4px" :size="14"/>
          {{ item.__name }}
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import inputConfig from '@/views/generator/panel/config/inputConfig'
import selectConfig from '@/views/generator/panel/config/selectConfig'
import diyConfig from '@/views/generator/panel/config/diyConfig'
import otherConfig from '@/views/generator/panel/config/otherConfig'
import { generateUUID } from '@/utils/tools'
import { ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

// 组件配置
const componentList = ref([
  {name: '原生组件', icon: 'el-icon-edit', list: inputConfig},
  {name: '选择组件', icon: 'el-icon-news', list: selectConfig},
  {name: '自定义组件', icon: 'el-icon-menu', list: diyConfig},
  {name: '其他组件', icon: 'el-icon-set-up', list: otherConfig}
])

const modelValue = defineModel()

// 添加
const addToCenter = (item) => {
  // todo: 封装组件，在复制时也用到
  ElMessageBox.prompt('请输入字段名称：', '字段名称', {confirmButtonText: '确定', cancelButtonText: '取消'})
      .then(({value}) => {
        const newItem = cloneDeep(item)
        modelValue.value.push({...newItem, __id: generateUUID(), __modelName: value})
      })
}
</script>

<style lang="less" scoped>
.left-title {
  display: flex;
  justify-content: start;
  align-items: center;
  font-size: 14px;
}

.left-component-item {
  display: flex;
  justify-content: left;
  align-items: center;
  padding: 8px;
  background-color: var(--el-bg-color);
  border-radius: 6px;
  font-size: 12px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 8px #0000000d;
  transition: transform .2s, box-shadow .2s;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px #0000001a;
    color: var(--el-color-primary);
  }
}
</style>