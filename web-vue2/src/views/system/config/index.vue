<template>
  <div class="app-container">
    <!-- 参数参数配置-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.cfgName" size="small" clearable style="width: 170px;margin-right: 10px;"
                class="filter-item" placeholder="请输入参数名称查询"/>
      <el-input v-model="searchData.cfgKey" size="small" clearable style="width: 170px;margin-right: 10px;"
                class="filter-item" placeholder="请输入参数键名查询"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">显示全部
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'config-zConfig-add'">新增
        </el-button>
      </div>
    </div>
    <!-- 参数参数配置-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="参数名称" prop="cfgName" align="center"/>
      <el-table-column label="参数键名" prop="cfgKey" align="center">
        <template v-slot="{row}">
          <el-tag type="primary" effect="plain">{{row.cfgKey}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="参数键值" prop="cfgValue" align="center"/>
      <el-table-column label="是否系统参数" prop="cfgIsSys" align="center" width="70">
        <template v-slot="{row}">
          <el-tag type="primary" size="small" v-if="row.cfgIsSys === '1'">是</el-tag>
          <el-tag type="danger" size="small" v-else>否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="cfgRemark" align="center" show-overflow-tooltip/>
      <el-table-column label="顺序" prop="orderIndex" align="center" width="60"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;"
                     size="small" @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'config-zConfig-update'"
                     type="text" size="small" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'config-zConfig-delete'" v-if="scope.row.cfgIsSys === '0'"
                     style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 参数参数配置-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="resetTemp" width="600px" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="110px" :disabled="dialogType==='view'">
        <el-form-item label="参数名称" prop="cfgName"
                      :rules="[{required: true, message: '参数名称不能为空'}]">
          <el-input v-model="temp.cfgName" placeholder="请输入参数名称"/>
        </el-form-item>
        <el-form-item label="参数键名" prop="cfgKey"
                      :rules="[{required: true, message: '参数键名不能为空'}]">
          <el-input v-model="temp.cfgKey" placeholder="请输入参数键名"/>
        </el-form-item>
        <el-form-item label="参数键值" prop="cfgValue"
                      :rules="[{required: true, message: '参数键值不能为空'}]">
          <el-input v-model="temp.cfgValue" placeholder="请输入参数键值"/>
        </el-form-item>

        <el-form-item label="是否系统参数" prop="cfgIsSys"
                      :rules="[{required: true, message: '是否系统参数不能为空'}]">
          <el-radio-group v-model="temp.cfgIsSys" size="small">
            <el-radio-button label="1">是</el-radio-button>
            <el-radio-button label="0">否</el-radio-button>
          </el-radio-group>
          <el-tag type="danger" style="margin-left:10px;"
                v-if="temp.cfgIsSys==='1'">系统参数不能删除</el-tag>
        </el-form-item>
        <el-form-item label="备注" prop="cfgRemark"
                      :rules="[]">
          <el-input type="textarea" v-model="temp.cfgRemark" placeholder="请输入备注"/>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
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
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加参数参数配置', update: '修改参数参数配置', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      isLoading: false,
      dialogIndex: 0,
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  methods: {
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.loadTableList()
    },
    // 显示全部
    resetTableList() {
      this.pager.page = 1
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/config/zConfig/list', method: 'get', params
      }).then((response) => {
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
    resetTemp() {
      this.temp = {orderIndex: 0, cfgIsSys: '1'}
      this.dialogIndex++
    },
    // 打开添加窗口
    openAdd() {
      this.resetTemp()
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
            request({
              url: '/config/zConfig/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/config/zConfig/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.cfgId)
          request({
            url: '/config/zConfig/delete', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)}
      downloadUtil.download('/config/zConfig/export/excel', params, '参数参数配置.xlsx')
    },
    // 导入Excel成功，提示
    importExcelSuccess(response) {
      if (response.code === '200') {
        this.$message({type: 'success', message: '导入成功！'})
        this.loadTableList()
      } else {
        this.$message({type: 'error', message: response.message})
      }
    },
  }
}
</script>
