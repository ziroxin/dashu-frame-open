<template>
  <div class="app-container">
    <el-row :gutter="15">
      <el-col :span="4">
        <el-input v-model="filterText" placeholder="输入关键字进行过滤" size="small"
                  style="margin-bottom: 10px;"/>
        <!-- 组织机构树 -->
        <el-tree ref="orgTreeRef" class="filter-tree" :data="orgSelectTreeData"
                 style="height: calc(100vh - 155px);overflow: auto;" :expand-on-click-node="false"
                 :highlight-current="true" :props="{children: 'children',label: 'label'}" node-key="value"
                 default-expand-all :filter-node-method="filterNode" @node-click="treeNodeClick"/>
      </el-col>
      <el-col :span="20" style="">
        <el-switch v-model="searchData.isSelf" active-value="self" inactive-value="notself" style="margin-right: 10px;"
                   size="small" active-text="包含下级" inactive-text="只查本级" @change="getUserList"/>
        <el-input v-model="searchData.userName" style="width: 125px;margin-right: 10px;"
                  size="small" class="filter-item" placeholder="输入用户名查询"/>
        <el-input v-model="searchData.name" style="width: 115px;margin-right: 10px;"
                  size="small" class="filter-item" placeholder="输入姓名查询"/>
        <el-button v-waves class="filter-item" type="primary" icon="el-icon-search"
                   size="small" @click="searchBtnHandle"/>
        <el-button v-waves class="filter-item" type="info" icon="el-icon-refresh"
                   size="small" @click="resetTableList"/>
        <div style="float: right;margin-bottom: 10px;">
          <!--  操作按钮  -->
          <el-button v-waves type="primary" v-permission="'user-add'" size="small"
                     @click="userAdd" icon="el-icon-plus">新增
          </el-button>
          <el-button v-waves v-permission="'reset-password'" type="warning" size="small"
                     @click="resetPassword(null)" icon="el-icon-key">重置密码
          </el-button>
          <el-button v-waves v-permission="'user-delete'" type="danger" size="small"
                     @click="userDelete(null)" icon="el-icon-delete">删除
          </el-button>
        </div>
        <!-- 表格部分 -->
        <el-table :data="userTable" row-key="userId" ref="dataTable" :height="this.$windowHeight-230"
                  style="width: 100%;" border @selection-change="selectionChangeHandlerOrder">
          <el-table-column align="center" type="selection" width="40"/>
          <el-table-column align="center" prop="roleName" label="角色" min-width="10%"/>
          <el-table-column align="center" prop="orgName" label="部门" min-width="10%"/>
          <el-table-column align="center" prop="userName" label="用户名" min-width="8%"/>
          <el-table-column align="center" prop="name" label="姓名" min-width="8%"/>
          <el-table-column align="center" prop="nickName" label="昵称" min-width="8%"/>
          <el-table-column align="center" prop="sex" label="性别" min-width="5%">
            <template slot-scope="scope">
              <span v-if="scope.row.sex === '0'">未知</span>
              <span v-else-if="scope.row.sex === '1'">男</span>
              <span v-else-if="scope.row.sex === '2'">女</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="status" label="状态" min-width="5%">
            <template #default="scope">
              <span v-if="scope.row.status === '0'" style="color: red;">禁用</span>
              <span v-else style="color: green;">正常</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="avatar" label="头像" width="121px">
            <template slot-scope="scope">
              <img v-if="scope.row.avatar" :src="$baseServer+'/'+scope.row.avatar"
                   style="max-width: 100px;max-height: 100px;object-fit: cover;">
              <span v-if="!scope.row.avatar">未上传</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="introduce" label="简介" min-width="10%"/>
          <el-table-column fixed="right" align="center" label="操作" width="120px">
            <template v-slot="scope">
              <el-button v-permission="'user-update'" type="text" size="mini"
                         @click="userUpdate(scope.row)">修改
              </el-button>
              <el-button v-permission="'reset-password'" type="text" size="mini"
                         @click="resetPassword(scope.row)">重置密码
              </el-button>
              <br/>
              <el-button v-if="scope.row.status === '0'" style="color: #13ce66;" v-permission="'change-status'"
                         type="text" size="mini" @click="changeStatus(1, scope.row)">启用
              </el-button>
              <el-button v-else style="color: #ff6d6d;" v-permission="'change-status'"
                         type="text" size="mini" @click="changeStatus(0, scope.row)">禁用
              </el-button>
              <el-button v-permission="'user-delete'" style="color: #ff6d6d;"
                         type="text" size="mini" @click="userDelete(scope.row)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!--分页-->
        <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                       :page-size="pager.limit" :current-page="pager.page"
                       :total="pager.totalCount" @current-change="handleCurrentChange"
                       @size-change="handleSizeChange"/>
      </el-col>
    </el-row>

    <!--  弹窗  -->
    <el-dialog :title="textMap[dialogStatus]" top="5vh" :close-on-click-modal="false" @close="closeDialog"
               :visible.sync="dialogFormVisible" width="680px" :key="'myDialog'+dialogIndex">
      <el-form ref="userDataForm" :model="temp" label-position="right"
               label-width="100px" style="width: 500px; margin-left: 50px;">
        <el-form-item label="用户名" prop="userName"
                      :rules="[{required: true, message: '请填写用户名', trigger: 'blur'}]">
          <el-input v-model="temp.userName" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-tag>默认密码 {{ defaultPassword }}，用户使用默认密码登录后，会强制修改密码</el-tag>
        </el-form-item>
        <el-form-item label="所在部门" prop="orgId"
                      :rules="[{required: true, message: '请选择所属组织机构', trigger: 'blur'}]">
          <el-cascader v-model="temp.orgId" placeholder="请选择上级" style="width: 100%;"
                       :options="orgSelectTreeData" filterable clearable
                       :props="{value:'value',label:'label',checkStrictly:true,emitPath:false}"/>
        </el-form-item>
        <el-form-item label="角色" prop="roleId"
                      :rules="[{required: true, message: '请给用户选择角色', trigger: 'blur'}]">
          <el-select v-model="temp.roleId" class="filter-item" :multiple="true" style="width: 400px;"
                     placeholder="请选择角色">
            <el-option v-for="item in roleNameOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio v-model="temp.sex" label="0">未知</el-radio>
          <el-radio v-model="temp.sex" label="1">男</el-radio>
          <el-radio v-model="temp.sex" label="2">女</el-radio>
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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitJudgment">保存</el-button>
        <el-button @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {getUserList, userAdd, userDelete, userResetPassword, userUpdate} from '@/api/user'
