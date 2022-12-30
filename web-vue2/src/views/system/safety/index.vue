<template>
	<div class="app-container">
		<!-- 密码安全等设置-管理按钮 -->
		<div style="margin-bottom: 20px;">
			<el-input v-model="searchData.id" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入配置ID查询"/>
			<el-input v-model="searchData.startLength" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入开始长度查询"/>
			<el-input v-model="searchData.endLength" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入结束长度查询"/>
			<el-input v-model="searchData.lowercase" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入小写字母 0必须无 1必须有 2可有可无查询"/>
			<el-input v-model="searchData.uppercase" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入大写字母 0必须无 1必须有 2可有可无查询"/>
			<el-input v-model="searchData.numbers" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入数字 0必须无 1必须有 2可有可无查询"/>
			<el-input v-model="searchData.specialCharacters" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入是否有特殊字符 0必须无 1必须有 2可有可无查询"/>
			<el-input v-model="searchData.banUsername" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入不能包含用户名 0否 1是查询"/>
			<el-input v-model="searchData.validTime" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入有效时间 天查询"/>
			<el-input v-model="searchData.prompt" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入提示语查询"/>
			<el-input v-model="searchData.loginFailedTimes" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入登录失败限制次数查询"/>
			<el-input v-model="searchData.lockTime" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入锁定时间 分钟查询"/>
			<el-input v-model="searchData.defaultPassword" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入默认密码查询"/>
			<el-date-picker v-model="searchData.updateTime" style="width: 150px;margin-right: 10px;"
							type="datetime" class="filter-item" placeholder="请选择修改时间查询"/>
			<el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="loadTableList">查询</el-button>
			<el-button v-waves class="filter-item" type="info" icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
			<div style="float: right;">
				<el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd"
                   v-permission="'zsafety-zSafety-add'">新增
				</el-button>
				<el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate"
                   v-permission="'zsafety-zSafety-update'">修改
				</el-button>
				<el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds"
                   v-permission="'zsafety-zSafety-delete'">删除
				</el-button>
				<el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel"
						   v-permission="'zsafety-zSafety-exportExcel'">导出Excel
				</el-button>
			</div>
		</div>
		<!-- 密码安全等设置-列表 -->
		<el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
			<el-table-column type="selection" width="50" align="center" header-align="center"/>
			<el-table-column label="配置ID" prop="id" align="center"/>
			<el-table-column label="开始长度" prop="startLength" align="center"/>
			<el-table-column label="结束长度" prop="endLength" align="center"/>
			<el-table-column label="小写字母 0必须无 1必须有 2可有可无" prop="lowercase" align="center"/>
			<el-table-column label="大写字母 0必须无 1必须有 2可有可无" prop="uppercase" align="center"/>
			<el-table-column label="数字 0必须无 1必须有 2可有可无" prop="numbers" align="center"/>
			<el-table-column label="是否有特殊字符 0必须无 1必须有 2可有可无" prop="specialCharacters" align="center"/>
			<el-table-column label="不能包含用户名 0否 1是" prop="banUsername" align="center"/>
			<el-table-column label="有效时间 天" prop="validTime" align="center"/>
			<el-table-column label="提示语" prop="prompt" align="center"/>
			<el-table-column label="登录失败限制次数" prop="loginFailedTimes" align="center"/>
			<el-table-column label="锁定时间 分钟" prop="lockTime" align="center"/>
			<el-table-column label="默认密码" prop="defaultPassword" align="center"/>
			<el-table-column label="修改时间" prop="updateTime" align="center"/>
			<el-table-column fixed="right" label="操作" width="100">
				<template v-slot="scope">
					<el-button type="text" size="small" @click="openView(scope.row)">查看详情</el-button>
				</template>
			</el-table-column>
		</el-table>
		<!-- 密码安全等设置-分页 -->
		<el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
		/>
		<!-- 添加修改弹窗 -->
		<el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible">
			<el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
               style="width: 500px; margin-left: 50px;" :disabled="dialogType=='view'">
				<el-form-item label="配置ID" prop="id"
                      :rules="[{required: true, message: '配置ID不能为空'},{type: 'number', message: '必须为数字'}]">
					<el-input v-model.number="temp.id" placeholder="请输入配置ID"/>
				</el-form-item>
				<el-form-item label="开始长度" prop="startLength"
					  :rules="[{required: true, message: '开始长度不能为空'}]">
					<el-input v-model="temp.startLength" placeholder="请输入开始长度"/>
				</el-form-item>
				<el-form-item label="结束长度" prop="endLength"
					  :rules="[{required: true, message: '结束长度不能为空'}]">
					<el-input v-model="temp.endLength" placeholder="请输入结束长度"/>
				</el-form-item>
				<el-form-item label="小写字母 0必须无 1必须有 2可有可无" prop="lowercase"
					  :rules="[{required: true, message: '小写字母 0必须无 1必须有 2可有可无不能为空'}]">
					<el-input v-model="temp.lowercase" placeholder="请输入小写字母 0必须无 1必须有 2可有可无"/>
				</el-form-item>
				<el-form-item label="大写字母 0必须无 1必须有 2可有可无" prop="uppercase"
					  :rules="[{required: true, message: '大写字母 0必须无 1必须有 2可有可无不能为空'}]">
					<el-input v-model="temp.uppercase" placeholder="请输入大写字母 0必须无 1必须有 2可有可无"/>
				</el-form-item>
				<el-form-item label="数字 0必须无 1必须有 2可有可无" prop="numbers"
					  :rules="[{required: true, message: '数字 0必须无 1必须有 2可有可无不能为空'}]">
					<el-input v-model="temp.numbers" placeholder="请输入数字 0必须无 1必须有 2可有可无"/>
				</el-form-item>
				<el-form-item label="是否有特殊字符 0必须无 1必须有 2可有可无" prop="specialCharacters"
					  :rules="[{required: true, message: '是否有特殊字符 0必须无 1必须有 2可有可无不能为空'}]">
					<el-input v-model="temp.specialCharacters" placeholder="请输入是否有特殊字符 0必须无 1必须有 2可有可无"/>
				</el-form-item>
				<el-form-item label="不能包含用户名 0否 1是" prop="banUsername"
					  :rules="[{required: true, message: '不能包含用户名 0否 1是不能为空'}]">
					<el-input v-model="temp.banUsername" placeholder="请输入不能包含用户名 0否 1是"/>
				</el-form-item>
				<el-form-item label="有效时间 天" prop="validTime"
					  :rules="[{required: true, message: '有效时间 天不能为空'}]">
					<el-input v-model="temp.validTime" placeholder="请输入有效时间 天"/>
				</el-form-item>
				<el-form-item label="提示语" prop="prompt">
					<el-input v-model="temp.prompt" :rules="[{required: true, message: '提示语不能为空'}]" type="textarea" maxlength="255"
                    placeholder="请输入提示语"/>
				</el-form-item>
				<el-form-item label="登录失败限制次数" prop="loginFailedTimes"
					  :rules="[{required: true, message: '登录失败限制次数不能为空'}]">
					<el-input v-model="temp.loginFailedTimes" placeholder="请输入登录失败限制次数"/>
				</el-form-item>
				<el-form-item label="锁定时间 分钟" prop="lockTime"
					  :rules="[{required: true, message: '锁定时间 分钟不能为空'}]">
					<el-input v-model="temp.lockTime" placeholder="请输入锁定时间 分钟"/>
				</el-form-item>
				<el-form-item label="默认密码" prop="defaultPassword">
					<el-input v-model="temp.defaultPassword" :rules="[{required: true, message: '默认密码不能为空'}]" type="textarea" maxlength="255"
                    placeholder="请输入默认密码"/>
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
      titleMap: {add: '添加密码安全等设置', update: '修改密码安全等设置', view: '查看详情'},
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
        url: '/zsafety/zSafety/list', method: 'get', params
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
              url: '/zsafety/zSafety/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/zsafety/zSafety/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.id)
          request({
            url: '/zsafety/zSafety/delete', method: 'post', data
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
        url: '/zsafety/zSafety/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '密码安全等设置.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    },
  }
}
</script>
