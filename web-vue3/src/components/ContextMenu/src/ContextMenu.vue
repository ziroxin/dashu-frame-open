<template>
  <!-- 右键菜单面板（标签页使用） -->
  <el-dropdown ref="elDropdownMenuRef" :class="prefixCls" :trigger="trigger" placement="bottom-start"
               @command="command" @visible-change="visibleChange" popper-class="v-context-menu-popper">
    <slot></slot>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-for="(item, index) in schema" :key="`dropdown${index}`"
                          :divided="item.divided" :disabled="item.disabled" :command="item">
          <my-icon :icon="item.icon"/>
          {{ t(item.label) }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { useDesign } from '@/hooks/web/useDesign'

interface ContextMenuSchema {
  disabled?: boolean
  divided?: boolean
  icon?: string
  label: string
  command?: (item: ContextMenuSchema) => void
}

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('context-menu')

const props = defineProps({
  schema: {type: Array, default: () => []},
  trigger: {type: String, default: 'contextmenu'},
  tagItem: {type: Object, default: () => ({})}
})

const command = (item: ContextMenuSchema) => {
  item.command && item.command(item)
}

const emit = defineEmits(['visibleChange'])
const visibleChange = (visible: boolean) => {
  emit('visibleChange', visible, props.tagItem)
}

const elDropdownMenuRef = ref<ComponentRef<typeof ElDropdown>>()
defineExpose({
  elDropdownMenuRef,
  tagItem: props.tagItem
})
</script>
