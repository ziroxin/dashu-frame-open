<template>
  <div class="app-container">
    <!-- 代码生成器表单-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.formName" class="searchInput w-150px!" placeholder="表单名称"/>
        <el-input v-model="searchData.tableName" class="searchInput w-130px!" placeholder="表名"/>
        <el-input v-model="searchData.author" class="searchInput w-130px!" placeholder="作者"/>
        <el-select v-model="searchData.status" class="searchInput" placeholder="状态">
          <el-option label="未生成" value="0"/>
          <el-option label="已生成" value="1"/>
        </el-select>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel">
        <base-button type="primary" icon="el-icon-plus" @click="openAdd"
                     v-permission="'generator-zFormGenerator-add'">新增
        </base-button>
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)"
                     v-permission="'generator-zFormGenerator-delete'">删除
        </base-button>
      </div>
    </div>
    <!-- 代码生成器表单-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="表单名称" prop="formName" align="center"/>
      <el-table-column label="表名" prop="tableName" align="center"/>
      <el-table-column label="表描述" prop="tableDecription" align="center"/>
      <el-table-column label="pom模块名" prop="basePackage" align="center" show-overflow-tooltip/>
      <el-table-column label="生成包名" prop="tablePackage" align="center"/>
      <el-table-column label="前端路径" prop="viewPath" align="center"/>
      <el-table-column label="作者" prop="author" align="center"/>
      <el-table-column label="状态" prop="status" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">未生成</el-tag>
          <el-tag v-if="scope.row.status === '1'" type="success">已生成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="顺序" prop="orderIndex" align="center"/>
      <el-table-column fixed="right" label="操作" width="100" align="center">
        <template #default="scope">
          <base-button v-permission="'generator-zFormGenerator-update'"
                       link size="small" type="primary" @click="openUpdate(scope.row)">修改表单
          </base-button>
          <br/>
          <base-button link size="small" style="color: #13ce66;" @click="openView(scope.row)">详情</base-button>
          <base-button v-permission="'generator-zFormGenerator-delete'" style="color: #ff6d6d;"
                       link size="small" @click="deleteByIds(scope.row)">删除
          </base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 代码生成器表单-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               v-model="dialogFormVisible" @close="resetTemp" width="90%" top="5vh">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="130px" :disabled="dialogType==='view'">
        <el-form-item label="表单名称" prop="formName"
                      :rules="[{required: true, message: '表单名称不能为空'}]">
          <el-input v-model="temp.formName" placeholder="请输入表单名称"/>
        </el-form-item>
        <el-form-item label="表单内容json格式" prop="formJson">
          <el-input v-model="temp.formJson" :rules="[{required: true, message: '表单内容json格式不能为空'}]"
                    type="textarea" maxlength="8,000" autosize
                    placeholder="请输入表单内容json格式"/>
        </el-form-item>
        <el-form-item label="表名" prop="tableName"
                      :rules="[{required: true, message: '表名不能为空'}]">
          <el-input v-model="temp.tableName" placeholder="请输入表名"/>
        </el-form-item>
        <el-form-item label="表描述" prop="tableDecription">
          <el-input v-model="temp.tableDecription" :rules="[{required: true, message: '表描述不能为空'}]"
                    type="textarea" maxlength="500"
                    placeholder="请输入表描述"/>
        </el-form-item>
        <el-form-item label="pom模块名" prop="basePackage"
                      :rules="[{required: true, message: 'pom模块名不能为空'}]">
          <el-input v-model="temp.basePackage" placeholder="请输入pom模块名"/>
        </el-form-item>
        <el-form-item label="作者" prop="author"
                      :rules="[{required: true, message: '作者不能为空'}]">
          <el-input v-model="temp.author" placeholder="请输入作者"/>
        </el-form-item>
        <el-form-item label="生成包名" prop="tablePackage"
                      :rules="[{required: true, message: '生成包名不能为空'}]">
          <el-input v-model="temp.tablePackage" placeholder="请输入生成包名"/>
        </el-form-item>
        <el-form-item label="前端路径" prop="viewPath"
                      :rules="[{required: true, message: '前端路径不能为空'}]">
          <el-input v-model="temp.viewPath" placeholder="请输入前端路径"/>
        </el-form-item>
        <el-form-item label="代码生成状态" prop="status"
                      :rules="[{required: true, message: '代码生成状态不能为空'}]">
          <el-radio-group v-model="temp.status">
            <el-radio-button value="0">未生成</el-radio-button>
            <el-radio-button value="1">已生成</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0" step-strictly/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button type="primary" v-if="dialogType!=='view'" @click="saveData">保存</base-button>
          <base-button @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util'

export default {
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
      titleMap: {add: '添加代码生成器表单', update: '修改代码生成器表单', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      isLoading: false
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
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)}
      request({url: '/generator/zFormGenerator/list', method: 'get', params}).then((response) => {
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
      this.dialogFormVisible = false
    },
    // 打开添加窗口
    openAdd() {
      this.$router.push({path: '/generator/form?openType=new'})
    },
    // 打开修改窗口
    openUpdate(row) {
      this.$router.push({path: '/generator/form?fid=' + row.formId})
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
          const data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '/generator/zFormGenerator/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/generator/zFormGenerator/add', method: 'post', data}).then(response => {
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
          const data = this.tableSelectRows.map(r => r.formId)
          request({url: '/generator/zFormGenerator/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)}
      downloadUtil.download('/generator/zFormGenerator/export/excel', params, '代码生成器表单.xlsx')
    },
    // 导入Excel成功，提示
    importExcelSuccess(response) {
      if (response.code === '200') {
        this.$message({type: 'success', message: '导入成功！'})
        this.loadTableList()
      } else {
        this.$message({type: 'error', message: response.message})
      }
    }
  }
}
</script>
