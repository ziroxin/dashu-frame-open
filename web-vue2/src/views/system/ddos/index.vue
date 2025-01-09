<template>
	<div class="app-container">
		<!-- ddos用户请求记录-管理按钮 -->
		<div style="margin-bottom: 10px;">
			<el-input v-model="searchData.userIp" size="small" style="width: 100px;margin-right: 10px;"
					  		class="filter-item" placeholder="用户IP"/>
			<el-input v-model="searchData.requestCount" size="small" style="width: 200px;margin-right: 10px;"
					  		class="filter-item" placeholder="请求次数超过多少的"/>
			<el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询</el-button>
			<el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">重置</el-button>
      <div style="float: right;">
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'ddos-zDdos-delete'">删除
        </el-button>
      </div>
		</div>
		<!-- ddos用户请求记录-列表 -->
		<el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange" v-loading="isLoading">
			<el-table-column type="selection" width="50" align="center" header-align="center"/>
			<el-table-column label="用户IP" prop="userIp" align="center"/>
			<el-table-column label="请求次数" prop="requestCount" align="center"/>
			<el-table-column label="限制配置" prop="limitJson" align="center"/>
			<el-table-column label="userId" prop="userId" align="center"/>
			<el-table-column label="记录时间" prop="createTime" align="center"/>
		</el-table>
		<!-- ddos用户请求记录-分页 -->
		<el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
		/>
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
      titleMap: {add: '添加ddos用户请求记录', update: '修改ddos用户请求记录', view: '查看详情'},
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
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/ddos/zDdos/list', method: 'get', params
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
      this.temp = {orderIndex: 0}
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
              url: '/ddos/zDdos/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/ddos/zDdos/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.ddosId)
          request({
            url: '/ddos/zDdos/delete', method: 'post', data
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
      downloadUtil.download('/ddos/zDdos/export/excel', params, 'ddos用户请求记录.xlsx')
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
