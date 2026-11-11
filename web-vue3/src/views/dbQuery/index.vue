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
                <my-icon icon="el-icon-refresh"/>
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
              highlight-current
              :current-node-key="currentNodeKey"
              :default-expanded-keys="defaultExpandedKeys"
              :filter-node-method="filterNode"
              @node-click="handleNodeClick"
              :expand-on-click-node="false"
              class="db-tree"
            >
              <template #default="{ data }">
                <el-tooltip placement="right" :show-after="200">
                  <template #content>
                    <div>{{ data.label }}</div>
                    <div v-if="data.comment">{{ data.comment }}</div>
                  </template>
                  <span class="tree-node">
                    <my-icon v-if="data.type === 'database'" icon="el-icon-coin" class="mr-5px"/>
                    <my-icon v-else-if="data.type === 'table'" icon="table" class="mr-5px"/>
                    <my-icon v-else icon="el-icon-document" class="mr-5px"/>
                    <span class="tree-label">{{ data.label }}</span>
                  </span>
                </el-tooltip>
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
            <div class="flex justify-between items-center sql-editor-header">
              <div class="flex items-center gap-10px">
                <el-select v-model="currentDatabase" placeholder="选择数据库" size="small" style="width: 160px">
                  <el-option v-for="db in databases" :key="db.databaseName" :label="db.databaseName" :value="db.databaseName"/>
                </el-select>
                <el-radio-group v-if="canWrite" v-model="mode" size="small">
                  <el-radio-button value="query">查询</el-radio-button>
                  <el-radio-button value="write">写操作</el-radio-button>
                </el-radio-group>
              </div>
              <div class="flex items-center gap-10px editor-actions">
                <el-button :type="mode === 'write' ? 'warning' : 'primary'" @click="runMain" :loading="executing">
                  <my-icon v-if="!executing" icon="el-icon-caret-right" class="mr-5px"/>{{ mode === 'write' ? '预览变更 (Ctrl+Enter)' : '执行 (Ctrl+Enter)' }}
                </el-button>
                <el-button @click="clearEditor"><my-icon icon="el-icon-delete" class="mr-5px"/>清空</el-button>
              </div>
            </div>
          </template>

          <!-- 已选表信息栏：位于输入框上方，与快捷操作分离 -->
          <div v-if="selectedTable" class="selected-table-bar mb-10px">
            <span class="st-label">当前表：</span>
            <span class="copy-text st-name" title="点击复制表名" @click="copyText(selectedTable.tableName)">
              {{ selectedTable.tableName }}
            </span>
            <span
              v-if="selectedTable.tableComment"
              class="copy-text st-comment"
              title="点击复制表注释"
              @click="copyText(selectedTable.tableComment)"
            >
              {{ selectedTable.tableComment }}
            </span>
            <span class="st-actions">
              <el-button size="small" type="primary" plain @click="openStructureDialog" :loading="structureLoading">表结构</el-button>
              <el-button size="small" link @click="clearTableSelect">取消选择</el-button>
            </span>
          </div>

          <!-- SQL 输入框 -->
          <div class="sql-input-wrap">
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
            <span v-if="sqlText" class="sql-clear" title="清空" @click="sqlText = ''">
              <my-icon icon="el-icon-circle-close"/>
            </span>
          </div>

          <!-- 快捷操作 -->
          <div class="quick-actions mt-10px">
            <!-- 第一行：选中表的快捷查询（红色）+ 基础查询（队尾） -->
            <div class="quick-row">
              <template v-if="selectedTable">
                <el-tag class="quick-btn" type="danger" effect="dark" @click="fillTop10">查询前10条</el-tag>
                <el-tag class="quick-btn" type="danger" effect="dark" @click="fillAllColumns">查询所有字段</el-tag>
                <el-tag class="quick-btn" type="danger" effect="dark" @click="fillCount">查询总行数</el-tag>
              </template>
              <el-tag
                v-for="item in baseActions"
                :key="item.label"
                class="quick-btn"
                @click="insertSql(item.sql)"
              >
                {{ item.label }}
              </el-tag>
            </div>

            <!-- 第二行：表名 / 字段名 快捷填充（文本样式，默认折叠一行，可展开收起） -->
            <div v-if="selectedTable && tableColumns.length" class="token-row-wrap mt-6px">
              <div
                ref="tokenAreaRef"
                class="token-area"
                :style="{ maxHeight: tokensExpanded ? 'none' : tokenOneRowH + 'px' }"
              >
                <span class="token-text table-token" title="点击插入表名" @click="insertToken(selectedTable.tableName)">
                  {{ selectedTable.tableName }}
                </span>
                <span
                  v-for="col in tableColumns"
                  :key="col"
                  class="token-text"
                  title="点击插入字段名"
                  @click="insertToken(col)"
                >
                  {{ col }}
                </span>
              </div>
              <el-button v-if="tokensOverflow" link type="primary" size="small" class="token-toggle" @click="tokensExpanded = !tokensExpanded">
                <my-icon :icon="tokensExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" class="mr-5px"/>
                {{ tokensExpanded ? '收起' : '展开' }}
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 最近查询历史 -->
        <el-card v-if="queryHistory.length" class="history-card mt-16px">
          <template #header>
            <div class="history-header">
              <span class="history-title">
                <my-icon icon="el-icon-document" class="mr-5px"/>最近查询 · {{ currentDatabase }}
              </span>
              <div class="history-filter">
                <el-input
                  v-model="historyFilter"
                  placeholder="筛选查询历史..."
                  size="small"
                  clearable
                  style="width: 200px"
                />
              </div>
              <el-button link type="danger" size="small" class="history-clear" @click="confirmClearHistory">清空全部</el-button>
            </div>
          </template>
          <div class="history-list">
            <div
              v-for="(h, i) in filteredHistory"
              :key="i"
              class="history-item"
              :title="h.sql"
              @click="useHistory(h.sql)"
            >
              <span class="history-index">{{ i + 1 }}</span>
              <el-tag :type="h.success ? 'success' : 'danger'" size="small" class="history-status">
                {{ h.success ? '成功' : '失败' }}
              </el-tag>
              <span class="history-sql">{{ h.sql }}</span>
              <span class="history-time">{{ formatHistoryTime(h.ts) }}</span>
            </div>
            <div v-if="!filteredHistory.length" class="history-empty">无匹配的查询历史</div>
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
              <el-button
                v-if="lastResult && lastResult.success && lastResult.columns?.length > 0"
                type="success"
                size="small"
                @click="exportCsv"
              >
                <my-icon icon="el-icon-download" class="mr-5px"/>导出CSV
              </el-button>
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
            <el-table
              :data="pagedRows"
              border
              stripe
              size="small"
              class="result-table"
              @cell-click="handleCellCopy"
            >
              <el-table-column
                v-for="col in lastResult.columns"
                :key="col"
                :prop="col"
                :label="col"
                show-overflow-tooltip
                min-width="150"
              >
                <template #default="{ row }">
                  <span class="copyable-cell">{{ formatCellValue(row[col]) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-scrollbar>

          <!-- 分页 -->
          <div v-if="lastResult && lastResult.success && resultTotal > resultPageSize" class="result-pagination mt-10px">
            <el-pagination
              v-model:current-page="resultPage"
              v-model:page-size="resultPageSize"
              :total="resultTotal"
              :page-sizes="[50, 100, 200, 500]"
              layout="total, sizes, prev, pager, next, jumper"
              background
              size="small"
            />
          </div>

          <!-- 空状态 -->
          <el-empty v-else-if="!executing && !(lastResult && lastResult.success && lastResult.columns?.length > 0)" description="执行 SQL 查询查看结果" :image-size="100"/>
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
                <my-icon v-if="row.primaryKey" icon="el-icon-key" color="#409eff"/>
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
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { MyIcon } from '@/components/MyIcon'
import { ElMessage, ElMessageBox } from 'element-plus'
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
const maxRows = ref(100)
const databases = ref<any[]>([])
const treeData = ref<any[]>([])
// 默认展开的节点 key（首个库）
const defaultExpandedKeys = ref<string[]>([])
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

// 表选择 / 快捷填充 / 结果分页
const currentNodeKey = ref('')
const selectedTable = ref<{ databaseName: string; tableName: string; tableComment?: string } | null>(null)
// 第二行 token 展开/收起：默认折叠一行
const tokenAreaRef = ref<HTMLElement | null>(null)
const tokensExpanded = ref(false)
const tokenOneRowH = ref(32)
const tokensOverflow = ref(false)
const structureCache = reactive<Record<string, any>>({})
const structureLoading = ref(false)
const resultPage = ref(1)
const resultPageSize = ref(100)

// 最近查询历史（按数据库分开；localStorage 存储）
type HistoryItem = { sql: string; ts: number; success: boolean }
const HISTORY_KEY = 'db-query:history'
const HISTORY_MAX = 50
const historyMap = ref<Record<string, HistoryItem[]>>({})
const queryHistory = computed<HistoryItem[]>(() => historyMap.value[currentDatabase.value] || [])
// 历史过滤：按 SQL 模糊匹配
const historyFilter = ref('')
const filteredHistory = computed<HistoryItem[]>(() => {
  const kw = historyFilter.value.trim().toLowerCase()
  if (!kw) return queryHistory.value
  return queryHistory.value.filter((h) => h.sql.toLowerCase().includes(kw))
})

// 选中表的字段名列表
const tableColumns = computed<string[]>(() => {
  const cols = currentTableStructure.value?.columns
  return Array.isArray(cols) ? cols.map((c: any) => c.columnName) : []
})

// 结果分页
const resultTotal = computed(() => (lastResult.value?.rows ? lastResult.value.rows.length : 0))
const pagedRows = computed(() => {
  const rows = lastResult.value?.rows || []
  const start = (resultPage.value - 1) * resultPageSize.value
  return rows.slice(start, start + resultPageSize.value)
})

// 仅当功能开启、允许写、且当前用户为开发管理员时，才显示写模式
const canWrite = computed(() => !featureDisabled.value && writeEnabled.value && developer.value)

// 树节点配置
const treeProps = {
  children: 'children',
  label: 'label'
}

// 基础快捷操作（未选表时也显示；选表时置于队尾）
const baseActions = [
  { label: '查询所有表', sql: 'SHOW TABLES' },
  { label: '查看版本', sql: 'SELECT VERSION()' },
  { label: '查看进程', sql: 'SHOW PROCESSLIST' }
]

// 监听过滤文本
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

// 组件挂载
onMounted(async () => {
  loadHistory()
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
      defaultExpandedKeys.value = [`db_${currentDatabase.value}`]
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

// 点击树节点：库→切换并清空选表；表→单选并加载结构（不再自动弹窗）
const handleNodeClick = async (data: any) => {
  if (data.type === 'database') {
    currentDatabase.value = data.databaseName
    clearTableSelect()
    if (data.children?.length === 0) {
      await loadTables(data.databaseName)
    }
  } else if (data.type === 'table') {
    currentNodeKey.value = data.id
    selectedTable.value = { databaseName: data.databaseName, tableName: data.tableName, tableComment: data.comment }
    currentTableStructure.value = null
    await ensureStructure(data.databaseName, data.tableName)
  }
}

// 测量单行 token 高度（用于折叠态只显示一行），并判断是否溢出多行
const measureTokenLine = () => {
  const area = tokenAreaRef.value
  const first = area?.firstElementChild as HTMLElement | null
  if (first && first.offsetHeight > 0) {
    tokenOneRowH.value = first.offsetHeight
  }
  if (area) {
    tokensOverflow.value = area.scrollHeight > tokenOneRowH.value + 2
  }
}

// 字段列表变化时：折叠并重新测量
watch(tableColumns, async () => {
  tokensExpanded.value = false
  await nextTick()
  measureTokenLine()
})

// 加载（带缓存）表结构，供"查询所有字段"与字段快捷填充使用
const ensureStructure = async (databaseName: string, tableName: string) => {
  const key = `${databaseName}.${tableName}`
  if (structureCache[key]) {
    currentTableStructure.value = structureCache[key]
    return structureCache[key]
  }
  structureLoading.value = true
  try {
    const res = await getTableStructureApi(databaseName, tableName)
    structureCache[key] = res.data
    currentTableStructure.value = res.data
    return res.data
  } catch (e) {
    ElMessage.error('获取表结构失败')
    return null
  } finally {
    structureLoading.value = false
  }
}

// 点击"表结构"按钮：弹窗展示
const openStructureDialog = async () => {
  if (!selectedTable.value) return
  const { databaseName, tableName } = selectedTable.value
  await ensureStructure(databaseName, tableName)
  if (currentTableStructure.value) {
    showStructureDialog.value = true
  }
}

// 取消表选择
const clearTableSelect = () => {
  selectedTable.value = null
  currentTableStructure.value = null
  currentNodeKey.value = ''
  treeRef.value?.setCurrentKey?.(null)
}

// 查询前10条
const fillTop10 = () => {
  if (!selectedTable.value) return
  sqlText.value = `SELECT * FROM \`${selectedTable.value.tableName}\` LIMIT 10`
  focusSqlInput()
}

// 查询所有字段
const fillAllColumns = () => {
  if (!selectedTable.value) return
  const cols = tableColumns.value
  const colList = cols.length ? cols.map((c) => `\`${c}\``).join(', ') : '*'
  sqlText.value = `SELECT ${colList} FROM \`${selectedTable.value.tableName}\``
  focusSqlInput()
}

// 查询总行数
const fillCount = () => {
  if (!selectedTable.value) return
  sqlText.value = `SELECT COUNT(*) AS total FROM \`${selectedTable.value.tableName}\``
  focusSqlInput()
}

// 插入 token（表名/字段名），前后自动加空格
const insertToken = (token: string) => {
  const cur = sqlText.value || ''
  sqlText.value = `${cur} ${token} `
  focusSqlInput()
}

const focusSqlInput = () => {
  const el = sqlInputRef.value
  const target = el?.textarea || el?.$refs?.textarea || el?.ref
  if (typeof el?.focus === 'function') el.focus()
  if (target && typeof target.focus === 'function') {
    target.focus()
    const len = target.value ? target.value.length : 0
    try { target.setSelectionRange(len, len) } catch (e) { /* 某些环境不支持光标定位，忽略 */ }
  }
}

// ===== 最近查询历史（按数据库分开，每个库最多 50 条；重复语句仅更新顺序；成功/失败都记录） =====
const loadHistory = () => {
  try {
    const raw = localStorage.getItem(HISTORY_KEY)
    const obj = raw ? JSON.parse(raw) : {}
    // 兼容旧数据（旧版为数组）：非对象或数组一律重置
    historyMap.value = obj && typeof obj === 'object' && !Array.isArray(obj) ? obj : {}
  } catch (e) {
    historyMap.value = {}
  }
}

const persistHistory = () => {
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(historyMap.value))
  } catch (e) {
    // 忽略：隐私模式 / 配额限制
  }
}

const addHistory = (sql: string, success: boolean) => {
  const s = (sql || '').trim()
  if (!s) return
  const db = currentDatabase.value
  const list = (historyMap.value[db] ? historyMap.value[db].slice() : [])
  const idx = list.findIndex((h) => h.sql === s)
  if (idx !== -1) list.splice(idx, 1)
  list.unshift({ sql: s, ts: Date.now(), success })
  if (list.length > HISTORY_MAX) list.length = HISTORY_MAX
  historyMap.value = { ...historyMap.value, [db]: list }
  persistHistory()
}

const useHistory = (sql: string) => {
  sqlText.value = sql
  focusSqlInput()
}

const clearHistory = () => {
  const db = currentDatabase.value
  historyMap.value = { ...historyMap.value, [db]: [] }
  persistHistory()
}

// 清空本库历史（二次确认）
const confirmClearHistory = async () => {
  try {
    await ElMessageBox.confirm('确认清空当前库的最近查询历史？', '清空历史', {
      type: 'warning',
      confirmButtonText: '确认清空',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  clearHistory()
  historyFilter.value = ''
}

const formatHistoryTime = (ts: number) => {
  const d = new Date(ts)
  const p = (n: number) => (n < 10 ? '0' + n : '' + n)
  return `${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}

// 查询/写操作模式：首关键词集合（与后端白名单保持一致）
const READ_KEYWORDS = ['SELECT', 'SHOW', 'DESC', 'DESCRIBE', 'EXPLAIN']
const WRITE_KEYWORDS = ['INSERT', 'UPDATE', 'DELETE']

// 取语句首个关键词
const firstKeyword = (sql: string) => {
  const m = sql.trim().toUpperCase().match(/^[A-Z_]+/)
  return m ? m[0] : ''
}

// 是否存在堆叠语句（去掉结尾分号后，中间仍有分号）
const hasStackedStatement = (sql: string) => {
  let body = sql.trim()
  while (body.endsWith(';')) body = body.slice(0, -1).trim()
  return body.indexOf(';') >= 0
}

// 前端按模式预校验（非权威，后端仍会完整校验；仅拦截明显的模式不符，减少无效请求）
const validateByMode = (sql: string, m: 'query' | 'write'): { ok: boolean; message?: string } => {
  const trimmed = (sql || '').trim()
  if (!trimmed) return { ok: false, message: '请输入 SQL 语句' }
  const kw = firstKeyword(trimmed)
  if (hasStackedStatement(trimmed)) return { ok: false, message: '禁止多语句（堆叠）执行' }

  if (m === 'query') {
    if (WRITE_KEYWORDS.includes(kw)) return { ok: false, message: `这是写操作(${kw})，请切换到"写操作"模式再执行` }
    if (!READ_KEYWORDS.includes(kw)) return { ok: false, message: `查询模式仅支持 ${READ_KEYWORDS.join('/')} 语句` }
  } else {
    if (READ_KEYWORDS.includes(kw)) return { ok: false, message: `这是查询语句(${kw})，请切换到"查询"模式再执行` }
    if (!WRITE_KEYWORDS.includes(kw)) return { ok: false, message: '写操作仅支持 INSERT/UPDATE/DELETE 语句' }
    if ((kw === 'UPDATE' || kw === 'DELETE') && !/\bWHERE\b/i.test(trimmed)) {
      return { ok: false, message: `${kw} 必须带 WHERE 条件` }
    }
  }
  return { ok: true }
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
    resultPage.value = 1

    if (!res.data.success) {
      ElMessage.error(res.data.errorMessage || '查询失败')
      addHistory(sqlText.value, false)
    } else {
      addHistory(sqlText.value, true)
    }
  } catch (e: any) {
    ElMessage.error(e.message || '查询失败')
    addHistory(sqlText.value, false)
  } finally {
    executing.value = false
  }
}

// 主按钮分发：查询模式执行查询，写模式先预览（执行前先做前端模式校验，减少无效请求）
const runMain = () => {
  const check = validateByMode(sqlText.value, mode.value)
  if (!check.ok) {
    ElMessage.warning(check.message || 'SQL 不符合当前模式')
    return
  }
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
    // request 拦截器对 blob 响应直接返回 Blob 本体，故此处 await 结果即为 Blob
    const blob = await exportToCsvApi({
      sql: sqlText.value,
      databaseName: currentDatabase.value,
      maxRows: 10000,
      timeout: 60
    })

    // 后端出错时可能返回 JSON（而非 CSV），需识别并提示
    if (blob && blob.type && blob.type.indexOf('application/json') >= 0) {
      const text = await blob.text()
      let msg = '导出失败'
      try {
        msg = JSON.parse(text).message || msg
      } catch (e) {
        /* 解析失败，使用默认提示 */
      }
      ElMessage.error(msg)
      return
    }

    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `export_${Date.now()}.csv`
    link.click()
    window.URL.revokeObjectURL(url)

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

// 点击结果单元格：复制其内容
const handleCellCopy = (row: any, column: any) => {
  if (!column || !column.property) return
  const val = formatCellValue(row[column.property])
  if (val === null || val === undefined || val === '') return
  copyText(val)
}

// 复制到剪贴板（生产多为 http，clipboard API 需安全上下文，故保留 execCommand 降级）
const copyText = async (text: string) => {
  const tip = '已复制: ' + (text.length > 30 ? text.slice(0, 30) + '…' : text)
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
      ElMessage.success(tip)
      return
    }
  } catch (e) {
    // 继续降级
  }
  try {
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.top = '0'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.focus()
    textarea.select()
    const ok = document.execCommand('copy')
    document.body.removeChild(textarea)
    ok ? ElMessage.success(tip) : ElMessage.error('复制失败')
  } catch (e) {
    ElMessage.error('复制失败')
  }
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

.sql-input-wrap {
  position: relative;
}

.sql-clear {
  position: absolute;
  top: 8px;
  right: 12px;
  z-index: 2;
  display: inline-flex;
  cursor: pointer;
  color: var(--el-text-color-placeholder);

  &:hover {
    color: var(--el-text-color-secondary);
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
  align-self: stretch;
  font-size: 13px;
  width: 100%;
  overflow: hidden;
}

.tree-label {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.selected-table-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  padding: 6px 8px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
  font-size: 13px;

  .st-label {
    color: var(--el-text-color-secondary);
  }

  .st-actions {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    margin-left: auto;
  }
}

.copy-text {
  cursor: pointer;
  user-select: none;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;

  &:hover {
    text-decoration: underline;
  }
}

.st-name {
  color: var(--el-color-primary);
  font-weight: 600;
}

.st-comment {
  color: var(--el-text-color-secondary);
}

.quick-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
}

.quick-btn {
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  }
}

.token-text {
  cursor: pointer;
  user-select: none;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 12px;
  color: var(--el-text-color-regular);
  padding: 1px 4px;
  border-radius: 3px;

  &:hover {
    color: var(--el-color-primary);
    background: var(--el-color-primary-light-9);
  }
}

.token-text.table-token {
  color: var(--el-color-primary);
  font-weight: 600;
}

.token-row-wrap {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.token-area {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  flex: 1;
  overflow: hidden;
  transition: max-height 0.2s ease;
}

.token-toggle {
  flex-shrink: 0;
}

.result-pagination {
  display: flex;
  justify-content: flex-end;
}

.copyable-cell {
  cursor: pointer;
}

.history-card {
  :deep(.el-card__body) {
    padding: 10px;
  }
}

.history-list {
  max-height: 160px;
  overflow-y: auto;
}

.history-empty {
  padding: 12px 8px;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

// SQL 头部：去掉相邻按钮默认左边距，间距只由 flex gap 控制（修复执行/清空间距过大）
.sql-editor-header {
  :deep(.el-button + .el-button) {
    margin-left: 0;
  }
}

// 查询/写操作单选：选中态颜色区分（查询=蓝，写操作=橙）
.mode-radio {
  :deep(.el-radio-button:nth-child(1) .el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background-color: #409eff;
    border-color: #409eff;
    color: #fff;
  }

  :deep(.el-radio-button:nth-child(2) .el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background-color: #e6a23c;
    border-color: #e6a23c;
    color: #fff;
  }
}

// 最近查询头部：标题左、筛选框居中、清空全部右（三者分离，避免误解为组合）
.history-header {
  display: flex;
  align-items: center;
  gap: 12px;

  .history-title {
    flex: none;
  }

  .history-filter {
    flex: 1;
    display: flex;
    justify-content: center;
  }

  .history-clear {
    flex: none;
  }
}

.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;

  &:hover {
    background: var(--el-fill-color-light);
  }
}

.history-index {
  flex: none;
  width: 20px;
  text-align: right;
  color: var(--el-text-color-secondary);
  font-size: 11px;
}

.history-sql {
  flex: 1;
  min-width: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-status {
  flex: none;
}

.history-time {
  flex: none;
  color: var(--el-text-color-secondary);
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
