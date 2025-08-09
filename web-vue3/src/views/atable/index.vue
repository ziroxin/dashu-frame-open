<template>
  <div class="app-container">
    <!-- 表描述-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.mobile" clearable class="searchInput" placeholder="手机号"/>
        <el-select v-model="searchData.state" clearable class="searchInput" placeholder="状态0禁用1启用">
          <el-option label="状态0" value="0"/>
          <el-option label="状态1" value="1"/>
        </el-select>
        <el-input v-model="searchData.field101" clearable class="searchInput" placeholder="级联选择"/>
        <el-input v-model="searchData.field102" clearable class="searchInput" placeholder="多选框组"/>
        <el-input v-model="searchData.field114" clearable class="searchInput" placeholder="ImageAvatar"/>
        <el-date-picker v-model="searchData.createTimeRange" clearable class="searchInput w-340px!"
                        type="datetimerange" start-placeholder="添加时间开始时间" end-placeholder="添加时间结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
        <el-date-picker v-model="searchData.updateTimeRange" clearable class="searchInput w-340px!"
                        type="datetimerange" start-placeholder="修改时间开始时间" end-placeholder="修改时间结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel w-685px!">
        <base-button type="primary" icon="el-icon-plus" @click="openAdd"
                     v-permission="'a_table-aTable-add'">新增
        </base-button>
        <base-button type="info" icon="el-icon-edit" @click="openUpdate(null)"
                     v-permission="'a_table-aTable-update'">修改
        </base-button>
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)"
                     v-permission="'a_table-aTable-delete'">删除
        </base-button>
        <base-button v-permission="'a_table-aTable-importExcel'" @click="dialogImportVisible=true"
                     type="primary" icon="el-icon-upload2">导入Excel
        </base-button>
        <base-button type="success" icon="el-icon-printer" @click="exportExcel"
                     v-permission="'a_table-aTable-exportExcel'">导出Excel
        </base-button>
        <base-button type="warning" icon="el-icon-time" @click="deleteLogsDialogVisible=true">删除日志</base-button>
      </div>
    </div>
    <!-- 表描述-列表 -->
    <el-table ref="dataTableRef" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange" @sort-change="handleTableSortChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="手机号" prop="mobile" align="center" sortable="custom"/>
      <el-table-column label="状态0禁用1启用" prop="state" align="center" sortable="custom">
        <template v-slot="scope">
          <el-tag type="info" v-if="scope.row.state==='0'">状态0</el-tag>
          <el-tag type="primary" v-if="scope.row.state==='1'">状态1</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="级联选择" prop="field101" align="center" sortable="custom"/>
      <el-table-column label="多选框组" prop="field102" align="center" sortable="custom"/>
      <el-table-column label="顺序" prop="orderIndex" align="center" sortable="custom"/>
      <el-table-column label="ImageAvatar" prop="field114" align="center" sortable="custom"/>
      <el-table-column label="添加时间" prop="createTime" align="center" sortable="custom"/>
      <el-table-column fixed="right" label="操作" width="140" align="center">
        <template v-slot="scope">
          <base-button link size="small" style="color: #13ce66" @click="openView(scope.row)">详情</base-button>
          <base-button v-permission="'a_table-aTable-update'"
                       link size="small" @click="openUpdate(scope.row)">修改
          </base-button>
          <base-button v-permission="'a_table-aTable-delete'" style="color: #ff6d6d"
                       link size="small" @click="deleteByIds(scope.row)">删除
          </base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 表描述-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" v-model="dialogFormVisible" width="800px" :key="'myDialog'+dialogIndex"
               :close-on-click-modal="dialogType==='view'" draggable @close="closeDialog">
      <el-form ref="dataFormRef" :model="formData" label-width="auto" :disabled="dialogType==='view'">
        <el-form-item label="手机号" prop="mobile" :rules="[{required: true, message: '手机号不能为空'}]">
          <el-input v-model="formData.mobile" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="状态0禁用1启用" prop="state" :rules="[]">
          <el-radio-group v-model="formData.state" placeholder="请选择状态0禁用1启用">
            <el-radio value="0">状态0</el-radio>
            <el-radio value="1">状态1</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="级联选择" prop="field101" :rules="[]">
          <el-input v-model="formData.field101" placeholder="请输入级联选择"/>
        </el-form-item>
        <el-form-item label="多选框组" prop="field102" :rules="[]">
          <el-input v-model="formData.field102" type="textarea" maxlength="255" placeholder="请输入多选框组"/>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="formData.orderIndex" :min="0" step-strictly/>
        </el-form-item>
        <el-form-item label="ImageAvatar" prop="field114" :rules="[]">
          <el-input v-model="formData.field114" placeholder="请输入ImageAvatar"/>
        </el-form-item>
      </el-form>
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
          <el-upload v-permission="'a_table-aTable-importExcel'"
                     :action="$baseServer+'a_table/aTable/import/excel'" :headers="getTokenHeader()"
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
    <!-- 删除日志弹窗 -->
    <el-dialog title="删除日志" v-model="deleteLogsDialogVisible" width="95%" top="5vh"
               @closed="dialogIndex++" :key="'deleteLogsDialog'+dialogIndex">
      <delete-logs/>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { getTokenHeader } from '@/utils/auth'
import deleteLogs from './deleteLogs.vue'


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
  request({url: 'a_table/aTable/list', method: 'get', params}).then((response) => {
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
      const data = tableSelectRows.value.map(r => r.id)
      request({url: 'a_table/aTable/delete', method: 'post', data}).then(response => {
        ElNotification({message: '删除成功！', title: '操作成功', type: 'success'})
        loadTableList()
      })
    })
  }
}
// ==================== 2查询_表格_分页_排序end ====================


// ==================== 3新增_修改_详情start ====================
// 弹窗标题
const titleMap = ref({add: '添加表描述', update: '修改表描述', view: '查看详情'})
// 添加/修改模式（add/update）
const dialogType = ref('')
// 弹窗显示隐藏
const dialogFormVisible = ref(false)
// 弹窗索引
const dialogIndex = ref(0)
// 表单临时数据
// 表单
const dataFormRef = ref()
const formData = ref({orderIndex: 0})

// 关闭弹窗（清空表单temp数据）
const closeDialog = () => {
  dialogFormVisible.value = false
  formData.value = {orderIndex: 0}
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
        request({url: 'a_table/aTable/update', method: 'post', data}).then(response => {
          ElMessage({type: 'success', message: '修改成功！', grouping: true})
          loadTableList()
          dialogFormVisible.value = false
        })
      } else {
        request({url: 'a_table/aTable/add', method: 'post', data}).then(response => {
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
  downloadUtil.download('a_table/aTable/export/excel', params, '表描述.xlsx')
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
  downloadUtil.download('a_table/aTable/import/downloadTemplate', {}, '表描述-导入模板.xlsx')
}
// ==================== 4导入_导出end ====================


// ==================== 5删除日志start ====================
// 删除日志弹窗
const deleteLogsDialogVisible = ref(false)
// ==================== 5删除日志end ====================
</script>