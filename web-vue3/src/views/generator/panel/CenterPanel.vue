<template>
  <el-form ref="dataFormRef" :model="formData" v-bind="{...formProps.__attrs}" class="min-h-[calc(100%-59px)] mt-5px">
    <!-- 普通布局 -->
    <template v-if="!formProps?.__layout?.layout">
      <vue-draggable v-model="formItemList" :animation="150" group="content" @click="changeC(null)"
                     class="flex flex-col h-[calc(100vh-var(--top-header-height)-30px)] overflow-auto">
        <template v-for="item in formItemList" :key="item.__id">
          <div :class="{'isActived':current.__id===item.__id}" class="citem cursor-move mx-10px p-10px b-rd-5px"
               @click.stop="changeC(item)">
            <!-- 表单元素 -->
            <center-form-items v-model="formData" :item="item" :key="'col-'+item.__id"/>
            <!-- 复制/删除 -->
            <div v-if="current.__id===item.__id" class="absolute bottom-5px right-10px z-9">
              <span class="text-12px text-gray-4 mr-10px">可以拖拽修改组件顺序</span>
              <base-button @click="copyC(item)" type="primary" icon="el-icon-copy-document" plain circle size="small"/>
              <base-button @click="deleteC(item)" type="danger" icon="el-icon-delete" plain circle size="small"/>
            </div>
          </div>
        </template>
      </vue-draggable>
    </template>
    <!-- 栅格布局 -->
    <template v-else>
      <el-row :gutter="formProps.__layout.gutter" :justify="formProps.__layout.justify"
              :align="formProps.__layout.align" class="w-full">
        <vue-draggable v-model="formItemList" :animation="150" group="content" @click="changeC(null)"
                       class="el-row h-[calc(100vh-var(--top-header-height)-30px)] overflow-auto">
          <template v-for="item in formItemList" :key="item.__id">
            <el-col :span="item?.__span">
              <div :class="{'isActived':current.__id===item.__id}" class="citem cursor-move mx-10px p-10px b-rd-5px"
                   @click.stop="changeC(item)">
                <!-- 表单元素 -->
                <center-form-items v-model="formData" :item="item" :key="'col-'+item.__id"/>
                <!-- 复制/删除 -->
                <div v-if="current.__id===item.__id" class="absolute bottom-5px right-10px z-9">
                  <base-button @click="copyC(item)" type="primary" icon="el-icon-copy-document"
                               plain circle size="small"/>
                  <base-button @click="deleteC(item)" type="danger" icon="el-icon-delete" plain circle size="small"/>
                </div>
              </div>
            </el-col>
          </template>
        </vue-draggable>
      </el-row>
    </template>
    <!-- 无数据 -->
    <div v-if="!formItemList||formItemList.length<=0"
         class="text-center text-[#6b7280] bg-[#6b728015] b-rd-5px m-5px p-10px">
      点击左侧组件添加表单项
    </div>
  </el-form>
  <!-- 测试按钮 -->
  <div class="w-full py-10px text-center b-t-1px b-t-dashed b-t-#ccc">
    <base-button @click="check" icon="el-icon-check" type="primary" plain>测试提交</base-button>
    <base-button @click="clearInterval" icon="el-icon-close" plain>重置</base-button>
    <el-tag type="info" class="ml-10px">此处按钮只用于测试表单，不生成代码！</el-tag>
    <a href="https://element-plus.org/zh-CN/component/overview.html" target="_blank">
      <base-button type="primary" link icon="el-icon-link" class="px-20px!">Element Plus 官方文档</base-button>
    </a>
  </div>
  <!-- 复制表单项弹窗 -->
  <form-item-add v-if="addDialogVisible" v-model="addDialogVisible"
                 v-model:addItem="addItem" v-model:formItemList="formItemList"/>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { objToStr } from '@/utils'
import { VueDraggable } from 'vue-draggable-plus'
import FormItemAdd from '@/views/generator/panel/FormItemAdd'
import CenterFormItems from '@/views/generator/panel/CenterFormItems'

// 编辑器Model
const formItemList: any = defineModel()
const current = defineModel('current', {type: Object, default: () => {}})
const formProps = defineModel('formProps', {type: Object, default: () => {}})
const html = defineModel('html', {type: String, default: ''})
const ts = defineModel('ts', {type: String, default: ''})

const dataFormRef = ref()
const formData = ref({})
const deleteC = (item) => {
  current.value = {}
  formItemList.value.splice(formItemList.value.indexOf(item), 1)
}
const changeC = (item) => {
  current.value = item ? {...item} : {}
}

// 复制表单项
const addDialogVisible = ref(false)
const addItem = ref({})
const copyC = (item) => {
  addDialogVisible.value = true
  addItem.value = cloneDeep(item)
}

const check = () => {
  console.log('表单验证前', objToStr(formData.value))
  ElMessage({message: '表单验证前：' + objToStr(formData.value), type: 'warning', grouping: true})
  dataFormRef.value.validate((valid) => {
    if (valid) {
      console.log('表单验证通过，提交表单：', objToStr(formData.value))
      ElMessageBox.alert('表单数据：<br/>'+objToStr(formData.value), '表单验证通过', {dangerouslyUseHTMLString: true})
    }
  })
}
const clearInterval = () => {dataFormRef.value.resetFields()}
</script>

<style lang="less" scoped>
.isActived {
  position: relative;
  background-color: #f5f7fa50;
  border: 1px dashed #dddfe1;
}

.citem:hover {
  border: 1px dashed #dddfe1;
  cursor: grab;
}
</style>