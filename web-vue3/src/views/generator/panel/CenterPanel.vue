<template>
  <el-form ref="dataFormRef" :model="formData" v-bind="{...formProps}" class="min-h-[calc(100%-54px)]">
    <vue-draggable v-model="formItemList" :animation="150" group="content" @click="changeC(null)"
                   class="flex flex-col h-[calc(100vh-var(--top-header-height)-30px)] overflow-auto">
      <!-- 普通布局 -->
      <template v-if="!formProps?.layout">
        <template v-for="item in formItemList" :key="item.__id">
          <!-- 表单内容-start -->
          <div :class="{'isActived':current.__id===item.__id}" class="cursor-move mx-10px p-10px b-rd-5px"
               @click.stop="changeC(item)">
            <el-form-item v-bind="{...item.__formItemAttrs}" :prop="item.__modelName"
                          :rules="getRules(item.__formItemAttrs)">
              <el-input v-if="item.__key==='el-input'" v-bind="{...item.__attrs}"
                        v-model="formData[item.__modelName]"/>
              <el-input-number v-if="item.__key==='el-input-number'" v-bind="{...item.__attrs}"
                               v-model="formData[item.__modelName]"/>
              <el-select v-if="item.__key==='el-select'" v-bind="{...item.__attrs}"
                         v-model="formData[item.__modelName]"/>
              <el-date-picker v-if="item.__key==='el-date-picker'" v-bind="{...item.__attrs}"
                              v-model="formData[item.__modelName]"/>
              <my-wang-editor v-if="item.__key==='my-wang-editor'" v-bind="{...item.__attrs}"
                              v-model="formData[item.__modelName]"/>
            </el-form-item>
            <div v-if="current.__id===item.__id" class="absolute bottom-5px right-10px z-9">
              <base-button @click="copyC(item)" type="primary" icon="el-icon-copy-document" plain circle size="small"/>
              <base-button @click="deleteC(item)" type="danger" icon="el-icon-delete" plain circle size="small"/>
            </div>
          </div>
          <!-- 表单内容-end -->
        </template>
      </template>
      <!-- 栅格布局 -->
      <template v-else>
        <el-row :gutter="formProps.gutter" :justify="formProps.justify" :align="formProps.align" class="w-full">
          <template v-for="item in formItemList" :key="item.__id">
            <el-col :span="item?.__span">
              <!-- 表单内容-start -->
              <div :class="{'isActived':current.__id===item.__id}" class="cursor-move mx-10px p-10px b-rd-5px"
                   @click.stop="changeC(item)">
                <el-form-item v-bind="{...item.__formItemAttrs}" :prop="item.__modelName"
                              :rules="getRules(item.__formItemAttrs)">
                  <el-input v-if="item.__key==='el-input'" v-bind="{...item.__attrs}"
                            v-model="formData[item.__modelName]"/>
                  <el-input-number v-if="item.__key==='el-input-number'" v-bind="{...item.__attrs}"
                                   v-model="formData[item.__modelName]"/>
                  <el-select v-if="item.__key==='el-select'" v-bind="{...item.__attrs}"
                             v-model="formData[item.__modelName]"/>
                  <el-date-picker v-if="item.__key==='el-date-picker'" v-bind="{...item.__attrs}"
                                  v-model="formData[item.__modelName]"/>
                  <my-wang-editor v-if="item.__key==='my-wang-editor'" v-bind="{...item.__attrs}"
                                  v-model="formData[item.__modelName]"/>
                </el-form-item>
                <div v-if="current.__id===item.__id" class="absolute bottom-5px right-10px z-9">
                  <base-button @click="copyC(item)" type="primary" icon="el-icon-copy-document" plain circle
                               size="small"/>
                  <base-button @click="deleteC(item)" type="danger" icon="el-icon-delete" plain circle size="small"/>
                </div>
              </div>
              <!-- 表单内容-end -->
            </el-col>
          </template>
        </el-row>
      </template>
    </vue-draggable>
    <div v-if="!formItemList||formItemList.length<=0" class="text-center text-[#6b7280] bg-[#6b728015] b-rd-5px m-5px p-10px">
      点击左侧组件添加表单项
    </div>
  </el-form>
  <div class="w-full py-10px text-center b-t-1px b-t-dashed b-t-#ccc">
    <base-button @click="check" icon="el-icon-check" type="primary" plain>测试表单</base-button>
    <base-button @click="clearInterval" icon="el-icon-close" plain>重置表单</base-button>
    <el-tag type="info" class="ml-10px">此处按钮只用于测试表单，不生成代码！</el-tag>
  </div>
  <!-- 复制表单项弹窗 -->
  <form-item-add v-if="addDialogVisible" v-model="addDialogVisible"
                 v-model:addItem="addItem" v-model:formItemList="formItemList"/>
</template>

<script setup lang="ts">
import { VueDraggable } from 'vue-draggable-plus'
import { MyWangEditor } from '@/components/MyWangEditor'
import { BaseButton } from '@/components/BaseButton'
import FormItemAdd from '@/views/generator/panel/FormItemAdd'
import { cloneDeep } from 'lodash-es'

// 编辑器Model
const formItemList = defineModel()
const current = defineModel('current', {type: Object, default: () => {}})
const formProps = defineModel('formProps', {type: Object, default: () => {}})
const html = defineModel('html', {type: String, default: ''})
const ts = defineModel('ts', {type: String, default: ''})

onMounted(() => {
})

const dataFormRef = ref()
const formData = ref({})
const deleteC = (item) => {
  formItemList.value.splice(formItemList.value.indexOf(item), 1)
  setTimeout(() => { current.value = {} }, 10)
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
  console.log(10, formData.value)
  dataFormRef.value.validate((valid) => {
    if (valid) {
      console.log(11, formData.value)
    }
  })
}
const clearInterval = () => {dataFormRef.value.resetFields()}

const getRules = (attrs: any) => {
  let result = []
  if (attrs.rules && attrs.rules.length > 0) {
    result = attrs.rules.map(item => {
      const rule = {...item}
      if (rule.required === 'pattern' && rule.pattern) {
        delete rule.required
        delete rule.validator
        rule.pattern = new RegExp(rule.pattern.slice(1, -1))
      } else if (rule.required === 'validator' && rule.validator) {
        delete rule.required
        delete rule.pattern
        delete rule.message
        rule.validator = eval(rule.validator)
      }
      return rule
    })
  }
  return result
}
</script>

<style lang="less" scoped>
.isActived {
  position: relative;
  background-color: var(--el-fill-color-light);
}
</style>