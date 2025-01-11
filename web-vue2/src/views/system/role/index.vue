<template>
  <div class="app-container">
    <el-row>
      <el-col :span="7">
        <!-- 角色管理按钮 -->
        <div style="margin-bottom: 10px;">
          <el-button v-permission="'system-role-add'" type="primary" icon="el-icon-plus" size="small" @click="roleAdd"/>
          <el-button v-permission="'system-role-update'" type="success" icon="el-icon-edit" size="small"
                     @click="roleUpdate"
          />
          <el-button v-permission="'system-role-delete'" type="danger" icon="el-icon-delete" size="small"
                     @click="roleDelete"
          />
          <el-button v-permission="'system-role-copy'" type="warning" icon="el-icon-copy-document" size="small"
                     @click="roleCopy"
          ></el-button>
        </div>
        <!-- 角色管理表格 -->
        <el-table ref="roleTable" :data="tableData" stripe border :height="this.$windowHeight-230" style="width: 95%"
                  @selection-change="handleTableSelectChange" v-loading="isLoading2">
          <el-table-column type="selection" width="50" align="center" header-align="center"/>
          <el-table-column label="角色" align="center">
            <template slot-scope="scope">
              <el-popover trigger="hover" placement="right"
                          :title="scope.row.roleName"
              >
                <p>
                  顺序：{{ scope.row.roleOrder }}<br>描述：{{ scope.row.roleDescription }}
                </p>
                <span slot="reference" style="cursor: pointer;">{{ scope.row.roleName }}</span>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <el-button type="text" size="small" @click="setMyApi(scope.row)">设置权限</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <el-pagination style="text-align: center;margin-top: 10px;" layout="total,prev,pager,next"
                       :page-size="pager.limit" :current-page="pager.page"
                       :total="totalCount" @current-change="handleCurrentChange"/>
        <!-- 添加修改弹窗 -->
        <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
                   :visible.sync="dialogFormVisible" width="650px" @close="resetTemp">
          <el-form ref="roleDateForm" :model="temp" label-position="left" label-width="100px"
                   style="width: 500px; margin-left: 50px;">
            <el-form-item label="角色名称" prop="roleName" :rules="{required: true, message: '角色名称不能为空'}">
              <el-input v-model="temp.roleName"/>
            </el-form-item>
            <el-form-item label="角色描述" prop="roleDescription">
              <el-input v-model="temp.roleDescription" type="textarea"/>
            </el-form-item>
            <el-form-item label="角色顺序" prop="roleOrder"
                          :rules="[{required: true, message: '角色顺序不能为空'},{type: 'number', message: '必须为数字'}]">
              <el-input v-model.number="temp.roleOrder"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="saveRoles">保存</el-button>
            <el-button @click="dialogFormVisible=false">取消</el-button>
          </div>
        </el-dialog>
      </el-col>
      <el-col :span="17" style="padding-left: 20px;border-left: 1px solid #dedede;" v-loading="isLoading">
        <!--        资源权限表格-->
        <div style="margin-bottom: 5px;">
          <el-button :disabled="!isSaveBtn" size="small" icon="el-icon-check"
                     v-permission="'system-role-save-permission'" type="primary"
                     @click="saveRolePermission()"
          >保存角色权限
          </el-button>
          <el-button size="small" @click="toggleTableOprate"
                     :icon="isExpand?'el-icon-arrow-up':'el-icon-arrow-down'">
            全部{{ isExpand ? '收起' : '展开' }}
          </el-button>
        </div>
        <el-table ref="permissionTable" :height="this.$windowHeight-200" style="width: 100%;"
                  :default-expand-all="isExpand" :data="tableData2" row-key="permissionId"
                  :tree-props="{children: 'children'}" @row-click="table2RowClick"
                  @select="table2RowSelect" resizable border
        >
          <el-table-column type="selection" width="50" align="center" header-align="center"/>
          <el-table-column label="路由/外链" min-width="30%">
            <template v-slot="{row}">
              <el-tag v-if="row.permissionType === '0'" disable-transitions size="mini">路由</el-tag>
              <el-tag v-if="row.permissionType === '2'" disable-transitions type="success" size="mini">外链</el-tag>
              <a style="cursor: pointer;margin-left: 5px;">{{ row.permissionTitle }}</a>
            </template>
          </el-table-column>
          <el-table-column label="按钮/其他" min-width="60%">
            <template v-slot="{row}">
              <div @click.stop="">
                <el-checkbox-group v-model="selectPermissionApiList">
                  <el-checkbox v-for="perm in row.buttonList" :key="perm.permissionId"
                               :label="perm.permissionId"
                               style="margin-left: 0px!important;" @change="(val)=>buttonCheckChange(val,row)"
                  >
                    {{ perm.permissionTitle }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  addRole,
  copyRole,
  deleteRoles,
  getPermissionList,
  getRoleList,
  saveRolePermission,
  updateRole
} from '@/api/role'

