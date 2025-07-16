<template>
  <!-- 使用 VueJsonPretty 组件来显示和编辑 JSON 数据 -->
  <vue-json-pretty v-model:data="localModelValue"
                   :deep="deep"
                   :show-length="showLength"
                   :show-line-numbers="showLineNumbers"
                   :show-line-number="showLineNumber"
                   :show-icon="showIcon"
                   :show-double-quotes="showDoubleQuotes"
                   :virtual="virtual"
                   :height="height"
                   :item-height="itemHeight"
                   :root-path="rootPath"
                   :node-selectable="nodeSelectable"
                   :selectable-type="selectableType"
                   :show-select-controller="showSelectController"
                   :select-on-click-node="selectOnClickNode"
                   :highlight-selected-node="highlightSelectedNode"
                   :collapsed-on-click-brackets="collapsedOnClickBrackets"
                   :render-node-key="renderNodeKey"
                   :render-node-value="renderNodeValue"
                   :editable="editable"
                   :editable-trigger="editableTrigger"
                   @node-click="nodeClick"
                   @brackets-click="bracketsClick"
                   @icon-click="iconClick"
                   @selected-change="selectedChange"/>
</template>

<script setup lang="ts">
import VueJsonPretty from 'vue-json-pretty'
import 'vue-json-pretty/lib/styles.css'
import { propTypes } from '@/utils/propTypes'

// 定义回调事件（触发）
const emits = defineEmits(['update:modelValue', 'node-click', 'brackets-click', 'icon-click', 'selected-value'])
// 传递参数
const props = defineProps({
  modelValue: {type: Object, default: () => {}},
  deep: propTypes.number.def(5), // JSON 数据的深度，默认为 5 级
  showLength: propTypes.bool.def(true), // 是否显示数组或对象的长度
  showLineNumbers: propTypes.bool.def(true), // 是否显示行号
  showLineNumber: propTypes.bool.def(true), // 是否显示行号（注意：此属性与 showLineNumbers 重复）
  showIcon: propTypes.bool.def(true), // 是否显示节点图标
  showDoubleQuotes: propTypes.bool.def(false), // 是否显示双引号
  virtual: propTypes.bool.def(false), // 是否使用虚拟滚动
  height: propTypes.number.def(400), // 组件的高度，默认为 400px
  itemHeight: propTypes.number.def(20), // 每个节点的高度，默认为 20px
  rootPath: propTypes.string.def('root'), // 根节点的路径名称
  nodeSelectable: propTypes.func.def(), // 节点是否可选的函数
  selectableType: propTypes.oneOf<'multiple' | 'single'>(['multiple', 'single']).def(), // 选择类型，单选或多选
  showSelectController: propTypes.bool.def(false), // 是否显示选择控制器
  selectOnClickNode: propTypes.bool.def(true), // 点击节点时是否自动选择
  highlightSelectedNode: propTypes.bool.def(true), // 选择节点时是否高亮显示
  collapsedOnClickBrackets: propTypes.bool.def(true), // 点击括号时是否折叠/展开
  renderNodeKey: propTypes.func.def(), // 自定义渲染节点键的函数
  renderNodeValue: propTypes.func.def(), // 自定义渲染节点值的函数
  editable: propTypes.bool.def(true), // 是否允许编辑 JSON 数据
  editableTrigger: propTypes.oneOf<'click' | 'dblclick'>(['click', 'dblclick']).def('click') // 编辑触发方式，单击或双击
})
// 计算属性，用于处理 modelValue 的双向绑定
const localModelValue = computed({
  get: () => props.modelValue, // 获取 modelValue 的值
  set: (val) => {
    console.log(val) // 当值更新时，打印新值到控制台
    emits('update:modelValue', val) // 触发 update:modelValue 事件以更新父组件的 modelValue
  }
})
// 节点点击事件处理函数
const nodeClick = (node: any) => { emits('node-click', node) }
// 括号点击事件处理函数
const bracketsClick = (collapsed: boolean) => { emits('brackets-click', collapsed) }
// 图标点击事件处理函数
const iconClick = (collapsed: boolean) => { emits('icon-click', collapsed) }
// 选择改变事件处理函数
const selectedChange = (newVal: any, oldVal: any) => {
  console.log(newVal, oldVal)
  emits('selected-value', newVal, oldVal)
}
</script>
