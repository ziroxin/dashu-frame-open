<template>
	<div class="app-container">
		<!-- ${table.comment!}-管理按钮 -->
		<div style="margin-bottom: 20px;">
<#list table.fields as field>
	<#if field.propertyType=='LocalDate' || field.propertyType=='Date'>
			<el-date-picker v-model="searchData.${field.propertyName}" size="small" style="width: 150px;margin-right: 10px;"
							type="date" class="filter-item" placeholder="请选择${field.comment}查询"/>
	<#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
			<el-date-picker v-model="searchData.${field.propertyName}" size="small" style="width: 150px;margin-right: 10px;"
							type="datetime" class="filter-item" placeholder="请选择${field.comment}查询"/>
	<#else>
			<el-input v-model="searchData.${field.propertyName}" size="small" style="width: 150px;margin-right: 10px;"
					  		class="filter-item" placeholder="请输入${field.comment}查询"/>
	</#if>
</#list>
			<el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="loadTableList">查询</el-button>
			<el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
			<div style="float: right;">
				<el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'${buttonNamePre}add'">新增
				</el-button>
				<el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate(null)" size="small"
                   v-permission="'${buttonNamePre}update'">修改
				</el-button>
				<el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'${buttonNamePre}delete'">删除
				</el-button>
				<el-upload v-permission="'${buttonNamePre}importExcel'" style="display: inline-block;margin: 0px 10px;"
						   :action="this.$baseServer+'${controllerMapping}/import/excel'" :headers="this.$headerToken"
						   :on-success="importExcelSuccess" accept=".xls,.xlsx"
						   :show-file-list="false" :auto-upload="true">
					<el-button v-waves type="warning" icon="el-icon-upload2" size="small">导入Excel</el-button>
				</el-upload>
				<el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel" size="small"
						   v-permission="'${buttonNamePre}exportExcel'">导出Excel
				</el-button>
			</div>
		</div>
		<!-- ${table.comment!}-列表 -->
		<el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
			<el-table-column type="selection" width="50" align="center" header-align="center"/>
<#list table.fields as field>
			<el-table-column label="${field.comment}" prop="${field.propertyName}" align="center"/>
</#list>
			<el-table-column fixed="right" label="操作" width="120">
				<template v-slot="scope">
					<el-button type="text" style="color: #13ce66;"
							   size="small" @click="openView(scope.row)">详情</el-button>
					<el-button v-permission="'${buttonNamePre}update'"
							   type="text" size="small" @click="openUpdate(scope.row)">修改
					</el-button>
					<el-button v-permission="'${buttonNamePre}delete'" style="color: #ff6d6d;"
							   type="text" size="small" @click="deleteByIds(scope.row)">删除
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<!-- ${table.comment!}-分页 -->
		<el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
		/>
		<!-- 添加修改弹窗 -->
		<el-dialog :title="titleMap[dialogType]" :close-on-click-modal="false"
				   :visible.sync="dialogFormVisible" @close="resetTemp" width="600px">
<#if templateHtml??>
			${templateHtml}
<#else>
			<el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
  <#list table.fields as field>
	<#if field.propertyName=='orderIndex'>
				<el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
					<el-input-number v-model="temp.orderIndex" :min="0"/>
				</el-form-item>
	<#elseif field.propertyName!='createTime' && field.propertyName!='updateTime'>
		<#--判断是否为null的规则-->
		<#assign rules1=field.metaInfo.nullable?string("","{required: true, message: '" + field.comment + "不能为空'}")>
		<#if field.propertyType=='String'>
			<#if field.metaInfo.length gte 255>
				<el-form-item label="${field.comment}" prop="${field.propertyName}">
					<el-input v-model="temp.${field.propertyName}" :rules="[${rules1}]" type="textarea" maxlength="${field.metaInfo.length}"
                    placeholder="请输入${field.comment}"/>
				</el-form-item>
			<#else>
				<el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules1}]">
					<el-input v-model="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
				</el-form-item>
			</#if>
		<#elseif field.propertyType=='Integer' || field.propertyType=='Long'
			|| field.propertyType=='BigDecimal'|| field.propertyType=='Double'>
			<#--数字的可能有2种规则，所以单独判断-->
			<#assign rules2=field.metaInfo.nullable?string("{type: 'number', message: '必须为数字'}","{required: true, message: '" + field.comment + "不能为空'},{type: 'number', message: '必须为数字'}")>
				<el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules2}]">
					<el-input v-model.number="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
				</el-form-item>
		<#elseif field.propertyType=='LocalDate' || field.propertyType=='Date'>
				<el-form-item label="${field.comment}" prop="${field.propertyName}"
					  :rules="[${rules1}]">
					<el-date-picker v-model="temp.${field.propertyName}" type="date" placeholder="请选择${field.comment}"/>
				</el-form-item>
        <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
				<el-form-item label="${field.comment}" prop="${field.propertyName}"
					  :rules="[${rules1}]">
					<el-date-picker v-model="temp.${field.propertyName}" type="datetime" placeholder="请选择${field.comment}"/>
				</el-form-item>
		<#else>
				<el-form-item label="${field.comment}" prop="${field.propertyName}"
					  :rules="[${rules1}]">
					<el-input v-model="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
				</el-form-item>
        </#if>
	</#if>
  </#list>
			</el-form>
</#if>
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
      titleMap: {add: '添加${table.comment!}', update: '修改${table.comment!}', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
<#if jsData??>
      ${jsData}
</#if>
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
<#if jsCreated??>
    ${jsCreated}
</#if>
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
        url: '${controllerMapping}/list', method: 'get', params
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
	// 分页条数改变
	handleSizeChange(size) {
      this.pager.limit = size
      this.loadTableList()
	},
    // 清空表单temp数据
    resetTemp() {
      this.temp = {orderIndex: 0}
<#if childTableList??>
	<#list childTableList as child>
      this.load${child}FileList()
	</#list>
</#if>
<#if jsMethods??>
      this.loadChkStr2Arr()
</#if>
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
        this.tableSelectRows = [row]
      }
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据修改！', type: 'warning'})
      } else if (this.tableSelectRows.length > 1) {
        this.$message({message: '修改时，只允许选择一条数据！', type: 'warning'})
      } else {
        // 修改弹窗
        this.temp = Object.assign({}, this.tableSelectRows[0])
<#if childTableList??>
	<#list childTableList as child>
        this.load${child}FileList()
	</#list>
</#if>
<#if jsMethods??>
        this.loadChkStr2Arr()
</#if>
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
<#if jsMethods??>
          this.loadChkArr2Str()
</#if>
          var data = {...this.temp}
          if (this.dialogType === 'update') {
            request({
              url: '${controllerMapping}/update', method: 'put', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '${controllerMapping}/add', method: 'post', data
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
        this.tableSelectRows = [row]
      }
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.${entityKeyName})
          request({
            url: '${controllerMapping}/delete', method: 'delete', data
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
        url: '${controllerMapping}/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '${table.comment!}.xlsx');
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
<#if jsMethods??>
    ${jsMethods}
</#if>
  }
}
</script>
<#if templateCss??>
<style>
    ${templateCss}
</style>
</#if>