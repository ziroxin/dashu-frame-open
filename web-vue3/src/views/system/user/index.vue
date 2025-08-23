<template>
  <div class="app-container">
    <el-row :gutter="15">
      <el-col :span="4">
        <el-input v-model="filterText" placeholder="关键字过滤" clearable class="mb-10px"/>
        <!-- 组织机构树 -->
        <el-tree ref="orgTreeRef" :data="orgSelectTreeData"
                 class="filter-tree overflow-auto" :expand-on-click-node="false"
                 :highlight-current="true" :props="{children: 'children',label: 'label'}" node-key="value"
                 default-expand-all :filter-node-method="filterNode" @node-click="treeNodeClick"/>
      </el-col>
      <el-col :span="20">
        <div class="searchPanel">
          <div class="searchForm">
            <el-switch v-model="searchData.isSelf" class="searchInput w-180px!"
                       active-value="self" inactive-value="notself"
                       active-text="包含下级" inactive-text="只查本级" @change="getUserList"/>
            <el-input v-model="searchData.userName" class="searchInput" placeholder="用户名" clearable/>
            <el-input v-model="searchData.name" class="searchInput" placeholder="姓名" clearable/>
            <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle"/>
            <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList"/>
          </div>
          <div class="operatePanel">
            <!--  操作按钮  -->
            <base-button type="primary" v-permission="'user-add'" @click="userAdd" icon="el-icon-plus">新增
            </base-button>
            <base-button v-permission="'reset-password'" type="warning"
                         @click="resetPassword(null)" icon="el-icon-key">重置密码
            </base-button>
            <base-button v-permission="'user-delete'" type="danger"
                         @click="userDelete(null)" icon="el-icon-delete">删除
            </base-button>
          </div>
        </div>
        <!-- 表格部分 -->
        <el-table :data="userTable" row-key="userId" ref="dataTable" class="w-100%" border v-loading="isLoading"
                  @selection-change="selectionChangeHandlerOrder">
          <el-table-column align="center" type="selection" width="40"/>
          <el-table-column align="center" prop="roleName" label="角色" min-width="10%"/>
          <el-table-column align="center" prop="orgName" label="部门" min-width="10%"/>
          <el-table-column align="center" prop="userName" label="用户名" min-width="8%"/>
          <el-table-column align="center" prop="name" label="姓名" min-width="8%"/>
          <el-table-column align="center" prop="nickName" label="昵称" min-width="8%"/>
          <el-table-column align="center" prop="sex" label="性别" min-width="5%">
            <template #default="scope">
              <span v-if="scope.row.sex === '0'">未知</span>
              <span v-else-if="scope.row.sex === '1'">男</span>
              <span v-else-if="scope.row.sex === '2'">女</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="status" label="状态" min-width="5%">
            <template #default="scope">
              <span v-if="scope.row.status === '0'" class="color-red">禁用</span>
              <span v-else class="color-green">正常</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="avatar" label="头像" width="121px">
            <template #default="scope">
              <el-image v-if="scope.row.avatar" :src="$baseServer+'/'+scope.row.avatar"
                        :preview-src-list="[$baseServer+'/'+scope.row.avatar]" preview-teleported hide-on-click-modal
                        class="max-w-100px max-h-100px object-cover"/>
              <el-tag type="info" size="small" v-if="!scope.row.avatar">未上传</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="introduce" label="简介" min-width="10%"/>
          <el-table-column fixed="right" align="center" label="操作" width="125px">
            <template #default="scope">
              <base-button v-permission="'user-update'" link size="small" type="primary"
                         @click="userUpdate(scope.row)">修改
              </base-button>
              <base-button v-permission="'reset-password'" link size="small" type="primary"
                         @click="resetPassword(scope.row)">重置密码
              </base-button>
              <br/>
              <base-button v-if="scope.row.status === '0'" class="color-#13ce66!" v-permission="'change-status'"
                         link size="small" @click="changeStatus(1, scope.row)">启用
              </base-button>
              <base-button v-else class="color-#ff6d6d!" v-permission="'change-status'"
                         link size="small" @click="changeStatus(0, scope.row)">禁用
              </base-button>
              <base-button v-permission="'user-delete'" class="color-#ff6d6d!"
                         link size="small" @click="userDelete(scope.row)">删除
              </base-button>
            </template>
          </el-table-column>
        </el-table>
        <!--分页-->
        <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                       :page-size="pager.limit" :current-page="pager.page"
                       :total="pager.totalCount" @current-change="handleCurrentChange"
                       @size-change="handleSizeChange"/>
      </el-col>
    </el-row>

    <!--  弹窗  -->
    <el-dialog :title="textMap[dialogStatus]" top="5vh" :close-on-click-modal="false" @close="closeDialog"
               v-model="dialogFormVisible" width="680px" :key="'myDialog'+dialogIndex">
      <el-form ref="userDataForm" :model="temp" label-position="right" class="w-500px ml-50px" label-width="100px">
        <el-form-item label="用户名" prop="userName"
                      :rules="[{required: true, message: '请填写用户名', trigger: 'blur'}]">
          <el-input v-model="temp.userName" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-tag>默认密码 {{ defaultPassword }}，用户使用默认密码登录后，会强制修改密码</el-tag>
        </el-form-item>
        <el-form-item label="所在部门" prop="orgId"
                      :rules="[{required: true, message: '请选择所属组织机构', trigger: 'blur'}]">
          <el-cascader v-model="temp.orgId" placeholder="请选择上级" class="w-100%"
                       :options="orgSelectTreeData" filterable clearable
                       :props="{value:'value',label:'label',checkStrictly:true,emitPath:false}"/>
        </el-form-item>
        <el-form-item label="角色" prop="roleId"
                      :rules="[{required: true, message: '请给用户选择角色', trigger: 'blur'}]">
          <el-select v-model="temp.roleId" class="filter-item w-400px" :multiple="true" placeholder="请选择角色">
            <el-option v-for="item in roleNameOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio v-model="temp.sex" value="0">未知</el-radio>
          <el-radio v-model="temp.sex" value="1">男</el-radio>
          <el-radio v-model="temp.sex" value="2">女</el-radio>
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="temp.nickName" placeholder="请输入昵称"/>
        </el-form-item>
        <el-form-item label="简介" prop="introduce">
          <el-input v-model="temp.introduce" type="textarea" placeholder="请输入简介"/>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <image-avatar v-model="temp.avatar" name="avatar"/>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="temp.name" placeholder="请输入姓名"/>
        </el-form-item>
        <el-form-item label="手机号" prop="phone"
                      :rules="[{pattern: /^1[3456789]\d{9}$/, message: '手机号格式不正确'}]">
          <el-input v-model="temp.phone" placeholder="请输入手机号"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button type="primary" icon="el-icon-check" @click="submitJudgment">保存</base-button>
          <base-button icon="el-icon-close" @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import ImageAvatar from '@/components/Upload/ImageAvatar'
