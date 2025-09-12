<template>
  <div class="my-10px">
    <div class="mb-10px flex justify-between items-center">
      <span class="text-16px color-#666">批量添加字段</span>
      <span class="text-12px color-[--el-color-primary] mx-10px">
        从 navicat->设计表->复制整行字段，粘贴至下方文本框，自动识别（已适配 Navicat v12/v17）
      </span>
      <base-button type="danger" plain icon="el-icon-close" size="small" @click="itemStr=''">清空</base-button>
    </div>
    <el-input type="textarea" v-model="itemStr" @input="inputChange" :rows="4" clearable/>
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
      <table-field v-model:visible="formItemAddBatchVisible"
                   v-model="fieldData" v-model:formItemList="formItemList"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { underlineToHump } from '@/utils'
import { generateUUID } from '@/utils/tools'
import TableField from '@/views/generator/panel/TableField'

// 表单列表数据
const formItemList = defineModel('formItemList', {type: Object, default: () => {}})
// 显示状态
const formItemAddBatchVisible = defineModel()
// 字段信息
const itemStr = ref('')
const colLen = ref(0)
const colIndexList = ref({fieldIdx: 0, typeIdx: 1, labelIdx: 11, requiredIdx: 4})
const fieldData: any = ref([])
const inputChange = (val) => {
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
</script>