<template>
  <div class="app-container">
    <el-row>
      <el-col :span="9" class="b-r-1px b-r-dashed b-r-#eee pr-15px">
        <!-- 字典类型 -->
        <div class="searchPanel flex-wrap justify-left">
          <div class="searchForm w-100%">
            <el-input v-model="searchData.typeName" class="searchInput w-176px!" placeholder="名称或code" clearable/>
            <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询
            </base-button>
            <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
          </div>
          <div class="w-100% flex mb-10px">
            <base-button type="primary" icon="el-icon-plus" @click="openAdd"
                         v-permission="'dictType-zDictType-add'">新增
            </base-button>
            <base-button type="info" icon="el-icon-edit" @click="openUpdate(null)"
                         v-permission="'dictType-zDictType-update'">修改
            </base-button>
            <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)"
                         v-permission="'dictType-zDictType-delete'">删除
            </base-button>
            <base-button icon="el-icon-document" @click="dialogDictDataVisible=true">示例
            </base-button>
          </div>
        </div>
        <!-- 字典类型-列表 -->
        <el-table ref="dataTable" :data="tableData" stripe border highlight-current-row
                  @selection-change="handleTableSelectChange">
          <el-table-column type="selection" width="50" align="center" header-align="center"/>
          <el-table-column label="字典类型" min-width="50%" prop="typeName">
            <template #default="scope">
              <div class="text-12px">{{ scope.row.typeName }}</div>
              <div class="text-12px">
                {{ scope.row.typeCode }}
                <el-button link size="small" class="color-#00b42a!" icon="el-icon-document-copy"
                           v-clipboard:copy="scope.row.typeCode">复制
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="65px" prop="status" align="center">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status === '1'" size="small">正常</el-tag>
              <el-tag type="danger" v-else size="small">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="55px" align="center">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openDictData(scope.row)" class="lh-14px!">
                字典<br/>数据
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 字典类型-分页 -->
        <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes"
                       :page-size="pager.limit" :current-page="pager.page"
                       :total="pager.totalCount" @current-change="handleCurrentChange"
                       @size-change="handleSizeChange"/>
        <!-- 添加修改弹窗 -->
        <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
                   v-model="dialogFormVisible" @close="resetTemp" width="600px">
          <el-form ref="dataForm" :model="temp" label-width="100px" class="mt-10px" :disabled="dialogType==='view'">
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
                         active-value="1" inactive-value="0"/>
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="dialog-footer">
              <el-button type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
              <el-button @click="dialogFormVisible=false">取消</el-button>
            </div>
          </template>
        </el-dialog>
      </el-col>
      <el-col :span="15">
        <!-- 字典数据 -->
        <div v-if="!currentDictType.typeId" class="flex flex-col justify-center items-center">
          <base-button class="mt-30px" type="danger" @click="clearDictCache(null)"
                       icon="el-icon-refresh-right">更新字典缓存
          </base-button>
          <el-divider class="w-95%!"/>
          <el-empty description="请点击左侧字典类型表中的 [ 字典数据 ] 按钮"/>
        </div>
        <dict-data v-else :current-dict-type="currentDictType"/>
      </el-col>
    </el-row>
    <el-dialog title="字典数据Demo" v-model="dialogDictDataVisible" width="400px">
      <dict-data-demo v-if="dialogDictDataVisible"/>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import DictData from '@/views/system/dictData/index.vue'
import DictDataDemo from '@/views/system/dictData/dictDataDemo.vue'
import { clearDictCache } from '@/utils/dict-util'

export default {
  components: {DictDataDemo, DictData},
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
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)}
      request({url: '/dictType/zDictType/list', method: 'get', params}).then((response) => {
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
      this.dialogFormVisible = false
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
          const data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '/dictType/zDictType/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '/dictType/zDictType/add', method: 'post', data}).then(response => {
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
          const data = this.tableSelectRows.map(r => r.typeId)
          request({url: '/dictType/zDictType/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)}
      request({url: '/dictType/zDictType/export/excel', method: 'get', params}).then(response => {
        // 创建a标签
        const link = document.createElement('a')
        // 组装下载地址
        link.href = this.$baseServer + response.data
        // 修改文件名
        link.setAttribute('download', '字典类型.xlsx')
        // 开始下载
        link.style.display = 'none'
        document.body.appendChild(link)
        link.click()
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
    }
  }
}
</script>
