<template>
  <div class="app-container">
    <!-- ${table.comment!}-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
<#if searchFields??>
  <#-- 根据前端配置searchFields - 生成查询字段 -->
  <#list table.fields as field>
    <#-- 只生成searchFields中指定的字段 -->
    <#if searchFields?seq_contains(field.annotationColumnName)>
      <#if field.propertyName=='createTime'>
        <el-date-picker v-model="searchData.createTimeRange" clearable class="searchInput w-340px!"
                        type="datetimerange" start-placeholder="添加时间开始时间" end-placeholder="添加时间结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
      <#elseif field.propertyName=='updateTime'>
        <el-date-picker v-model="searchData.updateTimeRange" clearable class="searchInput w-340px!"
                        type="datetimerange" start-placeholder="修改时间开始时间" end-placeholder="修改时间结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
      <#elseif field.propertyType=='LocalDate' || field.propertyType=='Date'>
        <el-date-picker v-model="searchData.${field.propertyName}" type="date" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>
      <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-date-picker v-model="searchData.${field.propertyName}" type="datetime" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
      <#else>
        <#if field.propertyType=='String' && field.metaInfo.length==1>
        <el-select v-model="searchData.${field.propertyName}" clearable class="searchInput" placeholder="${field.comment}">
          <el-option label="状态0" value="0"/>
          <el-option label="状态1" value="1"/>
        </el-select>
        <#else>
        <el-input v-model="searchData.${field.propertyName}" clearable class="searchInput" placeholder="${field.comment}"/>
        </#if>
      </#if>
    </#if>
  </#list>
<#else>
  <#-- 没有配置searchFields的情况下，按照默认配置 - 生成查询字段 -->
  <#list table.fields as field>
    <#-- 忽略字段：主键、排序字段、创建时间、更新时间、创建用户、更新用户 -->
    <#if field.propertyName!=entityKeyName && field.propertyName!='orderIndex'
              && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
              && field.propertyName!='createTime' && field.propertyName!='updateTime'>
      <#if field.propertyType=='LocalDate' || field.propertyType=='Date'>
        <el-date-picker v-model="searchData.${field.propertyName}" type="date" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>
      <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-date-picker v-model="searchData.${field.propertyName}" type="datetime" clearable class="searchInput"
                        placeholder="${field.comment}" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
      <#else>
        <#if field.propertyType=='String' && field.metaInfo.length==1>
        <el-select v-model="searchData.${field.propertyName}" clearable class="searchInput" placeholder="${field.comment}">
          <el-option label="状态0" value="0"/>
          <el-option label="状态1" value="1"/>
        </el-select>
        <#else>
        <el-input v-model="searchData.${field.propertyName}" clearable class="searchInput" placeholder="${field.comment}"/>
        </#if>
      </#if>
    <#elseif field.propertyName=='createTime'>
        <el-date-picker v-model="searchData.createTimeRange" clearable class="searchInput w-340px!"
                        type="datetimerange" start-placeholder="添加时间开始时间" end-placeholder="添加时间结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
    <#elseif field.propertyName=='updateTime'>
        <el-date-picker v-model="searchData.updateTimeRange" clearable class="searchInput w-340px!"
                        type="datetimerange" start-placeholder="修改时间开始时间" end-placeholder="修改时间结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
    </#if>
  </#list>
</#if>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel w-685px!">
        <base-button type="primary" icon="el-icon-plus" @click="openAdd"
                     v-permission="'${buttonNamePre}add'">新增
        </base-button>
        <base-button type="info" icon="el-icon-edit" @click="openUpdate(null)"
                     v-permission="'${buttonNamePre}update'">修改
        </base-button>
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)"
                     v-permission="'${buttonNamePre}delete'">删除
        </base-button>
        <base-button v-permission="'${buttonNamePre}importExcel'" @click="dialogImportVisible=true"
                     type="primary" icon="el-icon-upload2">导入Excel
        </base-button>
        <base-button type="success" icon="el-icon-printer" @click="exportExcel"
                     v-permission="'${buttonNamePre}exportExcel'">导出Excel
        </base-button>
