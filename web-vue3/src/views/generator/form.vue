<template>
  <!-- 顶部-Logo、操作按钮、菜单栏、内容区域 -->
  <div class="h-100vh">
    <el-splitter>
      <el-splitter-panel>
        <div class="flex justify-between items-center h-[var(--top-tool-height)] mx-10px">
          <!-- 顶左-Logo -->
          <base-button icon="el-icon-back" link type="danger" @click="back">返回后台</base-button>
          <div class="ml-10px text-22px font-bold cursor-pointer text-[var(--el-color-primary)]">
            <el-tooltip content="所见即所得，在线编辑页面，代码直接拷贝到vue页面中，即可使用！">
              代码在线编辑
            </el-tooltip>
          </div>
          <!-- 顶右-操作按钮 -->
          <div class="flex items-center">
            <el-divider direction="vertical"/>
            <base-button v-if="codeView" icon="el-icon-view" link type="primary" @click="change">预览模式</base-button>
            <base-button v-else icon="el-icon-edit-outline" link type="primary" @click="change">代码模式</base-button>
            <el-divider direction="vertical"/>
            <base-button icon="el-icon-delete" link type="danger" @click="clearCode">清空</base-button>
          </div>
        </div>
        <!-- 代码模式 -->
        <div v-if="codeView"
             class="b-t-1px b-t-dashed b-t-#ccc w-full h-[calc(100vh-var(--top-tool-height)-10px)]">
          <code-editor v-model:html="htmlCode" v-model:ts="tsCode"/>
        </div>
        <!-- 预览模式 -->
        <template v-else>
          <div class="b-t-1px b-t-dashed b-t-#ccc h-[calc(100vh-var(--top-tool-height))] flex">
            <div class="p-15px w-[var(--left-menu-max-width)] bg-[var(--el-fill-color-light)]">
              <!-- 左-菜单栏 -->
              <left-panel v-model="formItemList"/>
            </div>
            <div
                class="m-5px w-[calc(100%-var(--left-menu-max-width))] h-[calc(100%-10px)] overflow-y-auto b-1px b-dashed b-#ccc b-rd-5px">
              <!-- 中-内容区域 -->
              <center-panel v-model="formItemList" v-model:current="current" v-model:formProps="formProps"
                            v-model:html="htmlCode" v-model:ts="tsCode"/>
            </div>
          </div>
        </template>
      </el-splitter-panel>
      <el-splitter-panel size="300px">
        <!-- 右-表单编辑区 -->
        <right-panel v-model:current="current" v-model:formProps="formProps"/>
      </el-splitter-panel>
    </el-splitter>
  </div>
</template>

<script setup lang="ts">
import { BaseButton } from '@/components/BaseButton'
import { CodeEditor } from '@/components/CodeEditor'
import { ElMessage } from 'element-plus'
import LeftPanel from '@/views/generator/panel/LeftPanel.vue'
import CenterPanel from '@/views/generator/panel/CenterPanel.vue'
import RightPanel from '@/views/generator/panel/RightPanel.vue'
import storageKeys from '@/utils/storage-keys'
import formConfig from '@/views/generator/panel/config/formConfig'
// 返回按钮
const {push} = useRouter()
const back = () => { push('/generator') }
// 切换代码模式/预览模式
const codeView = ref(false)
const change = () => { codeView.value = !codeView.value }

// 中间内容区域组件列表
const formItemList = ref(JSON.parse(localStorage.getItem(storageKeys.l_formItemList)) || [])
const current = ref({})
watch(() => formItemList.value, (val) => {
  if (val && val.length > 0) {
    localStorage.setItem(storageKeys.l_formItemList, JSON.stringify(val))
  } else {
    current.value = {}
    localStorage.removeItem(storageKeys.l_formItemList)
  }
}, {immediate: true, deep: true})
watch(() => current.value, (val) => {
  if (val) {
    formItemList.value.forEach((item, index) => {
      if (item.__id === val.__id) {
        formItemList.value[index] = {...item, ...val}
      }
    })
  }
}, {immediate: true, deep: true})

// 表单属性
const formProps = ref(JSON.parse(localStorage.getItem(storageKeys.l_formProps)) || formConfig)
watch(() => formProps.value, (val) => {
  if (val) {
    localStorage.setItem(storageKeys.l_formProps, JSON.stringify(val))
  } else {
    localStorage.removeItem(storageKeys.l_formProps)
  }
}, {immediate: true, deep: true})


const htmlCode = ref(localStorage.getItem(storageKeys.l_htmlCode) || '')
const tsCode = ref(localStorage.getItem(storageKeys.l_tsCode) || '')
watch(() => htmlCode.value, (val) => { localStorage.setItem(storageKeys.l_htmlCode, val ? val : '')})
watch(() => tsCode.value, (val) => { localStorage.setItem(storageKeys.l_tsCode, val ? val : '')})

// 清空代码
const clearCode = () => {
  formItemList.value = []
  formProps.value = formConfig
  localStorage.removeItem(storageKeys.l_htmlCode)
  htmlCode.value = ''
  localStorage.removeItem(storageKeys.l_tsCode)
  tsCode.value = ''
  current.value = {}
  ElMessage({message: '清空代码成功！', type: 'success', grouping: true})
}
</script>

<style lang="less" scoped>
</style>