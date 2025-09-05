<template>
  <div class="my-10px">
    <div class="mb-10px flex justify-between items-center">
      <span class="text-16px color-#666">批量添加字段</span>
      <span class="text-12px color-[--el-color-primary] mx-10px">
        从 navicat->设计表->复制整行字段，粘贴至下方文本框，自动识别
      </span>
      <base-button type="danger" plain icon="el-icon-close" size="small" @click="itemStr=''">清空</base-button>
    </div>
    <el-input type="textarea" v-model="itemStr" @input="change" :rows="4" clearable/>
    <!-- 列数据 -->
    <el-divider>表单数据</el-divider>
    <div class="w-97% mx-auto">
      <div class="mb-5px pb-5px grid grid-cols-[repeat(5,1fr)] gap-5px">
        <template v-if="colLen>0">
          <!-- 列号 -->
          <div class="text-center text-12px color-[--el-color-primary] lh-22px">选择第几列：</div>
          <div class="flex justify-center items-center text-12px">
            <span>字段名列：</span>
            <el-select v-model="colIndexList.fieldIdx" size="small" class="w-80px!">
              <el-option v-for="idx in colLen" :key="idx" :label="idx-1" :value="idx-1"/>
            </el-select>
          </div>
          <div class="flex justify-center items-center text-12px">
            <span>Label列：</span>
            <el-select v-model="colIndexList.labelIdx" size="small" class="w-80px!">
              <el-option v-for="idx in colLen" :key="idx" :label="idx-1" :value="idx-1"/>
            </el-select>
          </div>
          <div class="flex justify-center items-center text-12px">
            <span>必填列：</span>
            <el-select v-model="colIndexList.requiredIdx" size="small" class="w-80px!">
              <el-option v-for="idx in colLen" :key="idx" :label="idx-1" :value="idx-1"/>
            </el-select>
          </div>
          <div class="flex justify-center items-center text-12px">
            <span>组件类型列：</span>
            <el-select v-model="colIndexList.typeIdx" size="small" class="w-80px!">
              <el-option v-for="idx in colLen" :key="idx" :label="idx-1" :value="idx-1"/>
            </el-select>
          </div>
        </template>
      </div>
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
    </div>
    <el-divider/>
    <div class="flex justify-center items-center">
      <base-button type="primary" icon="el-icon-check" @click="saveItems">保存</base-button>
      <base-button icon="el-icon-close" @click="formItemAddBatchVisible=false">取消</base-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { underlineToHump } from '@/utils'
import { generateUUID } from '@/utils/tools'
import allConfig from '@/views/generator/panel/config/allConfig'
// 组件列表
const componentList: any = ref(allConfig)
// 显示状态
const formItemAddBatchVisible = defineModel()
// 表单列表数据
const formItemList = defineModel('formItemList', {type: Object, default: () => {}})

// 字段信息
const itemStr = ref('')
const colLen = ref(0)
const colIndexList = ref({fieldIdx: 0, typeIdx: 1, labelIdx: 11, requiredIdx: 4})
const fieldData: any = ref([])
const change = (val) => {
  const result1: any[] = []
  val.split('\n').forEach(rowData => {
    const cols = rowData.split('\t')
    if (cols && cols.length > 1) result1.push(cols)
  })
  colLen.value = result1.length > 0 ? result1[0].length : 0
  const result2: any[] = []
  if (result1.length > 0 && colLen.value > 0) {
    for (let rowIdx = 0; rowIdx < result1.length; rowIdx++) {
      if (result1[rowIdx].join(',').startsWith('名称,类型,长度,小数点')) {
        continue
      }
      const __type1 = getComponentType(result1[rowIdx][colIndexList.value.typeIdx])
      const __required1 = result1[rowIdx][colIndexList.value.requiredIdx]
      result2.push({
        __id: generateUUID(),
        __modelName: underlineToHump(result1[rowIdx][colIndexList.value.fieldIdx]) || '',
        __label: result1[rowIdx][colIndexList.value.labelIdx] || '',
        __required: __required1 === 'true' || __required1 === '0' ? true : false,
        __type: __type1,
        ...(__type1 === 'el-input' ? {__type2: 'text'} : __type1 === 'el-date-picker' ? {__type2: 'date'} : {})
      })
    }
  }
  fieldData.value = result2
}

// 获取组件类型
const getComponentType = (type) => {
  if (['bigint', 'decimal', 'double', 'float', 'int', 'integer', 'smallint', 'tinyint'].includes(type.toLowerCase())) {
    return 'el-input-number'
  } else if (['bool', 'boolean'].includes(type.toLowerCase())) {
    return 'el-switch'
  } else if (['date', 'datetime'].includes(type.toLowerCase())) {
    return 'el-date-picker'
  } else {
    return 'el-input'
  }
}
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
  const list = componentList.value.flatMap(g => g.list.map(c => ({...c, parentType: g.type})))
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
  formItemAddBatchVisible.value = false
}
</script>