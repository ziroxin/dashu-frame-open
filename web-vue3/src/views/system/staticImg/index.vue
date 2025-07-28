<template>
  <div class="app-container">
    <!-- 静态资源文件表-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.fileOldName" class="searchInput w-200px!" placeholder="文件夹名称-模糊"/>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel">
        <base-button type="primary" icon="el-icon-plus" @click="openAdd">新增</base-button>
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)">删除</base-button>
      </div>
    </div>
    <div class="mb-10px text-12px lh-20px color-#666 b-1px b-dashed b-#ddd p-5px b-rd-5px">
      <b>用途：</b>静态资源管理
      <b class="ml-20px">统一文件前缀：</b>{{ copyUrlBase }}<br/>
      <b>说明：</b>静态资源上传后，不修改原文件名；如果上传同名文件，需要先删除旧文件<br/>
      <b>其他：</b>允许上传的文件格式、大小等，根据开发需要在FolderViews.vue文件中自行修改<br/>
      <b>举例：</b>小程序开发时，有文件大小限制等，可以将图片、附件等资源上传到这里，直接使用链接引用，建议统一设置文件前缀，方便后期修改域名
    </div>
    <!-- 静态资源文件表-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="文件夹名称" prop="fileOldName" align="center"/>
      <el-table-column label="文件夹地址" prop="fileUrl" align="center" show-overflow-tooltip>
        <template #default="scope">
          <base-button type="primary" circle plain icon="el-icon-document-copy" :icon-size="12" size="small"
                       v-clipboard:copy="copyUrlBase+scope.row.fileUrl"/>
          {{ copyUrlBase + scope.row.fileUrl }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center"/>
      <el-table-column label="修改时间" prop="updateTime" align="center"/>
      <el-table-column fixed="right" label="操作" width="200" align="center">
        <template #default="scope">
          <base-button link style="color: #13ce66;" size="small" @click="openViews(scope.row)">管理文件夹
          </base-button>
          <base-button link type="primary" size="small" @click="openUpdate(scope.row)">修改</base-button>
          <base-button link style="color: #ff6d6d;" size="small" @click="deleteByIds(scope.row)">删除</base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 静态资源文件表-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               v-model="dialogFormVisible" @close="closeDialog" width="600px" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
        <el-form-item label="文件夹名称" prop="fileOldName" :rules="[{required: true, message: '文件夹名称不能为空！'}]">
          <el-input v-model="temp.fileOldName" placeholder="请输入文件夹名称"/>
        </el-form-item>
        <el-form-item label="类型" prop="fileType" :rules="[{required: true, message: '类型不能为空'}]">
          <el-radio-group v-model="temp.fileType">
            <el-radio :value="'0'">文件夹</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button type="primary" v-if="dialogType!=='view'" @click="saveData">保存</base-button>
          <base-button @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
    <!-- 管理文件夹弹窗 -->
    <el-dialog title="管理文件夹" v-model="folderDialogVisible" @closed="dialogIndex++"
               :close-on-click-modal="false" width="80%" :key="'folder'+dialogIndex">
      <folder-views :currentParentId="currentParentId" v-if="folderDialogVisible"/>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import FolderViews from '@/views/system/staticImg/FolderViews.vue'

export default {
  components: {FolderViews},
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
      titleMap: {add: '添加文件夹', update: '修改文件夹', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {fileType: '0'},
      isLoading: false,
      dialogIndex: 0,
      // 管理文件夹内容相关
      currentParentId: '',
      folderDialogVisible: false
    }
  },
  computed: {
    // 拷贝路径根地址
    copyUrlBase() {
      return this.$baseServer.startsWith('http') ? this.$baseServer : window.location.origin + this.$baseServer
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
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const obj = {...this.searchData, fileType: '0'}
      const params = {...this.pager, params: JSON.stringify(obj)}
      request({url: '/filesStatic/zFilesStatic/list', method: 'get', params}).then((response) => {
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
    closeDialog() {
      this.temp = this.$options.data().temp
      this.dialogIndex++
      this.dialogFormVisible = false
    },
    // 打开添加窗口
    openAdd() {
      this.dialogFormVisible = true
      this.dialogType = 'add'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 打开修改窗口
    openUpdate(row) {
      if (row) {
        this.$refs.dataTable.clearSelection()
        this.$refs.dataTable.toggleRowSelection(row, true)
      }
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
    // 添加/修改，保存事件
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '/filesStatic/zFilesStatic/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/filesStatic/zFilesStatic/add', method: 'post', data}).then(response => {
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
          request({url: '/filesStatic/zFilesStatic/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 打开管理文件夹窗口
    openViews(row) {
      this.currentParentId = row.fileId
      this.folderDialogVisible = true
    }
  }
}
</script>
