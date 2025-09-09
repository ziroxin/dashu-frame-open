<template>
  <el-divider>数据</el-divider>
  <el-form-item label-width="0">
    <el-tabs v-model="current.dataType" class="dataTab w-full"
             type="border-card" stretch @tab-change="dataTypeChange">
      <el-tab-pane label="静态" name="static">
        <template v-if="'static'===current.dataType">
          <el-form-item label="Props 可修改" label-width="55px" class="mt-10px">
            <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                         :collapsedOnClickBrackets="false"/>
          </el-form-item>
          <el-form-item label="Options" label-width="60px" class="mt-10px">
            <el-tree :data="current.__attrs.options" :props="{children:'children',label:'label'}" class="w-full"
                     :expand-on-click-node="false" default-expand-all>
              <template #default="{ node, data }">
                <div class="flex flex-1 items-center justify-between text-14px pr-8px">
                  <span>{{ node.label }}</span>
                  <div>
                    <el-button type="primary" link @click="openAddDialog(data)">
                      <my-icon icon="el-icon-plus"/>
                    </el-button>
                    <el-button class="ml-5px" type="danger" link @click="removeStaticData(data)">
                      <my-icon icon="el-icon-delete"/>
                    </el-button>
                  </div>
                </div>
              </template>
            </el-tree>
            <base-button type="primary" link icon="el-icon-circle-plus-outline" size="small"
                         @click="openAddDialog(null)">添加父级
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
    </el-tabs>
  </el-form-item>
  <!-- 添加数据 -->
  <el-dialog title="添加数据" v-model="dialogFormVisible" width="600px" :key="'myDialog'+dialogIndex"
             draggable @close="closeDialog">
    <el-form ref="dataFormRef" :model="formData" label-width="auto">
      <el-form-item label="Label" prop="label" :rules="[{required: true, message: 'Label不能为空'}]">
        <el-input v-model="formData.label" placeholder="请输入Label"/>
      </el-form-item>
      <el-form-item label="Value" prop="value" :rules="[{required: true, message: 'Value不能为空'}]">
        <el-input v-model="formData.value" placeholder="请输入Value"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <base-button type="primary" icon="el-icon-check" @click="saveStaticData">确定</base-button>
        <base-button icon="el-icon-close" @click="dialogFormVisible=false">取消</base-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { JsonEditor } from '@/components/JsonEditor'
import { treeToList } from '@/utils/tree'
import request from '@/utils/request'

const current = defineModel()

const dialogFormVisible = ref(false)
const dialogIndex = ref(0)
const openAddDialog = (parent) => {
  dialogFormVisible.value = true
  formData.value = {label: '', value: ''}
  if (parent) formData.value.parentValue = parent.value
}
const closeDialog = () => {
  dialogFormVisible.value = false
  dialogIndex.value++
}
const formData = ref({})
const dataFormRef = ref(null)

onMounted(() => {
  if (!current.value.dataType) {
    current.value.dataType = 'static'
    dataTypeChange('static')
  }
})

// select组件，监听数据类型
const dataTypeChange = (val) => {
  if ('static' === val) {
    current.value.__attrs.props = {
      value: 'value', label: 'label', children: 'children', expandTrigger: 'click', multiple: false,
      checkStrictly: false, emitPath: true, checkOnClickNode: false, checkOnClickLeaf: true, showPrefix: true
    }
    current.value.__attrs.options = []
  } else if ('dynamic' === val) {
    current.value.__attrs.props = {
      value: 'orgId', label: 'orgName', children: 'children', expandTrigger: 'click', multiple: false,
      checkStrictly: false, emitPath: true, checkOnClickNode: false, checkOnClickLeaf: true, showPrefix: true
    }
    current.value.dataDynamic = {url: '/zorg/zOrganization/tree', dataKey: 'data'}
    current.value.__attrs.options = []
    refreshDynamicData()
  }
}
const saveStaticData = () => {
  const list = treeToList(current.value.__attrs.options)
  if (list.find(i => i.value === formData.value.value || i.label === formData.value.label)) {
    ElMessage({message: 'Label/Value值不能重复，请修改！', type: 'warning', grouping: true})
    return
  }
  const parentValue = formData.value.parentValue
  if (parentValue) {
    const pushChild = (treeData, parentValue) => {
      treeData.forEach(item => {
        if (item.value === parentValue) {
          if (!item.children) item.children = []
          item.children.push({label: formData.value.label, value: formData.value.value})
        } else {
          if (item.children) pushChild(item.children, parentValue)
        }
      })
    }
    pushChild(current.value.__attrs.options, parentValue)
  } else {
    current.value.__attrs.options.push({label: formData.value.label, value: formData.value.value})
  }
  dialogFormVisible.value = false
}
const removeStaticData = (data) => {
  const removeChild = (treeData, childValue) => {
    treeData.forEach((item, index) => {
      if (item.value === childValue) {
        treeData.splice(index, 1)
      } else {
        if (item.children) removeChild(item.children, childValue)
      }
    })
  }
  removeChild(current.value.__attrs.options, data.value)
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