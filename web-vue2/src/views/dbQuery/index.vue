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
              :filter-node-method="filterNode"
              @node-click="handleNodeClick"
              :expand-on-click-node="false"
              class="db-tree"
            >
              <span class="tree-node" slot-scope="{ node, data }">
                <i v-if="data.type === 'database'" class="el-icon-coin" style="margin-right: 5px"></i>
                <i v-else-if="data.type === 'table'" class="el-icon-grid" style="margin-right: 5px"></i>
                <i v-else class="el-icon-document" style="margin-right: 5px"></i>
                <span>{{ node.label }}</span>
                <el-tag v-if="data.comment" size="mini" type="info" style="margin-left: 5px">
                  {{ data.comment }}
                </el-tag>
              </span>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：SQL 编辑器 + 结果 -->
      <el-col :span="18" style="height: 100%; display: flex; flex-direction: column">
        <!-- SQL 编辑器区域 -->
        <el-card class="sql-card">
          <div slot="header" class="clearfix">
            <span><i class="el-icon-edit"></i> SQL 查询</span>
            <div style="float: right">
              <el-select v-model="currentDatabase" placeholder="选择数据库" size="mini" style="width: 140px; margin-right: 10px">
                <el-option v-for="db in databases" :key="db.databaseName" :label="db.databaseName" :value="db.databaseName"/>
              </el-select>
              <el-radio-group v-if="canWrite" v-model="mode" size="mini" style="margin-right: 10px">
                <el-radio-button label="query">查询</el-radio-button>
                <el-radio-button label="write">写操作</el-radio-button>
              </el-radio-group>
              <el-input-number v-if="mode === 'query'" v-model="maxRows" :min="1" :max="10000" size="mini" placeholder="最大行数" style="width: 110px; margin-right: 10px"/>
              <el-button :type="mode === 'write' ? 'warning' : 'primary'" size="mini" @click="runMain" :loading="executing" icon="el-icon-caret-right">
                {{ mode === 'write' ? '预览变更 (Ctrl+Enter)' : '执行 (Ctrl+Enter)' }}
              </el-button>
              <el-button size="mini" @click="clearEditor" icon="el-icon-delete">清空</el-button>
              <el-button v-if="mode === 'query'" type="success" size="mini" @click="exportCsv" :disabled="!lastResult" icon="el-icon-download">
                导出CSV
              </el-button>
            </div>
          </div>

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
            @keydown.ctrl.enter.native="runMain"
          />

          <!-- 快捷操作 -->
          <div class="quick-actions" style="margin-top: 10px">
            <el-tag
              v-for="item in quickActions"
              :key="item.label"
              style="margin-right: 5px; margin-bottom: 5px; cursor: pointer"
              @click="insertSql(item.sql)"
            >
              {{ item.label }}
            </el-tag>
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
            <el-table :data="lastResult.rows" border stripe size="mini" class="result-table">
              <el-table-column
                v-for="col in lastResult.columns"
                :key="col"
                :prop="col"
                :label="col"
                show-overflow-tooltip
                min-width="150"
              >
                <template slot-scope="{ row }">
                  {{ formatCellValue(row[col]) }}
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 空状态 -->
          <div v-else-if="!executing" style="text-align: center; padding: 60px 0; color: #909399">
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
      maxRows: 1000,
      databases: [],
      treeData: [],
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
      treeProps: {
        children: 'children',
        label: 'label'
      },
      quickActions: [
        { label: '查看所有表', sql: 'SHOW TABLES' },
        { label: '查看建表语句', sql: 'SHOW CREATE TABLE ' },
        { label: '查看表结构', sql: 'DESC ' },
        { label: '查看索引', sql: 'SHOW INDEX FROM ' },
        { label: '查看数据库', sql: 'SHOW DATABASES' },
        { label: '查看版本', sql: 'SELECT VERSION()' },
        { label: '查看当前时间', sql: 'SELECT NOW()' },
        { label: '查看进程', sql: 'SHOW PROCESSLIST' }
      ]
    }
  },
  computed: {
    canWrite() {
      return !this.featureDisabled && this.writeEnabled && this.developer
    }
  },
  watch: {
    filterText(val) {
      this.$refs.treeRef.filter(val)
    }
  },
  mounted() {
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
    // 主按钮分发：查询模式执行查询，写模式先预览
    runMain() {
      if (this.mode === 'write') {
        this.doPreviewWrite()
      } else {
        this.executeQuery()
      }
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
    // 点击树节点
    async handleNodeClick(data) {
      if (data.type === 'database') {
        this.currentDatabase = data.databaseName
        if (!data.children || data.children.length === 0) {
          await this.loadTables(data.databaseName)
        }
      } else if (data.type === 'table') {
        // 查看表结构
        this.showTableStructure(data.databaseName, data.tableName)
      }
    },
    // 显示表结构
    async showTableStructure(databaseName, tableName) {
      try {
        const res = await getTableStructureApi(databaseName, tableName)
        this.currentTableStructure = res.data
        this.showStructureDialog = true
      } catch (e) {
        this.$message.error('获取表结构失败')
      }
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

        if (!res.data.success) {
          this.$message.error(res.data.errorMessage || '查询失败')
        }
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.executing = false
      }
    },
    // 清空编辑器
    clearEditor() {
      this.sqlText = ''
      this.lastResult = null
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
        const res = await exportToCsvApi({
          sql: this.sqlText,
          databaseName: this.currentDatabase,
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
