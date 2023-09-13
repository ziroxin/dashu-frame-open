<template>
  <div class="app-container">
    <el-row>
      <el-col :span="8">
        <!-- 字典类型 -->
        <div style="margin-bottom: 10px;">
          <el-input v-model="searchData.typeName" size="small" style="width: 160px;margin-right: 10px;"
                    class="filter-item" placeholder="输入名称或code查询"/>
          <el-button v-waves class="filter-item" type="primary" size="small"
                     icon="el-icon-search" @click="searchBtnHandle">
          </el-button>
          <el-button v-waves class="filter-item" type="info" size="small"
                     icon="el-icon-refresh" @click="resetTableList">重置
          </el-button>
        </div>
        <div style="margin-bottom: 10px">
          <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                     v-permission="'dictType-zDictType-add'">新增
          </el-button>
          <el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate(null)" size="small"
                     v-permission="'dictType-zDictType-update'">修改
          </el-button>
          <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                     v-permission="'dictType-zDictType-delete'">删除
          </el-button>
          <el-button v-waves size="small" icon="el-icon-document"
                     @click="dialogDictDataVisible=true">示例
          </el-button>
        </div>
        <!-- 字典类型-列表 -->
        <el-table :data="tableData" stripe border :height="this.$windowHeight-270"
                  highlight-current-row
                  @selection-change="handleTableSelectChange">
          <el-table-column type="selection" width="50" align="center" header-align="center"/>
          <el-table-column label="字典类型" min-width="50%" prop="typeName">
            <template v-slot="scope">
              <div>{{ scope.row.typeName }}</div>
              <div>
                {{ scope.row.typeCode }}
                <el-button type="text" size="small" style="color: #00b42a;"
                           v-clipboard:copy="scope.row.typeCode">
                  <i class="el-icon-document-copy"/>复制
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="60px" prop="status" align="center">
            <template v-slot="scope">
              <el-tag type="success" v-if="scope.row.status === '1'" size="mini">正常</el-tag>
              <el-tag type="danger" v-else size="mini">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="50px" align="center">
            <template v-slot="scope">
              <el-button type="text" size="small" @click="openDictData(scope.row)" style="line-height: 14px;">
                字典<br/>数据
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 字典类型-分页 -->
        <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                       :page-size="pager.limit" :current-page="pager.page"
                       :total="pager.totalCount" @current-change="handleCurrentChange"
                       @size-change="handleSizeChange"
        />
        <!-- 添加修改弹窗 -->
        <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
                   :visible.sync="dialogFormVisible" @close="resetTemp" width="600px">
          <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
                   :disabled="dialogType==='view'">
            <el-form-item label="字典名称" prop="typeName"
                          :rules="[{required: true, message: '字典名称必填'}]">
              <el-input v-model="temp.typeName" placeholder="请输入字典名称"/>
            </el-form-item>
            <el-form-item label="字典code" prop="typeCode"
                          :rules="[{required: true, message: '字典code必填'}]">
              <el-input v-model="temp.typeCode" placeholder="请输入字典类型code"/>
            </el-form-item>
            <el-form-item label="状态" prop="status"
                          :rules="[{required: true, message: '字典状态必填'}]">
              <el-switch v-model="temp.status" active-text="正常" inactive-text="停用"
                         active-value="1" inactive-value="0">
              </el-switch>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
            <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
          </div>
        </el-dialog>
      </el-col>
      <el-col :span="16">
        <!-- 字典数据 -->
        <div v-if="!currentDictType.typeId">
          <el-empty description="请点击左侧字典类型表中的 [ 字典数据 ] 按钮"/>
          <div style="text-align: center;">
            <el-button v-waves class="filter-item" type="danger" size="small"
                       @click="clearDictCache(null)" icon="el-icon-refresh-right">更新全部字典缓存
            </el-button>
          </div>
        </div>
        <dict-data v-else :current-dict-type="currentDictType"></dict-data>
      </el-col>
    </el-row>
    <el-dialog title="字典数据Demo" :visible.sync="dialogDictDataVisible" width="400px">
      <dict-data-demo></dict-data-demo>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'
import DictData from "@/views/system/dictData/index.vue";
import {clearDictCache} from "@/api/dicts";
import DictDataDemo from "@/views/system/dictData/dictDataDemo.vue";

export default {
  components: {DictDataDemo, DictData},
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
      titleMap: {add: '添加字典类型', update: '修改字典类型', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      // 当前字典项
      currentDictType: {},
      // 数据字典demo弹窗显示隐藏
      dialogDictDataVisible: false
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  methods: {
    clearDictCache,
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.loadTableList()
    },
    // 显示全部
    resetTableList() {
      this.pager.page = 1
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/dictType/zDictType/list', method: 'get', params
      }).then((response) => {
        const {data} = response
        this.pager.totalCount = data.total
        this.tableData = data.records
      })
    },
    // 打开字典数据
    openDictData(row) {
      this.currentDictType = row
    },
    openDictDataDemo(row) {
      this.currentDictType = row
      this.dialogDictDataVisible = true
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
      this.temp = {status: '1'}
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
        this.tableSelectRows = [row]
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
          if (this.dialogType === 'update') {
            request({
              url: '/dictType/zDictType/update', method: 'put', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/dictType/zDictType/add', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '添加成功！'})
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
        this.tableSelectRows = [row]
      }
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.typeId)
          request({
            url: '/dictType/zDictType/delete', method: 'delete', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)};
      request({
        url: '/dictType/zDictType/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '字典类型.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    },
    // 导入Excel成功，提示
    importExcelSuccess(response) {
      if (response.code === '200') {
        this.$message({type: 'success', message: '导入成功！'})
        this.loadTableList()
      } else {
        this.$message({type: 'error', message: response.message})
      }
    },
  }
}
</script>
