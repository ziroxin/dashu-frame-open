<template>
  <div ref="editorRef"></div>
</template>

<script setup lang="tsx">
import { changeLanguage, changeTheme, createEditor, getEditor, updateEditorVal } from './helper'
// 接收参数
const props = defineProps({
  theme: {type: String, default: 'vs'},
  language: {type: String, default: 'html'},
  content: {type: String, default: ''}
})
// 编辑器
const editorRef = ref<HTMLElement>()
onMounted(() => {
  createEditor(editorRef, props.theme, props.language)
  updateValue(props.content)
})
// 监听切换主题
watch(() => props.theme, (newVal) => { changeTheme(newVal) })
// 监听切换语言
watch(() => props.language, (newVal) => { changeLanguage(newVal) })
// 监听内容变更
watch(() => props.content, (newVal) => { updateValue(newVal) })

// 更新内容方法
const updateValue = (val: string) => {
  if (val !== getEditor()?.getValue()) {
    updateEditorVal(typeof val === 'string' ? val : JSON.stringify(val))
  }
}
</script>
