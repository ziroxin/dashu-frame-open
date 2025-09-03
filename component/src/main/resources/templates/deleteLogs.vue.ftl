<template>
  <div>
    <!-- ${table.comment!}-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
<#list table.fields as field>
  <#if field.propertyName!=entityKeyName && field.propertyName!='orderIndex'
  && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
  && field.propertyName!='createTime' && field.propertyName!='updateTime'>
      <#if field.propertyType=='LocalDate' || field.propertyType=='Date'>
        <el-date-picker v-model="searchData.${field.propertyName}" size="small" type="date" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="yyyy-MM-dd" format="yyyy-MM-dd"/>
      <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-date-picker v-model="searchData.${field.propertyName}" size="small" type="datetime" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm:ss"/>
      <#else>
        <el-input v-model="searchData.${field.propertyName}" size="small" clearable class="searchInput" placeholder="${field.comment}"/>
      </#if>
  </#if>
</#list>
        <el-button class="searchBtn" type="primary" size="small" icon="el-icon-search"
                   @click="searchBtnHandle">查询
        </el-button>
        <el-button class="searchBtn" type="info" size="small" icon="el-icon-refresh"
                   @click="resetTableList">重置
        </el-button>
      </div>
      <div class="operatePanel" style="width: 685px;">
        <el-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small">删除</el-button>
        <el-button type="success" icon="el-icon-printer" @click="exportExcel" size="small">导出Excel</el-button>
      </div>
    </div>
    <!-- ${table.comment!}-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange" @sort-change="handleTableSortChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName && field.propertyName!='updateTime'
            && field.propertyName!='createUserId' && field.propertyName!='updateUserId'>
      <el-table-column label="${field.comment}" prop="${field.propertyName}" align="center" sortable="custom"/>
    </#if>
</#list>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;" size="small" @click="openView(scope.row)">详情</el-button>
          <el-button style="color: #ff6d6d;" type="text" size="small" @click="deleteByIds(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- ${table.comment!}-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 删除日志详情弹窗 -->
    <el-dialog title="删除日志详情" append-to-body :visible.sync="dialogFormVisible" width="600px"
               @close="closeDialog" :key="'myLogsDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="true">
<#list table.fields as field>
  <#if field.propertyName=='orderIndex'>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0" step-strictly/>
        </el-form-item>
  <#elseif field.propertyName!='createTime' && field.propertyName!='updateTime'
              && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
              && field.propertyName!=entityKeyName>
    <#--判断是否为null的规则-->
    <#assign rules1=field.metaInfo.nullable?string("","{required: true, message: '" + field.comment + "不能为空'}")>
    <#if field.propertyType=='String'>
      <#if field.metaInfo.length gte 255>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="temp.${field.propertyName}" type="textarea" maxlength="${field.metaInfo.length}" placeholder="请输入${field.comment}"/>
        </el-form-item>
      <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
      </#if>
    <#elseif field.propertyType=='Integer' || field.propertyType=='Long'
                || field.propertyType=='BigDecimal'|| field.propertyType=='Double'>
      <#--数字的可能有2种规则，所以单独判断-->
      <#assign rules2=field.metaInfo.nullable?string("{type: 'number', message: '必须为数字'}","{required: true, message: '" + field.comment + "不能为空'},{type: 'number', message: '必须为数字'}")>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules2}]">
          <el-input-number v-model.number="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
    <#elseif field.propertyType=='LocalDate' || field.propertyType=='Date'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-date-picker v-model="temp.${field.propertyName}" type="date" clearable placeholder="请选择${field.comment}"
                          value-format="yyyy-MM-dd" format="yyyy-MM-dd"/>
        </el-form-item>
    <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-date-picker v-model="temp.${field.propertyName}" type="datetime" clearable placeholder="请选择${field.comment}"
                          value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
    <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
    </#if>
  </#if>
</#list>
<#if attachmentField3??>
        <el-form-item label="附件" prop="${attachmentField3}">
          <file-upload v-model="formData.${attachmentField3}" :limit-size="1024*1024*10"/>
        </el-form-item>
</#if>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util';
<#-- 初始化 componentsArr 数组 -->
<#assign componentsArr = []>
<#if attachmentField3??>
  <#assign componentsArr = componentsArr + ["FileUpload"]>
import FileUpload from '@/components/Upload/FileUpload';
</#if>

export default {
  <#if componentsArr??>
  components: {<#list componentsArr as component>${component}<#if component_has_next>, </#if></#list>},
  </#if>
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
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {orderIndex: 0},
      isLoading: false,
      dialogIndex: 0,
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
      this.sortData = this.$options.data().sortData
      this.$refs.dataTable.clearSort()
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
      request({url: '${controllerMapping}/list', method: 'get', params}).then((response) => {
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
        this.sortData = this.$options.data().sortData
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
    // 打开查看窗口
    openView(row) {
      this.temp = Object.assign({}, row)
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
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
          const data = this.tableSelectRows.map(r => r.${entityKeyName})
          request({url: '${controllerMapping}/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData), sorts: JSON.stringify(this.sortData)}
      downloadUtil.download('${controllerMapping}/export/excel', params, '${table.comment!}.xlsx')
    },
  }
}
</script>
