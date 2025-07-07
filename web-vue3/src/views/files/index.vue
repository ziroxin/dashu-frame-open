<template>
  <div class="app-container">
    <!-- 文件记录表-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.fileMd5" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="输入文件md5-模糊"/>
      <el-input v-model="searchData.fileOldName" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="输入原文件名-模糊"/>
      <el-input v-model="searchData.fileExtend" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="输入扩展名-模糊"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'files-zFiles-add'">新增
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'files-zFiles-delete'">删除
        </el-button>
      </div>
    </div>
    <!-- 文件记录表-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="文件md5" prop="fileMd5" align="center"/>
      <el-table-column label="原文件名" prop="fileOldName" align="center"/>
      <el-table-column label="扩展名" prop="fileExtend" align="center" width="80"/>
      <el-table-column label="大小" prop="fileSize" align="center" width="100">
        <template v-slot="scope">
          {{ formatSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" width="100"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;"
                     size="small" @click="openView(scope.row)">详情
          </el-button>
          <el-button type="text"
                     size="small" @click="downloadFile(scope.row)">下载
          </el-button>
          <el-button v-permission="'files-zFiles-delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 文件记录表-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="loadTableList();resetTemp();dialogIndex++;" width="600px"
               :key="'upload' + dialogIndex">
      <div v-if="dialogType !== 'view'">
        <div
            style="text-align: center;font-size: 16px;padding-bottom: 20px;margin-bottom:20px;color: #2C7EEA;border-bottom: 1px dashed #CCCCCC;">
          上传完成后，关闭窗口，会刷新table数据
        </div>
        <file-second second-server-url="/upload/second/chunks" style="text-align: center;"
                     second-md5-url="/upload/second/md5"
                     upload-dir="testSecond"
                     mime-types=".zip,.rar"
                     :max-file-size="300*1024*1024" :chunk-size="10*1024*1024"
                     :is-copy="true"></file-second>
      </div>
      <div v-else>
        <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
                 :disabled="dialogType==='view'">
          <el-form-item label="文件id" prop="fileId"
                        :rules="[{required: true, message: '文件id不能为空'}]">
            <el-input v-model="temp.fileId" placeholder="请输入文件id"/>
          </el-form-item>
          <el-form-item label="文件md5" prop="fileMd5"
                        :rules="[]">
            <el-input v-model="temp.fileMd5" placeholder="请输入文件md5"/>
          </el-form-item>
          <el-form-item label="文件地址（文件访问地址）" prop="fileUrl"
                        :rules="[]">
            <el-input v-model="temp.fileUrl" placeholder="请输入文件地址（文件访问地址）"/>
          </el-form-item>
          <el-form-item label="原文件名" prop="fileOldName"
                        :rules="[]">
            <el-input v-model="temp.fileOldName" placeholder="请输入原文件名"/>
          </el-form-item>
          <el-form-item label="存储文件名" prop="fileName"
                        :rules="[]">
            <el-input v-model="temp.fileName" placeholder="请输入存储文件名"/>
          </el-form-item>
          <el-form-item label="文件扩展名" prop="fileExtend"
                        :rules="[]">
            <el-input v-model="temp.fileExtend" placeholder="请输入文件扩展名"/>
          </el-form-item>
          <el-form-item label="文件大小" prop="fileSize"
                        :rules="[{type: 'number', message: '必须为数字'}]">
            <el-input-number v-model.number="temp.fileSize" placeholder="请输入文件大小"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
          <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util';
import FileSecond from "@/views/demo/files/FileSecond.vue";

export default {
  components: {FileSecond},
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
      titleMap: {add: '添加文件记录表', update: '修改文件记录表', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      isLoading: false,
      dialogIndex: 0,
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
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({url: '/files/zFiles/list', method: 'get', params}).then((response) => {
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
    },
    // 打开添加窗口
    openAdd() {
      this.resetTemp()
      this.dialogFormVisible = true
      this.dialogType = 'add'
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
    // 下载
    downloadFile(row) {
      downloadUtil.saveAs(this.$baseServer + row.fileUrl, row.fileOldName)
    },
    // 添加/修改，保存事件
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          let data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '/files/zFiles/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/files/zFiles/add', method: 'post', data}).then(response => {
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
          const data = this.tableSelectRows.map(r => r.fileId)
          request({url: '/files/zFiles/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)}
      downloadUtil.download('/files/zFiles/export/excel', params, '文件记录表.xlsx')
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
    formatSize(size) {
      let sizeStr = size + 'B';
      if (size >= 1024 * 1024 * 1024) {
        sizeStr = (size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
      } else if (size >= 1024 * 1024) {
        sizeStr = (size / 1024 / 1024).toFixed(2) + 'MB'
      } else if (size >= 1024) {
        sizeStr = (size / 1024).toFixed(2) + 'KB'
      } else {
        sizeStr = size + 'B'
      }
      return sizeStr;
    }
  }
}
</script>
