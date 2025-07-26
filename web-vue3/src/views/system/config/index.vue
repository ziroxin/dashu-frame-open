<template>
  <div class="app-container">
    <!-- 参数参数配置-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.cfgName" clearable class="searchInput w-170px" placeholder="参数名称"/>
        <el-input v-model="searchData.cfgKey" clearable class="searchInput w-170px" placeholder="参数键名"/>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel">
        <base-button type="primary" icon="el-icon-plus" @click="openAdd" v-permission="'config-zConfig-add'">新增
        </base-button>
      </div>
    </div>
    <!-- 参数参数配置-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="参数名称" prop="cfgName" align="center"/>
      <el-table-column label="参数键名" prop="cfgKey" align="center">
        <template #default="{row}">
          <el-tag type="primary" effect="plain">{{ row.cfgKey }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="参数键值" prop="cfgValue" align="center"/>
      <el-table-column label="是否系统参数" prop="cfgIsSys" align="center" width="70">
        <template #default="{row}">
          <el-tag type="primary" size="small" v-if="row.cfgIsSys === '1'">是</el-tag>
          <el-tag type="danger" size="small" v-else>否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="cfgRemark" align="center" show-overflow-tooltip/>
      <el-table-column label="顺序" prop="orderIndex" align="center" width="60"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template #default="scope">
          <base-button link style="color: #13ce66;" size="small" @click="openView(scope.row)">详情</base-button>
          <base-button v-permission="'config-zConfig-update'" link type="primary" size="small"
                       @click="openUpdate(scope.row)">修改
          </base-button>
          <base-button v-permission="'config-zConfig-delete'" v-if="scope.row.cfgIsSys === '0'"
                       link size="small" style="color: #ff6d6d;" @click="deleteByIds(scope.row)">删除
          </base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 参数参数配置-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               v-model="dialogFormVisible" @close="resetTemp" width="600px" :key="'myDialog'+dialogIndex">
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
          <!-- 系统参数，不能改成非系统参数 -->
          <template v-if="dialogType==='view'||(dialogType==='update'&&temp.noUpdateCfgIsSys==='1')">
            <el-tag type="primary" v-if="temp.cfgIsSys==='1'">是</el-tag>
            <el-tag type="primary" v-if="temp.cfgIsSys==='0'">否</el-tag>
          </template>
          <el-radio-group v-else v-model="temp.cfgIsSys">
            <el-radio-button value="1">是</el-radio-button>
            <el-radio-button value="0">否</el-radio-button>
          </el-radio-group>
          <el-tag type="danger" class="ml-10px" v-if="temp.cfgIsSys==='1'" disable-transitions>系统参数不能删除</el-tag>
        </el-form-item>
        <el-form-item label="备注" prop="cfgRemark" :rules="[]">
          <el-input type="textarea" v-model="temp.cfgRemark" placeholder="请输入备注"/>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0" step-strictly/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button type="primary" v-if="dialogType!=='view'" @click="saveData">保存</base-button>
          <base-button @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util'

export default {
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
      dialogIndex: 0
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
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)}
      request({url: '/config/zConfig/list', method: 'get', params}).then((response) => {
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
      this.dialogFormVisible = false
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
        this.temp.noUpdateCfgIsSys = this.temp.cfgIsSys// 如果是系统参数，设置不能修改该属性
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
          const data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '/config/zConfig/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/config/zConfig/add', method: 'post', data}).then(response => {
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
          request({url: '/config/zConfig/delete', method: 'post', data}).then(response => {
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
    }
  }
}
</script>
