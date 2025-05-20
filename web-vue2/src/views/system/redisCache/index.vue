<template>
  <div class="app-container" v-loading="isLoading">
    <el-row :gutter="24">
      <el-col :span="12">
        <el-card style="height: calc(100vh - 125px);overflow: auto">
          <div slot="header">
            <span><i class="el-icon-collection"></i> 缓存列表</span>
            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh-right"
                       @click="loadRedisCacheList()">刷新
            </el-button>
          </div>
          <el-table :data="cacheList" style="width: 100%"
                    border highlight-current-row>
            <el-table-column prop="key" label="缓存名称" show-overflow-tooltip/>
            <el-table-column prop="expireTime" label="过期时间" width="100" align="center">
              <template v-slot="scope">
                <el-tag v-if="scope.row.expireTime===-1" type="success">永不过期</el-tag>
                <el-tag v-else type="danger">{{ scope.row.expireTime }} 秒</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template v-slot="scope">
                <el-button size="mini" type="text" icon="el-icon-view"
                           @click="cacheForm=scope.row">详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card :bordered="false" style="height: calc(100vh - 125px)">
          <div slot="header">
            <span><i class="el-icon-document"></i> 缓存内容</span>
          </div>
          <el-form :model="cacheForm">
            <el-row :gutter="32" v-if="cacheForm.key">
              <el-col :offset="1" :span="22">
                <el-form-item label="缓存键名:" prop="cacheName">
                  <el-input :value="cacheForm.key" :readOnly="true"/>
                </el-form-item>
              </el-col>
              <el-col :offset="1" :span="22">
                <el-form-item label="缓存内容:" prop="cacheValue">
                  <el-input :value="cacheForm.value" type="textarea" :rows="8" :readOnly="true"/>
                </el-form-item>
              </el-col>
              <el-col :offset="1" :span="22">
                <el-form-item label="缓存过期时间:" prop="cacheKey">
                  <el-input :value="cacheForm.expireTime===-1?'永不过期':cacheForm.expireTime" :readOnly="true"/>
                </el-form-item>
              </el-col>
              <el-col :offset="1" :span="22">
                <el-button type="danger" icon="el-icon-delete"
                           @click="handleDeleteRedisCache()">删除此缓存
                </el-button>
              </el-col>
            </el-row>
            <div v-else class="cacheInfo">
              点击左侧表格 [详情] 按钮，查看缓存内容详情
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  data() {
    return {
      cacheList: [],
      cacheForm: {},
      isLoading: false
    }
  },
  mounted() {
    this.loadRedisCacheList()
  },
  methods: {
    loadRedisCacheList() {
      this.isLoading = true
      this.$request({url: '/redis/cache/keys/list', method: 'get'})
          .then((response) => {
            const {data} = response
            this.cacheList = data
            this.cacheForm = {}
            this.isLoading = false
          })
    },
    handleDeleteRedisCache() {
      this.$confirm('此操作将永久删除该缓存, 是否继续?', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        const params = {key: this.cacheForm.key}
        this.$request({url: '/redis/cache/delete', method: 'get', params}).then((response) => {
          this.$message({type: 'success', message: '删除成功!'})
          this.loadRedisCacheList()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
.cacheInfo {
  color: #D7000F;
  text-align: center;
  font-size: 18px;
  margin: 50px auto;
}
</style>