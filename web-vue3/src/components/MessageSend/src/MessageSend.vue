<template>
  <div class="message-to">
    <el-form-item v-if="type === 'user'"
                  label="通知用户" prop="ids" :label-width="labelWidth">
      <el-select v-model="ids" :style="{width: width}" multiple clearable
                 @change="handleUserChange">
        <el-option label="全部" key="userAll" value="all"/>
        <el-option v-for="item in userList" :key="item.userId"
                   :label="item.userName" :value="item.userId"/>
      </el-select>
    </el-form-item>
    <el-form-item v-if="type === 'role'"
                  label="通知角色" prop="ids" :label-width="labelWidth">
      <el-select v-model="ids" :style="{width: width}" multiple clearable
                 @change="handleRoleChange">
        <el-option label="全部" key="roleAll" value="all"/>
        <el-option v-for="item in roleList" :key="item.roleId"
                   :label="item.roleName" :value="item.roleId"/>
      </el-select>
    </el-form-item>
    <el-form-item v-if="type === 'org'"
                  label="通知组织" prop="ids" :label-width="labelWidth">
      <el-cascader v-model="ids" :options="orgTree" clearable :style="{width: width}"
                   :props="casProps"/>
    </el-form-item>
  </div>
</template>

<script setup>
import request from '@/utils/request'

const props = defineProps({
  modelValue: {type: Array, default: () => []},
  // user=用户；role=角色；org=组织机构
  type: {type: String, required: true, default: 'user'},
  // all=全部；children=下级；selfAndChildren=本机构及下级
  scope: {type: String, required: true, default: 'all'},
  labelWidth: {type: String, default: '100px'},
  width: {type: String, default: '100%'}
})

const emit = defineEmits(['update:modelValue'])

const userList = ref([])
const roleList = ref([])
const orgTree = ref([])
const ids = ref(props.modelValue)
const casProps = ref({
  checkStrictly: true, // 是否严格的遵守父子节点不互相关联
  expandTrigger: 'hover', // 次级菜单的展开方式
  emitPath: false, // 是否返回选中节点的完整路径
  multiple: true, label: 'orgName', children: 'children', value: 'orgId'
})

watch(() => props.modelValue, (val) => { ids.value = val })
watch(() => ids.value, (val) => { emit('update:modelValue', val) })
watch(() => props.type, (val) => { loadData(val) })

onMounted(() => {
  loadData(props.type)
})

function loadData(type) {
  if (type === 'user') {
    loadUserList()
  } else if (type === 'role') {
    loadRoleList()
  } else if (type === 'org') {
    loadOrgTree()
  }
}

function loadUserList() {
  // 加载“通知用户”下拉框数据
  const params = {scope: props.scope}
  request({url: '/message/to/user/list', method: 'get', params}).then((response) => {
    userList.value = response.data
  })
}

function loadRoleList() {
  // 加载“通知角色”下拉框数据
  request({url: '/message/to/role/list', method: 'get'}).then((response) => {
    roleList.value = response.data
  })
}

function loadOrgTree() {
  // 加载“通知组织”下拉框数据
  const params = {scope: props.scope}
  request({url: '/message/to/org/tree', method: 'get', params}).then((response) => {
    orgTree.value = response.data
  })
}

function handleUserChange() {
  // 处理“通知用户”下拉框的选择变化
  if (ids.value.includes('all')) {
    ids.value = userList.value.map(item => item.userId)
  }
}

function handleRoleChange() {
  // 处理“通知角色”下拉框的选择变化
  if (ids.value.includes('all')) {
    ids.value = roleList.value.map(item => item.roleId)
  }
}
</script>
