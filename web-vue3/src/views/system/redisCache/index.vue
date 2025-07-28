<template>
  <div class="m-[var(--app-content-margin)] p-5px" v-loading="isLoading">
    <el-row :gutter="24">
      <el-col :span="10">
        <el-card class="h-[var(--app-content-height)] overflow-auto">
          <template #header>
            <span>
              <my-icon icon="el-icon-collection" class="mr-5px"/>缓存列表
            </span>
            <base-button class="float-right p-[3px_0]" link type="primary" icon="el-icon-refresh-right"
                         @click="loadRedisCacheList()">刷新
            </base-button>
          </template>
          <el-table :data="cacheList" border highlight-current-row>
            <el-table-column prop="key" label="缓存名称" show-overflow-tooltip/>
            <el-table-column prop="expireTime" label="过期时间" width="100" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.expireTime===-1" type="success" size="small">永不过期</el-tag>
                <el-tag v-else type="danger" size="small">{{ scope.row.expireTime }} 秒</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="scope">
                <base-button link type="primary" icon="el-icon-view" :icon-size="14" size="small"
                             @click="cacheForm=scope.row">详情
                </base-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card :bordered="false" class="h-[var(--app-content-height)]">
          <template #header>
            <span>
              <my-icon icon="el-icon-document" class="mr-5px"/>缓存内容
            </span>
          </template>
          <el-form :model="cacheForm" label-width="100px">
            <div v-if="cacheForm.key">
              <el-form-item label="缓存键名" prop="cacheName">
                <el-input :model-value="cacheForm.key" :readOnly="true"/>
              </el-form-item>
              <el-form-item label="缓存内容" prop="cacheValue">
                <el-input :model-value="cacheForm.value" type="textarea" :rows="20" :readOnly="true"/>
              </el-form-item>
              <el-form-item label="缓存过期时间" prop="cacheKey">
                <el-input :model-value="cacheForm.expireTime===-1?'永不过期':cacheForm.expireTime" :readOnly="true"/>
              </el-form-item>
              <el-form-item label="">
                <base-button type="danger" icon="el-icon-delete" @click="handleDeleteRedisCache">删除此缓存
                </base-button>
              </el-form-item>
            </div>
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
import request from '@/utils/request'
import { MyIcon } from '@/components/MyIcon'

export default {
  components: {MyIcon},
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
      request({url: '/redis/cache/keys/list', method: 'get'})
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
        request({url: '/redis/cache/delete', method: 'get', params}).then((response) => {
          this.$message({type: 'success', message: '删除成功!'})
          this.loadRedisCacheList()
        })
      })
    }
  }
}
</script>
<style scoped lang="less">
.cacheInfo {
  color: #D7000F;
  text-align: center;
  font-size: 18px;
  margin: 50px auto;
}
</style>