import {getRoleList} from '@/api/role';
import ImageAvatar from '@/components/Upload/ImageAvatar';
import request from '@/utils/request';
import SelectTree from '@/components/SelectTree';
import waves from '@/directive/waves';

export default {
  components: {ImageAvatar, SelectTree},
  directives: {waves},
  data() {
    return {
      // 表格数据
      userTable: [],
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      searchData: {},
      roleNameOptions: [],
      textMap: {update: '修改', create: '新增'},
      // 对话框属性
      dialogStatus: '',
      dialogFormVisible: false,
      dialogIndex: 0,
      //选中的数据
      changeData: [],
      //删除的IDS
      userIds: [],
      temp: {},
      // （新增/修改弹窗）下拉树-组织机构
      orgSelectTreeData: [],
      // 左侧组织机构树相关数据
      filterText: '',
      leftTreeData: [],
      // 默认密码
      defaultPassword: 'ABCabc@123',
    }
  },
  watch: {
    filterText(val) {
      // 左侧组织机构树过滤
      this.$refs.orgTreeRef.filter(val);
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
      this.$request({url: '/zsafety/zSafety/getSafety', method: 'get'}).then((res) => {
        this.defaultPassword = res.data.defaultPassword
      })
    },
    // 左侧树过滤
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
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
        userName: '',
        orgId: '',
        roleId: [],
        password: '',
        sex: '0',
        nickName: '',
        introduce: '',
        avatar: '',
        name: '',
        phone: ''
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
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      getUserList(params).then(response => {
        this.userTable = response.data.records
        this.pager.totalCount = response.data.total
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
      getRoleList().then(response => {
        this.roleNameOptions = response.data.records
      })
    },
    closeDialog() {
      this.dialogIndex++
      if (this.searchData && this.searchData.orgId) {
        this.$refs.orgTreeRef.setCurrentKey(this.searchData.orgId)
      }
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
          let data = {...this.temp}
          if (this.dialogStatus === 'update') {
            userUpdate(data).then(reponse => {
              this.$message({type: 'success', message: '修改成功！'})
              this.dialogFormVisible = false
              this.getUserList()
            })
          } else {
            userAdd(data).then(reponse => {
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
          this.userIds = []
          this.userIds.push(...this.changeData.map(r => r.userId))
          userDelete(this.userIds).then(response => {
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
          this.userIds = []
          this.userIds.push(...this.changeData.map(r => r.userId))
          console.log(this.userIds)
          userResetPassword(this.userIds).then(response => {
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
      let msg = status === 0 ? '禁用' : '启用'
      if (this.changeData.length <= 0) {
        this.$message({message: '至少选择一个用户' + msg + '！', type: 'error'})
      } else {
        this.userIds = []
        this.userIds.push(...this.changeData.map(r => r.userId))
        let data = {userIds: this.userIds, status: status}
        request({url: '/user/change/status', method: 'post', data}).then(response => {
          this.$message({type: 'success', message: '用户' + msg + '成功！'})
          this.getUserList()
        })
      }
    }
  }
}
</script>
<style>
</style>

