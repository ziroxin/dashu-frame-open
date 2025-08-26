<template>
  <div class="w-full h-full">
    <div class="flex justify-between items-center b-b-1px b-b-solid b-b-#ccc px-10px">
      <el-tabs v-model="currentTab" stretch class="w-320px h-39px!">
        <el-tab-pane name="json" label="JSON"/>
        <el-tab-pane name="html" label="HTML"/>
        <el-tab-pane name="ts" label="TypeScript"/>
      </el-tabs>
      <div class="flex items-center">
        <span class="text-12px color-gray mr-5px">主题：</span>
        <el-select v-model="theme" class="w-120px!" placeholder="请选择主题样式" size="small">
          <el-option v-for="item in ['vs','vs-dark','hc-black','hc-light']"
                     :key="item" :label="item" :value="item"/>
        </el-select>
      </div>
    </div>
    <div class="mt-10px w-full h-[calc(100%-50px)] flex">
      <div v-show="currentTab==='json'" ref="jsonEditorRef" class="flex-1"></div>
      <div v-show="currentTab==='html'" ref="htmlEditorRef" class="flex-1"></div>
      <div v-show="currentTab==='ts'" ref="tsEditorRef" class="flex-1"></div>
    </div>
  </div>
</template>

<script setup lang="tsx">
import { changeTheme, createEditor, updateEditorVal } from './helper'
// Tab类型
const currentTab = ref('json')
// 主题切换
const theme = ref('vs')
watch(theme, () => { changeTheme(theme.value) })

// 编辑器内容
const props = defineProps({
  json: {type: Object, default: () => {}},
  html: {type: String, default: ''},
  ts: {type: String, default: ''}
})
// 编辑器
const jsonEditorRef = ref<HTMLElement>()
const htmlEditorRef = ref<HTMLElement>()
const tsEditorRef = ref<HTMLElement>()

// 初始化editor
let jsonEditor, htmlEditor, tsEditor
onMounted(() => {
  // json编辑器
  jsonEditor = createEditor(jsonEditorRef, 'json')
  updateValue(jsonEditor, `${JSON.stringify(props.json)}`)
  // html编辑器
  htmlEditor = createEditor(htmlEditorRef, 'html')
  updateValue(htmlEditor, props.html)
  // typescript编辑器
  tsEditor = createEditor(tsEditorRef, 'typescript')
  updateValue(tsEditor, props.ts)
})
// 监听 modelValue 变化
watch(() => props.json, () => { updateValue(jsonEditor, JSON.stringify(props.json)) })
watch(() => props.html, () => { updateValue(htmlEditor, props.html) })
watch(() => props.ts, () => { updateValue(tsEditor, props.ts) })

// 更新编辑器内容
const updateValue = (editor: any, val: string) => {
  if (val !== editor?.getValue()) {
    updateEditorVal(editor, val)
  }
}
</script>
