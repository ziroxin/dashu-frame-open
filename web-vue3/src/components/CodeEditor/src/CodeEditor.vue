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

// 编辑器Model
const json = defineModel('json', {type: Object, default: () => {}})
const html = defineModel('html', {type: String, default: ''})
const ts = defineModel('ts', {type: String, default: ''})

// 编辑器
const jsonEditorRef = ref<HTMLElement>()
const htmlEditorRef = ref<HTMLElement>()
const tsEditorRef = ref<HTMLElement>()

// 初始化editor
let jsonEditor, htmlEditor, tsEditor
onMounted(() => {
  // json编辑器
  jsonEditor = createEditor(jsonEditorRef, 'json')
  jsonEditor?.onDidChangeModelContent(() => { json.value = jsonEditor!.getValue() })
  updateValue(jsonEditor, `${JSON.stringify(json.value)}`)
  // html编辑器
  htmlEditor = createEditor(htmlEditorRef, 'html')
  htmlEditor?.onDidChangeModelContent(() => { html.value = htmlEditor!.getValue() })
  updateValue(htmlEditor, html.value)
  // typescript编辑器
  tsEditor = createEditor(tsEditorRef, 'typescript')
  tsEditor?.onDidChangeModelContent(() => { ts.value = tsEditor!.getValue() })
  updateValue(tsEditor, ts.value)
})

// 更新编辑器内容
const updateValue = (editor: any, val: string) => {
  if (val !== editor?.getValue()) {
    updateEditorVal(editor, val)
  }
}

// 监听 modelValue 变化
watch(() => json.value, () => { updateValue(jsonEditor, json.value) })
watch(() => html.value, () => { updateValue(htmlEditor, html.value) })
watch(() => ts.value, () => { updateValue(tsEditor, ts.value) })

// 主题切换
const theme = ref('vs')
watch(theme, () => { changeTheme(theme.value) })
</script>
