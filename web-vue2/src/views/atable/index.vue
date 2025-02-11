<template>
  <div class="app-container">
    <!-- 我的表a_table-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.mobile" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入手机号查询"/>
      <el-input v-model="searchData.field116" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入测试单行文本查询"/>
      <el-input v-model="searchData.a" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入测试decimal查询"/>
      <el-input v-model="searchData.b" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入测试富文本查询"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'atable-aTable-add'">新增
        </el-button>
        <el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate(null)" size="small"
                   v-permission="'atable-aTable-update'">修改
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'atable-aTable-delete'">删除
        </el-button>
        <el-button v-waves v-permission="'atable-aTable-importExcel'" @click="dialogImportVisible=true"
                   type="primary" icon="el-icon-upload2" size="small">导入Excel
        </el-button>
        <el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel" size="small"
                   v-permission="'atable-aTable-exportExcel'">导出Excel
        </el-button>
      </div>
    </div>
    <!-- 我的表a_table-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange" @sort-change="handleTableSortChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="手机号" prop="mobile" align="center" sortable="custom"/>
      <el-table-column label="顺序" prop="orderIndex" align="center" sortable="custom"/>
      <el-table-column label="测试单行文本" prop="field116" align="center" sortable="custom"/>
      <el-table-column label="测试decimal" prop="a" align="center" sortable="custom"/>
      <el-table-column label="测试富文本" prop="b" align="center" sortable="custom"/>
      <el-table-column label="添加时间" prop="createTime" align="center" sortable="custom"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;"
                     size="small" @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'atable-aTable-update'"
                     type="text" size="small" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'atable-aTable-delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 我的表a_table-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="closeDialog" width="600px" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
        <el-form-item label="手机号" prop="mobile"
                      :rules="[{required: true, message: '手机号不能为空'}]">
          <el-input v-model="temp.mobile" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0"/>
        </el-form-item>
        <el-form-item label="测试单行文本" prop="field116"
                      :rules="[]">
          <el-input v-model="temp.field116" placeholder="请输入测试单行文本"/>
        </el-form-item>
        <el-form-item label="测试decimal" prop="a"
                      :rules="[{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model.number="temp.a" placeholder="请输入测试decimal"/>
        </el-form-item>
        <el-form-item label="测试富文本" prop="b"
                      :rules="[]">
          <el-input v-model="temp.b" type="textarea" maxlength="16,777,215"
                    placeholder="请输入测试富文本"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
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
        </el-form-item>
        <el-divider></el-divider>
        <el-form-item label="导入：">
          <el-upload v-permission="'atable-aTable-importExcel'"
                     :action="$baseServer+'/atable/aTable/import/excel'" :headers="$store.getters.headerToken"
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
import downloadUtil from '@/utils/download-util';

export default {
  directives: {waves},
  data() {
    return {
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {},
      // 排序数据
      sortData: {},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加我的表a_table', update: '修改我的表a_table', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {orderIndex: 0},
      isLoading: false,
      dialogIndex: 0,
      // 导入弹窗
      dialogImportVisible: false,
      isImportLoading: false,
    }
  },
  created() {
    this.loadTableList()
  },
  methods: {
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.loadTableList()
    },
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {
        ...this.pager,
        params: JSON.stringify(this.searchData),
        sorts: JSON.stringify(this.sortData)
      };
      request({url: '/atable/aTable/list', method: 'get', params}).then((response) => {
        const {data} = response
        this.pager.totalCount = data.total
        this.tableData = data.records
        this.isLoading = false
      })
    },
    // 监听选中行
    handleTableSelectChange(rows) {
      this.tableSelectRows = rows
    },
    // 监听排序
    handleTableSortChange(sort) {
      if (sort.order) {
        this.sortData = {column: sort.prop, order: sort.order === 'descending' ? 'DESC' : 'ASC'}
      } else {
        this.sortData = {}
      }
      this.loadTableList()
    },
    // 监听分页
    handleCurrentChange(page) {
      this.pager.page = page
      this.loadTableList()
    },
    // 分页条数改变
    handleSizeChange(size) {
      this.pager.limit = size
      this.loadTableList()
    },
    // 清空表单temp数据
    closeDialog() {
      this.temp = this.$options.data().temp
      this.dialogIndex++
    },
    // 打开添加窗口
    openAdd() {
      this.dialogFormVisible = true
      this.dialogType = 'add'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
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
          let data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '/atable/aTable/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/atable/aTable/add', method: 'post', data}).then(response => {
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
          const data = this.tableSelectRows.map(r => r.id)
          request({url: '/atable/aTable/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData), sorts: JSON.stringify(this.sortData)}
      downloadUtil.download('/atable/aTable/export/excel', params, '我的表a_table.xlsx')
    },
    // 导入Excel之前，显示loading
    beforeImportUpload(file) {
      this.isImportLoading = true
    },
    // 导入Excel成功
    importExcelSuccess(response) {
      this.isImportLoading = false
      if (response.message === "Success") {
        this.$message({type: 'success', message: '导入成功！'})
        this.dialogImportVisible = false
        this.loadTableList()
      } else {
        this.$alert(response.message, "提示",
            {confirmButtonText: "确定", dangerouslyUseHTMLString: true, customClass: 'width800'});
      }
    },
    // 导入Excel失败，取消loading状态
    importExcelError() {
      this.isImportLoading = false
    },
    // 下载模板
    downloadExcelTemplate() {
      downloadUtil.download('/atable/aTable/import/downloadTemplate', {}, '我的表a_table-导入模板.xlsx')
    },
  }
}
</script>
