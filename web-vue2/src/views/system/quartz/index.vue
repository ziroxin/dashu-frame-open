<template>
  <div class="app-container">
    <!-- 定时任务调度表-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.jobName" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入名称查询"
      />
      <el-input v-model="searchData.jobClass" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入类名查询"
      />
      <el-button v-waves class="filter-item" size="small" type="primary" icon="el-icon-search" @click="loadTableList"
      ></el-button>
      <el-button v-waves class="filter-item reset-btn" size="small" icon="el-icon-refresh" @click="resetTableList"
      ></el-button>
      <div style="float: right;">
        <el-button v-waves v-permission="'zquartz-zQuartz-add'" type="primary"
                   size="small"
                   @click="openAdd"
        >新增
        </el-button>
        <el-button v-waves v-permission="'zquartz-zQuartz-delete'" type="danger" size="small"
                   @click="deleteByIds"
        >删除
        </el-button>
      </div>
    </div>
    <!-- 定时任务调度表-列表 -->
    <el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="任务名称" prop="jobName" align="center" min-width="10%"/>
      <el-table-column label="任务执行类" prop="jobClass" align="center" min-width="20%"/>
      <el-table-column label="任务执行时间" prop="jobTimeCron" align="center" min-width="10%"/>
      <el-table-column label="任务描述" prop="description" align="center" min-width="20%"/>
      <el-table-column label="状态" align="center" min-width="10%">
        <template v-slot="scope">
          <span v-if="scope.row.status==='1'" style="color: #2ac06d;">开启</span>
          <span v-else style="color: #dd1100;">关闭</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="160px">
        <template v-slot="{row}">
          <template v-if="row.status==='1'">
            <el-tooltip effect="dark" content="请先停用！才能修改" placement="top">
              <el-button type="info" size="mini" icon="el-icon-edit">修改</el-button>
            </el-tooltip>
            <el-button v-waves size="mini" type="danger" @click="updateStatus(row,0)">
              停用
            </el-button>
          </template>
          <template v-else>
            <el-button v-waves type="success" v-permission="'zquartz-zQuartz-update'" size="mini"
                       @click="openUpdate(row)"
            >修改
            </el-button>
            <el-button v-waves
                       size="mini" type="success" @click="updateStatus(row,1)"
            >启用
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 定时任务调度表-分页 -->
    <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
               style="width: 500px; margin-left: 50px;" :disabled="dialogType==='view'"
      >
        <el-form-item label="定时任务id" prop="quartzId" style="display: none;">
          <el-input v-model="temp.quartzId" placeholder="请输入定时任务id"/>
        </el-form-item>
        <el-form-item label="任务名称" prop="jobName"
                      :rules="[]"
        >
          <el-input v-model="temp.jobName" placeholder="请输入任务名称（不能重复）"/>
        </el-form-item>
        <el-form-item label="任务执行类" prop="jobClass"
                      :rules="[]"
        >
          <el-input v-model="temp.jobClass" placeholder="请输入任务执行类（该类必须实现org.quartz.Job）"/>
        </el-form-item>
        <el-form-item label="任务执行时间" prop="jobTimeCron"
                      :rules="[]"
        >
          <el-input v-model="temp.jobTimeCron" placeholder="请输入任务执行时间（Cron表达式：秒 分 时 日 月 年）"/>
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input v-model="temp.description" placeholder="请输入任务描述"/>
        </el-form-item>
        <el-form-item label="状态" prop="status"
                      :rules="[]"
        >
          <el-switch v-model="temp.status" active-text="开启" inactive-text="关闭"
                     active-value="1" inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="dialogType!=='view'" v-waves type="primary" @click="saveData">保存</el-button>
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
    // 显示全部
    resetTableList() {
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/zquartz/zQuartz/list', method: 'get', params
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
        this.tableSelectRows = []
        this.tableSelectRows.push(row)
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
          var data = this.temp;
          if (this.dialogType === 'update') {
            request({
              url: '/zquartz/zQuartz/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/zquartz/zQuartz/add', method: 'post', data
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
    deleteByIds() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.quartzId)
          request({
            url: '/zquartz/zQuartz/delete', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 启用/停用
    updateStatus(row, status) {
      row.status = status
      let data = {...row};
      request({
        url: '/zquartz/zQuartz/update', method: 'post', data
      }).then(response => {
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
      request({
        url: '/zquartz/zQuartz/refresh', method: 'get'
      }).then(response => {
        this.$message({type: 'success', message: '刷新状态成功！'})
        this.loadTableList()
      })
    },
    // 导出Excel文件
    exportExcel() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/zquartz/zQuartz/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '定时任务调度表.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    }
  }
}
</script>
