<template>
  <el-table :data="fieldData" stripe border>
    <el-table-column label="唯一ID" prop="__id" align="center" show-overflow-tooltip>
      <template #default="scope">
        <base-button type="danger" icon="el-icon-delete" link size="small"
                     @click="fieldData.splice(scope.$index,1)">删除
        </base-button>
        {{ scope.row.__id }}
      </template>
    </el-table-column>
    <el-table-column label="字段名" prop="__modelName" align="center">
      <template #default="scope">
        <el-input v-model="scope.row.__modelName" size="small" :key="'name'+scope.$index"/>
      </template>
    </el-table-column>
    <el-table-column label="Label" prop="__label" align="center">
      <template #default="scope">
        <el-input v-model="scope.row.__label" size="small" :key="'label'+scope.$index"/>
      </template>
    </el-table-column>
    <el-table-column label="必填" prop="__required" align="center">
      <template #default="scope">
        <el-switch v-model="scope.row.__required" active-text="必填" inactive-text="非必填"
                   size="small" :key="'required'+scope.$index"/>
      </template>
    </el-table-column>
    <el-table-column label="组件类型" prop="__type" align="center">
      <template #default="scope">
        <el-select v-model="scope.row.__type" size="small" :key="'type'+scope.$index">
          <el-option-group v-for="group in componentList" :key="group.name" :label="group.name">
            <el-option v-for="item in group.list" :key="item.__key" :label="item.__name" :value="item.__key">
              <my-icon v-if="item.__icon" :icon="item.__icon"/>
              {{ item.__name }}
            </el-option>
          </el-option-group>
        </el-select>
        <el-select v-if="scope.row.__type==='el-input'" v-model="scope.row.__type2"
                   size="small" :key="'type2'+scope.$index">
          <el-option label="text" value="text"/>
          <el-option label="textarea" value="textarea"/>
          <el-option label="password" value="password"/>
        </el-select>
        <el-select v-if="scope.row.__type==='el-date-picker'" v-model="scope.row.__type2"
                   size="small" :key="'type2'+scope.$index">
          <el-option label="date" value="date"/>
          <el-option label="datetime" value="datetime"/>
        </el-select>
      </template>
    </el-table-column>
  </el-table>
  <el-divider/>
  <div class="flex justify-center items-center">
    <base-button type="primary" icon="el-icon-check" @click="saveItems">保存</base-button>
    <base-button icon="el-icon-close" @click="visible=false">取消</base-button>
  </div>
</template>

<script setup lang="ts">
import { cloneDeep } from 'lodash-es'
import { ElMessage } from 'element-plus'
import allConfig from '@/views/generator/panel/config/allConfig'

// 组件列表
const componentList: any = cloneDeep(allConfig)

const fieldData = defineModel()
// 显示状态
const visible = defineModel('visible', {type: Boolean, default: true})
// 表单列表数据
const formItemList = defineModel('formItemList', {type: Object, default: () => {}})

// 保存
const saveItems = () => {
  if (fieldData.value.some(item => !item.__modelName)) {
    ElMessage({message: '字段名不能为空！', type: 'error', grouping: true})
    return
  }
  if (fieldData.value.some(item => formItemList.value.some(field => field.__id === item.__id))) {
    ElMessage({message: '组件ID不能重复！', type: 'error', grouping: true})
    return
  }
  if (fieldData.value.some(item => formItemList.value.some(field => field.__modelName === item.__modelName))) {
    ElMessage({message: '字段名不能重复！', type: 'error', grouping: true})
    return
  }
  const result: any[] = []
  const list = componentList.flatMap(g => g.list.map(c => ({...c, parentType: g.type})))
  fieldData.value.forEach(item => {
    const cp = cloneDeep(list.find(c => c.__key === item.__type))
    cp.__id = item.__id
    cp.__modelName = item.__modelName
    cp.__name = item.__label
    cp.__formItemAttrs.label = item.__label
    cp.__formItemAttrs.rules = item.__required ? [{required: true, message: item.__label + '不能为空'}] : []
    if (item.__type2) {
      cp.__attrs.type = item.__type2
      if (item.__type === 'el-date-picker') {
        cp.__attrs.format = item.__type2 === 'datetime' ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'
        cp.__attrs.valueFormat = item.__type2 === 'datetime' ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'
      }
    }
    if (cp.parentType === 'input') {
      cp.__attrs.placeholder = item.__label ? '请输入' + item.__label : cp.__attrs.placeholder
    } else if (cp.parentType === 'select') {
      cp.__attrs.placeholder = item.__label ? '请选择' + item.__label : cp.__attrs.placeholder
    }
    result.push(cp)
  })
  formItemList.value.push(...result)
  ElMessage({message: '操作成功！', type: 'success', grouping: true})
  visible.value = false
}
</script>