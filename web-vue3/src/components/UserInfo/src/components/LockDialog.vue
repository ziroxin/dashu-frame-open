<template>
  <Dialog v-model="dialogVisible" width="500px" max-height="170px" :class="prefixCls" :title="t('lock.lockScreen')">
    <div class="flex flex-col items-center">
      <img src="@/assets/imgs/avatar.jpg" alt="" class="w-70px h-70px rounded-[50%]"/>
      <span class="text-14px my-10px text-[var(--top-header-text-color)]">Archer</span>
    </div>
    <el-form ref="lockFormRef" :model="formData">
      <el-form-item :label="t('lock.lockPassword')" :rules="[{required:true, message: t('common.required')}]">
        <el-input ref="passwordRef" type="password" v-model="formData.password"
                  show-password @keydown.enter.stop="handleLock"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <base-button type="primary" @click="handleLock">{{ t('lock.lock') }}</base-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { Dialog } from '@/components/Dialog'
import { useDesign } from '@/hooks/web/useDesign'
import { useLockStore } from '@/store/modules/lock'

const prefixCls = useDesign().getPrefixCls('lock-dialog')
const {t} = useI18n()

// 双向绑定弹窗的显示/隐藏
const {modelValue} = defineProps({modelValue: {type: Boolean}})
const emit = defineEmits(['update:modelValue'])
// 弹窗是否显示
const dialogVisible = computed({get: () => modelValue, set: (val) => { emit('update:modelValue', val) }})

// 表单
const formData = ref({password: ''})
const lockFormRef = ref()
const passwordRef = ref()
// 打开弹窗自动聚焦输入框
watch(dialogVisible, async (val) => {
  if (val) {
    setTimeout(() => { passwordRef.value?.focus() }, 10)
  }
}, {immediate: true})

// 锁定屏幕
const handleLock = async () => {
  lockFormRef.value.validate((isValid) => {
    if (isValid) {
      dialogVisible.value = false
      useLockStore().setLockInfo({isLock: true, ...formData.value})
    }
  })
}
</script>

<style lang="less" scoped>
:global(.v-lock-dialog) {
  @media (width <= 767px) {
    max-width: calc(100vw - 16px);
  }
}
</style>