<#if hasDeleteLog?? && hasDeleteLog>
        <base-button type="warning" icon="el-icon-time" @click="deleteLogsDialogVisible=true">删除日志</base-button>
</#if>
      </div>
    </div>
    <!-- ${table.comment!}-列表 -->
    <el-table ref="dataTableRef" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange" @sort-change="handleTableSortChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
<#if listFields??>
  <#-- 根据前端配置listFields - 生成列表字段 -->
  <#list table.fields as field>
    <#-- 只生成listFields中指定的字段 -->
    <#if listFields?seq_contains(field.annotationColumnName)>
      <el-table-column label="${field.comment}" prop="${field.propertyName}" align="center" sortable="custom"/>
    </#if>
  </#list>
<#else>
  <#list table.fields as field>
    <#-- 忽略字段：主键、更新时间、创建用户、更新用户 -->
    <#if field.propertyName!=entityKeyName && field.propertyName!='updateTime'
            && field.propertyName!='createUserId' && field.propertyName!='updateUserId'>
      <#if field.propertyType=='String' && field.metaInfo.length==1>
      <el-table-column label="${field.comment}" prop="${field.propertyName}" align="center" sortable="custom">
        <template v-slot="scope">
          <el-tag type="info" v-if="scope.row.${field.propertyName}==='0'">状态0</el-tag>
          <el-tag type="primary" v-if="scope.row.${field.propertyName}==='1'">状态1</el-tag>
        </template>
      </el-table-column>
      <#else>
      <el-table-column label="${field.comment}" prop="${field.propertyName}" align="center" sortable="custom"/>
      </#if>
    </#if>
  </#list>
</#if>
      <el-table-column fixed="right" label="操作" width="140" align="center">
        <template v-slot="scope">
          <base-button link size="small" style="color: #13ce66" @click="openView(scope.row)">详情</base-button>
          <base-button v-permission="'${buttonNamePre}update'"
                       link size="small" @click="openUpdate(scope.row)">修改
          </base-button>
          <base-button v-permission="'${buttonNamePre}delete'" style="color: #ff6d6d"
                       link size="small" @click="deleteByIds(scope.row)">删除
          </base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- ${table.comment!}-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" v-model="dialogFormVisible" width="800px" :key="'myDialog'+dialogIndex"
               :close-on-click-modal="dialogType==='view'" draggable @close="closeDialog">
<#if templateHtml??>
<#-- 在线表单，直接用表单生成的代码 -->
      ${templateHtml}
<#else>
<#-- 非在线表单，使用以下代码生成 -->
      <el-form ref="dataFormRef" :model="formData" label-width="auto" :disabled="dialogType==='view'">
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
        <#--字符串，长度>=255时，使用textarea-->
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="formData.${field.propertyName}" type="textarea" maxlength="${field.metaInfo.length}" placeholder="请输入${field.comment}"/>
        </el-form-item>
        <#elseif field.metaInfo.length==1>
        <#--长度=1时，使用单选框，一般代表状态-->
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-radio-group v-model="formData.${field.propertyName}" placeholder="请选择${field.comment}">
            <el-radio value="0">状态0</el-radio>
            <el-radio value="1">状态1</el-radio>
          </el-radio-group>
        </el-form-item>
        <#else>
        <#--其他情况，使用输入框-->
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
                          value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>
        </el-form-item>
      <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-date-picker v-model="formData.${field.propertyName}" type="datetime" clearable placeholder="请选择${field.comment}"
                          value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
      <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}" :rules="[${rules1}]">
          <el-input v-model="formData.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
      </#if>
    </#if>
  </#list>
      </el-form>