import request from '@/utils/request'

export default {
  components: {ImageAvatar},
  data() {
    return {
      // 表格数据
      userTable: [],
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      searchData: {},
      isLoading: false,
      roleNameOptions: [],
      textMap: {update: '修改', create: '新增'},
      // 对话框属性
      dialogStatus: '',
      dialogFormVisible: false,
      dialogIndex: 0,
      //选中的数据
      changeData: [],
      temp: {},
      // （新增/修改弹窗）下拉树-组织机构
      orgSelectTreeData: [],
      // 左侧组织机构树相关数据
      filterText: '',
      leftTreeData: [],
      // 默认密码
      defaultPassword: 'ABCabc@123'
    }
  },
  watch: {
    filterText(val) {
      // 左侧组织机构树过滤
      this.$refs.orgTreeRef.filter(val)
    }
  },
  created() {
    this.loadOrgTreeForSelect()
    this.getUserList()
    this.getRoleList()
    this.loadDefaultPassword()
  },
  methods: {
    loadDefaultPassword() {
      request({url: '/zsafety/zSafety/getSafety', method: 'get'}).then((res) => {
        this.defaultPassword = res.data.defaultPassword
      })
    },
    // 左侧树过滤
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    // 点击左侧树
    treeNodeClick(row) {
      this.searchData.orgId = row.value
      this.getUserList()
    },
    // 表格勾选
    selectionChangeHandlerOrder(val) {
      this.changeData = val
    },
    resetTemp() {
      this.temp = {
        userName: '', orgId: '', roleId: [], password: '', sex: '0', nickName: '',
        introduce: '', avatar: '', name: '', phone: ''
      }
    },
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.getUserList()
    },
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.$refs.orgTreeRef.setCurrentKey(null)
      this.getUserList()
    },
    //查询用户列表
    getUserList() {
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)}
      request({url: '/user/list', method: 'get', params}).then(response => {
        this.userTable = response.data.records
        this.pager.totalCount = response.data.total
        this.isLoading = false
      })
    },
    //分页
    handleCurrentChange(page) {
      this.pager.page = page
      this.getUserList()
    },
    // 分页条数改变
    handleSizeChange(size) {
      this.pager.limit = size
      this.getUserList()
    },
    // 查询角色
    getRoleList() {
      request({url: '/role/list', method: 'get'}).then(response => {
        this.roleNameOptions = response.data.records
      })
    },
    closeDialog() {
      this.dialogIndex++
      if (this.searchData && this.searchData.orgId) {
        this.$refs.orgTreeRef.setCurrentKey(this.searchData.orgId)
      }
      this.dialogFormVisible = false
    },
    userAdd() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      if (this.searchData && this.searchData.orgId) {
        this.temp.orgId = this.searchData.orgId + ''
      }
      this.$nextTick(() => {
        this.$refs['userDataForm'].clearValidate()
        this.loadOrgTreeForSelect()
      })
    },
    userUpdate(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      if (this.changeData.length <= 0) {
        this.$message({type: 'warning', message: '请选择一条数据进行修改！'})
      } else if (this.changeData.length > 1) {
        this.$message({type: 'warning', message: '修改时，只允许选择一条数据！'})
      } else {
        this.temp = Object.assign({}, this.changeData[0])
        this.temp.roleId = this.changeData[0].roleId ? this.changeData[0].roleId.split(',') : []
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['userDataForm'].clearValidate()
          this.loadOrgTreeForSelect()
        })
      }
    },
    // 加载下拉选择框组织机构树
    loadOrgTreeForSelect() {
      request({url: '/user/org/tree', method: 'get'}).then((response) => {
        this.orgSelectTreeData = response.data
      })
    },
    // 提交数据
    submitJudgment() {
      // 表单验证
      this.$refs['userDataForm'].validate((valid) => {
        if (valid) {
          const data = {...this.temp}
          if (this.dialogStatus === 'update') {
            request({url: '/user/update', method: 'post', data}).then(reponse => {
              this.$message({type: 'success', message: '修改成功！'})
              this.dialogFormVisible = false
              this.getUserList()
            })
          } else {
            request({url: '/user/add', method: 'post', data}).then(reponse => {
              this.$message({type: 'success', message: '添加成功！'})
              this.dialogFormVisible = false
              this.getUserList()
            })
          }
        }
      })
    },
    userDelete(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      if (this.changeData.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.changeData.map(r => r.userId)
          request({url: '/user/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.getUserList()
          })
        })
      }
    },
    // 重置密码
    resetPassword(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      if (this.changeData.length <= 0) {
        this.$message({message: '至少选择一个用户重置密码！', type: 'error'})
      } else {
        this.$confirm('确定要重置成  默认密码：' + this.defaultPassword + '  吗?', '重置密码提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          const data = this.changeData.map(r => r.userId)
          request({url: '/user/reset/password', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '重置密码成功！'})
            this.getUserList()
          })
        })
      }
    },
    // 启用/禁用用户
    changeStatus(status, row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      const msg = status === 0 ? '禁用' : '启用'
      if (this.changeData.length <= 0) {
        this.$message({message: '至少选择一个用户' + msg + '！', type: 'error'})
      } else {
        const data = {userIds: this.changeData.map(r => r.userId), status: status}
        request({url: '/user/change/status', method: 'post', data}).then(response => {
          this.$message({type: 'success', message: '用户' + msg + '成功！'})
          this.getUserList()
        })
      }
    }
  }
}
</script>
