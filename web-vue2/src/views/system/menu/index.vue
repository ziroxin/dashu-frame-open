<template>
  <div class="app-container">
    <el-row :gutter="24">
      <el-col :span="elColSpanValue">
        <!--  操作按钮  -->
        <div style="margin-bottom: 10px;">
          <el-button v-permission="'system-menu-add'" size="small" type="primary"
                     @click="permissionAdd" icon="el-icon-plus">新增一级菜单
          </el-button>
          <el-button v-permission="'system-menu-delete'" size="small" type="danger"
                     @click="permissionDelete" icon="el-icon-delete">批量删除
          </el-button>
        </div>
        <!-- 表格部分 -->
        <el-table ref="dataTable" :data="tableData" row-key="permissionId" :height="this.$windowHeight-200"
                  border :tree-props="{children: 'children'}" :default-expand-all="true"
                  highlight-current-row @selection-change="selectionChangeHandlerOrder"
        >
          <el-table-column type="selection" width="50" header-align="center" align="center"/>
          <el-table-column prop="permissionTitle" label="菜单名称" min-width="60%">
            <template v-slot="{row}">
              <li class="menu-item">
                <item :icon="row.permissionIcon" :title="row.permissionTitle"/>
                <el-tag v-if="row.permissionType === '0'" disable-transitions size="mini">路由</el-tag>
                <el-tag v-if="row.permissionType === '2'" disable-transitions type="success" size="mini">外链</el-tag>
                <el-tag v-if="!row.permissionIsShow" disable-transitions type="danger" size="mini">隐藏</el-tag>
                <el-tag v-if="!row.permissionIsEnabled" disable-transitions type="danger" size="mini">禁用</el-tag>
                <div v-if="!buttonTableVisible" style="display: inline-block;margin-left: 10px;">
                  <el-button v-permission="'system-menu-update-parent'" type="text" plain
                             icon="el-icon-sort" size="mini" @click="permissionUpdateParent(row)"
                  />
                  <el-button v-permission="'system-menu-update'" type="text" plain size="mini"
                             @click="permissionUpdate(row)"
                  >修改
                  </el-button>
                  <el-button v-if="row.permissionType === '0'" type="text" plain size="mini"
                             @click="subordinatesAdd(row)"
                  >添加下级
                  </el-button>
                </div>
                <div v-if="!buttonTableVisible" style="float: right;">
                  <el-tag type="info" size="mini">{{ row.permissionOrder }}</el-tag>
                </div>
              </li>
            </template>
          </el-table-column>
          <el-table-column prop="permissionRouter" label="菜单详情" width="300" :show-overflow-tooltip="true">
            <template v-slot="{row}">
              <el-tooltip v-if="row.permissionType === '0'" class="item" effect="dark" placement="left">
                <div slot="content" :key="'tipcontent'+row.permissionId" style="line-height: 30px;">
                  菜单标记：{{ row.permissionName }}
                  <br>菜单地址：{{ row.permissionRouter }}
                  <br>组件地址：{{ row.permissionComponent }}
                  <br>noRedirect: {{ row.noRedirect }}
                  <br>noCache: {{ row.noCache }}
                  <br>breadcrumb: {{ row.breadcrumb }}
                  <br>affix: {{ row.affix }}
                  <br>activeMenu: {{ row.activeMenu }}
                </div>
                <el-button>{{ row.permissionRouter }}</el-button>
              </el-tooltip>
              <a v-else :href="row.permissionRouter" style="text-decoration: underline;color: #1890ff;"
                 target="_blank"
              >{{ row.permissionRouter }}</a>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="100" header-align="center" align="center">
            <template slot-scope="{row}">
              <div>
                <el-button v-if="row.permissionIsEnabled" type="text" plain size="mini"
                           @click="changeIsEnabled(row, false)"
                >禁用
                </el-button>
                <el-button v-else type="text" plain size="mini" @click="changeIsEnabled(row, true)">启用</el-button>
                <el-button v-if="row.permissionType !== '2'" type="text" plain size="mini"
                           @click="openButtonTable(row)"
                >按钮
                </el-button>
              </div>
              <!--              <div v-if="row.permissionType === '2'">-->
              <!--                <el-button v-if="row.permissionIsEnabled" type="text" plain size="mini"-->
              <!--                           @click="changeIsEnabled(row, false)"-->
              <!--                >禁用-->
              <!--                </el-button>-->
              <!--                <el-button v-else type="text" plain size="mini" @click="changeIsEnabled(row, true)">启用</el-button>-->
              <!--              </div>-->
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col v-if="buttonTableVisible" :span="8" :style="rightStyle">
        <PermissionButton :current-permission-row="currentPermissionRow" :close-button-table="closeButtonTable"/>
      </el-col>
    </el-row>

    <!--    添加和修改菜单窗口-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="700px"
               :close-on-click-modal="false"
    >
      <el-form ref="dataForm" :model="temp" :rules="rules" label-position="right" label-width="110px"
               style="width: 600px; margin-left: 30px;"
      >
        <el-form-item label="菜单类型：" prop="permissionType">
          <el-radio v-model="temp.permissionType" label="0" @change="routerShow=true">路由</el-radio>
          <el-radio v-model="temp.permissionType" label="2" @change="routerShow=false">外链</el-radio>
        </el-form-item>
        <el-form-item label="菜单名称：" prop="permissionTitle">
          <el-input v-model="temp.permissionTitle" placeholder="菜单显示名称"/>
        </el-form-item>
        <el-form-item v-if="routerShow" label="菜单标记：" prop="permissionName">
          <el-input v-model="temp.permissionName"
                    placeholder="唯一标记-用于控制权限，推荐格式：父包-模块（例：system-menu）"
          />
        </el-form-item>
        <el-form-item label="菜单描述：" prop="permissionDescription">
          <el-input v-model="temp.permissionDescription" type="textarea" placeholder="菜单功能简介"/>
        </el-form-item>
        <el-form-item label="菜单图标：" prop="permissionIcon">
          <IconPicker v-model="temp.permissionIcon"/>
        </el-form-item>
        <el-form-item label="菜单地址：" prop="permissionRouter">
          <el-input v-if="routerShow" v-model="temp.permissionRouter" placeholder="菜单地址，不含/index（例：/system/menu）"
                    @input="permissionRouterInput"
          />
          <el-input v-else v-model="temp.permissionRouter" placeholder="外链以 http:// 或 https:// 开头"/>
        </el-form-item>
        <el-form-item v-if="routerShow" label="组件地址：" prop="permissionComponent">
          <el-input v-model="temp.permissionComponent" placeholder="组件完整地址（例：/system/menu/index）"/>
          <el-tag type="info">根节点，且有子菜单时，请填写：/layout/index</el-tag>
        </el-form-item>

        <el-form-item v-if="routerShow" label="是否显示：" prop="permissionIsShow">
          <el-switch v-model="temp.permissionIsShow" :active-value="true" active-text="显示"
                     :inactive-value="false" inactive-text="隐藏"
          />
        </el-form-item>
        <el-form-item v-if="routerShow && !temp.permissionIsShow" label="activeMenu：" prop="activeMenu">
          <el-input v-model="temp.activeMenu" placeholder="本路由hidden时，请填写菜单栏高亮显示的路由"/>
        </el-form-item>

        <el-form-item v-if="routerShow" label="redirect：" prop="noRedirect">
          <el-switch v-model="temp.noRedirect" active-value="noRedirect" active-text="NoRedirect"
                     inactive-value="" inactive-text="否"
          />
          <el-tag type="info" style="margin-left: 20px;">等于noRedirect时,在面包屑导航不可点击</el-tag>
        </el-form-item>
        <el-form-item v-if="routerShow" label="noCache：" prop="noCache">
          <el-switch v-model="temp.noCache" :active-value="true" active-text="TRUE"
                     :inactive-value="false" inactive-text="FALSE"
          />
          <el-tag type="info" style="margin-left: 20px;">默认false,为true时不被keep-alive缓存</el-tag>
        </el-form-item>
        <el-form-item v-if="routerShow" label="breadcrumb：" prop="breadcrumb">
          <el-switch v-model="temp.breadcrumb" :active-value="true" active-text="TRUE"
                     :inactive-value="false" inactive-text="FALSE"
          />
          <el-tag type="info" style="margin-left: 20px;">默认true,为false时不在面包屑中显示</el-tag>
        </el-form-item>
        <el-form-item v-if="routerShow" label="affix：" prop="affix">
          <el-switch v-model="temp.affix" :active-value="true" active-text="TRUE"
                     :inactive-value="false" inactive-text="FALSE"
          />
          <el-tag type="info" style="margin-left: 20px;">默认false,为true时固定在标签里</el-tag>
        </el-form-item>

        <el-form-item label="菜单顺序：" prop="permissionOrder">
          <el-input-number v-model.number="temp.permissionOrder" :min="0"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitJudgment(dialogStatus)">保存</el-button>
        <el-button @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
    <!--    修改上下级菜单-->
    <el-dialog title="修改上下级" :visible.sync="parentDialogVisible" width="400px">
      <el-form ref="parentDataForm" :model="temp2">
        <el-button v-if="temp2.parentId==='-1'" type="danger">已选择顶级</el-button>
        <el-button v-else @click="handleNodeClick('-1')">顶级</el-button>
        <el-divider content-position="center">请选择父级菜单</el-divider>
        <el-tree ref="parentTree" :key="temp2.permissionId" :data="tableData"
                 :props="{children: 'children',label: 'permissionTitle'}" :highlight-current="true"
                 :default-expand-all="true"
                 :expand-on-click-node="false" node-key="permissionId" :current-node-key.sync="temp2.parentId"
                 @node-click="handleNodeClick"
        />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPermissionParent">保存</el-button>
        <el-button @click="parentDialogVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  permissionAdd,
  permissionDelete,
  permissionTreeList,
  permissionUpdate,
  updateParentId
} from '@/api/permission.js'
// 引入图标选择器
import IconPicker from '@/views/system/menu/IconPicker/index';
// 引入按钮组件
import PermissionButton from '@/views/system/menu/permissionButton/index'
// 菜单项目
import Item from '@/layout/components/Sidebar/Item';
import request from '@/utils/request';
import {generateUUID} from "@/utils/tools";