</#if>
      <template #footer>
        <div class="dialog-footer">
          <base-button type="primary" icon="el-icon-check" v-if="dialogType!=='view'" @click="saveData">保存
          </base-button>
          <base-button icon="el-icon-close" @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
    <!-- 批量导入弹窗 -->
    <el-dialog title="批量导入" v-model="dialogImportVisible" draggable width="600px" :key="'importDialog'+dialogIndex"
               :close-on-click-modal="false" @closed="dialogIndex++">
      <el-form ref="importForm" label-width="auto" v-loading="isImportLoading">
        <el-form-item label="下载模板：">
          <base-button type="success" plain icon="el-icon-download" @click="downloadExcelTemplate">下载Excel模板
          </base-button>
        </el-form-item>
        <el-divider/>
        <el-form-item label="导入：">
          <el-upload v-permission="'${buttonNamePre}importExcel'"
                     :action="$baseServer+'${controllerMapping}/import/excel'" :headers="getTokenHeader()"
                     :before-upload="beforeImportUpload" :on-error="importExcelError"
                     :on-success="importExcelSuccess" accept=".xls,.xlsx"
                     :show-file-list="false" :auto-upload="true">
            <base-button type="primary" plain icon="el-icon-upload2">点击上传Excel并导入</base-button>
          </el-upload>
          <el-tag type="info" size="small" class="mt-5px">
            说明：点击上方按钮上传Excel文件，上传成功后会自动开始导入！
          </el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button @click="dialogImportVisible=false">关闭</base-button>
        </div>
      </template>
    </el-dialog>
<#if hasDeleteLog?? && hasDeleteLog>
    <!-- 删除日志弹窗 -->
    <el-dialog title="删除日志" v-model="deleteLogsDialogVisible" width="95%" top="5vh"
               @closed="dialogIndex++" :key="'deleteLogsDialog'+dialogIndex">
      <delete-logs/>
    </el-dialog>
</#if>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { getTokenHeader } from '@/utils/auth'
<#if templateHtml?? && templateHtml?contains("my-wang-editor")>
import { MyWangEditor } from '@/components/MyWangEditor'
</#if>
<#if templateHtml?? && templateHtml?contains("image-avatar")>
import ImageAvatar from '@/components/Upload/ImageAvatar.vue'
</#if>
<#if templateHtml?? && templateHtml?contains("image-one")>
import ImageOne from '@/components/Upload/ImageOne.vue'
</#if>
<#if templateHtml?? && templateHtml?contains("image-upload")>
import ImageUpload from '@/components/Upload/ImageUpload.vue'
</#if>
<#if templateHtml?? && templateHtml?contains("file-upload")>
import FileUpload from '@/components/Upload/FileUpload.vue'
</#if>
<#if hasDeleteLog?? && hasDeleteLog>
import deleteLogs from './deleteLogs.vue'
</#if>


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
  const obj = {...searchData.value}
  if (obj.createTimeRange && obj.createTimeRange.length > 0) {
    obj.createTimeStart = obj.createTimeRange[0]
    obj.createTimeEnd = obj.createTimeRange[1]
    delete obj.createTimeRange
  }
  if (obj.updateTimeRange && obj.updateTimeRange.length > 0) {
    obj.updateTimeStart = obj.updateTimeRange[0]
    obj.updateTimeEnd = obj.updateTimeRange[1]
    delete obj.updateTimeRange
  }
  const params = {...pager.value, params: JSON.stringify(obj), sorts: JSON.stringify(sortData.value)}
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


// ==================== 3新增_修改_详情start ====================
// 弹窗标题
const titleMap = ref({add: '添加${table.comment!}', update: '修改${table.comment!}', view: '查看详情'})
// 添加/修改模式（add/update）
const dialogType = ref('')
// 弹窗显示隐藏
const dialogFormVisible = ref(false)
// 弹窗索引
const dialogIndex = ref(0)
// 表单临时数据
<#assign formDataObject = ''>
<#list table.fields as field>
  <#if field.propertyName=='orderIndex'>
    <#assign formDataObject = '{orderIndex: 0}'>
  </#if>
