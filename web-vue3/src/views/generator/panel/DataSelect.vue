<template>
  <el-divider>数据</el-divider>
  <el-form-item label-width="0">
    <el-tabs v-model="current.dataType" class="dataTab w-full"
             type="border-card" stretch @tab-change="dataTypeChange">
      <el-tab-pane label="静态" name="static">
        <template v-if="'static'===current.dataType">
          <el-form-item label="Props" label-width="60px" class="mt-10px">
            <div class="color-#666">默认值不可修改</div>
            <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                         :collapsedOnClickBrackets="false" :editable="false"/>
          </el-form-item>
          <el-form-item label="Options" label-width="60px" class="mt-10px">
            <vue-draggable v-model="current.__attrs.options" :animation="150"
                           :key="'drag'+current.__attrs.options.length">
              <div v-for="(item,idx) in current.__attrs.options" :key="idx" class="flex items-center mb-10px">
                <my-icon icon="el-icon-rank" :size="20" color="#409eff" class="mr-5px cursor-grab"/>
                <el-input v-model="item.label" class="mr-5px"/>
                <el-input v-model="item.value" class="mr-5px"/>
                <base-button type="danger" link icon="el-icon-delete" class="mr-5px" size="small"
                             @click="()=>{current.__attrs.options.splice(idx,1)}"/>
              </div>
            </vue-draggable>
            <base-button type="primary" link icon="el-icon-circle-plus-outline" size="small" class="ml-20px"
                         @click="addStaticData">添加数据
            </base-button>
          </el-form-item>
        </template>
      </el-tab-pane>
      <el-tab-pane label="动态" name="dynamic">
        <template v-if="'dynamic'===current.dataType">
          <el-form-item label="Props 可修改" label-width="55px" class="mt-10px">
            <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                         :collapsedOnClickBrackets="false"/>
          </el-form-item>
          <el-form-item label="接口" label-width="70px" class="mt-10px">
            <el-input v-model="current.dataDynamic.url" placeholder="接口地址">
              <template #prefix>
                <span class="bg-#efefef color-#777 px-10px ml-[-6px]! b-rd-4px">GET</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="数据字段" label-width="70px" class="mt-10px">
            <el-input v-model="current.dataDynamic.dataKey" placeholder="数据字段"/>
          </el-form-item>
          <base-button type="primary" link icon="el-icon-refresh" size="small" class="ml-70px"
                       @click="refreshDynamicData">刷新数据
          </base-button>
        </template>
      </el-tab-pane>
      <el-tab-pane label="字典" name="dict">
        <template v-if="'dict'===current.dataType">
          <el-form-item label="Props" label-width="60px" class="mt-10px">
            <div class="color-#666">默认值不可修改</div>
            <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                         :collapsedOnClickBrackets="false" :editable="false"/>
          </el-form-item>
          <el-form-item label="字典类型code" label-width="100px" class="mt-10px">
            <el-input v-model="current.dictCode" placeholder="字典类型code"/>
          </el-form-item>
          <base-button type="primary" link icon="el-icon-refresh" size="small" class="ml-100px"
                       @click="refreshDictData">刷新数据
          </base-button>
        </template>
      </el-tab-pane>
    </el-tabs>
  </el-form-item>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { VueDraggable } from 'vue-draggable-plus'
import { JsonEditor } from '@/components/JsonEditor'
import { getDict } from '@/utils/dict-util'
import request from '@/utils/request'

const current = defineModel()

onMounted(() => {
  if (!current.value.dataType) {
    current.value.dataType = 'static'
    dataTypeChange('static')
  }
})

// select组件，监听数据类型
const dataTypeChange = (val) => {
  if ('static' === val) {
    current.value.__attrs.props = {value: 'value', label: 'label', disabled: 'disabled'}
    current.value.__attrs.options = [{value: 'value1', label: 'label1'}]
  } else if ('dynamic' === val) {
    current.value.dataDynamic = {url: '/role/list', dataKey: 'data.records'}
    current.value.__attrs.props = {value: 'roleId', label: 'roleName', options: 'children', disabled: 'disabled'}
    current.value.__attrs.options = []
    refreshDynamicData()
  } else if ('dict' === val) {
    current.value.dictCode = 'sys_user_sex'
    current.value.__attrs.props = {value: 'dictValue', label: 'dictLabel', disabled: 'disabled'}
    current.value.__attrs.options = []
    refreshDictData()
  }
}
const addStaticData = () => {
  const obj = cloneDeep(current.value.__attrs.options)
  current.value.__attrs.options = []
  current.value.__attrs.options = [...obj, {value: 'value' + (obj.length + 1), label: 'label' + (obj.length + 1)}]
}
const refreshDynamicData = () => {
  const obj = current.value.dataDynamic
  if (!obj.url) {
    ElMessage({message: '接口地址不能为空！', type: 'warning', grouping: true})
    return
  }
  if (!obj.dataKey) {
    ElMessage({message: '数据字段不能为空！', type: 'warning', grouping: true})
    return
  }
  request({url: obj.url, method: 'get'}).then((response) => {
    current.value.__attrs.options = obj.dataKey.split('.').reduce((acc, k) => acc[k], response) || []
  })
}
const refreshDictData = () => {
  if (!current.value.dictCode) {
    ElMessage({message: '数据字典类型code不能为空！', type: 'warning', grouping: true})
    return
  }
  current.value.__attrs.options = getDict(current.value.dictCode)
}
</script>

<style lang="less" scoped>
.dataTab {
  :deep(.el-tabs__item) {
    height: 30px;
  }
  :deep(.el-tabs__content) {
    padding: 5px 5px 5px 0;
  }
}
</style>