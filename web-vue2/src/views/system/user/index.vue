<template>
  <div class="app-container">
    <div style="margin-bottom: 20px;">
      <!--  操作按钮  -->
      <el-button icon="el-icon-plus" v-permission="'user-add'" @click="userAdd">新增</el-button>
      <el-button icon="el-icon-edit" v-permission="'user-update'" @click="userUpdate">修改</el-button>
      <el-button type="danger" icon="el-icon-delete" v-permission="'user-delete'" @click="userDelete">删除</el-button>
      <el-button type="primary" icon="el-icon-refresh-right" v-permission="'reset-password'" @click="resetPassword">重置密码</el-button>
      <el-button type="warning" icon="el-icon-lock" v-permission="'change-status'" @click="changeStatus(0)">禁用</el-button>
      <el-button type="success" icon="el-icon-unlock" v-permission="'change-status'" @click="changeStatus(1)">启用</el-button>
    </div>

    <!-- 表格部分 -->
    <el-table :data="userTable" row-key="userId" style="width: 100%;" border @selection-change="selectionChangeHandlerOrder">
      <el-table-column type="selection" width="40"/>
      <el-table-column prop="roleName" label="角色" min-width="10%"/>
      <el-table-column prop="userName" label="用户名" min-width="10%"/>
      <el-table-column prop="name" label="姓名" min-width="10%"/>
      <el-table-column prop="phone" label="手机号" min-width="12%"/>
      <el-table-column prop="nickName" label="昵称" min-width="10%"/>
      <el-table-column prop="sex" label="性别" min-width="5%">
        <template slot-scope="scope">
          <span v-if="scope.row.sex === '0'">未知</span>
          <span v-else-if="scope.row.sex === '1'">男</span>
          <span v-else-if="scope.row.sex === '2'">女</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="5%">
        <template slot-scope="scope">
          <span v-if="scope.row.status === '0'" style="color: red;">禁用</span>
          <span v-else style="color: green;">正常</span>
        </template>
      </el-table-column>
      <el-table-column prop="avatar" label="头像" width="121px">
        <template slot-scope="scope">
          <img v-if="scope.row.avatar" :src="$baseServer+'/'+scope.row.avatar" style="max-width: 100px;max-height: 100px;object-fit: cover;"/>
          <span v-if="!scope.row.avatar">未上传</span>
        </template>
      </el-table-column>
      <el-table-column prop="introduce" label="简介" min-width="30%"/>
    </el-table>

    <!--分页-->
    <el-pagination style="text-align: center;" background layout="total, pager"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
    />

    <!--  弹窗  -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="userDataForm" :model="temp" :rules="rules" label-position="right"
               label-width="100px" style="width: 500px; margin-left: 50px;">
        <el-form-item label="用户名：" prop="userName">
          <el-input v-model="temp.userName" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码：" prop="password" v-if="this.dialogStatus=='create'">
          <el-input v-model="temp.password" show-password placeholder="请输入密码"/>
        </el-form-item>
        <el-form-item label="所在部门：" prop="orgId">
          <select-tree v-model="temp.orgId" empty-text="请选择所在部门" empty-value=""
                       :props="{children: 'children', label: 'label'}"
                       :data="orgSelectTreeData" style="width: 400px;"></select-tree>
        </el-form-item>
        <el-form-item label="角色：" prop="roleId">
          <el-select v-model="temp.roleId" class="filter-item" style="width: 400px;" placeholder="请选择角色">
            <el-option v-for="item in roleNameOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="性别：" prop="sex">
          <el-radio v-model="temp.sex" label="0">未知</el-radio>
          <el-radio v-model="temp.sex" label="1">男</el-radio>
          <el-radio v-model="temp.sex" label="2">女</el-radio>
        </el-form-item>
        <el-form-item label="昵称：" prop="nickName">
          <el-input v-model="temp.nickName" placeholder="请输入昵称"/>
        </el-form-item>
        <el-form-item label="简介：" prop="introduce">
          <el-input v-model="temp.introduce" type="textarea" placeholder="请输入简介"/>
        </el-form-item>
        <el-form-item label="头像：" prop="avatar">
          <image-avatar name="avatar" v-model="temp.avatar"></image-avatar>
        </el-form-item>
        <el-form-item label="姓名：" prop="name">
          <el-input v-model="temp.name" placeholder="请输入姓名"/>
        </el-form-item>
        <el-form-item label="手机号：" prop="phone">
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
import ImageAvatar from "@/components/Upload/ImageAvatar";
import request from "@/utils/request";
import SelectTree from "@/components/SelectTree";

