<template>
  <div class="app-container">
    <!-- 操作日志表-管理按钮 -->
    <div style="margin-bottom: 20px;">
      <el-input v-model="searchData.userName" style="width: 160px;margin-right: 10px;"
                class="filter-item" placeholder="输入用户名模糊查询"
      />
      <el-input v-model="searchData.logMethod" style="width: 160px;margin-right: 10px;"
                class="filter-item" placeholder="输入方法名模糊查询"
      />
      <el-input v-model="searchData.ip" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入IP地址查询"
      />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="loadTableList">查询</el-button>
      <el-button v-waves class="filter-item" type="info" icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
      <div style="float: right;">
        <el-button v-waves v-permission="'zlog-zOperateLog-delete'" type="danger" icon="el-icon-delete"
                   @click="deleteByIds"
        >删除
        </el-button>
        <el-button v-waves v-permission="'zlog-zOperateLog-exportExcel'" type="success" icon="el-icon-printer"
                   @click="exportExcel"
        >导出Excel
        </el-button>
      </div>
    </div>
    <!-- 操作日志表-列表 -->
    <el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center" />
      <el-table-column label="用户名" prop="userName" align="center" min-width="10%" />
      <el-table-column label="方法名称" prop="logMethod" align="center" min-width="20%" />
      <el-table-column label="方法描述" prop="logMsg" align="center" min-width="30%" />
      <el-table-column label="请求路径" prop="actionUrl" align="center" min-width="10%" />
      <el-table-column label="IP地址" prop="ip" align="center" min-width="10%" />
      <el-table-column label="操作时间" prop="createTime" align="center" width="100" />
      <el-table-column fixed="right" label="操作" align="center" width="80">
        <template v-slot="scope">
          <el-button type="text" size="small" @click="openView(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 操作日志表-分页 -->
    <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
               style="width: 500px; margin-left: 50px;" :disabled="dialogType==='view'"
      >
        <el-form-item label="操作日志id" prop="logId" style="display: none;"
                      :rules="[{required: true, message: '操作日志id不能为空'}]"
        >
          <el-input v-model="temp.logId" placeholder="请输入操作日志id" />
        </el-form-item>
        <el-form-item label="操作人用户id" prop="userId"
                      :rules="[]"
        >
          <el-input v-model="temp.userId" placeholder="请输入用户id" />
        </el-form-item>
        <el-form-item label="操作人用户名" prop="userName"
                      :rules="[]"
        >
          <el-input v-model="temp.userName" placeholder="请输入操作人用户名" />
        </el-form-item>
        <el-form-item label="执行方法名称" prop="logMethod"
                      :rules="[]"
        >
          <el-input v-model="temp.logMethod" placeholder="请输入执行方法名称" />
        </el-form-item>
        <el-form-item label="执行方法描述" prop="logMsg"
                      :rules="[]"
        >
          <el-input v-model="temp.logMsg" placeholder="请输入执行方法描述" />
        </el-form-item>
        <el-form-item label="参数" prop="content">
          <el-input v-model="temp.content" :rules="[]" type="textarea" :rows="8"
                    placeholder="请输入操作内容"
          />
        </el-form-item>
        <el-form-item label="请求路径" prop="actionUrl"
                      :rules="[]"
        >
          <el-input v-model="temp.actionUrl" placeholder="请输入请求路径" />
        </el-form-item>
        <el-form-item label="IP地址" prop="ip"
                      :rules="[]"
        >
          <el-input v-model="temp.ip" placeholder="请输入IP地址" />
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
      titleMap: {add: '添加操作日志表', update: '修改操作日志表', view: '查看详情'},
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
        url: '/zlog/zOperateLog/list', method: 'get', params
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
    openUpdate() {
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
        for (const $elElement of this.$refs['dataForm'].$el) {
          $elElement.placeholder = '';
        }
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
              url: '/zlog/zOperateLog/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/zlog/zOperateLog/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.logId)
          request({
            url: '/zlog/zOperateLog/delete', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/zlog/zOperateLog/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '操作日志表.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    }
  }
}
</script>
