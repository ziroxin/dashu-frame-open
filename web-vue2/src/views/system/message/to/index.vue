<template>
	<div class="app-container">
		<!-- 消息发送至-管理按钮 -->
		<div style="margin-bottom: 10px;">
			<el-input v-model="searchData.toId" size="small" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入消息发送至ID查询"/>
			<el-input v-model="searchData.msgId" size="small" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入消息ID查询"/>
			<el-input v-model="searchData.toUserId" size="small" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入收信用户ID查询"/>
			<el-input v-model="searchData.sendUserId" size="small" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入发信用户ID查询"/>
			<el-input v-model="searchData.msgStatus" size="small" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入消息状态（0未读1已读）查询"/>
			<el-date-picker v-model="searchData.readTime" size="small" style="width: 150px;margin-right: 10px;"
							type="datetime" class="filter-item" placeholder="请选择已读时间查询"/>
			<el-date-picker v-model="searchData.createTime" size="small" style="width: 150px;margin-right: 10px;"
							type="datetime" class="filter-item" placeholder="请选择创建时间查询"/>
			<el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询</el-button>
			<el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
			<div style="float: right;">
				<el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'messageTo-zMessageTo-add'">新增
				</el-button>
				<el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate(null)" size="small"
                   v-permission="'messageTo-zMessageTo-update'">修改
				</el-button>
				<el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'messageTo-zMessageTo-delete'">删除
				</el-button>
				<el-upload v-permission="'messageTo-zMessageTo-importExcel'" style="display: inline-block;margin: 0px 10px;"
						   :action="$baseServer+'/messageTo/zMessageTo/import/excel'" :headers="$store.getters.headerToken"
						   :on-success="importExcelSuccess" accept=".xls,.xlsx"
						   :show-file-list="false" :auto-upload="true">
					<el-button v-waves type="warning" icon="el-icon-upload2" size="small">导入Excel</el-button>
				</el-upload>
				<el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel" size="small"
						   v-permission="'messageTo-zMessageTo-exportExcel'">导出Excel
				</el-button>
			</div>
		</div>
		<!-- 消息发送至-列表 -->
		<el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange" v-loading="isLoading">
			<el-table-column type="selection" width="50" align="center" header-align="center"/>
			<el-table-column label="消息发送至ID" prop="toId" align="center"/>
			<el-table-column label="消息ID" prop="msgId" align="center"/>
			<el-table-column label="收信用户ID" prop="toUserId" align="center"/>
			<el-table-column label="发信用户ID" prop="sendUserId" align="center"/>
			<el-table-column label="消息状态（0未读1已读）" prop="msgStatus" align="center"/>
			<el-table-column label="已读时间" prop="readTime" align="center"/>
			<el-table-column label="创建时间" prop="createTime" align="center"/>
			<el-table-column fixed="right" label="操作" width="120" align="center">
				<template v-slot="scope">
					<el-button type="text" style="color: #13ce66;"
							   size="small" @click="openView(scope.row)">详情</el-button>
					<el-button v-permission="'messageTo-zMessageTo-update'"
							   type="text" size="small" @click="openUpdate(scope.row)">修改
					</el-button>
					<el-button v-permission="'messageTo-zMessageTo-delete'" style="color: #ff6d6d;"
							   type="text" size="small" @click="deleteByIds(scope.row)">删除
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<!-- 消息发送至-分页 -->
		<el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
		/>
		<!-- 添加修改弹窗 -->
		<el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
				   :visible.sync="dialogFormVisible" @close="resetTemp" width="600px" :key="'myDialog'+dialogIndex">
			<el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
				<el-form-item label="消息发送至ID" prop="toId"
                      :rules="[{required: true, message: '消息发送至ID不能为空'}]">
					<el-input v-model="temp.toId" placeholder="请输入消息发送至ID"/>
				</el-form-item>
				<el-form-item label="消息ID" prop="msgId"
                      :rules="[{required: true, message: '消息ID不能为空'}]">
					<el-input v-model="temp.msgId" placeholder="请输入消息ID"/>
				</el-form-item>
				<el-form-item label="收信用户ID" prop="toUserId"
                      :rules="[{required: true, message: '收信用户ID不能为空'}]">
					<el-input v-model="temp.toUserId" placeholder="请输入收信用户ID"/>
				</el-form-item>
				<el-form-item label="发信用户ID" prop="sendUserId"
                      :rules="[{required: true, message: '发信用户ID不能为空'}]">
					<el-input v-model="temp.sendUserId" placeholder="请输入发信用户ID"/>
				</el-form-item>
				<el-form-item label="消息状态（0未读1已读）" prop="msgStatus"
                      :rules="[{required: true, message: '消息状态（0未读1已读）不能为空'}]">
					<el-input v-model="temp.msgStatus" placeholder="请输入消息状态（0未读1已读）"/>
				</el-form-item>
				<el-form-item label="已读时间" prop="readTime"
					  :rules="[{required: true, message: '已读时间不能为空'}]">
					<el-date-picker v-model="temp.readTime" type="datetime" placeholder="请选择已读时间"/>
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
      titleMap: {add: '添加消息发送至', update: '修改消息发送至', view: '查看详情'},
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
        url: '/messageTo/zMessageTo/list', method: 'get', params
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
              url: '/messageTo/zMessageTo/update', method: 'put', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/messageTo/zMessageTo/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.toId)
          request({
            url: '/messageTo/zMessageTo/delete', method: 'delete', data
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
      downloadUtil.download('/messageTo/zMessageTo/export/excel', params, '消息发送至.xlsx')
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
