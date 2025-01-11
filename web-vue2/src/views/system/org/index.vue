<template>
  <div class="app-container">
    <!-- 组织机构表-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.orgName" style="width: 220px;margin-right: 10px;"
                class="filter-item" placeholder="请输入组织机构名称查询" clearable
      />
      <el-button v-waves class="filter-item" size="small" type="primary"
                 icon="el-icon-search" @click="loadTableList">查询
      </el-button>
      <el-button v-waves class="filter-item" size="small" type="info"
                 icon="el-icon-refresh" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves v-permission="'zorg-zOrganization-add'" type="primary"
                   @click="openAdd(null)" size="small" icon="el-icon-plus">新增
        </el-button>
        <el-button v-waves v-permission="'zorg-zOrganization-importExcel'" @click="dialogImportVisible=true"
                   type="primary" size="small" icon="el-icon-upload2">批量导入
        </el-button>
        <el-button v-waves v-permission="'zorg-zOrganization-delete'" type="danger"
                   @click="deleteByIds(null)" size="small" icon="el-icon-delete">删除
        </el-button>
      </div>
    </div>
    <!-- 组织机构表-列表 -->
    <el-table ref="dataTable" :data="tableData" :tree-props="{children: 'children'}"
              row-key="orgId" :height="this.$windowHeight-197" v-loading="isLoading"
              default-expand-all border stripe @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="组织机构名称" prop="orgName" min-width="40%">
        <template v-slot="scope">
          <span>{{ scope.row.orgName }}</span>
          <div style="float: right;">
            <el-button v-permission="'zorg-zOrganization-add'" v-if="maxLevel===-1 || scope.row.orgLevel<maxLevel"
                       type="text" size="mini" @click="openAdd(scope.row)">添加下级
            </el-button>
            <el-button v-permission="'zorg-zOrganization-update'" type="text" size="mini"
                       @click="openUpdate(scope.row)">修改
            </el-button>
            <el-button v-permission="'zorg-zOrganization-delete'" style="color: #f56c6c;"
                       type="text" size="mini" @click="deleteByIds(scope.row)">删除
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remarks" align="center" min-width="30%"/>
      <el-table-column label="层级" prop="orgLevel" align="center" width="100"/>
      <el-table-column label="顺序" prop="orderIndex" align="center" width="100"/>
    </el-table>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="120px"
               style="width: 600px;" :disabled="dialogType==='view'">
        <el-form-item label="组织机构名称" prop="orgName"
                      :rules="[{required: true, message: '组织机构名称不能为空'}]">
          <el-input v-model="temp.orgName" placeholder="请输入组织机构名称"/>
        </el-form-item>
        <el-form-item label="上级部门" prop="orgParentId" v-if="this.temp.orgId!==this.currentOrgId"
                      :rules="[{required: true, message: '上级部门不能为空'}]">
          <el-cascader v-model="temp.orgParentId" placeholder="请选择上级" style="width: 100%;"
                       :options="treeSelectData" filterable
                       :props="{value:'orgId',label:'orgName',checkStrictly:true,emitPath:false}"/>
        </el-form-item>
        <el-form-item label="上级部门" v-else>
          <el-tag type="info">不能修改自己的上级部门</el-tag>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]"
        >
          <el-input-number v-model="temp.orderIndex" :min="0"/>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="temp.remarks" placeholder="请输入备注" type="textarea"
                    :autosize="{minRows: 2, maxRows: 5}" maxlength="300"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="dialogType!=='view'" v-waves type="primary" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog title="批量导入" :close-on-click-modal="false" :visible.sync="dialogImportVisible"
               @close="dialogIndex++" width="600px" :key="'importDialog'+dialogIndex">
      <el-form ref="importForm" label-width="120px" v-loading="isImportLoading">
        <el-form-item label="下载模板：">
          <el-button v-waves type="success" plain @click="downloadExcelTemplate"
                     icon="el-icon-download" size="small">下载Excel模板
          </el-button>
          <el-tag type="danger">
            说明：[上级部门]字段，填写上级部门名称，若是最高一级部门，请填写“顶级”
          </el-tag>
        </el-form-item>
        <el-divider></el-divider>
        <el-form-item label="导入：">
          <el-upload v-permission="'zorg-zOrganization-importExcel'"
                     :action="$baseServer+'/zorg/zOrganization/import/excel'" :headers="$store.getters.headerToken"
                     :before-upload="beforeImportUpload" :on-error="importExcelError"
                     :on-success="importExcelSuccess" accept=".xls,.xlsx"
                     :show-file-list="false" :auto-upload="true">
            <el-button v-waves type="primary" plain icon="el-icon-upload2" size="small">点击上传Excel并导入</el-button>
          </el-upload>
          <el-tag type="info" size="small">
            说明：点击上方按钮上传Excel文件，上传成功后会自动开始导入！
          </el-tag>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves @click="dialogImportVisible=false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'
