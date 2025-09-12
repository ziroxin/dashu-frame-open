<template>
  <el-dialog title="导入表" v-model="visible" width="95%" top="5vh">
    <template v-if="fieldData&&fieldData.length>0">
      <base-button type="danger" plain icon="el-icon-back" @click="()=>{fieldData=[]}"
                   class="mb-10px">返回重新选择表格
      </base-button>
      <table-field v-model:visible="visible"
                   v-model="fieldData" v-model:formItemList="formItemList"/>
    </template>
    <el-table v-else :data="myImportTableList" border>
      <el-table-column label="表名称" prop="tableName"/>
      <el-table-column label="表描述" prop="tableComment"/>
      <el-table-column label="操作" width="200" align="center">
        <template v-slot="scope">
          <el-button type="text" size="small" @click="tableImportToForm(scope.row)">准备导入</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { generateUUID } from '@/utils/tools'
import { underlineToHump } from '@/utils'
import request from '@/utils/request'
import TableField from '@/views/generator/panel/TableField'

const visible = defineModel()
// 表单列表数据
const formItemList = defineModel('formItemList', {type: Object, default: () => {}})

// 导入表列表
const myImportTableList = ref([])
const isLoading = ref(false)
const loadTableList = () => {
  isLoading.value = true
  request({url: '/generator/code/tableList', method: 'get'}).then((response) => {
    myImportTableList.value = response.data
    isLoading.value = false
  })
}
loadTableList()

// 列表字段数据
const fieldData: any = ref([])
const tableImportToForm = (row) => {
  isLoading.value = true
  // 1 查询表内所有字段
  const params = {tableName: row.tableName}
  request({url: '/generator/code/tableInfo', method: 'get', params}).then((response) => {
    const result2: any[] = []
    response.data.forEach(item => {
      const __type1 = getComponentType(item.type)
      result2.push({
        __id: generateUUID(),
        __modelName: underlineToHump(item.name) || '',
        __label: item.title || '',
        __required: item.required,
        __type: __type1,
        ...(__type1 === 'el-input' ? {__type2: 'text'} : __type1 === 'el-date-picker' ? {__type2: 'date'} : {})
      })
    })
    fieldData.value = result2
    isLoading.value = true
  })
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