export default {
  components: {IconPicker, PermissionButton, Item},
  data() {
    return {
      // 表格数据
      tableData: [],
      // 查询数据条数
      total: 0,
      // 分页属性
      listQuery: {page: 1, limit: 10},
      // 勾选的数据
      changeData: [],
      // 勾选的ID
      permissionIds: [],
      // 传给按钮组件的数据
      currentPermissionRow: '',
      // 对话框属性
      dialogStatus: '',
      textMap: {update: '修改', create: '新增', subordinates: '添加下级'},
      // 对话框弹出控制
      dialogFormVisible: false,
      // 按钮表格显示控制
      buttonTableVisible: false,
      // <el-col>的span值
      elColSpanValue: 24,
      temp: {},
      rules: {
        permissionTitle: [{required: true, message: '请填写菜单名称', trigger: 'blur'}],
        permissionName: [{required: true, message: '请填写菜单标记', trigger: 'blur'}],
        permissionRouter: [{required: true, message: '请填写路由地址', trigger: 'blur'}],
        permissionComponent: [{required: true, message: '请填写组件地址', trigger: 'blur'}],
        permissionOrder: [{required: true, message: '请填写菜单顺序', trigger: 'blur'}, {
          type: 'number',
          message: '请填写数字'
        }]
      },
      // vue路由相关配置表单
      routerShow: true,
      // 修改上下级关系表单
      temp2: {},
      parentDialogVisible: false
    }
  },
  computed: {
    rightStyle() {
      return 'background-color:#f6f6f6;border-radius:5px;height:' + (window.innerHeight - 125) + 'px;'
    }
  },
  created() {
    this.getPermissionTreeList()
    this.resetTemp();
  },
  methods: {
    toggleRouterShow(val) {
      this.routerShow = val === '0'
    },
    //查询下拉树
    async getPermissionTreeList() {
      this.listLoading = true
      const {data} = await permissionTreeList()
      this.tableData = data
      this.listLoading = false
    },
    resetTemp() {
      this.temp = {
        permissionId: '',
        parentId: '-1',
        permissionName: '',
        permissionDescription: '',
        permissionType: '0',
        permissionTitle: '',
        permissionIcon: '',
        permissionRouter: '',
        permissionComponent: '',
        permissionIsEnabled: true,
        permissionIsShow: true,
        noRedirect: 'noRedirect',
        noCache: false,
        breadcrumb: true,
        affix: false,
        permissionOrder: 0
      }
      this.routerShow = true
    },
    // 查询表格数据
    getList() {
      permissionTreeList().then(response => {
        this.tableData = response.data
        this.total = response.data.length
      })
    },
    // 表格勾选
    selectionChangeHandlerOrder(val) {
      this.changeData = val
    },
    // 点击新增按钮后
    permissionAdd() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 新增数据提交
    createData() {
      // 表单验证
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.temp.noRedirect !== 'noRedirect') {
            this.temp.noRedirect = this.temp.permissionRouter
          }
          // 外链处理菜单标记
          if (this.temp.permissionType === '2') {
            this.temp.permissionName = generateUUID()
          }
          permissionAdd(this.temp).then(response => {
            this.dialogFormVisible = false
            if (response.data) {
              this.$message({
                type: 'success',
                message: '添加成功！'
              });
              this.getPermissionTreeList()
            } else {
              this.$message({
                type: 'error',
                message: '添加失败！'
              });
            }
          })
        }
      })
    },
    // 点击修改按钮后
    permissionUpdate(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      if (this.changeData.length <= 0) {
        this.$message({
          message: '请选择一条数据进行修改！',
          type: 'warning'
        });
      } else if (this.changeData.length > 1) {
        this.$message({
          message: '修改时只允许选择一条数据！',
          type: 'warning'
        });
      } else {
        // Object.assign：把changeData[0]的值复制到集合{}
        this.temp = Object.assign({}, this.changeData[0])
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          // 路由or外链
          this.routerShow = (this.temp.permissionType === '0')
          // 清除表单验证
          this.$refs['dataForm'].clearValidate()
        })
      }
    },
    // 修改上下级关系
    permissionUpdateParent(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      if (this.changeData.length <= 0) {
        this.$message({
          message: '请选择一条数据进行修改！',
          type: 'warning'
        });
      } else if (this.changeData.length > 1) {
        this.$message({
          message: '修改时只允许选择一条数据！',
          type: 'warning'
        });
      } else {
        const changeData = this.changeData
        // Object.assign：把changeData[0]的值复制到集合{}
        this.temp2 = Object.assign({}, changeData[0])
        this.parentDialogVisible = true
        this.$nextTick(() => {
          // 清除表单验证
          this.$refs['parentDataForm'].clearValidate()
        })
      }
    },
    //  提交修改数据
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.temp.noRedirect !== 'noRedirect') {
            this.temp.noRedirect = this.temp.permissionRouter
          }
          permissionUpdate(this.temp).then(response => {
            this.dialogFormVisible = false
            if (response.data) {
              this.$message({
                type: 'success',
                message: '修改成功！'
              });
              this.getPermissionTreeList()
            } else {
              this.$message({
                type: 'error',
                message: '修改失败！'
              });
            }
          })
        }
      })
    },
    // 数据删除
    permissionDelete() {
      if (this.changeData.length <= 0) {
        this.$message({message: '请选择一条数据进行删除！', type: 'warning'});
      } else {
        const changeData = this.changeData
        this.$confirm('永久删除菜单无法恢复，确定要删除吗?', '提示', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          this.$confirm('会同时删除子元素（按钮等），确定要删除吗?', '提示', {
            confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
          }).then(() => {
            for (var i = 0; i < changeData.length; i++) {
              this.permissionIds.push(changeData[i].permissionId)
            }
            permissionDelete(this.permissionIds).then(response => {
              if (response.data) {
                this.$message({
                  type: 'success',
                  message: '删除成功！'
                });
                this.getPermissionTreeList()
              } else {
                this.$message({
                  type: 'error',
                  message: '删除失败！'
                });
              }
            })
          })
        });
      }
    },
    // 添加下级
    subordinatesAdd(row) {
      if (row.permissionType === '1') {
        this.$message({
          message: '按钮无法添加下级！',
          type: 'warning'
        })
      } else {
        this.resetTemp()
        this.temp = {
          ...this.temp,
          parentId: row.permissionId,
          permissionName: row.permissionName + '-',
          permissionRouter: row.permissionRouter + '/'
        }
        this.dialogFormVisible = true
        this.dialogStatus = 'subordinates'
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      }
    },
    // 启用/禁用
    changeIsEnabled(row, isEnabled) {
      let data = {permissionId: row.permissionId, permissionIsEnabled: isEnabled}
      request({
        url: '/permission/changeIsEnabled', method: 'post', data
      }).then((response) => {
        this.$message({type: 'success', message: isEnabled ? '启用菜单成功！' : '禁用菜单成功！'})
        this.getPermissionTreeList()
      })
    },
    // 保存菜单
    submitJudgment(dialogStatus) {
      if (dialogStatus === 'update') {
        this.updateData()
      } else {
        this.createData()
      }
    },
    //点击[按钮]，打开右侧按钮面板
    openButtonTable(row) {
      this.buttonTableVisible = true
      this.elColSpanValue = 16
      this.currentPermissionRow = row
    },
    //关闭右侧按钮面板
    closeButtonTable() {
      this.buttonTableVisible = false
      this.elColSpanValue = 24
    },
    // 选择父级菜单点击事件
    handleNodeClick(node) {
      if (node === '-1') {
        this.temp2.parentId = '-1'
        this.$refs.parentTree.setCurrentKey(null)
      } else {
        this.temp2.parentId = node.permissionId
      }
    },
    // 保存上下级关系
    submitPermissionParent() {
      updateParentId(this.temp2).then(response => {
        this.parentDialogVisible = false
        if (response.data) {
          this.$message({
            type: 'success',
            message: '修改成功！'
          });
          this.getPermissionTreeList()
        } else {
          this.$message({
            type: 'error',
            message: '修改失败！'
          });
        }
      })
    },
    // 路由输入监听
    permissionRouterInput() {
      if (this.temp.permissionComponent === '' || this.temp.permissionComponent.endsWith('/index')) {
        this.temp.permissionComponent = this.temp.permissionRouter + '/index'
      }
    }
  }
}
</script>
<style>
.menu-item {
  list-style: none;
  display: inline;
}

.menu-item > span {
  padding: 0px 5px !important;
}
</style>