import SelectTree from '@/components/SelectTree';
import downloadUtil from '@/utils/download-util';

export default {
  components: {SelectTree},
  directives: {waves},
  data() {
    return {
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加组织机构表', update: '修改组织机构表', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      // 下拉树-组织机构
      treeSelectData: [],
      // 最大层级
      maxLevel: 0,
      // 当前用户的OrgId
      currentOrgId: '',
      isLoading: false,
      dialogIndex: 0,
      // 导入弹窗
      dialogImportVisible: false,
      isImportLoading: false,
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
    this.loadMaxLevel()
  },
  methods: {
    loadMaxLevel() {
      this.$request({url: '/zorg/zOrganization/getMaxLevel', method: 'get'}).then((response) => {
        this.maxLevel = response.data.maxLevel
        this.currentOrgId = response.data.currentOrgId
      })
    },
    // 重置
    resetTableList() {
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {...this.searchData};
      request({url: '/zorg/zOrganization/tree', method: 'get', params}).then((response) => {
        this.tableData = response.data
        this.isLoading = false
      })
    },
    // 加载下拉选择框组织机构树
    loadParentTree() {
      request({url: '/zorg/zOrganization/parentTree', method: 'get'}).then((response) => {
        this.treeSelectData = response.data
        this.treeSelectData.forEach(item => {
          if (item.orgId === '-1') item.orgName = '顶级部门'
        })
      })
    },
    // 监听选中行
    handleTableSelectChange(rows) {
      this.tableSelectRows = rows
    },
    // 清空表单temp数据
    resetTemp() {
      this.temp = {orderIndex: 0}
    },
    // 打开添加窗口
    openAdd(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      this.resetTemp()
      if (this.tableSelectRows.length === 1) {
        this.temp.orgParentId = this.tableSelectRows[0].orgId
      }
      this.dialogFormVisible = true
      this.dialogType = 'add'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        this.loadParentTree()
      })
    },
    // 打开修改窗口
    openUpdate(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
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
          this.$refs['dataForm'].clearValidate()
          this.loadParentTree()
        })
      }
    },
    // 打开查看窗口
    openView(row) {
      this.temp = Object.assign({}, row)
      this.dialogType = 'view'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 添加/修改，保存事件
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var data = this.temp;
          if (this.dialogType === 'update') {
            request({
              url: '/zorg/zOrganization/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/zorg/zOrganization/add', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '添加成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          }
        }
      })
    },
    // 删除
    deleteByIds(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.orgId)
          request({url: '/zorg/zOrganization/delete', method: 'post', data}).then(() => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)};
      request({
        url: '/zorg/zOrganization/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '组织机构表.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    },
    // 导入Excel成功，提示
    importExcelSuccess(response) {
      this.isImportLoading = false
      if (response.message === 'Success') {
        this.$message({type: 'success', message: '导入成功！'})
        this.dialogImportVisible = false
        this.loadTableList()
      } else {
        this.$alert(response.message, "提示",
            {confirmButtonText: "确定", dangerouslyUseHTMLString: true, customClass: 'width800'});
      }
    },
    // 导入Excel之前，显示loading
    beforeImportUpload(file) {
      this.isImportLoading = true
    },
    // 导入Excel失败，取消loading状态
    importExcelError() {
      this.isImportLoading = false
    },
    // 下载模板
    downloadExcelTemplate() {
      downloadUtil.download('/zorg/zOrganization/import/downloadTemplate', {}, '组织机构-导入模板.xlsx')
    },
  }
}
</script>
