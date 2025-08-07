<template>
  <el-button :class="`${prefixCls}`" v-bind="{ ...props,icon:null }" @click="handleClick">
    <my-icon v-if="icon" :icon="icon" :size="iconSize" :class="hasDefaultSlot?'mr-5px!':''"/>
    <slot></slot>
    <slot name="loading"></slot>
  </el-button>
</template>
<script setup lang="ts">
import { MyIcon } from '@/components/MyIcon'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('button')
// 传参
const props = defineProps({
  size: {type: String, default: undefined},
  type: {type: String, default: 'default'},
  disabled: {type: Boolean, default: false},
  plain: {type: Boolean, default: false},
  text: {type: Boolean, default: false},
  bg: {type: Boolean, default: false},
  link: {type: Boolean, default: false},
  round: {type: Boolean, default: false},
  circle: {type: Boolean, default: false},
  loading: {type: Boolean, default: false},
  loadingIcon: {type: [String, Object], default: undefined},
  icon: {type: String, default: undefined},
  autofocus: {type: Boolean, default: false},
  nativeType: {type: String, default: 'button'},
  autoInsertSpace: {type: Boolean, default: false},
  color: {type: String, default: ''},
  darker: {type: Boolean, default: false},
  tag: {type: [String, Object], default: 'button'},
  iconSize: {type: Number, default: 16}
})

// 默认插槽是否有值
const hasDefaultSlot = computed(() => !!useSlots().default)

// 触发父级组件的点击事件
const emits = defineEmits(['click'])
const handleClick = () => { emits('click') }
</script>