</#list>
// 表单
const dataFormRef = ref()
const formData = ref(<#if formDataObject??>${formDataObject}<#else>{}</#if>)

// 关闭弹窗（清空表单temp数据）
const closeDialog = () => {
  dialogFormVisible.value = false
  formData.value = <#if formDataObject??>${formDataObject}<#else>{}</#if>
  dialogIndex.value++
}
// 打开添加窗口
const openAdd = () => {
  dialogFormVisible.value = true
  dialogType.value = 'add'
  nextTick(() => { dataFormRef.value.clearValidate() })
}
// 打开修改窗口
const openUpdate = (row) => {
  if (row) {
    dataTableRef.value.clearSelection()
    dataTableRef.value.toggleRowSelection(row, true)
  }
  if (tableSelectRows.value.length <= 0) {
    ElMessage({message: '请选择一条数据修改！', type: 'warning', grouping: true})
  } else if (tableSelectRows.value.length > 1) {
    ElMessage({message: '修改时，只允许选择一条数据！', type: 'warning', grouping: true})
  } else {
    // 修改弹窗
    formData.value = Object.assign({}, tableSelectRows.value[0])
    dialogType.value = 'update'
    dialogFormVisible.value = true
    nextTick(() => { dataFormRef.value.clearValidate() })
  }
}
// 打开查看窗口
const openView = (row) => {
  dialogFormVisible.value = true
  formData.value = Object.assign({}, row)
  dialogType.value = 'view'
  nextTick(() => { dataFormRef.value.clearValidate() })
}
// 添加、修改，保存事件
const saveData = () => {
  dataFormRef.value.validate((valid) => {
    if (valid) {
      const data = {...formData.value}
      if (dialogType.value === 'update') {
        request({url: '${controllerMapping}/update', method: 'post', data}).then(response => {
          ElMessage({type: 'success', message: '修改成功！', grouping: true})
          loadTableList()
          dialogFormVisible.value = false
        })
      } else {
        request({url: '${controllerMapping}/add', method: 'post', data}).then(response => {
          ElMessage({type: 'success', message: '添加成功！', grouping: true})
          loadTableList()
          dialogFormVisible.value = false
        })
      }
    }
  })
}
// ==================== 3新增_修改_详情end ====================


// ==================== 4导入_导出start ====================
// 导入弹窗
const dialogImportVisible = ref(false)
const isImportLoading = ref(false)

// 导出Excel文件
const exportExcel = () => {
  const params = {params: JSON.stringify(searchData.value), sorts: JSON.stringify(sortData.value)}
  downloadUtil.download('${controllerMapping}/export/excel', params, '${table.comment!}.xlsx')
}
// 导入Excel之前，显示loading
const beforeImportUpload = (file) => {
  isImportLoading.value = true
}
// 导入Excel成功
const importExcelSuccess = (response) => {
  isImportLoading.value = false
  if (response.message === 'Success') {
    ElMessage({type: 'success', message: '导入成功！', grouping: true})
    dialogImportVisible.value = false
    loadTableList()
  } else {
    ElMessageBox.alert(response.message, '提示',
        {confirmButtonText: '确定', dangerouslyUseHTMLString: true, customClass: 'min-w-800px!'})
  }
}
// 导入Excel失败，取消loading状态
const importExcelError = () => {
  isImportLoading.value = false
}
// 下载模板
const downloadExcelTemplate = () => {
  downloadUtil.download('${controllerMapping}/import/downloadTemplate', {}, '${table.comment!}-导入模板.xlsx')
}
// ==================== 4导入_导出end ====================


// ==================== 5删除日志start ====================
<#if hasDeleteLog?? && hasDeleteLog>
// 删除日志弹窗
const deleteLogsDialogVisible = ref(false)
</#if>
// ==================== 5删除日志end ====================
</script>