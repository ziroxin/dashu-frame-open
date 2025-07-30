<template>
  <div :class="prefixCls" class="flex flex-wrap space-x-14px">
    <span v-for="(item, i) in schema" :key="`radio-${i}`"
          class="w-20px h-20px cursor-pointer rounded-2px border-solid border-gray-300 border-2px text-center leading-20px mb-5px"
          :class="{'is-active':colorVal===item}" :style="{background:item}" @click="colorVal=item">
      <my-icon v-if="colorVal===item" icon="vi-ep:check" :size="16"
               :class="item==='#ffffff'?'color-[var(--el-color-primary)]!':'color-white!'"/>
    </span>
  </div>
</template>
<script setup lang="ts">
import { PropType, ref, unref, watch } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { useDesign } from '@/hooks/web/useDesign'

const prefixCls = useDesign().getPrefixCls('color-radio-picker')

const props = defineProps({
  schema: {type: Array as PropType<string[]>, default: () => []},
  modelValue: propTypes.string.def('')
})
const emit = defineEmits(['update:modelValue', 'change'])
const colorVal = ref(props.modelValue)

// 监听
watch(() => props.modelValue, (val: string) => { if (val === unref(colorVal)) return colorVal.value = val })
watch(() => colorVal.value, (val: string) => {
  emit('update:modelValue', val)
  emit('change', val)
})
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-color-radio-picker';
.@{prefix-cls} {
  .is-active {
    border-color: var(--el-color-primary);
  }
}
</style>
