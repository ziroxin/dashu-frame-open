<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :inline="true" size="default">
      <el-form-item label="模糊查询：">
        <el-input v-model="searchPattern" placeholder="*查询全部,例: user:*, ddos:*" @keyup.enter="handleSearch"/>
      </el-form-item>
      <el-form-item>
        <base-button type="primary" icon="el-icon-search" @click="handleSearch">查询</base-button>
        <base-button type="danger" icon="el-icon-refresh" @click="handleRefresh" :loading="refreshing">强制刷新
        </base-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="24">
      <!-- 左侧：缓存列表 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="flex justify-between items-center">
              <span><my-icon icon="el-icon-collection" class="mr-5px"/>缓存列表</span>
              <span class="text-12px text-gray-500">共 {{ pager.totalCount }} 条</span>
            </div>
          </template>
          <el-tag type="info" class="mb-10px">
            当前Key列表为缓存数据(1小时)，点击"强制刷新"按钮，强制刷新一次！
          </el-tag>
          <el-table :data="cacheList" border highlight-current-row height="calc(var(--app-content-height) - 270px)">
            <el-table-column type="index" label="序号" width="60" align="center"/>
            <el-table-column prop="key" label="缓存键名" show-overflow-tooltip/>
            <el-table-column label="操作" width="80" align="center" fixed="right">
              <template #default="scope">
                <base-button link type="primary" icon="el-icon-view" :icon-size="14" size="small"
                             @click="handleViewDetail(scope.row)">详情
                </base-button>
              </template>
            </el-table-column>
          </el-table>
          <!-- 字典数据-分页 -->
          <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                         :page-size="pager.pageSize" :current-page="pager.page"
                         :total="pager.totalCount" @current-change="handleCurrentChange"
                         @size-change="handleSizeChange"/>
        </el-card>
      </el-col>
      <!-- 右侧：缓存详情 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span><my-icon icon="el-icon-document" class="mr-5px"/>缓存内容</span>
          </template>
          <el-form :model="cacheForm" label-width="100px" size="default">
            <div v-if="cacheForm.key">
              <el-form-item label="缓存键名">
                <el-input :model-value="cacheForm.key" readonly/>
              </el-form-item>
              <el-form-item label="缓存类型">
                <el-tag :type="getTypeTag(cacheForm.type)">{{ cacheForm.type || 'unknown' }}</el-tag>
              </el-form-item>
              <el-form-item label="过期时间">
                <el-tag :type="cacheForm.expireTime === -1 ? 'success' : 'warning'">
                  {{ cacheForm.expireTime === -1 ? '永不过期' : cacheForm.expireTime + ' 秒' }}
                </el-tag>
              </el-form-item>
              <el-form-item label="缓存内容">
                <el-input :model-value="formatValue(cacheForm.value)" type="textarea"
                          :rows="18" readonly class="font-mono text-12px" style="line-height: 1.6"/>
              </el-form-item>
              <el-form-item>
                <base-button type="danger" icon="el-icon-delete" @click="handleDeleteCache">删除缓存</base-button>
              </el-form-item>
            </div>
            <div v-else class="cacheInfo">
              <my-icon icon="el-icon-info" class="text-48px text-gray-300"/>
              <p class="text-gray-500 mt-10px text-14px">点击左侧表格 [详情] 按钮，查看缓存内容</p>
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import request from '@/utils/request'
import { MyIcon } from '@/components/MyIcon'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const searchPattern = ref('*')
const cacheList = ref<any[]>([])
const cacheForm = ref<any>({})
const refreshing = ref(false)
const pager = reactive({page: 1, pageSize: 10, totalCount: 0})

// 组件挂载时加载数据
onMounted(() => { loadCacheList()})

// 加载缓存列表（带分页）
const loadCacheList = (forceRefresh = false) => {
  const params = {
    pattern: searchPattern.value,
    page: pager.page,
    pageSize: pager.pageSize,
    refresh: forceRefresh
  }
  request({url: '/redis/cache/keys', method: 'get', params})
      .then((response: any) => {
        cacheList.value = response.data.records.map((k: string) => ({key: k}))
        pager.totalCount = response.data.total
        if (!forceRefresh) {
          ElMessage.success('加载成功')
        }
      })
      .catch(() => { ElMessage.error('加载失败') })
      .finally(() => { refreshing.value = false })
}

// 搜索
const handleSearch = () => {
  pager.page = 1
  loadCacheList(false)
}

// 强制刷新
const handleRefresh = () => {
  refreshing.value = true
  loadCacheList(true)
}

// 查看详情
const handleViewDetail = (row: any) => {
  request({url: '/redis/cache/key/detail', method: 'get', params: {key: row.key}})
      .then((response: any) => { cacheForm.value = response.data })
      .catch(() => { ElMessage.error('获取详情失败') })
}

// 分页页码变化
const handleCurrentChange = (page: number) => {
  pager.page = page
  loadCacheList()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pager.pageSize = val
  pager.page = 1
  loadCacheList()
}

// 删除缓存
const handleDeleteCache = () => {
  ElMessageBox.confirm(`确定要删除缓存 "${cacheForm.value.key}" 吗？`, '警告',
      {confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'})
      .then(() => {
        request({url: '/redis/cache/key/delete', method: 'delete', params: {key: cacheForm.value.key}})
            .then(() => {
              ElMessage.success('删除成功！正在刷新..')
              cacheForm.value = {}
              // 删除后重新加载列表（强制刷新）
              handleRefresh()
            })
            .catch(() => { ElMessage.error('删除失败') })
      })
}

// 格式化显示值
const formatValue = (value: any) => {
  if (value === null || value === undefined) {
    return 'null'
  }
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2)
  }
  return String(value)
}

// 获取类型标签颜色
const getTypeTag = (type: string) => {
  const typeMap: Record<string, string> = {
    'string': '',
    'list': 'success',
    'set': 'warning',
    'zset': 'danger',
    'hash': 'info'
  }
  return typeMap[type?.toLowerCase()] || ''
}
</script>

<style scoped lang="less">
.cacheInfo {
  text-align: center;
  padding: 80px 20px;
}

:deep(.el-textarea__inner) {
  font-family: 'Consolas', 'Monaco', monospace;
}
</style>