<template>
  <div class="message-to">
    <el-form-item v-if="type === 'user'"
                  label="通知用户" prop="ids" :label-width="labelWidth">
      <el-select v-model="ids" :style="{width: width}" multiple clearable
                 @change="handleUserChange">
        <el-option label="全部" key="userAll" value="all"></el-option>
        <el-option v-for="item in userList" :key="item.userId"
                   :label="item.userName" :value="item.userId"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="type === 'role'"
                  label="通知角色" prop="ids" :label-width="labelWidth">
      <el-select v-model="ids" :style="{width: width}" multiple clearable
                 @change="handleRoleChange">
        <el-option label="全部" key="roleAll" value="all"></el-option>
        <el-option v-for="item in roleList" :key="item.roleId"
                   :label="item.roleName" :value="item.roleId"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="type === 'org'"
                  label="通知组织" prop="ids" :label-width="labelWidth">
      <el-cascader v-model="ids" :options="orgTree" clearable :style="{width: width}"
                   :props="casProps">
      </el-cascader>
    </el-form-item>
  </div>
</template>
<script>
export default {
  name: "MessageSend",
  props: {
    value: {type: Array, default: () => []},
    // user=用户；role=角色；org=组织机构
    type: {type: String, required: true, default: 'user'},
    // all=全部；children=下级；selfAndChildren=本机构及下级
    scope: {type: String, required: true, default: 'all'},
    labelWidth: {type: String, default: '100px'},
    width: {type: String, default: '100%'}
  },
  data() {
    return {
      userList: [],
      roleList: [],
      orgTree: [],
      ids: this.value,
      casProps: {
        checkStrictly: true,// 是否严格的遵守父子节点不互相关联
        expandTrigger: 'hover',// 次级菜单的展开方式
        emitPath: false,// 是否返回选中节点的完整路径
        multiple: true, label: 'orgName', children: 'children', value: 'orgId'
      }
    }
  },
  watch: {
    value(val) {
      this.ids = val
    },
    ids(val) {
      this.$emit('input', val)
    },
    type(val) {
      this.loadData()
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      if (this.type === 'user') {
        this.loadUserList();
      } else if (this.type === 'role') {
        this.loadRoleList();
      } else if (this.type === 'org') {
        this.loadOrgTree();
      }
    },
    loadUserList() {
      // 加载“通知用户”下拉框数据
      const params = {scope: this.scope}
      this.$request({url: '/message/to/user/list', method: 'get', params}).then((response) => {
        this.userList = response.data
      })
    },
    loadRoleList() {
      // 加载“通知角色”下拉框数据
      this.$request({url: '/message/to/role/list', method: 'get'}).then((response) => {
        this.roleList = response.data
      })
    },
    loadOrgTree() {
      // 加载“通知组织”下拉框数据
      const params = {scope: this.scope}
      this.$request({url: '/message/to/org/tree', method: 'get', params}).then((response) => {
        this.orgTree = response.data
        console.log(this.orgTree)
      })
    },
    handleUserChange() {
      // 处理“通知用户”下拉框的选择变化
      if (this.ids.includes('all')) {
        this.ids = []
        this.ids.push(...this.userList.map(item => item.userId))
      }
    },
    handleRoleChange() {
      // 处理“通知角色”下拉框的选择变化
      if (this.ids.includes('all')) {
        this.ids = []
        this.ids.push(...this.roleList.map(item => item.roleId))
      }
    }
  }
}
</script>
<style scoped lang="scss">

</style>