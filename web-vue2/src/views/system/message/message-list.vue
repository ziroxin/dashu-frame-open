<template>
  <div>
    <!-- 消息中心-管理按钮 -->
    <div style="margin: 15px 0px 25px 0px;">
      <el-input v-model="searchData.msgTitle" size="small" style="width: 240px;margin-right: 10px;"
                class="filter-item" placeholder="请输入消息标题查询"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" size="small" v-if="msgStatus !== '1'"
                   icon="el-icon-check" @click="markAllRead">全部标记已读
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'message-zMessage-delete'">批量删除
        </el-button>
      </div>
    </div>
    <!-- 消息中心-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe :show-header="false"
              @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="消息标题" prop="msgTitle" max-width="50%">
        <template v-slot="scope">
          <div :class="{'unread':scope.row.msgStatus==='0'}">
            {{ scope.row.msgTitle }}
            <template v-if="scope.row.msgRouter">
              <el-link v-if="scope.row.msgRouter.indexOf('http')===0" target="_blank"
                 :underline="false" type="primary" style="margin-left: 10px;"
                 :href="scope.row.msgRouter" @click="$router.push(scope.row.msgRouter)">打开
              </el-link>
              <el-link v-else :underline="false" type="primary" style="margin-left: 10px;"
                       @click="$router.push(scope.row.msgRouter)">打开
              </el-link>
            </template>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="消息创建时间" prop="createTime" width="150" align="center"/>
      <el-table-column fixed="right" label="操作" width="150" align="center">
        <template v-slot="scope">
          <el-button type="text"
                     size="small" @click="openView(scope.row)">查看详情
          </el-button>
          <el-button v-permission="'message-zMessage-delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 消息中心-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="resetTemp" width="600px" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="60px" :disabled="dialogType==='view'">
        <el-form-item label="标题：" prop="msgTitle">
          <div>{{ temp.msgTitle }}</div>
        </el-form-item>
        <el-form-item label="内容：" prop="msgContent">
          <div>{{ temp.msgContent }}</div>
        </el-form-item>
        <template v-if="temp.msgRouter">
          <el-divider></el-divider>
          <el-form-item label="链接：" prop="msgRouter">
            <el-link :underline="false" type="primary"
                     @click="$router.push(temp.msgRouter)">点击进入页面
            </el-link>
          </el-form-item>
        </template>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util';

export default {
  name: 'MessageList',
  props: ['msgStatus'],
  directives: {waves},
  data() {
    return {
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {msgStatus: this.msgStatus},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加消息中心', update: '修改消息中心', view: '查看详情'},
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
    // 显示全部
    resetTableList() {
      this.pager.page = 1
      this.searchData = {msgStatus: this.msgStatus}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/message/zMessage/list', method: 'get', params
      }).then((response) => {
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
      this.dialogIndex++
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
      // 查询详情
      if (row.msgStatus === '0') {
        // 标记已读
        this.$emit('unread-count-change');
        const params = {msgId: row.msgId, msgStatus: row.msgStatus}
        this.$request({
          url: '/message/zMessage/read', method: 'get', params
        }).then((response) => {
          this.loadTableList();
        })
      }
      // 显示内容
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
          let data = {...this.temp}
          if (this.dialogType === 'update') {
            request({
              url: '/message/zMessage/update', method: 'put', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/message/zMessage/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.msgId)
          request({
            url: '/message/zMessage/delete', method: 'delete', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 全部标记已读
    markAllRead() {
      this.$confirm('确定要全部标记为已读吗?', '标记提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.$request({
          url: '/message/zMessage/readAll', method: 'get'
        }).then((response) => {
          this.$emit('all-read');
          this.loadTableList();
        })
      })
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)}
      downloadUtil.download('/message/zMessage/export/excel', params, '消息中心.xlsx')
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
  }
}
</script>
<style scoped lang="scss">
.app-container {
  .unread {
    &::before {
      content: "";
      display: inline-block;
      width: 8px;
      height: 8px;
      background-color: red;
      border-radius: 100%;
      margin-right: 8px;
    }
  }
}
</style>