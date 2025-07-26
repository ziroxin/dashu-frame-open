<template>
  <div class="app-container">
    <!-- 定时任务调度表-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.jobName" class="searchInput w-150px!" placeholder="名称"/>
        <el-input v-model="searchData.jobClass" class="searchInput w-150px!" placeholder="类名"/>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel">
        <base-button v-permission="'zquartz-zQuartz-add'" type="primary" icon="el-icon-plus" @click="openAdd">新增
        </base-button>
        <base-button type="warning" v-permission="'zquartz-zQuartz-copy'" icon="el-icon-copy-document"
                     @click="copyById">复制任务
        </base-button>
        <base-button v-permission="'zquartz-zQuartz-delete'" type="danger" icon="el-icon-delete"
                     @click="deleteByIds">删除
        </base-button>
      </div>
    </div>
    <!-- 定时任务调度表-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="任务名称" prop="jobName" align="center" min-width="10%" show-overflow-tooltip/>
      <el-table-column label="任务执行类" prop="jobClass" align="center" min-width="20%" show-overflow-tooltip/>
      <el-table-column label="任务执行时间" prop="jobTimeCron" align="center" min-width="10%"/>
      <el-table-column label="任务描述" prop="description" align="center" min-width="20%" show-overflow-tooltip/>
      <el-table-column label="状态" align="center" min-width="10%">
        <template #default="scope">
          <el-tag v-if="scope.row.status==='1'" type="success" disable-transitions>开启</el-tag>
          <el-tag v-else type="danger" disable-transitions>关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="160px">
        <template #default="{row}">
          <template v-if="row.status==='1'">
            <el-tooltip effect="dark" content="请先停用！才能修改" placement="top">
              <base-button type="info" icon="el-icon-edit" size="small">修改</base-button>
            </el-tooltip>
            <base-button type="danger" @click="updateStatus(row,0)" size="small">停用</base-button>
          </template>
          <template v-else>
            <base-button type="primary" v-permission="'zquartz-zQuartz-update'"
                         icon="el-icon-edit" @click="openUpdate(row)" size="small">修改
            </base-button>
            <base-button type="success" @click="updateStatus(row,1)" size="small">启用</base-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 定时任务调度表-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               v-model="dialogFormVisible" width="660px" @close="resetTemp">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="110px"
               class="w-500px ml-50px" :disabled="dialogType==='view'">
        <el-form-item label="任务名称" prop="jobName" :rules="[{required: true, message: '请输入任务名称'}]">
          <el-input v-model="temp.jobName" placeholder="请输入任务名称（不能重复）"/>
        </el-form-item>
        <el-form-item label="任务执行类" prop="jobClass" :rules="[{required: true, message: '请输入任务执行类'}]">
          <el-input v-model="temp.jobClass" placeholder="请输入任务执行类（该类必须实现org.quartz.Job）"/>
        </el-form-item>
        <el-form-item label="任务执行时间" prop="jobTimeCron"
                      :rules="[{required: true, message: '请输入任务执行时间'}]">
          <el-input v-model="temp.jobTimeCron" placeholder="请输入任务执行时间（Cron表达式：秒 分 时 日 月 年）"/>
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input type="textarea" autosize v-model="temp.description" placeholder="请输入任务描述"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="temp.status" active-text="开启" inactive-text="关闭" active-value="1" inactive-value="0"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button v-if="dialogType!=='view'" type="primary" @click="saveData">保存</base-button>
          <base-button @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

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
      titleMap: {add: '添加定时任务调度表', update: '修改定时任务调度表', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {}
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
      const params = {...this.pager, params: JSON.stringify(this.searchData)}
      request({url: '/zquartz/zQuartz/list', method: 'get', params}).then((response) => {
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
      this.dialogFormVisible = false
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
          const data = this.temp
          if (this.dialogType === 'update') {
            request({url: '/zquartz/zQuartz/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/zquartz/zQuartz/add', method: 'post', data}).then(response => {
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
          const data = this.tableSelectRows.map(r => r.quartzId)
          request({url: '/zquartz/zQuartz/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 复制
    copyById() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据复制！', type: 'warning'})
      } else if (this.tableSelectRows.length > 1) {
        this.$message({message: '只能选择一条数据复制！', type: 'warning'})
      } else {
        // 复制弹窗
        this.temp = Object.assign({}, this.tableSelectRows[0])
        this.temp.quartzId = null
        this.dialogType = 'add'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      }
    },
    // 启用/停用
    updateStatus(row, status) {
      row.status = status
      const data = {...row}
      request({url: '/zquartz/zQuartz/update', method: 'post', data}).then(response => {
        if (status === '1') {
          this.$message({type: 'success', message: '启用成功！'})
        } else {
          this.$message({type: 'success', message: '停用成功！'})
        }
        this.loadTableList()
      })
    },
    // 刷新状态
    refresh() {
      request({url: '/zquartz/zQuartz/refresh', method: 'get'}).then(response => {
        this.$message({type: 'success', message: '刷新状态成功！'})
        this.loadTableList()
      })
    },
    // 导出Excel文件
    exportExcel() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)}
      request({url: '/zquartz/zQuartz/export/excel', method: 'get', params}).then(response => {
        // 创建a标签
        const link = document.createElement('a')
        // 组装下载地址
        link.href = this.$baseServer + response.data
        // 修改文件名
        link.setAttribute('download', '定时任务调度表.xlsx')
        // 开始下载
        link.style.display = 'none'
        document.body.appendChild(link)
        link.click()
      })
    }
  }
}
</script>
