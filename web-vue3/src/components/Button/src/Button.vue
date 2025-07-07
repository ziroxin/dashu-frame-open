<template>
  <el-button :class="`${prefixCls} color-#fff`" v-bind="{ ...props }"
             :color="color" :style="style" @click="() => emits('click')">
    <slot></slot>
    <slot name="icon"></slot>
    <slot name="loading"></slot>
  </el-button>
</template>
<script setup lang="ts">
import { useDesign } from '@/hooks/web/useDesign'
import { useAppStore } from '@/store/modules/app'
import { ButtonType, ComponentSize } from 'element-plus'

const prefixCls = useDesign().getPrefixCls('button')

const props = defineProps({
  size: {type: String as PropType<ComponentSize>, default: undefined},
  type: {type: String as PropType<ButtonType>, default: 'default'},
  disabled: {type: Boolean, default: false},
  plain: {type: Boolean, default: false},
  text: {type: Boolean, default: false},
  bg: {type: Boolean, default: false},
  link: {type: Boolean, default: false},
  round: {type: Boolean, default: false},
  circle: {type: Boolean, default: false},
  loading: {type: Boolean, default: false},
  loadingIcon: {type: [String, Object] as PropType<string | Component>, default: undefined},
  icon: {type: [String, Object] as PropType<string | Component>, default: undefined},
  autofocus: {type: Boolean, default: false},
  nativeType: {type: String as PropType<'button' | 'submit' | 'reset'>, default: 'button'},
  autoInsertSpace: {type: Boolean, default: false},
  color: {type: String, default: ''},
  darker: {type: Boolean, default: false},
  tag: {type: [String, Object] as PropType<string | Component>, default: 'button'}
})

const emits = defineEmits(['click'])

const getTheme = computed(() => useAppStore().getTheme)
const color = computed(() => (props.type === 'primary' && !props.link) ? unref(getTheme).elColorPrimary : '')
const style = computed(() => (props.type === 'primary' && !props.link) ?
    '--el-button-text-color: #fff;--el-button-hover-text-color: #fff;' : '')
</script>
