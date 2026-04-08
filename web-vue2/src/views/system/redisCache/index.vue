<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :inline="true" size="small">
      <el-form-item label="模糊查询：">
        <el-input v-model="searchPattern" placeholder="*查询全部,例: user:*, ddos:*" @keyup.enter.native="handleSearch"/>
      </el-form-item>
      <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">查询</el-button>
      <el-button icon="el-icon-refresh" size="small" @click="handleRefresh" v-loading="refreshing">强制刷新</el-button>
    </el-form>
    <el-row :gutter="24">
      <!-- 左侧：缓存列表 -->
      <el-col :span="12">
        <el-card style="height: calc(100vh - 180px); overflow: hidden">
          <div slot="header" class="clearfix">
            <span><i class="el-icon-collection"></i> 缓存列表</span>
            <span style="float: right; color: #909399; font-size: 12px">共 {{ this.pager.totalCount }} 条</span>
          </div>
          <el-tag type="info" style="margin-bottom: 10px;">
            当前Key列表为缓存数据(1小时)，点击“强制刷新”按钮，强制刷新一次！
          </el-tag>
          <el-table :data="cacheList" style="width: 100%" border highlight-current-row height="calc(100vh - 350px)">
            <el-table-column type="index" label="序号" width="50" align="center"/>
            <el-table-column prop="key" label="缓存键名" show-overflow-tooltip/>
            <el-table-column label="操作" width="80" align="center" fixed="right">
              <template v-slot="scope">
                <el-button size="mini" type="text" icon="el-icon-view" @click="handleViewDetail(scope.row)">详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                         :page-size="pager.limit" :current-page="pager.page"
                         :total="pager.totalCount" @current-change="handleCurrentChange"
                         @size-change="handleSizeChange"/>
        </el-card>
      </el-col>

      <!-- 右侧：缓存详情 -->
      <el-col :span="12">
        <el-card style="height: calc(100vh - 180px)">
          <div slot="header">
            <span><i class="el-icon-document"></i> 缓存内容</span>
          </div>
          <el-form :model="cacheForm" label-width="120px" size="small">
            <div v-if="cacheForm.key">
              <el-form-item label="缓存键名:">
                <el-input :value="cacheForm.key" readonly/>
              </el-form-item>
              <el-form-item label="缓存类型:">
                <el-tag :type="getTypeTag(cacheForm.type)">{{ cacheForm.type || 'unknown' }}</el-tag>
              </el-form-item>
              <el-form-item label="过期时间:">
                <el-tag :type="cacheForm.expireTime === -1 ? 'success' : 'warning'">
                  {{ cacheForm.expireTime === -1 ? '永不过期' : cacheForm.expireTime + ' 秒' }}
                </el-tag>
              </el-form-item>
              <el-form-item label="缓存内容:">
                <el-input :value="cacheForm.value" type="textarea" :rows="18"
                          readonly style="font-family: monospace"/>
              </el-form-item>
              <el-form-item>
                <el-button v-if="cacheForm.key" type="danger" icon="el-icon-delete" @click="handleDeleteCache">
                  删除缓存
                </el-button>
              </el-form-item>
            </div>

            <div v-else class="cacheInfo">
              <i class="el-icon-info" style="font-size: 48px; color: #DCDFE6"></i>
              <p style="color: #909399; margin-top: 10px">点击左侧表格 [详情] 按钮，查看缓存内容</p>
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'RedisCacheManager',
  data() {
    return {
      searchPattern: '*',
      cacheList: [],
      pager: {page: 1, pageSize: 10, totalCount: 0},
      cacheForm: {},
      refreshing: false
    }
  },
  mounted() {
    this.loadCacheList()
  },
  methods: {
    // 加载缓存列表（带分页）
    loadCacheList(forceRefresh = false) {
      const params = {pattern: this.searchPattern, ...this.pager, refresh: forceRefresh}
      this.$request({url: '/redis/cache/keys', method: 'get', params})
          .then((response) => {
            this.cacheList = response.data.records.map(k => ({key: k}))
            this.pager.totalCount = response.data.total
            this.$message.success('加载成功')
            this.refreshing = false
          })
    },
    // 搜索
    handleSearch() {
      this.pager.page = 1
      this.loadCacheList(false)
    },
    // 强制刷新
    handleRefresh() {
      this.refreshing = true
      this.loadCacheList(true)
    },
    // 查看详情
    handleViewDetail(row) {
      this.$request({url: '/redis/cache/key/detail', method: 'get', params: {key: row.key}})
          .then((response) => { this.cacheForm = response.data })
    },
    // 监听分页
    handleCurrentChange(page) {
      this.pager.page = page
      this.loadCacheList()
    },
    // 删除缓存
    handleDeleteCache() {
      this.$confirm(`确定要删除缓存 "${this.cacheForm.key}" 吗？`, '警告',
          {confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'}).then(() => {
        this.$request({url: '/redis/cache/key/delete', method: 'delete', params: {key: this.cacheForm.key}})
            .then((response) => {
              this.$message.success('删除成功！正在刷新..')
              this.cacheForm = {}
              // 删除后重新加载列表（强制刷新）
              this.handleRefresh()
            })
      })
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.pager.pageSize = val
      this.pager.page = 1
      this.loadCacheList()
    },
    // 获取类型标签颜色
    getTypeTag(type) {
      const typeMap = {'string': '', 'list': 'success', 'set': 'warning', 'zset': 'danger', 'hash': 'info'}
      return typeMap[type?.toLowerCase()] || ''
    }
  }
}
</script>

<style scoped lang="scss">
.search-card {
  margin-bottom: 0;
  ::v-deep .el-card__body {
    padding: 15px 20px;
  }
}

.cacheInfo {
  text-align: center;
  padding: 80px 20px;
  p {
    margin: 10px 0 0;
    font-size: 14px;
  }
}

::v-deep .el-textarea__inner {
  font-size: 12px;
  line-height: 1.6;
}
</style>