<template>
	<div class="app-container">
		<!-- 组织机构表-管理按钮 -->
		<div style="margin-bottom: 20px;">
			<el-input v-model="searchData.orgId" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入组织机构ID查询"/>
			<el-input v-model="searchData.orgName" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入组织机构名称查询"/>
			<el-input v-model="searchData.orgParentId" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入父级ID查询"/>
			<el-input v-model="searchData.orgPath" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入组织机构路径(格式:id1.id2.id3)查询"/>
			<el-input v-model="searchData.orgLevel" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入层级查询"/>
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
                   v-permission="'zorg-zOrganization-add'">新增
				</el-button>
				<el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate"
                   v-permission="'zorg-zOrganization-update'">修改
				</el-button>
				<el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds"
                   v-permission="'zorg-zOrganization-delete'">删除
				</el-button>
				<el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel"
						   v-permission="'zorg-zOrganization-exportExcel'">导出Excel
				</el-button>
			</div>
		</div>
		<!-- 组织机构表-列表 -->
		<el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
			<el-table-column type="selection" width="50" align="center" header-align="center"/>
			<el-table-column label="组织机构ID" prop="orgId" align="center"/>
			<el-table-column label="组织机构名称" prop="orgName" align="center"/>
			<el-table-column label="父级ID" prop="orgParentId" align="center"/>
			<el-table-column label="组织机构路径(格式:id1.id2.id3)" prop="orgPath" align="center"/>
			<el-table-column label="层级" prop="orgLevel" align="center"/>
			<el-table-column label="顺序" prop="orderIndex" align="center"/>
			<el-table-column label="添加时间" prop="createTime" align="center"/>
			<el-table-column label="修改时间" prop="updateTime" align="center"/>
			<el-table-column fixed="right" label="操作" width="100">
				<template v-slot="scope">
					<el-button type="text" size="small" @click="openView(scope.row)">查看详情</el-button>
				</template>
			</el-table-column>
		</el-table>
		<!-- 组织机构表-分页 -->
		<el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
		/>
		<!-- 添加修改弹窗 -->
		<el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible">
			<el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
               style="width: 500px; margin-left: 50px;" :disabled="dialogType=='view'">
				<el-form-item label="组织机构ID" prop="orgId"
                      :rules="[{required: true, message: '组织机构ID不能为空'}]">
					<el-input v-model="temp.orgId" placeholder="请输入组织机构ID"/>
				</el-form-item>
				<el-form-item label="组织机构名称" prop="orgName"
                      :rules="[]">
					<el-input v-model="temp.orgName" placeholder="请输入组织机构名称"/>
				</el-form-item>
				<el-form-item label="父级ID" prop="orgParentId"
                      :rules="[]">
					<el-input v-model="temp.orgParentId" placeholder="请输入父级ID"/>
				</el-form-item>
				<el-form-item label="组织机构路径(格式:id1.id2.id3)" prop="orgPath">
					<el-input v-model="temp.orgPath" :rules="[]" type="textarea" maxlength="1,000"
                    placeholder="请输入组织机构路径(格式:id1.id2.id3)"/>
				</el-form-item>
				<el-form-item label="层级" prop="orgLevel"
                      :rules="[{type: 'number', message: '必须为数字'}]">
					<el-input v-model.number="temp.orgLevel" placeholder="请输入层级"/>
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
      titleMap: {add: '添加组织机构表', update: '修改组织机构表', view: '查看详情'},
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
        url: '/zorg/zOrganization/list', method: 'get', params
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
    deleteByIds() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.orgId)
          request({
            url: '/zorg/zOrganization/delete', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
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
  }
}
</script>
