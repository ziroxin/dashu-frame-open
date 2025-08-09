<template>
  <div>
    <!-- 表描述-删除日志表-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.id" clearable class="searchInput" placeholder="主键"/>
        <el-input v-model="searchData.mobile" clearable class="searchInput" placeholder="手机号"/>
        <el-select v-model="searchData.state" clearable class="searchInput" placeholder="状态0禁用1启用">
          <el-option value="0">禁用</el-option>
          <el-option value="1">启用</el-option>
        </el-select>
        <el-input v-model="searchData.field101" clearable class="searchInput" placeholder="级联选择"/>
        <el-input v-model="searchData.field102" clearable class="searchInput" placeholder="多选框组"/>
        <el-input v-model="searchData.field114" clearable class="searchInput" placeholder="ImageAvatar"/>
        <el-date-picker v-model="searchData.deleteTime" type="datetime" clearable class="searchInput"
                        placeholder="删除时间" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="el-icon-refresh" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel w-685px!">
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)">删除</base-button>
        <base-button type="success" icon="el-icon-printer" @click="exportExcel">导出Excel</base-button>
      </div>
    </div>
    <!-- 表描述-删除日志表-列表 -->
    <el-table ref="dataTableRef" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange" @sort-change="handleTableSortChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="主键" prop="id" align="center" sortable="custom"/>
      <el-table-column label="手机号" prop="mobile" align="center" sortable="custom"/>
      <el-table-column label="状态0禁用1启用" prop="state" align="center" sortable="custom"/>
      <el-table-column label="级联选择" prop="field101" align="center" sortable="custom"/>
      <el-table-column label="多选框组" prop="field102" align="center" sortable="custom"/>
      <el-table-column label="顺序" prop="orderIndex" align="center" sortable="custom"/>
      <el-table-column label="ImageAvatar" prop="field114" align="center" sortable="custom"/>
      <el-table-column label="添加时间" prop="createTime" align="center" sortable="custom"/>
      <el-table-column label="删除时间" prop="deleteTime" align="center" sortable="custom"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <base-button link style="color: #13ce66" size="small" @click="openView(scope.row)">详情</base-button>
          <base-button link style="color: #ff6d6d" size="small" @click="deleteByIds(scope.row)">删除</base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 表描述-删除日志表-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 删除日志详情弹窗 -->
    <el-dialog title="删除日志详情" append-to-body v-model="dialogFormVisible" width="800px" draggable
               @close="closeDialog" :key="'myLogsDialog'+dialogIndex">
      <el-form ref="dataFormRef" :model="formData" label-position="right" label-width="auto" disabled>
        <el-form-item label="主键" prop="id" :rules="[{required: true, message: '主键不能为空'}]">
          <el-input v-model="formData.id" placeholder="请输入主键"/>
        </el-form-item>
        <el-form-item label="手机号" prop="mobile" :rules="[{required: true, message: '手机号不能为空'}]">
          <el-input v-model="formData.mobile" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="状态0禁用1启用" prop="state" :rules="[]">
          <el-radio-group v-model="formData.state">
            <el-radio value="0">禁用</el-radio>
            <el-radio value="1">启用</el-radio>
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
        <el-form-item label="删除时间" prop="deleteTime" :rules="[{required: true, message: '删除时间不能为空'}]">
          <el-date-picker v-model="formData.deleteTime" type="datetime" clearable placeholder="请选择删除时间"
                          value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
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
  request({url: 'a_table/aTableLogs/list', method: 'get', params}).then((response) => {
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
      const data = tableSelectRows.value.map(r => r.logsId)
      request({url: 'a_table/aTableLogs/delete', method: 'post', data}).then(response => {
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
// 表单
const dataFormRef = ref()
const formData = ref({orderIndex: 0})

// 关闭弹窗（清空表单temp数据）
const closeDialog = () => {
  dialogFormVisible.value = false
  formData.value = {orderIndex: 0}
  dialogIndex.value++
}
// 打开查看窗口
const openView = (row) => {
  dialogFormVisible.value = true
  formData.value = Object.assign({}, row)
  nextTick(() => { dataFormRef.value.clearValidate() })
}
// ==================== 3删除日志详情end ====================


// ==================== 4导出start ====================
// 导出Excel文件
const exportExcel = () => {
  const params = {params: JSON.stringify(searchData.value), sorts: JSON.stringify(sortData.value)}
  downloadUtil.download('a_table/aTableLogs/export/excel', params, '表描述-删除日志表.xlsx')
}
// ==================== 4导出end ====================
</script>
