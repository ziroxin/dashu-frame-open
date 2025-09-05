<template>
  <!-- 批量添加按钮 -->
  <div class="el-divider el-divider--horizontal">
    <div class="el-divider__text is-center bg-[var(--el-fill-color-light)]! text-12px!">批量添加</div>
  </div>
  <div class="w-full px-15px">
    <base-button icon="el-icon-plus" class="w-full" @click="formItemAddBatchVisible=true">批量添加</base-button>
  </div>
  <!-- 单个添加-组件列表 -->
  <div class="el-divider el-divider--horizontal">
    <div class="el-divider__text is-center bg-[var(--el-fill-color-light)]! text-12px!">单个添加</div>
  </div>
  <div v-for="row in componentList" :key="row.name" class="b-b-1px b-b-dashed b-b-#bbb mb-15px mx-15px">
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
  <!-- 单个添加弹窗 -->
  <form-item-add v-if="addDialogVisible" v-model="addDialogVisible"
                 v-model:addItem="addItem" v-model:formItemList="formItemList"/>
  <!-- 批量添加表单项弹窗 -->
  <el-dialog title="批量添加表单项" v-model="formItemAddBatchVisible" top="5vh" width="90%"
             :close-on-click-modal="false">
    <form-item-add-batch v-if="formItemAddBatchVisible" v-model="formItemAddBatchVisible"
                         v-model:formItemList="formItemList"/>
  </el-dialog>
</template>

<script setup lang="ts">
import { cloneDeep } from 'lodash-es'
import allConfig from '@/views/generator/panel/config/allConfig'
import FormItemAdd from '@/views/generator/panel/FormItemAdd'
import FormItemAddBatch from '@/views/generator/panel/FormItemAddBatch'

// 组件配置
const componentList = cloneDeep(allConfig)

const formItemList = defineModel()

// 单个添加弹窗
const addDialogVisible = ref(false)
const addItem = ref({})
const addToCenter = (item) => {
  addDialogVisible.value = true
  addItem.value = cloneDeep(item)
}

// 批量添加表单项弹窗
const formItemAddBatchVisible = ref(false)
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