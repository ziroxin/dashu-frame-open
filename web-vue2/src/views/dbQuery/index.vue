<template>
  <div class="app-container db-query-container">
    <!-- 功能关闭提示 -->
    <div v-if="featureDisabled" class="feature-disabled">
      <i class="el-icon-warning" style="font-size: 48px; color: #e6a23c"></i>
      <h3>功能已关闭</h3>
      <p>数据库查询工具当前已关闭，如需使用请联系管理员开启。</p>
    </div>

    <el-row v-else :gutter="16" style="height: 100%">
      <!-- 左侧：数据库/表结构树 -->
      <el-col :span="6" style="height: 100%">
        <el-card class="tree-card" style="height: 100%">
          <div slot="header" class="clearfix">
            <span><i class="el-icon-coin"></i> 数据库</span>
            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="loadDatabases" :loading="treeLoading"></el-button>
          </div>

          <el-input v-model="filterText" placeholder="搜索表名..." size="mini" clearable style="margin-bottom: 10px"/>

          <div style="height: calc(100% - 90px); overflow-y: auto">
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
              <el-tooltip slot-scope="{ data }" placement="right" :open-delay="200">
                <div slot="content">
                  <div>{{ data.label }}</div>
                  <div v-if="data.comment">{{ data.comment }}</div>
                </div>
                <span class="tree-node">
                  <i v-if="data.type === 'database'" class="el-icon-coin" style="margin-right: 5px"></i>
                  <i v-else-if="data.type === 'table'" class="el-icon-grid" style="margin-right: 5px"></i>
                  <i v-else class="el-icon-document" style="margin-right: 5px"></i>
                  <span class="tree-label">{{ data.label }}</span>
                </span>
              </el-tooltip>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：SQL 编辑器 + 结果 -->
      <el-col :span="18" style="height: 100%; display: flex; flex-direction: column">
        <!-- SQL 编辑器区域 -->
        <el-card class="sql-card">
          <div slot="header" class="clearfix">
            <div style="float: left">
              <el-select v-model="currentDatabase" placeholder="选择数据库" size="mini" style="width: 160px; margin-right: 10px">
                <el-option v-for="db in databases" :key="db.databaseName" :label="db.databaseName" :value="db.databaseName"/>
              </el-select>
              <el-radio-group v-if="canWrite" v-model="mode" size="mini">
                <el-radio-button label="query">查询</el-radio-button>
                <el-radio-button label="write">写操作</el-radio-button>
              </el-radio-group>
            </div>
            <div style="float: right">
              <el-button :type="mode === 'write' ? 'warning' : 'primary'" size="mini" @click="runMain" :loading="executing" icon="el-icon-caret-right">
                {{ mode === 'write' ? '预览变更 (Ctrl+Enter)' : '执行 (Ctrl+Enter)' }}
              </el-button>
              <el-button size="mini" @click="clearEditor" icon="el-icon-delete">清空</el-button>
            </div>
          </div>

          <!-- 已选表信息栏：位于输入框上方，与快捷操作分离 -->
          <div v-if="selectedTable" class="selected-table-bar" style="margin-bottom: 10px">
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
              <el-button size="mini" type="primary" plain @click="openStructureDialog" :loading="structureLoading">表结构</el-button>
              <el-button size="mini" type="text" @click="clearTableSelect">取消选择</el-button>
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
              @keydown.ctrl.enter.native="runMain"
            />
            <span v-if="sqlText" class="sql-clear" title="清空" @click="sqlText = ''">
              <i class="el-icon-circle-close"></i>
            </span>
          </div>

          <!-- 快捷操作 -->
          <div class="quick-actions" style="margin-top: 10px">
            <!-- 第一行：选中表的快捷查询（红色）+ 基础查询（队尾） -->
            <div class="quick-row">
              <template v-if="selectedTable">
                <el-tag class="quick-btn" type="danger" effect="dark" @click.native="fillTop10">查询前10条</el-tag>
                <el-tag class="quick-btn" type="danger" effect="dark" @click.native="fillAllColumns">查询所有字段</el-tag>
                <el-tag class="quick-btn" type="danger" effect="dark" @click.native="fillCount">查询总行数</el-tag>
              </template>
              <el-tag
                v-for="item in baseActions"
                :key="item.label"
                class="quick-btn"
                @click.native="insertSql(item.sql)"
              >
                {{ item.label }}
              </el-tag>
            </div>

            <!-- 第二行：表名 / 字段名 快捷填充（文本样式，默认折叠一行，可展开收起） -->
            <div v-if="selectedTable && tableColumns.length" class="token-row-wrap" style="margin-top: 6px">
              <div
                ref="tokenArea"
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
              <el-button v-if="tokensOverflow" type="text" size="mini" class="token-toggle" @click="tokensExpanded = !tokensExpanded">
                <i :class="tokensExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" style="margin-right: 3px"></i>{{ tokensExpanded ? '收起' : '展开' }}
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 最近查询历史 -->
        <el-card v-if="queryHistory.length" class="history-card" style="margin-top: 16px">
          <div slot="header" class="history-header">
            <span class="history-title"><i class="el-icon-time"></i> 最近查询 · {{ currentDatabase }}</span>
            <div class="history-filter">
              <el-input v-model="historyFilter" placeholder="筛选查询历史..." size="mini" clearable style="width: 200px"/>
            </div>
            <el-button type="text" icon="el-icon-delete" class="history-clear" @click="confirmClearHistory">清空全部</el-button>
          </div>
          <div class="history-list">
            <div
              v-for="(h, i) in filteredHistory"
              :key="i"
              class="history-item"
              :title="h.sql"
              @click="useHistory(h.sql)"
            >
              <span class="history-index">{{ i + 1 }}</span>
              <el-tag :type="h.success ? 'success' : 'danger'" size="mini" class="history-status">{{ h.success ? '成功' : '失败' }}</el-tag>
              <span class="history-sql">{{ h.sql }}</span>
              <span class="history-time">{{ formatHistoryTime(h.ts) }}</span>
            </div>
            <div v-if="!filteredHistory.length" class="history-empty">无匹配的查询历史</div>
          </div>
        </el-card>

        <!-- 查询结果区域 -->
        <el-card class="result-card" style="flex: 1; margin-top: 16px">
          <div slot="header" class="clearfix">
            <span>
              <i class="el-icon-document"></i> 查询结果
              <el-tag v-if="lastResult" type="info" size="mini" style="margin-left: 10px">
                {{ lastResult.totalRows }} 行
              </el-tag>
              <el-tag v-if="lastResult && lastResult.truncated" type="warning" size="mini" style="margin-left: 5px">
                已截断
              </el-tag>
              <el-tag v-if="lastResult && lastResult.executeTime" type="success" size="mini" style="margin-left: 5px">
                {{ lastResult.executeTime }}ms
              </el-tag>
            </span>
            <el-button
              v-if="lastResult && lastResult.success && lastResult.columns && lastResult.columns.length > 0"
              type="success"
              size="mini"
              icon="el-icon-download"
              style="float: right"
              @click="exportCsv"
            >导出CSV</el-button>
          </div>

          <!-- 错误提示 -->
          <el-alert
            v-if="lastResult && !lastResult.success"
            :title="lastResult.errorMessage"
            type="error"
            show-icon
            closable
            style="margin-bottom: 10px"
          />

          <!-- 结果表格 -->
          <div v-if="lastResult && lastResult.success && lastResult.columns && lastResult.columns.length > 0" style="max-height: 400px; overflow: auto">
            <el-table :data="pagedRows" border stripe size="mini" class="result-table" @cell-click="handleCellCopy">
              <el-table-column
                v-for="col in lastResult.columns"
                :key="col"
                :prop="col"
                :label="col"
                show-overflow-tooltip
                min-width="150"
              >
                <template slot-scope="{ row }">
                  <span class="copyable-cell">{{ formatCellValue(row[col]) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 分页 -->
          <div v-if="lastResult && lastResult.success && resultTotal > resultPageSize" class="result-pagination" style="margin-top: 10px">
            <el-pagination
              :current-page="resultPage"
              :page-size="resultPageSize"
              :page-sizes="[50, 100, 200, 500]"
              :total="resultTotal"
              layout="total, sizes, prev, pager, next, jumper"
              background
              size="mini"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>

          <!-- 空状态 -->
          <div v-else-if="!executing && !(lastResult && lastResult.success && lastResult.columns && lastResult.columns.length > 0)" style="text-align: center; padding: 60px 0; color: #909399">
            <i class="el-icon-info" style="font-size: 48px"></i>
            <p style="margin-top: 10px">执行 SQL 查询查看结果</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表结构详情弹窗 -->
    <el-dialog title="表结构" :visible.sync="showStructureDialog" width="800px">
      <el-tabs v-model="structureTab">
        <el-tab-pane label="列信息" name="columns">
          <el-table :data="currentTableStructure ? currentTableStructure.columns : []" border size="mini">
            <el-table-column prop="columnName" label="列名" width="150"/>
            <el-table-column prop="columnType" label="类型" width="120"/>
            <el-table-column prop="nullable" label="可空" width="60" align="center">
              <template slot-scope="{ row }">
                <el-tag :type="row.nullable ? 'success' : 'danger'" size="mini">
                  {{ row.nullable ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="defaultValue" label="默认值" width="100"/>
            <el-table-column prop="primaryKey" label="主键" width="60" align="center">
              <template slot-scope="{ row }">
                <i v-if="row.primaryKey" class="el-icon-key" style="color: #409eff"></i>
              </template>
            </el-table-column>
            <el-table-column prop="autoIncrement" label="自增" width="60" align="center">
              <template slot-scope="{ row }">
                <el-tag v-if="row.autoIncrement" type="warning" size="mini">AI</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="columnComment" label="注释"/>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="索引信息" name="indexes">
          <el-table :data="currentTableStructure ? currentTableStructure.indexes : []" border size="mini">
            <el-table-column prop="indexName" label="索引名" width="200"/>
            <el-table-column prop="indexType" label="类型" width="100"/>
            <el-table-column prop="nonUnique" label="唯一" width="80" align="center">
              <template slot-scope="{ row }">
                <el-tag :type="!row.nonUnique ? 'success' : 'info'" size="mini">
                  {{ !row.nonUnique ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="列">
              <template slot-scope="{ row }">
                <el-tag v-for="col in row.columns" :key="col" size="mini" style="margin-right: 5px">
                  {{ col }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 写操作预览确认弹窗 -->
    <el-dialog title="写操作预览确认" :visible.sync="showPreviewDialog" width="900px" :close-on-click-modal="false">
      <el-alert
        v-if="previewData && previewData.success"
        :title="previewData.message"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 10px"
      />
      <el-descriptions v-if="previewData" :column="3" border size="small" style="margin-bottom: 10px">
        <el-descriptions-item label="语句类型">{{ previewData.sqlType }}</el-descriptions-item>
        <el-descriptions-item label="目标表">{{ previewData.table }}</el-descriptions-item>
        <el-descriptions-item label="受影响行数">
          <span style="color: #e6a23c; font-weight: 600">
            {{ previewData.affectedRows === null || previewData.affectedRows === undefined ? '（插入语句不统计）' : previewData.affectedRows }}
          </span>
        </el-descriptions-item>
      </el-descriptions>

      <div class="sql-preview-text" style="margin-bottom: 10px">{{ sqlText }}</div>

      <div v-if="previewData && previewData.rows && previewData.rows.length" style="margin-bottom: 5px; font-weight: 600">
        原始数据样本（最多 {{ previewData.rows.length }} 行）：
      </div>
      <div v-if="previewData && previewData.rows && previewData.rows.length" style="max-height: 320px; overflow: auto">
        <el-table :data="previewData.rows" border stripe size="mini">
          <el-table-column
            v-for="col in previewData.columns"
            :key="col"
            :prop="col"
            :label="col"
            show-overflow-tooltip
            min-width="140"
          >
            <template slot-scope="{ row }">{{ formatCellValue(row[col]) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="showPreviewDialog = false">取消</el-button>
        <el-button type="danger" size="mini" :loading="executing" @click="confirmExecute">确认执行</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
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

export default {
  name: 'DbQuery',
  data() {
    return {
      treeLoading: false,
      executing: false,
      filterText: '',
      sqlText: '',
      currentDatabase: '',
      maxRows: 100,
      databases: [],
      treeData: [],
      defaultExpandedKeys: [],
      lastResult: null,
      showStructureDialog: false,
      currentTableStructure: null,
      structureTab: 'columns',
      featureDisabled: false,
      writeEnabled: false,
      developer: false,
      mode: 'query',
      showPreviewDialog: false,
      previewData: null,
      currentNodeKey: '',
      selectedTable: null,
      tokensExpanded: false,
      tokenOneRowH: 32,
      tokensOverflow: false,
      structureCache: {},
      structureLoading: false,
      resultPage: 1,
      resultPageSize: 100,
      historyMap: {},
      historyMax: 50,
      historyFilter: '',
      treeProps: {
        children: 'children',
        label: 'label'
      },
      baseActions: [
        { label: '查询所有表', sql: 'SHOW TABLES' },
        { label: '查看版本', sql: 'SELECT VERSION()' },
        { label: '查看进程', sql: 'SHOW PROCESSLIST' }
      ]
    }
  },
  computed: {
    canWrite() {
      return !this.featureDisabled && this.writeEnabled && this.developer
    },
    tableColumns() {
      const cols = this.currentTableStructure && this.currentTableStructure.columns
      return Array.isArray(cols) ? cols.map(c => c.columnName) : []
    },
    resultTotal() {
      return this.lastResult && this.lastResult.rows ? this.lastResult.rows.length : 0
    },
    pagedRows() {
      const rows = (this.lastResult && this.lastResult.rows) || []
      const start = (this.resultPage - 1) * this.resultPageSize
      return rows.slice(start, start + this.resultPageSize)
    },
    queryHistory() {
      return this.historyMap[this.currentDatabase] || []
    },
    filteredHistory() {
      const kw = (this.historyFilter || '').trim().toLowerCase()
      if (!kw) return this.queryHistory
      return this.queryHistory.filter(h => h.sql.toLowerCase().includes(kw))
    }
  },
  watch: {
    filterText(val) {
      this.$refs.treeRef.filter(val)
    },
    // 字段列表变化时：折叠并重新测量单行高度
    tableColumns() {
      this.tokensExpanded = false
      this.$nextTick(() => this.measureTokenLine())
    }
  },
  mounted() {
    this.loadHistory()
    this.loadStatus()
    // 监听键盘快捷键
    document.addEventListener('keydown', this.handleKeydown)
  },
  beforeDestroy() {
    document.removeEventListener('keydown', this.handleKeydown)
  },
  methods: {
    // 键盘事件
    handleKeydown(e) {
      if (e.ctrlKey && e.key === 'Enter') {
        this.runMain()
      }
    },
    // 探测模块状态（接口 404 / enabled=false 视为功能已关闭）
    async loadStatus() {
      try {
        const res = await getStatusApi()
        const data = res.data || {}
        this.featureDisabled = data.enabled === false
        this.writeEnabled = data.writeEnabled === true
        this.developer = data.developer === true
      } catch (e) {
        // 模块未注入（关闭）时接口不存在
        this.featureDisabled = true
        return
      }
      if (!this.featureDisabled) {
        this.loadDatabases()
      }
    },
    // 主按钮分发：查询模式执行查询，写模式先预览（执行前先做前端模式校验，减少无效请求）
    runMain() {
      const check = this.validateByMode(this.sqlText, this.mode)
      if (!check.ok) {
        this.$message.warning(check.message || 'SQL 不符合当前模式')
        return
      }
      if (this.mode === 'write') {
        this.doPreviewWrite()
      } else {
        this.executeQuery()
      }
    },
    // 取语句首个关键词
    firstKeyword(sql) {
      const m = (sql || '').trim().toUpperCase().match(/^[A-Z_]+/)
      return m ? m[0] : ''
    },
    // 是否存在堆叠语句（去掉结尾分号后，中间仍有分号）
    hasStackedStatement(sql) {
      let body = (sql || '').trim()
      while (body.endsWith(';')) body = body.slice(0, -1).trim()
      return body.indexOf(';') >= 0
    },
    // 前端按模式预校验（非权威，后端仍会完整校验；仅拦截明显的模式不符，减少无效请求）
    validateByMode(sql, mode) {
      const READ = ['SELECT', 'SHOW', 'DESC', 'DESCRIBE', 'EXPLAIN']
      const WRITE = ['INSERT', 'UPDATE', 'DELETE']
      const trimmed = (sql || '').trim()
      if (!trimmed) return { ok: false, message: '请输入 SQL 语句' }
      const kw = this.firstKeyword(trimmed)
      if (this.hasStackedStatement(trimmed)) return { ok: false, message: '禁止多语句（堆叠）执行' }
      if (mode === 'query') {
        if (WRITE.indexOf(kw) >= 0) return { ok: false, message: `这是写操作(${kw})，请切换到"写操作"模式再执行` }
        if (READ.indexOf(kw) < 0) return { ok: false, message: `查询模式仅支持 ${READ.join('/')} 语句` }
      } else {
        if (READ.indexOf(kw) >= 0) return { ok: false, message: `这是查询语句(${kw})，请切换到"查询"模式再执行` }
        if (WRITE.indexOf(kw) < 0) return { ok: false, message: '写操作仅支持 INSERT/UPDATE/DELETE 语句' }
        if ((kw === 'UPDATE' || kw === 'DELETE') && !/\bWHERE\b/i.test(trimmed)) {
          return { ok: false, message: `${kw} 必须带 WHERE 条件` }
        }
      }
      return { ok: true }
    },
    // 预览写 SQL
    async doPreviewWrite() {
      if (!this.sqlText || !this.sqlText.trim()) {
        this.$message.warning('请输入 SQL 语句')
        return
      }
      this.executing = true
      try {
        const res = await previewWriteApi({
          sql: this.sqlText,
          databaseName: this.currentDatabase,
          maxBackupRows: 200
        })
        this.previewData = res.data
        if (res.data && res.data.success) {
          this.showPreviewDialog = true
        } else {
          this.$message.error((res.data && res.data.errorMessage) || '预览失败')
        }
      } catch (e) {
        this.$message.error(e.message || '预览失败')
      } finally {
        this.executing = false
      }
    },
    // 二次确认后执行写 SQL
    async confirmExecute() {
      if (!this.previewData || !this.previewData.token) {
        this.$message.error('预览已失效，请重新预览')
        return
      }
      try {
        await this.$confirm(
          '此操作将直接修改数据库数据，且不可撤销。请确认影响行数与原始数据无误后继续。',
          '危险操作确认',
          { type: 'warning', confirmButtonText: '我确认执行', cancelButtonText: '取消', confirmButtonClass: 'el-button--danger' }
        )
      } catch (e) {
        return
      }

      this.executing = true
      try {
        const res = await executeWriteApi({
          token: this.previewData.token,
          sql: this.sqlText,
          databaseName: this.currentDatabase
        })
        const data = res.data
        if (data && data.success) {
          this.$message.success(data.message || '执行成功')
          this.showPreviewDialog = false
          this.previewData = null
        } else {
          this.$message.error((data && data.errorMessage) || '执行失败')
        }
      } catch (e) {
        this.$message.error(e.message || '执行失败')
      } finally {
        this.executing = false
      }
    },
    // 加载数据库列表
    async loadDatabases() {
      this.treeLoading = true
      try {
        const res = await getDatabasesApi()
        this.databases = res.data || []

        // 构建树形数据
        this.treeData = this.databases.map(db => ({
          id: `db_${db.databaseName}`,
          label: db.databaseName,
          type: 'database',
          comment: `${db.tableCount || 0} 个表`,
          databaseName: db.databaseName,
          children: []
        }))

        // 默认选择第一个数据库
        if (this.databases.length > 0 && !this.currentDatabase) {
          this.currentDatabase = this.databases[0].databaseName
          this.defaultExpandedKeys = [`db_${this.currentDatabase}`]
          this.loadTables(this.currentDatabase)
        }
      } catch (e) {
        this.$message.error('加载数据库列表失败')
      } finally {
        this.treeLoading = false
      }
    },
    // 加载表列表
    async loadTables(databaseName) {
      try {
        const res = await getTablesApi(databaseName)
        const tables = res.data || []

        // 找到对应的数据库节点，添加表子节点
        const dbNode = this.treeData.find(db => db.databaseName === databaseName)
        if (dbNode) {
          dbNode.children = tables.map(table => ({
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
    },
    // 过滤节点
    filterNode(value, data) {
      if (!value) return true
      return data.label.toLowerCase().includes(value.toLowerCase())
    },
    // 点击树节点：库→切换并清空选表；表→单选并加载结构（不再自动弹窗）
    async handleNodeClick(data) {
      if (data.type === 'database') {
        this.currentDatabase = data.databaseName
        this.clearTableSelect()
        if (!data.children || data.children.length === 0) {
          await this.loadTables(data.databaseName)
        }
      } else if (data.type === 'table') {
        this.currentNodeKey = data.id
        this.selectedTable = { databaseName: data.databaseName, tableName: data.tableName, tableComment: data.comment }
        this.currentTableStructure = null
        await this.ensureStructure(data.databaseName, data.tableName)
      }
    },
    // 加载（带缓存）表结构，供"查询所有字段"与字段快捷填充使用
    async ensureStructure(databaseName, tableName) {
      const key = `${databaseName}.${tableName}`
      if (this.structureCache[key]) {
        this.currentTableStructure = this.structureCache[key]
        return this.structureCache[key]
      }
      this.structureLoading = true
      try {
        const res = await getTableStructureApi(databaseName, tableName)
        this.$set(this.structureCache, key, res.data)
        this.currentTableStructure = res.data
        return res.data
      } catch (e) {
        this.$message.error('获取表结构失败')
        return null
      } finally {
        this.structureLoading = false
      }
    },
    // 点击"表结构"按钮：弹窗展示
    async openStructureDialog() {
      if (!this.selectedTable) return
      const { databaseName, tableName } = this.selectedTable
      await this.ensureStructure(databaseName, tableName)
      if (this.currentTableStructure) {
        this.showStructureDialog = true
      }
    },
    // 取消表选择
    clearTableSelect() {
      this.selectedTable = null
      this.currentTableStructure = null
      this.currentNodeKey = ''
      if (this.$refs.treeRef && this.$refs.treeRef.setCurrentKey) {
        this.$refs.treeRef.setCurrentKey(null)
      }
    },
    // 查询前10条
    fillTop10() {
      if (!this.selectedTable) return
      this.sqlText = `SELECT * FROM \`${this.selectedTable.tableName}\` LIMIT 10`
      this.focusSqlInput()
    },
    // 查询所有字段
    fillAllColumns() {
      if (!this.selectedTable) return
      const cols = this.tableColumns
      const colList = cols.length ? cols.map(c => `\`${c}\``).join(', ') : '*'
      this.sqlText = `SELECT ${colList} FROM \`${this.selectedTable.tableName}\``
      this.focusSqlInput()
    },
    // 查询总行数
    fillCount() {
      if (!this.selectedTable) return
      this.sqlText = `SELECT COUNT(*) AS total FROM \`${this.selectedTable.tableName}\``
      this.focusSqlInput()
    },
    // 测量单行 token 高度（用于折叠态只显示一行），并判断是否溢出多行
    measureTokenLine() {
      const area = this.$refs.tokenArea
      const first = area && area.firstElementChild
      if (first && first.offsetHeight > 0) {
        this.tokenOneRowH = first.offsetHeight
      }
      if (area) {
        this.tokensOverflow = area.scrollHeight > this.tokenOneRowH + 2
      }
    },
    // 插入 token（表名/字段名），前后自动加空格
    insertToken(token) {
      const cur = this.sqlText || ''
      this.sqlText = `${cur} ${token} `
      this.focusSqlInput()
    },
    focusSqlInput() {
      const target = this.$refs.sqlInputRef && this.$refs.sqlInputRef.$el
        ? this.$refs.sqlInputRef.$el.querySelector('textarea')
        : null
      if (target) {
        target.focus()
        try { target.setSelectionRange(target.value.length, target.value.length) } catch (e) { /* noop */ }
      }
    },
    // ===== 最近查询历史（按数据库分开，每库最多 50 条；重复语句仅更新顺序；成功/失败都记录） =====
    loadHistory() {
      try {
        const raw = localStorage.getItem('db-query:history')
        const obj = raw ? JSON.parse(raw) : {}
        // 兼容旧数据（旧版为数组）：非对象或数组一律重置
        this.historyMap = obj && typeof obj === 'object' && !Array.isArray(obj) ? obj : {}
      } catch (e) {
        this.historyMap = {}
      }
    },
    persistHistory() {
      try {
        localStorage.setItem('db-query:history', JSON.stringify(this.historyMap))
      } catch (e) {
        // 忽略：隐私模式 / 配额限制
      }
    },
    addHistory(sql, success) {
      const s = (sql || '').trim()
      if (!s) return
      const db = this.currentDatabase
      const list = this.historyMap[db] ? this.historyMap[db].slice() : []
      const idx = list.findIndex(h => h.sql === s)
      if (idx !== -1) list.splice(idx, 1)
      list.unshift({ sql: s, ts: Date.now(), success })
      if (list.length > this.historyMax) list.length = this.historyMax
      this.$set(this.historyMap, db, list)
      this.persistHistory()
    },
    useHistory(sql) {
      this.sqlText = sql
      this.focusSqlInput()
    },
    clearHistory() {
      this.$set(this.historyMap, this.currentDatabase, [])
      this.persistHistory()
    },
    // 清空本库历史（二次确认）
    async confirmClearHistory() {
      try {
        await this.$confirm('确认清空当前库的最近查询历史？', '清空历史', {
          type: 'warning',
          confirmButtonText: '确认清空',
          cancelButtonText: '取消'
        })
      } catch (e) {
        return
      }
      this.clearHistory()
      this.historyFilter = ''
    },
    formatHistoryTime(ts) {
      const d = new Date(ts)
      const p = n => (n < 10 ? '0' + n : '' + n)
      return `${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
    },
    // 执行查询
    async executeQuery() {
      if (!this.sqlText || !this.sqlText.trim()) {
        this.$message.warning('请输入 SQL 语句')
        return
      }

      this.executing = true
      try {
        const res = await executeQueryApi({
          sql: this.sqlText,
          databaseName: this.currentDatabase,
          maxRows: this.maxRows,
          timeout: 30
        })
        this.lastResult = res.data
        this.resultPage = 1

        if (!res.data.success) {
          this.$message.error(res.data.errorMessage || '查询失败')
          this.addHistory(this.sqlText, false)
        } else {
          this.addHistory(this.sqlText, true)
        }
      } catch (e) {
        this.$message.error(e.message || '查询失败')
        this.addHistory(this.sqlText, false)
      } finally {
        this.executing = false
      }
    },
    // 清空编辑器
    clearEditor() {
      this.sqlText = ''
      this.lastResult = null
      this.resultPage = 1
    },
    // 插入 SQL
    insertSql(sql) {
      this.sqlText = sql
      this.$refs.sqlInputRef.focus()
    },
    // 导出 CSV
    async exportCsv() {
      if (!this.lastResult) return

      try {
        // request 拦截器对 blob 响应直接返回 Blob 本体，故此处 await 结果即为 Blob
        const blob = await exportToCsvApi({
          sql: this.sqlText,
          databaseName: this.currentDatabase,
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
            // 解析失败，使用默认提示
          }
          this.$message.error(msg)
          return
        }

        // 创建下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `export_${Date.now()}.csv`
        link.click()
        window.URL.revokeObjectURL(url)

        this.$message.success('导出成功')
      } catch (e) {
        this.$message.error('导出失败')
      }
    },
    // 格式化单元格值
    formatCellValue(value) {
      if (value === null || value === undefined) {
        return 'NULL'
      }
      if (typeof value === 'object') {
        return JSON.stringify(value)
      }
      return String(value)
    },
    // 分页：切换页码
    handlePageChange(page) {
      this.resultPage = page
    },
    // 分页：切换每页条数
    handleSizeChange(size) {
      this.resultPageSize = size
      this.resultPage = 1
    },
    // 点击结果单元格：复制其内容
    handleCellCopy(row, column) {
      if (!column || !column.property) return
      const val = this.formatCellValue(row[column.property])
      if (val === null || val === undefined || val === '') return
      this.copyText(val)
    },
    // 复制到剪贴板（生产多为 http，clipboard API 需安全上下文，故保留 execCommand 降级）
    copyText(text) {
      const tip = '已复制: ' + (text.length > 30 ? text.slice(0, 30) + '…' : text)
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text)
          .then(() => this.$message.success(tip))
          .catch(() => this.copyTextFallback(text, tip))
        return
      }
      this.copyTextFallback(text, tip)
    },
    copyTextFallback(text, tip) {
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
        ok ? this.$message.success(tip) : this.$message.error('复制失败')
      } catch (e) {
        this.$message.error('复制失败')
      }
    }
  }
}
</script>

<style scoped lang="scss">
.db-query-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tree-card {
  ::v-deep .el-card__body {
    height: calc(100% - 50px);
    padding: 10px;
  }
}

.sql-card {
  ::v-deep .el-card__body {
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
  cursor: pointer;
  color: #c0c4cc;

  &:hover {
    color: #909399;
  }
}

.sql-input {
  ::v-deep .el-textarea__inner {
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.6;
  }
}

.result-card {
  ::v-deep .el-card__body {
    padding: 15px;
  }
}

.result-table {
  ::v-deep .el-table__cell {
    padding: 8px 0;
  }
}

.db-tree {
  background: transparent;
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
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;

  .st-label {
    color: #909399;
  }

  .st-actions {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    margin-left: auto;
  }
}

.quick-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
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

.token-text {
  cursor: pointer;
  user-select: none;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 12px;
  color: #606266;
  padding: 1px 4px;
  border-radius: 3px;
}

.token-text:hover {
  color: #409eff;
  background: #ecf5ff;
}

.token-text.table-token {
  color: #409eff;
  font-weight: 600;
}

/* 当前表：文本式可复制 */
.copy-text {
  cursor: pointer;
  user-select: none;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.copy-text:hover {
  text-decoration: underline;
}

.st-name {
  color: #409eff;
  font-weight: 600;
}

.st-comment {
  color: #909399;
}

/* 查询/写操作单选：选中态颜色区分（查询=蓝，写操作=橙） */
.mode-radio {
  ::v-deep .el-radio-button:nth-child(1) .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background-color: #409eff;
    border-color: #409eff;
    color: #fff;
  }

  ::v-deep .el-radio-button:nth-child(2) .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background-color: #e6a23c;
    border-color: #e6a23c;
    color: #fff;
  }
}

.result-pagination {
  display: flex;
  justify-content: flex-end;
}

.copyable-cell {
  cursor: pointer;
}

.history-card {
  ::v-deep .el-card__body {
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
  color: #909399;
  font-size: 12px;
}

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
    background: #f5f7fa;
  }
}

.history-index {
  flex: none;
  width: 20px;
  text-align: right;
  color: #909399;
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
  color: #909399;
}

.quick-actions {
  .quick-btn {
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
    }
  }
}

.feature-disabled {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.sql-preview-text {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