export default {
  components: {ImageAvatar, SelectTree},
  data() {
    return {
      // 表格数据
      userTable: [],
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      roleNameOptions: [],
      textMap: {update: '修改', create: '新增'},
      // 对话框属性
      dialogStatus: '',
      dialogFormVisible: false,
      //选中的数据
      changeData: [],
      //删除的IDS
      userIds: [],
      temp: {},
      rules: {
        userName: [{required: true, message: '请填写用户名', trigger: 'blur'}],
        password: [{required: true, message: '请填写密码', trigger: 'blur'}],
        roleId: [{required: true, message: '请给用户选择角色', trigger: 'blur'}],
        phone: [{
          required: false, trigger: 'blur', validator: (r, v, b) => {
            (v && !(/^(?:(?:\+|00)86)?1\d{10}$/.test(v))) ? b('手机号格式不正确') : b()
          }
        }]
      },
      // 下拉树-组织机构
      orgSelectTreeData: []
    }
  },
  created() {
    this.getUserList()
    this.getRoleList()
  },
  methods: {
    // 表格勾选
    selectionChangeHandlerOrder(val) {
      this.changeData = val
    },
    resetTemp() {
      this.temp = {
        userName: '',
        orgId: '',
        roleId: '',
        password: '',
        sex: '0',
        nickName: '',
        introduce: '',
        avatar: '',
        name: '',
        phone: ''
      }
    },
    //查询用户列表
    getUserList() {
      getUserList().then(response => {
        this.userTable = response.data.records
        this.pager.totalCount = response.data.total
        // this.userTable = data.records
      })
    },
    //分页
    handleCurrentChange(page) {
      this.pager.page = page
    },
    // 查询角色
    getRoleList() {
      getRoleList(this.pager).then(response => {
        const {data} = response
        this.roleNameOptions = data.records
      })
    },
    userAdd() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      this.$nextTick(() => {
        this.$refs['userDataForm'].clearValidate()
        this.loadOrgTreeForSelect()
      })
    },
    userUpdate() {
      if (this.changeData.length <= 0) {
        this.$message({type: 'warning', message: '请选择一条数据进行修改！'})
      } else if (this.changeData.length > 1) {
        this.$message({type: 'warning', message: '修改时，只允许选择一条数据！'})
      } else {
        this.temp = Object.assign({}, this.changeData[0])
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
      request({
        url: '/user/org/tree', method: 'get'
      }).then((response) => {
        this.orgSelectTreeData = response.data
      })
    },
    // 提交数据
    submitJudgment() {
      // 表单验证
      this.$refs['userDataForm'].validate((valid) => {
        if (valid) {
          if (this.dialogStatus === 'update') {
            userUpdate(this.temp).then(reponse => {
              this.$message({type: 'success', message: '修改成功！'})
              this.dialogFormVisible = false
              this.getUserList()
            })
          } else {
            userAdd(this.temp).then(reponse => {
              this.$message({type: 'success', message: '添加成功！'})
              this.dialogFormVisible = false
              this.getUserList()
            })
          }
        }
      })
    },
    userDelete() {
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
    resetPassword() {
      if (this.changeData.length <= 0) {
        this.$message({message: '至少选择一个用户重置密码！', type: 'error'})
      } else {
        this.$confirm('确定要重置密码吗?', '重置密码提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          this.userIds.push(...this.changeData.map(r => r.userId))
          userResetPassword(this.userIds).then(response => {
            this.$message({type: 'success', message: '重置密码成功！'})
            this.getUserList()
          })
        })
      }
    },
    // 启用/禁用用户
    changeStatus(status) {
      let msg = status == 0 ? '禁用' : '启用'
      if (this.changeData.length <= 0) {
        this.$message({message: '至少选择一个用户' + msg + '！', type: 'error'})
      } else {
        this.userIds = []
        this.userIds.push(...this.changeData.map(r => r.userId))
        let data = {userIds: this.userIds, status: status}
        request({url: '/user/change/status', method: 'post', data})
          .then(response => {
            this.$message({type: 'success', message: '用户' + msg + '成功！'})
            this.getUserList()
          })
      }
    },
  }
}
</script>
<style>
</style>

