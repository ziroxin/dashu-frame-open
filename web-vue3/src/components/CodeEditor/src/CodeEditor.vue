<template>
  <div class="w-full h-full">
    <div class="my-10px text-left">
      <el-select v-model="currentTheme" class="w-200px!" placeholder="请选择主题样式">
        <el-option v-for="item in themeOptions" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-tag class="ml-10px">请把需要编辑的vue页面代码，直接粘贴至下方编辑器，切换预览模式即可看到效果。</el-tag>
    </div>
    <div ref="monacoEditorRef" class="b-1px b-dashed b-#ccc w-full h-[calc(100%-50px)]"></div>
  </div>
</template>

<script setup lang="tsx">
import { changeTheme, createEditor, getEditor, updateEditorVal } from './helper'

const props = defineProps({
  // 编辑器内容
  modelValue: {type: String, default: ''},
  // 主题
  theme: {type: String, default: 'vs-dark'},
  // 更多选项
  options: {type: Object, default: () => ({})}
})

// 编辑器
const monacoEditorRef = ref<HTMLElement>()
const emits = defineEmits(['blur', 'update:modelValue'])
onMounted(() => {
  // 初始化editor
  const editor = createEditor(monacoEditorRef, props.options)
  // 更新内容
  updateValue(props.modelValue)
  // 监听内容变化
  editor?.onDidChangeModelContent(() => { emits('update:modelValue', editor!.getValue()) })
  // 监听失焦
  editor?.onDidBlurEditorText(() => { emits('blur') })
})

// 监听 modelValue 变化
watch(() => props.modelValue, () => { updateValue(props.modelValue) })
// 更新编辑器内容
const updateValue = (val: string) => { if (val !== getEditor()?.getValue()) updateEditorVal(val) }

// 主题选项
const themeOptions = [
  {label: 'vs', value: 'vs'}, {label: 'vs-dark', value: 'vs-dark'},
  {label: 'hc-black', value: 'hc-black'}, {label: 'hc-light', value: 'hc-light'}
]
const currentTheme = ref(props.theme)
watch(currentTheme, (val) => { changeTheme(val) })
</script>
