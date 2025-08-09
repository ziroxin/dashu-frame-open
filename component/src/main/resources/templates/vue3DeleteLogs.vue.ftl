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
        <el-date-picker v-model="searchData.${field.propertyName}" type="date" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="yyyy-MM-dd" format="yyyy-MM-dd"/>
      <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-date-picker v-model="searchData.${field.propertyName}" type="datetime" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm:ss"/>
      <#else>
        <el-input v-model="searchData.${field.propertyName}" clearable class="searchInput" placeholder="${field.comment}"/>
      </#if>
  </#if>
</#list>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="el-icon-refresh" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel w-685px!">
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)">删除</base-button>
        <base-button type="success" icon="el-icon-printer" @click="exportExcel">导出Excel</base-button>
      </div>
    </div>
    <!-- ${table.comment!}-列表 -->
    <el-table ref="dataTableRef" :data="tableData" stripe border v-loading="isLoading"
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
          <base-button link style="color: #13ce66" size="small" @click="openView(scope.row)">详情</base-button>
          <base-button link style="color: #ff6d6d" size="small" @click="deleteByIds(scope.row)">删除</base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- ${table.comment!}-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 删除日志详情弹窗 -->
    <el-dialog title="删除日志详情" append-to-body v-model="dialogFormVisible" width="800px" draggable
               @close="closeDialog" :key="'myLogsDialog'+dialogIndex">
      <el-form ref="dataFormRef" :model="formData" label-position="right" label-width="auto" disabled>
<#list table.fields as field>
  <#if field.propertyName=='orderIndex'>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="formData.orderIndex" :min="0" step-strictly/>
        </el-form-item>
  <#elseif field.propertyName!='createTime' && field.propertyName!='updateTime'
              && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
              && field.propertyName!=entityKeyName>
    <#--判断是否为null的规则-->
    <#assign rules1=field.metaInfo.nullable?string("","{required: true, message: '" + field.comment + "不能为空'}")>
    <#if field.propertyType=='String'>
      <#if field.metaInfo.length gte 255>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="formData.${field.propertyName}" type="textarea" maxlength="${field.metaInfo.length}" placeholder="请输入${field.comment}"/>
        </el-form-item>
      <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="formData.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
      </#if>
    <#elseif field.propertyType=='Integer' || field.propertyType=='Long'
                || field.propertyType=='BigDecimal'|| field.propertyType=='Double'>
      <#--数字的可能有2种规则，所以单独判断-->
      <#assign rules2=field.metaInfo.nullable?string("{type: 'number', message: '必须为数字'}","{required: true, message: '" + field.comment + "不能为空'},{type: 'number', message: '必须为数字'}")>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules2}]">
          <el-input-number v-model.number="formData.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
    <#elseif field.propertyType=='LocalDate' || field.propertyType=='Date'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-date-picker v-model="formData.${field.propertyName}" type="date" clearable placeholder="请选择${field.comment}"
                          value-format="yyyy-MM-dd" format="yyyy-MM-dd"/>
        </el-form-item>
    <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-date-picker v-model="formData.${field.propertyName}" type="datetime" clearable placeholder="请选择${field.comment}"
                          value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
    <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="formData.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
    </#if>
  </#if>
</#list>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button icon="el-icon-close" @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'


// ==================== 1生命周期start ====================
onMounted(() => {
  loadTableList()
})
// ==================== 1生命周期end ====================


// ==================== 2查询_表格_分页_排序start ====================
// 分页数据
const pager = ref({page: 1, limit: 10, totalCount: 0})
// 表格
const dataTableRef = ref()
const tableData = ref([])
// 查询表单数据
const searchData = ref({})
// 排序数据
const sortData = ref({})
// 选中行
const tableSelectRows = ref([])
// 是否加载中
const isLoading = ref(false)

// 查询按钮
const searchBtnHandle = () => {
  pager.value.page = 1
  loadTableList()
}
// 重置
const resetTableList = () => {
  pager.value.page = 1
  searchData.value = {}
  sortData.value = {}
  dataTableRef.value.clearSort()
  loadTableList()
}
// 加载表格
const loadTableList = () => {
  isLoading.value = true
  const params = {...pager.value, params: JSON.stringify(searchData.value), sorts: JSON.stringify(sortData.value)}
  request({url: '${controllerMapping}/list', method: 'get', params}).then((response) => {
    const {data} = response
    pager.value.totalCount = data.total
    tableData.value = data.records
    isLoading.value = false
  })
}
// 监听选中行
const handleTableSelectChange = (rows) => {
  tableSelectRows.value = rows
}
// 监听排序
const handleTableSortChange = (sort) => {
  if (sort.order) {
    sortData.value = {column: sort.prop, order: sort.order === 'descending' ? 'DESC' : 'ASC'}
  } else {
    sortData.value = {}
  }
  loadTableList()
}
// 监听分页
const handleCurrentChange = (page) => {
  pager.value.page = page
  loadTableList()
}
// 分页条数改变
const handleSizeChange = (size) => {
  pager.value.limit = size
  loadTableList()
}
// 删除
const deleteByIds = (row) => {
  if (row) {
    dataTableRef.value.clearSelection()
    dataTableRef.value.toggleRowSelection(row, true)
  }
  if (tableSelectRows.value.length <= 0) {
    ElMessage({message: '请选择一条数据删除！', type: 'warning', grouping: true})
  } else {
    ElMessageBox.confirm('确定要删除吗?', '删除提醒', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    }).then(() => {
      // 执行删除
      const data = tableSelectRows.value.map(r => r.${entityKeyName})
      request({url: '${controllerMapping}/delete', method: 'post', data}).then(response => {
        ElNotification({message: '删除成功！', title: '操作成功', type: 'success'})
        loadTableList()
      })
    })
  }
}
// ==================== 2查询_表格_分页_排序end ====================


// ==================== 3删除日志详情start ====================
// 弹窗显示隐藏
const dialogFormVisible = ref(false)
// 弹窗索引
const dialogIndex = ref(0)
// 表单临时数据
<#assign tempObject = {}>
<#list table.fields as field>
  <#if field.propertyName=='orderIndex'>
    <#assign tempObject = {orderIndex: 0}>
  </#if>
</#list>
// 表单
const dataFormRef = ref()
const formData = ref(<#if tempObject??>${tempObject?json}<#else>{}</#if>)

// 关闭弹窗（清空表单temp数据）
const closeDialog = () => {
  formData.value = <#if tempObject??>${tempObject?json}<#else>{}</#if>
  dialogIndex.value++
}
// 打开查看窗口
const openView = (row) => {
  formData.value = Object.assign({}, row)
  dialogFormVisible.value = true
  dataFormRef.value.clearValidate()
}
// ==================== 3删除日志详情end ====================


// ==================== 4导出start ====================
// 导出Excel文件
const exportExcel = () => {
  const params = {params: JSON.stringify(searchData.value), sorts: JSON.stringify(sortData.value)}
  downloadUtil.download('${controllerMapping}/export/excel', params, '${table.comment!}.xlsx')
}
// ==================== 4导出end ====================
</script>
