<template>
  <div style="padding: 0px 0px 0px 15px;">
    <!-- 字典数据-管理按钮 -->
    <div style="margin-bottom: 10px">
      <el-input v-model="searchData.dictLabel" size="small" style="width: 160px;margin-right: 10px;"
                class="filter-item" placeholder="输入数据标签/值查询"/>
      <el-select v-model="searchData.status" size="small" style="width: 80px;margin-right: 10px;"
                 class="filter-item" placeholder="状态">
        <el-option key="2" label="全部" value=""/>
        <el-option key="0" label="停用" value="0"/>
        <el-option key="1" label="正常" value="1"/>
      </el-select>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">重置
      </el-button>
      <el-button v-waves class="filter-item" type="danger" size="small" style="float: right;width: 156px;"
                 @click="clearDictCache(currentDictType.typeCode)" icon="el-icon-refresh-right">更新当前字典缓存
      </el-button>
    </div>
    <div style="margin-bottom: 10px;">
      <el-tag size="small" style="min-width: 160px;margin-right: 10px;height: 33px;line-height: 33px;">
        {{ '当前字典：' + currentDictType.typeName }}
      </el-tag>
      <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                 v-permission="'dictData-zDictData-add'">新增
      </el-button>
      <el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate(null)" size="small"
                 v-permission="'dictData-zDictData-update'">修改
      </el-button>
      <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                 v-permission="'dictData-zDictData-delete'">删除
      </el-button>
      <div style="float: right;">
        <el-button v-waves v-permission="'dictData-zDictData-importExcel'" @click="dialogImportVisible=true"
                   type="primary" size="small" icon="el-icon-upload2">导入
        </el-button>
        <el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel" size="small"
                   v-permission="'dictData-zDictData-exportExcel'">导出
        </el-button>
      </div>
    </div>
    <!-- 字典数据-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border :height="this.$windowHeight-270" v-loading="isLoading"
              @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="数据标签" prop="dictLabel" align="center"/>
      <el-table-column label="数据值" prop="dictValue" align="center" show-overflow-tooltip/>
      <el-table-column label="状态" prop="status" align="center" width="60">
        <template v-slot="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" size="mini">
            {{ scope.row.status === '1' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="顺序" prop="orderIndex" align="center" width="60"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;"
                     size="mini" @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'dictData-zDictData-update'"
                     type="text" size="mini" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'dictData-zDictData-delete'" style="color: #ff6d6d;"
                     type="text" size="mini" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 字典数据-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="resetTemp" width="600px">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
        <el-form-item label="当前字典">
          <el-tag size="small" style="width: 100%;height: 60px;line-height: 30px;">
            {{ '字典名称：' + currentDictType.typeName }}<br/>
            {{ '字典Code：' + currentDictType.typeCode }}
          </el-tag>
        </el-form-item>
        <el-form-item label="数据标签" prop="dictLabel"
                      :rules="[{required: true, message: '字典数据标签必填'}]">
          <el-input v-model="temp.dictLabel" placeholder="请输入字典数据标签"/>
        </el-form-item>
        <el-form-item label="数据值" prop="dictValue"
                      :rules="[{required: true, message: '字典数据值必填'}]">
          <el-input v-model="temp.dictValue" placeholder="请输入字典数据值"/>
        </el-form-item>

        <el-form-item label="状态" prop="status"
                      :rules="[{required: true, message: '字典数据状态必填'}]">
          <el-switch v-model="temp.status" active-text="正常" inactive-text="停用"
                     active-value="1" inactive-value="0">
          </el-switch>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog title="批量导入" :close-on-click-modal="false" :visible.sync="dialogImportVisible"
               @close="dialogIndex++" width="600px" :key="'importDialog'+dialogIndex">
      <el-form ref="importForm" label-width="120px" v-loading="isImportLoading">
        <el-form-item label="下载模板：">
          <el-button v-waves type="success" plain @click="downloadExcelTemplate"
                     icon="el-icon-download" size="small">下载Excel模板
          </el-button>
        </el-form-item>
        <el-divider></el-divider>
        <el-form-item label="导入：">
          <el-upload v-permission="'dictData-zDictData-importExcel'"
                     :action="$baseServer+'/dictData/zDictData/import/excel'"
                     :headers="$store.getters.headerToken" :data="{typeCode:currentDictType.typeCode}"
                     :before-upload="beforeImportUpload" :on-error="importExcelError"
                     :on-success="importExcelSuccess" accept=".xls,.xlsx"
                     :show-file-list="false" :auto-upload="true">
            <el-button v-waves type="primary" plain icon="el-icon-upload2" size="small">点击上传Excel并导入</el-button>
          </el-upload>
          <el-tag type="info" size="small">
            说明：点击上方按钮上传Excel文件，上传成功后会自动开始导入！
          </el-tag>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves @click="dialogImportVisible=false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'
import {clearDictCache} from "@/api/dicts";
import downloadUtil from '@/utils/download-util';

export default {
  name: 'DictData',
  props: {'currentDictType': Object},
  directives: {waves},
  data() {
    return {
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加字典数据', update: '修改字典数据', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      isLoading: false,
      dialogIndex: 0,
      // 导入弹窗
      dialogImportVisible: false,
      isImportLoading: false,
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  watch: {
    'currentDictType.typeCode'(val) {
      this.loadTableList()
      this.resetTemp()
    }
  },
  methods: {
    clearDictCache,
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.loadTableList()
    },
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      this.searchData.typeCode = this.currentDictType.typeCode
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({url: '/dictData/zDictData/list', method: 'get', params}).then((response) => {
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
      this.temp = {orderIndex: 0, status: '1'}
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
          var data = {...this.temp}
          // 字典code
          data.typeCode = this.currentDictType.typeCode
          if (this.dialogType === 'update') {
            request({
              url: '/dictData/zDictData/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              // 更新当前字典缓存
              this.clearDictCache(this.currentDictType.typeCode)
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/dictData/zDictData/add', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '添加成功！'})
              // 更新当前字典缓存
              this.clearDictCache(this.currentDictType.typeCode)
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
          const data = this.tableSelectRows.map(r => r.dictId)
          request({url: '/dictData/zDictData/delete', method: 'post', data}).then(() => {
            this.$message({type: 'success', message: '删除成功！'})
            // 更新当前字典缓存
            this.clearDictCache(this.currentDictType.typeCode)
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)};
      request({
        url: '/dictData/zDictData/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '字典数据.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    },
    // 导入Excel成功，提示
    importExcelSuccess(response) {
      this.isImportLoading = false
      if (response.message === 'Success') {
        this.$message({type: 'success', message: '导入成功！'})
        this.dialogImportVisible = false
        this.loadTableList()
      } else {
        this.$alert(response.message, "提示",
            {confirmButtonText: "确定", dangerouslyUseHTMLString: true, customClass: 'width800'});
      }
    },
    // 导入Excel之前，显示loading
    beforeImportUpload(file) {
      this.isImportLoading = true
    },
    // 导入Excel失败，取消loading状态
    importExcelError() {
      this.isImportLoading = false
    },
    // 下载模板
    downloadExcelTemplate() {
      downloadUtil.download('/dictData/zDictData/import/downloadTemplate', {}, '字典数据-导入模板.xlsx')
    },
  }
}
</script>
