<template>
  <!-- 密码输入框（带密码强度显示） -->
  <div :class="[prefixCls, `${prefixCls}--${useConfigGlobal().configGlobal?.size}`]">
    <el-input v-bind="$attrs" v-model="modelValue" showPassword type="password"/>
    <div v-if="showStrength" :class="`${prefixCls}__bar`" class="relative h-6px mt-10px mb-6px mr-auto ml-auto">
      <div :class="`${prefixCls}__bar--fill`" :data-score="getPasswordStrength"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { zxcvbn } from '@zxcvbn-ts/core'
import { useConfigGlobal } from '@/hooks/web/useConfigGlobal'
import { useDesign } from '@/hooks/web/useDesign'
// 全局css
const prefixCls = useDesign().getPrefixCls('input-password')

// 传入参数
const modelValue = defineModel<string>()
const props = defineProps({
  // 是否显示密码强度
  showStrength: {type: Boolean, default: true}
})

// 获取密码强度
const getPasswordStrength = computed(() => {
  return modelValue.value ? zxcvbn(modelValue.value).score : -1
})
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-input-password';

.@{prefix-cls} {
  :deep(.@{elNamespace}-input__clear) {
    margin-left: 5px;
  }

  &__bar {
    background-color: var(--el-text-color-disabled);
    border-radius: var(--el-border-radius-base);

    &::before,
    &::after {
      position: absolute;
      z-index: 10;
      display: block;
      width: 20%;
      height: inherit;
      background-color: transparent;
      border-color: var(--el-color-white);
      border-style: solid;
      border-width: 0 5px;
      content: '';
    }

    &::before {
      left: 20%;
    }

    &::after {
      right: 20%;
    }

    &--fill {
      position: absolute;
      width: 0;
      height: inherit;
      background-color: transparent;
      border-radius: inherit;
      transition: width 0.5s ease-in-out,
      background 0.25s;

      &[data-score='0'] {
        width: 20%;
        background-color: var(--el-color-danger);
      }

      &[data-score='1'] {
        width: 40%;
        background-color: var(--el-color-danger);
      }

      &[data-score='2'] {
        width: 60%;
        background-color: var(--el-color-warning);
      }

      &[data-score='3'] {
        width: 80%;
        background-color: var(--el-color-success);
      }

      &[data-score='4'] {
        width: 100%;
        background-color: var(--el-color-success);
      }
    }
  }

  &--mini > &__bar {
    border-radius: var(--el-border-radius-small);
  }
}
</style>
