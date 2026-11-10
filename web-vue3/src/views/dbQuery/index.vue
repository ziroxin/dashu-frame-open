<template>
  <div class="app-container db-query-container">
    <!-- 功能关闭提示 -->
    <el-result
      v-if="featureDisabled"
      icon="warning"
      title="功能已关闭"
      sub-title="数据库查询工具当前已关闭，如需使用请联系管理员开启。"
      class="h-full flex-center"
    />

    <el-row v-show="!featureDisabled" :gutter="16" class="h-full">
      <!-- 左侧：数据库/表结构树 -->
      <el-col :span="6" class="h-full">
        <el-card class="h-full tree-card">
          <template #header>
            <div class="flex justify-between items-center">
              <span><my-icon icon="el-icon-coin" class="mr-5px"/>数据库</span>
              <el-button link type="primary" @click="loadDatabases" :loading="treeLoading">
                <el-icon><refresh /></el-icon>
              </el-button>
            </div>
          </template>

          <el-input v-model="filterText" placeholder="搜索表名..." size="small" clearable class="mb-10px"/>

          <el-scrollbar height="calc(100% - 90px)">
            <el-tree
              ref="treeRef"
              :data="treeData"
              :props="treeProps"
              node-key="id"
              :filter-node-method="filterNode"
              @node-click="handleNodeClick"
              :expand-on-click-node="false"
              class="db-tree"
            >
              <template #default="{ node, data }">
                <span class="tree-node">
                  <el-icon v-if="data.type === 'database'" class="mr-5px"><coin /></el-icon>
                  <el-icon v-else-if="data.type === 'table'" class="mr-5px"><grid /></el-icon>
                  <el-icon v-else class="mr-5px"><document /></el-icon>
                  <span>{{ node.label }}</span>
                  <el-tag v-if="data.comment" size="small" type="info" class="ml-5px">
                    {{ data.comment }}
                  </el-tag>
                </span>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>

      <!-- 右侧：SQL 编辑器 + 结果 -->
      <el-col :span="18" class="h-full flex flex-col">
        <!-- SQL 编辑器区域 -->
        <el-card class="sql-card">
          <template #header>
            <div class="flex justify-between items-center">
              <span><my-icon icon="el-icon-edit" class="mr-5px"/>SQL 查询</span>
              <div class="flex items-center gap-10px">
                <el-select v-model="currentDatabase" placeholder="选择数据库" size="small" style="width: 160px">
                  <el-option v-for="db in databases" :key="db.databaseName" :label="db.databaseName" :value="db.databaseName"/>
                </el-select>
                <el-radio-group v-if="canWrite" v-model="mode" size="small">
                  <el-radio-button value="query">查询</el-radio-button>
                  <el-radio-button value="write">写操作</el-radio-button>
                </el-radio-group>
                <el-input-number v-if="mode === 'query'" v-model="maxRows" :min="1" :max="10000" size="small" placeholder="最大行数" style="width: 120px"/>
                <el-button :type="mode === 'write' ? 'warning' : 'primary'" @click="runMain" :loading="executing" icon="caret-right">
                  {{ mode === 'write' ? '预览变更 (Ctrl+Enter)' : '执行 (Ctrl+Enter)' }}
                </el-button>
                <el-button @click="clearEditor" icon="delete">清空</el-button>
                <el-button v-if="mode === 'query'" type="success" @click="exportCsv" :disabled="!lastResult" icon="download">
                  导出CSV
                </el-button>
              </div>
            </div>
          </template>

          <!-- SQL 输入框 -->
          <el-input
            ref="sqlInputRef"
            v-model="sqlText"
            type="textarea"
            :rows="6"
            :placeholder="mode === 'write'
              ? '输入写操作 SQL... (仅支持单条 INSERT/UPDATE/DELETE；UPDATE/DELETE 必须带 WHERE)'
              : '输入 SQL 语句... (支持 SELECT, SHOW, DESC 等只读操作)'"
            class="sql-input"
            @keydown.ctrl.enter="runMain"
          />

          <!-- 快捷操作 -->
          <div class="quick-actions mt-10px">
            <el-tag
              v-for="item in quickActions"
              :key="item.label"
              class="mr-5px mb-5px cursor-pointer"
              @click="insertSql(item.sql)"
            >
              {{ item.label }}
            </el-tag>
          </div>
        </el-card>

        <!-- 查询结果区域 -->
        <el-card class="result-card flex-1 mt-16px">
          <template #header>
            <div class="flex justify-between items-center">
              <span>
                <my-icon icon="el-icon-document" class="mr-5px"/>查询结果
                <el-tag v-if="lastResult" type="info" size="small" class="ml-10px">
                  {{ lastResult.totalRows }} 行
                </el-tag>
                <el-tag v-if="lastResult?.truncated" type="warning" size="small" class="ml-5px">
                  已截断
                </el-tag>
                <el-tag v-if="lastResult?.executeTime" type="success" size="small" class="ml-5px">
                  {{ lastResult.executeTime }}ms
                </el-tag>
              </span>
            </div>
          </template>

          <!-- 错误提示 -->
          <el-alert
            v-if="lastResult && !lastResult.success"
            :title="lastResult.errorMessage"
            type="error"
            show-icon
            closable
            class="mb-10px"
          />

          <!-- 结果表格 -->
          <el-scrollbar v-if="lastResult && lastResult.success && lastResult.columns?.length > 0" height="400px">
            <el-table :data="lastResult.rows" border stripe size="small" class="result-table">
              <el-table-column
                v-for="col in lastResult.columns"
                :key="col"
                :prop="col"
                :label="col"
                show-overflow-tooltip
                min-width="150"
              >
                <template #default="{ row }">
                  {{ formatCellValue(row[col]) }}
                </template>
              </el-table-column>
            </el-table>
          </el-scrollbar>

          <!-- 空状态 -->
          <el-empty v-else-if="!executing" description="执行 SQL 查询查看结果" :image-size="100"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表结构详情弹窗 -->
    <el-dialog v-model="showStructureDialog" title="表结构" width="800px">
      <el-tabs v-model="structureTab">
        <el-tab-pane label="列信息" name="columns">
          <el-table :data="currentTableStructure?.columns || []" border size="small">
            <el-table-column prop="columnName" label="列名" width="150"/>
            <el-table-column prop="columnType" label="类型" width="120"/>
            <el-table-column prop="nullable" label="可空" width="60" align="center">
              <template #default="{ row }">
                <el-tag :type="row.nullable ? 'success' : 'danger'" size="small">
                  {{ row.nullable ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="defaultValue" label="默认值" width="100"/>
            <el-table-column prop="primaryKey" label="主键" width="60" align="center">
              <template #default="{ row }">
                <el-icon v-if="row.primaryKey" color="#409eff"><key /></el-icon>
              </template>
            </el-table-column>
            <el-table-column prop="autoIncrement" label="自增" width="60" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.autoIncrement" type="warning" size="small">AI</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="columnComment" label="注释"/>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="索引信息" name="indexes">
          <el-table :data="currentTableStructure?.indexes || []" border size="small">
            <el-table-column prop="indexName" label="索引名" width="200"/>
            <el-table-column prop="indexType" label="类型" width="100"/>
            <el-table-column prop="nonUnique" label="唯一" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="!row.nonUnique ? 'success' : 'info'" size="small">
                  {{ !row.nonUnique ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="列">
              <template #default="{ row }">
                <el-tag v-for="col in row.columns" :key="col" size="small" class="mr-5px">
                  {{ col }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 写操作预览确认弹窗 -->
    <el-dialog v-model="showPreviewDialog" title="写操作预览确认" width="900px" :close-on-click-modal="false">
      <el-alert
        v-if="previewData && previewData.success"
        :title="previewData.message"
        type="warning"
        show-icon
        :closable="false"
        class="mb-10px"
      />
      <el-descriptions v-if="previewData" :column="3" border size="small" class="mb-10px">
        <el-descriptions-item label="语句类型">{{ previewData.sqlType }}</el-descriptions-item>
        <el-descriptions-item label="目标表">{{ previewData.table }}</el-descriptions-item>
        <el-descriptions-item label="受影响行数">
          <span style="color: #e6a23c; font-weight: 600">
            {{ previewData.affectedRows == null ? '（插入语句不统计）' : previewData.affectedRows }}
          </span>
        </el-descriptions-item>
      </el-descriptions>

      <div class="mb-10px sql-preview-text">{{ sqlText }}</div>

      <div v-if="previewData && previewData.rows && previewData.rows.length" class="mb-5px">
        <span style="font-weight: 600">原始数据样本（最多 {{ previewData.rows.length }} 行）：</span>
      </div>
      <el-scrollbar v-if="previewData && previewData.rows && previewData.rows.length" max-height="320px">
        <el-table :data="previewData.rows" border stripe size="small">
          <el-table-column
            v-for="col in previewData.columns"
            :key="col"
            :prop="col"
            :label="col"
            show-overflow-tooltip
            min-width="140"
          >
            <template #default="{ row }">{{ formatCellValue(row[col]) }}</template>
          </el-table-column>
        </el-table>
      </el-scrollbar>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPreviewDialog = false">取消</el-button>
          <el-button type="danger" :loading="executing" @click="confirmExecute">确认执行</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { MyIcon } from '@/components/MyIcon'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Coin, Grid, Document, CaretRight, Delete, Download, Key } from '@element-plus/icons-vue'
import {
  executeQueryApi,
  getDatabasesApi,
  getTablesApi,
  getTableStructureApi,
  exportToCsvApi,
  getStatusApi,
  previewWriteApi,
  executeWriteApi
} from '@/api/dbQuery'

// 响应式数据
const treeRef = ref()
const sqlInputRef = ref()
const treeLoading = ref(false)
const executing = ref(false)
const filterText = ref('')
const sqlText = ref('')
const currentDatabase = ref('')
const maxRows = ref(1000)
const databases = ref<any[]>([])
const treeData = ref<any[]>([])
const lastResult = ref<any>(null)
const showStructureDialog = ref(false)
const currentTableStructure = ref<any>(null)
const structureTab = ref('columns')

// 写模式相关状态
const featureDisabled = ref(false)
const writeEnabled = ref(false)
const developer = ref(false)
const mode = ref<'query' | 'write'>('query')
const showPreviewDialog = ref(false)
const previewData = ref<any>(null)

// 仅当功能开启、允许写、且当前用户为开发管理员时，才显示写模式
const canWrite = computed(() => !featureDisabled.value && writeEnabled.value && developer.value)

// 树节点配置
const treeProps = {
  children: 'children',
  label: 'label'
}

// 快捷操作
const quickActions = [
  { label: '查看所有表', sql: 'SHOW TABLES' },
  { label: '查看建表语句', sql: 'SHOW CREATE TABLE ' },
  { label: '查看表结构', sql: 'DESC ' },
  { label: '查看索引', sql: 'SHOW INDEX FROM ' },
  { label: '查看数据库', sql: 'SHOW DATABASES' },
  { label: '查看版本', sql: 'SELECT VERSION()' },
  { label: '查看当前时间', sql: 'SELECT NOW()' },
  { label: '查看进程', sql: 'SHOW PROCESSLIST' }
]

// 计算属性
const isLoading = computed(() => treeLoading.value || executing.value)

// 监听过滤文本
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

// 组件挂载
onMounted(async () => {
  await loadStatus()
  if (!featureDisabled.value) {
    loadDatabases()
  }
})

// 探测模块状态（接口 404 / enabled=false 视为功能已关闭）
const loadStatus = async () => {
  try {
    const res = await getStatusApi()
    const data = res.data || {}
    featureDisabled.value = data.enabled === false
    writeEnabled.value = data.writeEnabled === true
    developer.value = data.developer === true
  } catch (e) {
    // 模块未注入（关闭）时接口不存在
    featureDisabled.value = true
  }
}

// 加载数据库列表
const loadDatabases = async () => {
  treeLoading.value = true
  try {
    const res = await getDatabasesApi()
    databases.value = res.data || []

    // 构建树形数据
    treeData.value = databases.value.map((db: any) => ({
      id: `db_${db.databaseName}`,
      label: db.databaseName,
      type: 'database',
      comment: `${db.tableCount || 0} 个表`,
      databaseName: db.databaseName,
      children: []
    }))

    // 默认选择第一个数据库
    if (databases.value.length > 0 && !currentDatabase.value) {
      currentDatabase.value = databases.value[0].databaseName
      loadTables(currentDatabase.value)
    }
  } catch (e) {
    ElMessage.error('加载数据库列表失败')
  } finally {
    treeLoading.value = false
  }
}

// 加载表列表
const loadTables = async (databaseName: string) => {
  try {
    const res = await getTablesApi(databaseName)
    const tables = res.data || []

    // 找到对应的数据库节点，添加表子节点
    const dbNode = treeData.value.find((db: any) => db.databaseName === databaseName)
    if (dbNode) {
      dbNode.children = tables.map((table: any) => ({
        id: `table_${databaseName}_${table.tableName}`,
        label: table.tableName,
        type: 'table',
        comment: table.tableComment,
        databaseName,
        tableName: table.tableName,
        children: []
      }))
    }
  } catch (e) {
    console.error('加载表列表失败', e)
  }
}

// 过滤节点
const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.label.toLowerCase().includes(value.toLowerCase())
}

// 点击树节点
const handleNodeClick = async (data: any) => {
  if (data.type === 'database') {
    currentDatabase.value = data.databaseName
    if (data.children?.length === 0) {
      await loadTables(data.databaseName)
    }
  } else if (data.type === 'table') {
    // 双击查看表结构
    showTableStructure(data.databaseName, data.tableName)
  }
}

// 显示表结构
const showTableStructure = async (databaseName: string, tableName: string) => {
  try {
    const res = await getTableStructureApi(databaseName, tableName)
    currentTableStructure.value = res.data
    showStructureDialog.value = true
  } catch (e) {
    ElMessage.error('获取表结构失败')
  }
}

// 执行查询
const executeQuery = async () => {
  if (!sqlText.value.trim()) {
    ElMessage.warning('请输入 SQL 语句')
    return
  }

  executing.value = true
  try {
    const res = await executeQueryApi({
      sql: sqlText.value,
      databaseName: currentDatabase.value,
      maxRows: maxRows.value,
      timeout: 30
    })
    lastResult.value = res.data

    if (!res.data.success) {
      ElMessage.error(res.data.errorMessage || '查询失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    executing.value = false
  }
}

// 主按钮分发：查询模式执行查询，写模式先预览
const runMain = () => {
  if (mode.value === 'write') {
    doPreviewWrite()
  } else {
    executeQuery()
  }
}

// 预览写 SQL
const doPreviewWrite = async () => {
  if (!sqlText.value.trim()) {
    ElMessage.warning('请输入 SQL 语句')
    return
  }
  executing.value = true
  try {
    const res = await previewWriteApi({
      sql: sqlText.value,
      databaseName: currentDatabase.value,
      maxBackupRows: 200
    })
    previewData.value = res.data
    if (res.data && res.data.success) {
      showPreviewDialog.value = true
    } else {
      ElMessage.error((res.data && res.data.errorMessage) || '预览失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '预览失败')
  } finally {
    executing.value = false
  }
}

// 二次确认后执行写 SQL
const confirmExecute = async () => {
  if (!previewData.value || !previewData.value.token) {
    ElMessage.error('预览已失效，请重新预览')
    return
  }
  // 二次确认：要求管理员明确确认
  try {
    await ElMessageBox.confirm(
      '此操作将直接修改数据库数据，且不可撤销。请确认影响行数与原始数据无误后继续。',
      '危险操作确认',
      { type: 'warning', confirmButtonText: '我确认执行', cancelButtonText: '取消', confirmButtonClass: 'el-button--danger' }
    )
  } catch {
    return
  }

  executing.value = true
  try {
    const res = await executeWriteApi({
      token: previewData.value.token,
      sql: sqlText.value,
      databaseName: currentDatabase.value
    })
    const data = res.data
    if (data && data.success) {
      ElMessage.success(data.message || '执行成功')
      showPreviewDialog.value = false
      previewData.value = null
    } else {
      ElMessage.error((data && data.errorMessage) || '执行失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '执行失败')
  } finally {
    executing.value = false
  }
}

// 清空编辑器
const clearEditor = () => {
  sqlText.value = ''
  lastResult.value = null
}

// 插入 SQL
const insertSql = (sql: string) => {
  sqlText.value = sql
  sqlInputRef.value?.focus()
}

// 导出 CSV
const exportCsv = async () => {
  if (!lastResult.value) return

  try {
    const res = await exportToCsvApi({
      sql: sqlText.value,
      databaseName: currentDatabase.value,
      maxRows: 10000,
      timeout: 60
    })

    // 创建下载链接
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `export_${Date.now()}.csv`
    link.click()
    URL.revokeObjectURL(link.href)

    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

// 格式化单元格值
const formatCellValue = (value: any) => {
  if (value === null || value === undefined) {
    return 'NULL'
  }
  if (typeof value === 'object') {
    return JSON.stringify(value)
  }
  return String(value)
}
</script>

<style scoped lang="less">
.db-query-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tree-card {
  height: 100%;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
    overflow: hidden;
    padding: 10px;
  }
}

.sql-card {
  :deep(.el-card__body) {
    padding: 15px;
  }
}

.sql-input {
  :deep(.el-textarea__inner) {
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.6;
  }
}

.result-card {
  :deep(.el-card__body) {
    padding: 15px;
  }
}

.result-table {
  :deep(.el-table__cell) {
    padding: 8px 0;
  }
}

.db-tree {
  background: transparent;
  --el-tree-node-hover-bg-color: var(--el-fill-color-light);
}

.tree-node {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.quick-actions {
  .el-tag {
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    }
  }
}

.flex-col {
  display: flex;
  flex-direction: column;
}

.flex-1 {
  flex: 1;
}

.gap-10px {
  gap: 10px;
}

.h-full {
  height: 100%;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.sql-preview-text {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  background: var(--el-fill-color-light);
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