export default {
  data() {
    return {
      //角色表格数据
      pager: {page: 1, limit: 10},
      totalCount: 0,
      tableData: [],
      tableSelectRows: [],
      roleId: '',
      //添加修改弹窗
      titleMap: {add: '添加角色', update: '修改角色'},
      dialogType: '',
      dialogFormVisible: false,
      temp: {},
      //右侧资源列表数据
      tableData2: [],
      currentRow: 0,
      isExpand: true,
      // 按钮-权限idList
      selectPermissionApiList: [],
      // 菜单-权限idList
      selectPermissionApiList2: [],
      isSaveBtn: false,
      isLoading: false,
      isLoading2: false
    }
  },
  created() {
    this.loadRoleList()
    this.loadPermissionTreeList()
    this.resetTemp()
  },
  methods: {
    //加载角色列表
    loadRoleList() {
      this.isLoading2 = true
      getRoleList(this.pager).then((response) => {
        this.isLoading2 = false
        const {data} = response
        this.totalCount = data.total
        this.tableData = data.records
      })
    },
    //选中行
    handleTableSelectChange(rows) {
      this.tableSelectRows = rows
    },
    //分页
    handleCurrentChange(page) {
      this.pager.page = page
      this.loadRoleList()
    },
    //清空temp数据
    resetTemp() {
      this.temp = {
        roleId: '',
        roleName: '',
        roleDescription: '',
        roleOrder: 1
      }
    },
    //角色管理
    roleAdd() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.dialogType = 'add'
      this.$nextTick(() => {
        this.$refs['roleDateForm'].clearValidate()
      })
    },
    roleUpdate() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据修改！', type: 'warning'})
      } else if (this.tableSelectRows.length > 1) {
        this.$message({message: '修改时，只允许选择一条数据！', type: 'warning'})
      } else {
        // 修改弹窗
        this.temp = Object.assign({}, this.tableSelectRows[0])
        this.dialogType = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['roleDateForm'].clearValidate()
        })
      }
    },
    roleDelete() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          this.isLoading2 = true
          // 执行删除
          const roleIds = this.tableSelectRows.map(r => r.roleId)
          deleteRoles(roleIds).then(response => {
            const {code} = response
            if (code === '200') {
              this.$message({type: 'success', message: '删除成功！'})
              this.loadRoleList()
            } else {
              this.isLoading2 = false
              this.$message({type: 'error', message: '删除失败！'})
            }
          })
        })
      }
    },
    // 复制角色
    roleCopy() {
      if (this.tableSelectRows.length !== 1) {
        this.$message({message: '请选择一个角色进行复制！', type: 'error'})
      } else {
        this.isLoading2 = true
        let params = {'roleId': this.tableSelectRows[0].roleId};
        copyRole(params).then(response => {
          this.$message({type: 'success', message: '复制角色成功！'})
          this.loadRoleList()
        })
      }
    },
    saveRoles() {
      this.$refs['roleDateForm'].validate((valid) => {
        if (valid) {
          if (this.dialogType === 'update') {
            updateRole(this.temp).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadRoleList()
              this.dialogFormVisible = false
            })
          } else {
            addRole(this.temp).then(response => {
              this.$message({type: 'success', message: '添加成功！'})
              this.loadRoleList()
              this.dialogFormVisible = false
            })
          }
        }
      })
    },
    //加载资源列表树
    loadPermissionTreeList() {
      this.isLoading = true
      let params = this.roleId === '' ? {} : {'roleId': this.roleId}
      this.$nextTick(() => {
        getPermissionList(params).then(response => {
          const {data} = response
          this.tableData2 = data
          this.$nextTick(() => {
            this.selectPermissionApiList = []
            this.selectPermissionApiList2 = []
            this.toggleRowSelectionAll(this.tableData2)
          })
          this.isLoading = false
        })
      })
    },
    //展开和收起
    toggleTableOprate() {
      this.isExpand = !this.isExpand
      this.toggleRowExpansionAll(this.tableData2, this.isExpand)
    },
    toggleRowExpansionAll(data, isExpansion) {
      data.forEach((item) => {
        this.$refs.permissionTable.toggleRowExpansion(item, isExpansion)
        if (item.children !== undefined && item.children !== null) {
          this.toggleRowExpansionAll(item.children, isExpansion)
        }
      })
    },
    //设置权限按钮
    setMyApi(row) {
      this.$refs.roleTable.clearSelection()
      this.$refs.roleTable.toggleRowSelection(row, true)
      this.roleId = row.roleId
      this.isSaveBtn = true
      this.isExpand = true
      this.loadPermissionTreeList()
    },
    // 设置权限-按钮点击时-权限行选中
    toggleRowSelectionAll(data) {
      data.forEach(item => {
        //按钮列表
        item.buttonList.forEach(btn => {
          if (btn.hasPermission) {
            this.selectPermissionApiList.push(btn.permissionId)
          }
        })
        //权限列表
        if (item.hasPermission) {
          this.selectPermissionApiList2.push(item.permissionId)
        }
        this.$refs.permissionTable.toggleRowSelection(item, item.hasPermission)
        if (item.children !== undefined && item.children !== null) {
          this.toggleRowSelectionAll(item.children)
        }
      })
    },
    //行选中
    table2RowSelect(selection, row) {
      let select = selection.filter(r => r.permissionId === row.permissionId).length > 0
      //设置当前行的选中状态
      if (select) {
        this.selectPermissionApiList2.push(row.permissionId)
      } else {
        this.selectPermissionApiList2 = this.selectPermissionApiList2.filter(o => o !== row.permissionId)
      }
      this.$refs.permissionTable.toggleRowSelection(row, select)
      //设置按钮选中状态
      this.cancelButtonChecked(this.tableData2, row.permissionId, select)
      if (select) {
        //自动选中父级
        this.parentRowSelected(this.tableData2, row.parentId, 0)
      }
      //设置子级选中状态
      this.allChildrenSelected(this.tableData2, row.permissionId, false, select)
      //去重
      this.selectPermissionApiList = [...new Set(this.selectPermissionApiList)]
      this.selectPermissionApiList2 = [...new Set(this.selectPermissionApiList2)]
    },
    //行点击
    table2RowClick(row) {
      let hasSelected = this.selectPermissionApiList2.filter(o => o === row.permissionId).length > 0
      //设置当前行的选中状态
      if (!hasSelected) {
        this.selectPermissionApiList2.push(row.permissionId)
      } else {
        this.selectPermissionApiList2 = this.selectPermissionApiList2.filter(o => o !== row.permissionId)
      }
      this.$refs.permissionTable.toggleRowSelection(row, !hasSelected)
      //设置按钮选中状态
      this.cancelButtonChecked(this.tableData2, row.permissionId, !hasSelected)
      if (!hasSelected) {
        //自动选中父级
        this.parentRowSelected(this.tableData2, row.parentId, 0)
      }
      //设置子级选中状态
      this.allChildrenSelected(this.tableData2, row.permissionId, false, !hasSelected)
      //去重
      this.selectPermissionApiList = [...new Set(this.selectPermissionApiList)]
      this.selectPermissionApiList2 = [...new Set(this.selectPermissionApiList2)]
    },
    cancelButtonChecked(data, rowId, hasSelected) {
      data.forEach(item => {
        if (item.permissionId === rowId) {
          if (item.buttonList !== undefined && item.buttonList !== null) {
            if (!hasSelected) {
              // 取消选中，本行所有按钮
              item.buttonList.forEach(btn => {
                this.selectPermissionApiList = this.selectPermissionApiList.filter(o => o !== btn.permissionId)
              })
            } else {
              // 选中，本行所有按钮
              item.buttonList.forEach(btn => {
                this.selectPermissionApiList.push(btn.permissionId)
              })
            }
          }
        } else {
          if (item.children !== undefined && item.children !== null) {
            this.cancelButtonChecked(item.children, rowId, hasSelected)
          }
        }
      })
    },
    parentRowSelected(data, parentId, level) {
      data.forEach(item => {
        if (item.permissionId === parentId) {
          //选了子级，自动选择父级
          this.selectPermissionApiList2.push(item.permissionId)
          this.$refs.permissionTable.toggleRowSelection(item, true)
          if (level !== 0) {
            //再遍历父级
            this.parentRowSelected(data, item.parentId)
          }
        } else {
          if (item.children !== undefined && item.children !== null) {
            this.parentRowSelected(item.children, parentId, level++)
          }
        }
      })
    },
    allChildrenSelected(data, parentId, iscurrent, hasSelected) {
      if (!iscurrent) {
        //找到当前节点
        data.forEach(item => {
          if (item.children !== undefined && item.children !== null) {
            if (item.permissionId === parentId) {
              this.allChildrenSelected(item.children, parentId, true, hasSelected)//去选中
            } else {
              this.allChildrenSelected(item.children, parentId, false, hasSelected)//继续找
            }
          }
        })
      } else {
        //遍历选中子列表
        data.forEach(item => {
          //按钮列表
          item.buttonList.forEach(btn => {
            if (!hasSelected) {
              //取消选择按钮
              this.selectPermissionApiList = this.selectPermissionApiList.filter(o => o !== btn.permissionId)
            } else {
              //选择按钮
              this.selectPermissionApiList.push(btn.permissionId)
            }
          })
          //菜单行
          if (hasSelected) {
            this.selectPermissionApiList2.push(item.permissionId)
          } else {
            this.selectPermissionApiList2 = this.selectPermissionApiList2.filter(o => o !== item.permissionId)
          }
          this.$refs.permissionTable.toggleRowSelection(item, hasSelected)
          if (item.children !== undefined && item.children !== null) {
            this.allChildrenSelected(item.children, parentId, true, hasSelected)//继续选中
          }
        })
      }
    },
    //按钮checkbox（选择按钮，自动选择上级路由）
    buttonCheckChange(val, item) {
      if (val === true) {
        this.selectPermissionApiList2.push(item.permissionId)
        //自动选中父级
        this.parentRowSelected(this.tableData2, item.parentId, 0)
        this.$refs.permissionTable.toggleRowSelection(item, true)
        this.selectPermissionApiList2 = [...new Set(this.selectPermissionApiList2)]
      }
    },
    //保存角色权限
    saveRolePermission() {
      this.isLoading = true
      let pids = []
      pids.push(...this.selectPermissionApiList)
      pids.push(...this.selectPermissionApiList2)
      saveRolePermission({'roleId': this.roleId, 'permissionIds': pids}).then(response => {
        this.$notify({title: '保存成功', message: '保存权限成功！', type: 'success'})
        this.isLoading = false
      })
    }
  }
}
</script>
