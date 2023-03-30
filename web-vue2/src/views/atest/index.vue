<template>
	<div class="app-container">
		<!-- 测试表-管理按钮 -->
		<div style="margin-bottom: 20px;">
			<el-input v-model="searchData.testId" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入测试id查询"/>
			<el-input v-model="searchData.testName" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入姓名查询"/>
			<el-input v-model="searchData.testAge" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入年龄查询"/>
			<el-input v-model="searchData.testSex" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入性别查询"/>
			<el-input v-model="searchData.orderIndex" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入顺序查询"/>
			<el-date-picker v-model="searchData.createTime" style="width: 150px;margin-right: 10px;"
							type="datetime" class="filter-item" placeholder="请选择添加时间查询"/>
			<el-date-picker v-model="searchData.updateTime" style="width: 150px;margin-right: 10px;"
							type="datetime" class="filter-item" placeholder="请选择修改时间查询"/>
			<el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="loadTableList">查询</el-button>
			<el-button v-waves class="filter-item" type="info" icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
			<div style="float: right;">
				<el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd"
                   v-permission="'atest-aTest-add'">新增
				</el-button>
				<el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate"
                   v-permission="'atest-aTest-update'">修改
				</el-button>
				<el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds"
                   v-permission="'atest-aTest-delete'">删除
				</el-button>
				<el-upload v-permission="'atest-aTest-importExcel'" style="display: inline-block;margin: 0px 10px;"
						   :action="this.$baseServer+'/atest/aTest/import/excel'" :headers="this.$headerToken"
						   :on-success="importExcelSuccess" accept=".xls,.xlsx"
						   :show-file-list="false" :auto-upload="true">
					<el-button v-waves type="warning" icon="el-icon-upload2">导入Excel</el-button>
				</el-upload>
				<el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel"
						   v-permission="'atest-aTest-exportExcel'">导出Excel
				</el-button>
			</div>
		</div>
		<!-- 测试表-列表 -->
		<el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
			<el-table-column type="selection" width="50" align="center" header-align="center"/>
			<el-table-column label="测试id" prop="testId" align="center"/>
			<el-table-column label="姓名" prop="testName" align="center"/>
			<el-table-column label="年龄" prop="testAge" align="center"/>
			<el-table-column label="性别" prop="testSex" align="center"/>
			<el-table-column label="顺序" prop="orderIndex" align="center"/>
			<el-table-column label="添加时间" prop="createTime" align="center"/>
			<el-table-column label="修改时间" prop="updateTime" align="center"/>
			<el-table-column fixed="right" label="操作" width="100">
				<template v-slot="scope">
					<el-button type="text" size="small" @click="openView(scope.row)">查看详情</el-button>
				</template>
			</el-table-column>
		</el-table>
		<!-- 测试表-分页 -->
		<el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
		/>
		<!-- 添加修改弹窗 -->
		<el-dialog :title="titleMap[dialogType]" :close-on-click-modal="false"
				   :visible.sync="dialogFormVisible" @close="resetTemp" width="600px">
			<el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType=='view'">
				<el-form-item label="测试id" prop="testId"
                      :rules="[{required: true, message: '测试id不能为空'}]">
					<el-input v-model="temp.testId" placeholder="请输入测试id"/>
				</el-form-item>
				<el-form-item label="姓名" prop="testName"
                      :rules="[]">
					<el-input v-model="temp.testName" placeholder="请输入姓名"/>
				</el-form-item>
				<el-form-item label="年龄" prop="testAge"
                      :rules="[{type: 'number', message: '必须为数字'}]">
					<el-input v-model.number="temp.testAge" placeholder="请输入年龄"/>
				</el-form-item>
				<el-form-item label="性别" prop="testSex"
                      :rules="[]">
					<el-input v-model="temp.testSex" placeholder="请输入性别"/>
				</el-form-item>
				<el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
					<el-input-number v-model="temp.orderIndex" :min="0"/>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button v-waves type="primary" v-if="dialogType!='view'" @click="saveData">保存</el-button>
				<el-button v-waves @click="dialogFormVisible=false">取消</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'

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
      titleMap: {add: '添加测试表', update: '修改测试表', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  methods: {
    // 显示全部
    resetTableList() {
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/atest/aTest/list', method: 'get', params
      }).then((response) => {
        const {data} = response
        this.pager.totalCount = data.total
        this.tableData = data.records
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
    // 清空表单temp数据
    resetTemp() {
      this.temp = {orderIndex: 0}
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
    openUpdate() {
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
      // 修改弹窗
      this.temp = Object.assign({}, row)
      this.dialogType = 'view'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        for (const $elElement of this.$refs['dataForm'].$el) {
          $elElement.placeholder = '';
        }
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
              url: '/atest/aTest/update', method: 'put', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/atest/aTest/add', method: 'post', data
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
    deleteByIds() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.testId)
          request({
            url: '/atest/aTest/delete', method: 'delete', data
          }).then(response => {
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
        url: '/atest/aTest/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '测试表.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
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
