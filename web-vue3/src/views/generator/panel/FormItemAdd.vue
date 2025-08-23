<template>
  <el-dialog v-model="addDialogVisible" @close="addDialogVisible=false" title="添加表单项">
    <el-form ref="addFormRef" :model="addItem" label-width="auto">
      <el-form-item label="唯一ID" prop="__id" :rules="[{ required: true, message: '唯一ID不能为空' }]">
        <el-input v-model="addItem.__id" placeholder="请输入唯一ID" clearable/>
      </el-form-item>
      <el-form-item label="字段名" prop="__modelName" :rules="[{ required: true, message: '字段名不能为空' }]">
        <el-input v-model="addItem.__modelName" placeholder="请输入字段名" clearable/>
      </el-form-item>
      <el-form-item label="Label" prop="label" :rules="[]">
        <el-input v-model="addItem.__formItemAttrs.label" placeholder="请输入Label" clearable/>
      </el-form-item>
      <el-divider>表单规则（tips:允许无规则，允许多个规则）</el-divider>
      <form-item-rules v-model="addItem.__formItemAttrs.rules"/>
    </el-form>
    <template #footer>
      <div class="dialog-footer b-t-1px b-t-solid b-t-#ccc pt-10px">
        <base-button type="primary" icon="el-icon-check" @click="addClick">确定添加</base-button>
        <base-button type="default" icon="el-icon-close" @click="addDialogVisible=false">取消</base-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import FormItemRules from '@/views/generator/panel/FormItemRules'
import { generateUUID } from '@/utils/tools'

const addDialogVisible = defineModel()
const addItem = defineModel('addItem', {type: Object, default: () => {}})
const formItemList = defineModel('formItemList', {type: Object, default: () => {}})
onMounted(() => {
  addItem.value = {...addItem.value, __id: generateUUID(), __modelName: ''}
})

const addFormRef = ref()
const addClick = () => {
  addFormRef.value.validate((valid) => {
    if (valid) {
      if (formItemList.value.some(item => item.__id === addItem.value.__id)) {
        ElMessage({message: '组件ID重复，请修改！', type: 'success', grouping: true})
        return
      }
      if (formItemList.value.some(item => item.__modelName === addItem.value.__modelName)) {
        ElMessage({message: '字段名称重复，请修改！', type: 'success', grouping: true})
        return
      }
      formItemList.value.push({...addItem.value})
      addDialogVisible.value = false
    }
  })
}
</script>

<style lang="less" scoped>

</style>