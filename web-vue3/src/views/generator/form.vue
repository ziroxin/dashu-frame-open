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
              代码在线编辑（开发中...）
            </el-tooltip>
          </div>
          <!-- 顶右-操作按钮 -->
          <div class="flex items-center">
            <el-divider direction="vertical"/>
            <base-button icon="el-icon-edit-outline" link type="primary" @click="openCodeDialog">打开代码</base-button>
            <el-divider direction="vertical"/>
            <base-button icon="el-icon-delete" link type="danger" @click="clearCode">清空</base-button>
          </div>
        </div>
        <!-- 预览模式 -->
        <div class="b-t-1px b-t-dashed b-t-#ccc h-[calc(100vh-var(--top-tool-height))] flex">
          <div class="w-260px bg-[var(--el-fill-color-light)]">
            <!-- 左-菜单栏 -->
            <left-panel v-model="formItemList"/>
          </div>
          <div
              class="m-5px w-[calc(100%-260px)] h-[calc(100%-10px)] overflow-y-auto b-1px b-dashed b-#ccc b-rd-5px">
            <!-- 中-内容区域 -->
            <center-panel v-model="formItemList" v-model:current="current" v-model:formProps="formProps"/>
          </div>
        </div>
      </el-splitter-panel>
      <el-splitter-panel size="300px">
        <!-- 右-表单编辑区 -->
        <right-panel v-model:current="current" v-model:formProps="formProps"/>
      </el-splitter-panel>
    </el-splitter>
    <!-- 代码模式 -->
    <el-dialog v-model="codeDialogVisible" fullscreen class="p-0!"
               header-class="hidden!" footer-class="hidden!" body-class="h-94vh p-0!">
      <code-panel v-if="codeDialogVisible" v-model:codeDialogVisible="codeDialogVisible"
                  v-model="formItemList" v-model:formProps="formProps"/>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import LeftPanel from '@/views/generator/panel/LeftPanel'
import CenterPanel from '@/views/generator/panel/CenterPanel'
import CodePanel from '@/views/generator/panel/CodePanel'
import RightPanel from '@/views/generator/panel/RightPanel'
import storageKeys from '@/utils/storage-keys'
import formConfig from '@/views/generator/panel/config/formConfig'

// ==================== 顶部按钮组start ====================
// 返回按钮
const {push} = useRouter()
const back = () => { push('/generator') }
// 切换代码模式/预览模式
const codeDialogVisible = ref(false)
const openCodeDialog = () => { codeDialogVisible.value = true }
// 清空代码
const clearCode = () => {
  formProps.value = formConfig
  formItemList.value = []
  current.value = {}
  ElMessage({message: '清空代码成功！', type: 'success', grouping: true})
  setTimeout(() => { location.reload() }, 100)
}
// ==================== 顶部按钮组end ====================

// ==================== 表单属性start ====================
// 表单属性
const formProps = ref(JSON.parse(localStorage.getItem(storageKeys.l_formProps)) || formConfig)
// 监听表单属性修改
watch(() => formProps.value, (val) => {
  if (val) {
    localStorage.setItem(storageKeys.l_formProps, JSON.stringify(val))
  } else {
    localStorage.removeItem(storageKeys.l_formProps)
  }
}, {immediate: true, deep: true})
// ==================== 表单属性end ====================

// ==================== 表单项start ====================
// 中间内容-表单项列表
const formItemList = ref(JSON.parse(localStorage.getItem(storageKeys.l_formItemList)) || [])
// 当前选中表单项
const current = ref({})
// 监听表单项列表修改
watch(() => formItemList.value, (val) => {
  if (val && val.length > 0) {
    localStorage.setItem(storageKeys.l_formItemList, JSON.stringify(val))
  } else {
    current.value = {}
    localStorage.removeItem(storageKeys.l_formItemList)
  }
}, {immediate: true, deep: true})
// 监听当前选中表单项属性修改
watch(() => current.value, (val) => {
  if (val) {
    formItemList.value.forEach((item, index) => {
      if (item.__id === val.__id) formItemList.value[index] = {...item, ...val}
    })
  }
}, {immediate: true, deep: true})
// ==================== 表单项end ====================
</script>

<style lang="less" scoped>
</style>