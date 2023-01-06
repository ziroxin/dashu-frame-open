<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane label="临时锁定用户管理" name="second">
        <!-- 用户锁定-管理按钮 -->
        <div style="margin-bottom: 20px;float: right;">
          <el-button v-waves type="danger" icon="el-icon-unlock" @click="deleteByIds"
                     v-permission="'zuserlock-zUserLock-unlock'">用户解锁
          </el-button>
        </div>
        <!-- 用户锁定-列表 -->
        <el-table :data="tableData2" stripe border @selection-change="handleTableSelectChange">
          <el-table-column type="selection" width="50" align="center" header-align="center"/>
          <el-table-column label="用户名" prop="userName" align="center" min-width="20%"/>
          <el-table-column label="锁定原因" prop="lockReason" align="left" min-width="75%"/>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="永久锁定用户管理" name="first">
        <!-- 用户锁定-管理按钮 -->
        <div style="margin-bottom: 20px;">
          <el-input v-model="searchData.userName" style="width: 150px;margin-right: 10px;"
                    class="filter-item" placeholder="请输入用户名查询"/>
          <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="loadTableList">查询</el-button>
          <el-button v-waves class="filter-item" type="info" icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
          <div style="float: right;">
            <el-button v-waves type="danger" icon="el-icon-unlock" @click="deleteByIds"
                       v-permission="'zuserlock-zUserLock-unlock'">用户解锁
            </el-button>
          </div>
        </div>
        <!-- 用户锁定-列表 -->
        <el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
          <el-table-column type="selection" width="50" align="center" header-align="center"/>
          <el-table-column label="用户名" prop="userName" align="center" min-width="20%"/>
          <el-table-column label="锁定原因" prop="lockReason" align="center" min-width="60%"/>
          <el-table-column label="锁定时间" prop="createTime" align="center" min-width="20%"/>
        </el-table>
        <!-- 用户锁定-分页 -->
        <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                       :page-size="pager.limit" :current-page="pager.page"
                       :total="pager.totalCount" @current-change="handleCurrentChange"
        />
      </el-tab-pane>
    </el-tabs>


  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'

export default {
  directives: {waves},
  data() {
    return {
      activeName: 'second',
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      // 表格
      tableData: [],
      tableData2: [],
      // 查询表单数据
      searchData: {},
      // 选中行
      tableSelectRows: [],
    }
  },
  created() {
    request({
      url: '/zsafety/zSafety/getSafety', method: 'get'
    }).then((response) => {
      const {data} = response
      if (data.lockTime > 0) {
        // 临时锁定
        this.activeName = 'second'
        this.loadTable2List()
      } else {
        this.activeName = 'first'
        this.loadTableList()
      }
    })
  },
  methods: {
    handleTabClick(tab, event) {
      if (tab.name === 'first') {
        this.loadTableList()
      } else {
        this.loadTable2List()
      }
    },
    // 显示全部
    resetTableList() {
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/zuserlock/zUserLock/list', method: 'get', params
      }).then((response) => {
        const {data} = response
        this.pager.totalCount = data.total
        this.tableData = data.records
      })
    },
    loadTable2List() {
      request({
        url: '/zuserlock/zUserLock/cacheList', method: 'get'
      }).then((response) => {
        const {data} = response
        this.tableData2 = data
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
    // 删除
    deleteByIds() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.userName)
          request({
            url: '/zuserlock/zUserLock/unlock', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
            this.loadTable2List()
          })
        })
      }
    },
  }
}
</script>
