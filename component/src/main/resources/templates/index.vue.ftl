<template>
  <div class="app-container">
    <!-- ${table.comment!}-管理按钮 -->
    <div style="margin-bottom: 10px;">
<#list table.fields as field>
  <#if field.propertyName!=entityKeyName && field.propertyName!='orderIndex'
            && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
            && field.propertyName!='createTime' && field.propertyName!='updateTime'>
    <#if field.propertyType=='LocalDate' || field.propertyType=='Date'>
      <el-date-picker v-model="searchData.${field.propertyName}" size="small" style="width: 150px;margin-right: 10px;"
                type="date" clearable class="filter-item" placeholder="${field.comment}"/>
    <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
      <el-date-picker v-model="searchData.${field.propertyName}" size="small" style="width: 150px;margin-right: 10px;"
                type="datetime" clearable class="filter-item" placeholder="${field.comment}"/>
    <#else>
      <el-input v-model="searchData.${field.propertyName}" size="small" style="width: 150px;margin-right: 10px;"
                clearable class="filter-item" placeholder="${field.comment}"/>
    </#if>
  </#if>
</#list>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'${buttonNamePre}add'">新增
        </el-button>
        <el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate(null)" size="small"
                   v-permission="'${buttonNamePre}update'">修改
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'${buttonNamePre}delete'">删除
        </el-button>
        <el-button v-waves v-permission="'${buttonNamePre}importExcel'" @click="dialogImportVisible=true"
                   type="primary" icon="el-icon-upload2" size="small">导入Excel
        </el-button>
        <el-button v-waves type="success" icon="el-icon-printer" @click="exportExcel" size="small"
                   v-permission="'${buttonNamePre}exportExcel'">导出Excel
        </el-button>
      </div>
    </div>
    <!-- ${table.comment!}-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange" @sort-change="handleTableSortChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName && field.propertyName!='updateTime'
            && field.propertyName!='createUserId' && field.propertyName!='updateUserId'>
      <el-table-column label="${field.comment}" prop="${field.propertyName}" align="center" sortable="custom"/>
    </#if>
</#list>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;"
                     size="small" @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'${buttonNamePre}update'"
                     type="text" size="small" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'${buttonNamePre}delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- ${table.comment!}-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="closeDialog" width="600px" :key="'myDialog'+dialogIndex">
<#if templateHtml??>
<#-- 在线表单，直接用表单生成的代码 -->
      ${templateHtml}
<#else>
<#-- 非在线表单，使用以下代码生成 -->
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
  <#list table.fields as field>
  <#if field.propertyName=='orderIndex'>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0"/>
        </el-form-item>
  <#elseif field.propertyName!='createTime' && field.propertyName!='updateTime'
                && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
                && field.propertyName!=entityKeyName>
    <#--判断是否为null的规则-->
    <#assign rules1=field.metaInfo.nullable?string("","{required: true, message: '" + field.comment + "不能为空'}")>
    <#if field.propertyType=='String'>
      <#if field.metaInfo.length gte 255>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules1}]">
          <el-input v-model="temp.${field.propertyName}" type="textarea" maxlength="${field.metaInfo.length}"
                    placeholder="请输入${field.comment}"/>
        </el-form-item>
      <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules1}]">
          <el-input v-model="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
      </#if>
    <#elseif field.propertyType=='Integer' || field.propertyType=='Long'
      || field.propertyType=='BigDecimal'|| field.propertyType=='Double'>
      <#--数字的可能有2种规则，所以单独判断-->
      <#assign rules2=field.metaInfo.nullable?string("{type: 'number', message: '必须为数字'}","{required: true, message: '" + field.comment + "不能为空'},{type: 'number', message: '必须为数字'}")>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules2}]">
          <el-input-number v-model.number="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
    <#elseif field.propertyType=='LocalDate' || field.propertyType=='Date'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules1}]">
          <el-date-picker v-model="temp.${field.propertyName}" type="date" placeholder="请选择${field.comment}"/>
        </el-form-item>
    <#elseif field.propertyType=='LocalDateTime' || field.propertyType=='DateTime'>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules1}]">
          <el-date-picker v-model="temp.${field.propertyName}" type="datetime" placeholder="请选择${field.comment}"/>
        </el-form-item>
    <#else>
        <el-form-item label="${field.comment}" prop="${field.propertyName}"
                      :rules="[${rules1}]">
          <el-input v-model="temp.${field.propertyName}" placeholder="请输入${field.comment}"/>
        </el-form-item>
    </#if>
  </#if>
  </#list>
      </el-form>
</#if>
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
          <el-upload v-permission="'${buttonNamePre}importExcel'"
                     :action="$baseServer+'${controllerMapping}/import/excel'" :headers="$store.getters.headerToken"
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
import downloadUtil from '@/utils/download-util';
<#-- 初始化 componentsArr 数组 -->
<#assign componentsArr = []>
<#-- 根据条件将组件名称添加到 componentsArr 中 -->
<#if templateHtml?? && templateHtml?contains("my-wang-editor")>
  <#assign componentsArr = componentsArr + ["MyWangEditor"]>
import MyWangEditor from '@/components/MyWangEditor/index.vue';
</#if>
<#if templateHtml?? && templateHtml?contains("image-avatar")>
  <#assign componentsArr = componentsArr + ["ImageAvatar"]>
import ImageAvatar from '@/components/Upload/ImageAvatar';
</#if>
<#if templateHtml?? && templateHtml?contains("image-one")>
  <#assign componentsArr = componentsArr + ["ImageOne"]>
import ImageOne from '@/components/Upload/ImageOne';
</#if>
<#if templateHtml?? && templateHtml?contains("image-upload")>
  <#assign componentsArr = componentsArr + ["ImageUpload"]>
import ImageUpload from '@/components/Upload/ImageUpload';
</#if>
<#if templateHtml?? && templateHtml?contains("file-upload")>
  <#assign componentsArr = componentsArr + ["FileUpload"]>
import FileUpload from '@/components/Upload/FileUpload';
</#if>

export default {
  <#-- 如果 componentsArr 不为空，则使用它来定义组件 -->
  <#if componentsArr??>
  components: {<#list componentsArr as component>${component}<#if component_has_next>, </#if></#list>},
  </#if>
  directives: {waves},
  data() {
    return {
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {},
      // 排序数据
      sortData: {},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加${table.comment!}', update: '修改${table.comment!}', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {orderIndex: 0},
      isLoading: false,
      dialogIndex: 0,
      // 导入弹窗
      dialogImportVisible: false,
      isImportLoading: false,
<#if jsData??>
      ${jsData}
</#if>
    }
  },
  created() {
    this.loadTableList()
<#if jsCreated??>
    ${jsCreated}
</#if>
  },
  methods: {
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.loadTableList()
    },
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.sortData = this.$options.data().sortData
      this.$refs.dataTable.clearSort()
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.isLoading = true
      const params = {
        ...this.pager,
        params: JSON.stringify(this.searchData),
        sorts: JSON.stringify(this.sortData)
      };
      request({url: '${controllerMapping}/list', method: 'get', params}).then((response) => {
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
    // 监听排序
    handleTableSortChange(sort) {
      if (sort.order) {
        this.sortData = {column: sort.prop, order: sort.order === 'descending' ? 'DESC' : 'ASC'}
      } else {
        this.sortData = this.$options.data().sortData
      }
      this.loadTableList()
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
    closeDialog() {
      this.temp = this.$options.data().temp
      this.dialogIndex++
<#if childTableList??>
  <#list childTableList as child>
      this.load${child}FileList()
  </#list>
</#if>
<#if jsMethods??>
      this.loadChkStr2Arr()
</#if>
    },
    // 打开添加窗口
    openAdd() {
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
<#if childTableList??>
  <#list childTableList as child>
        this.load${child}FileList()
  </#list>
</#if>
<#if jsMethods??>
        this.loadChkStr2Arr()
</#if>
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
<#if childTableList??>
    <#list childTableList as child>
      this.load${child}FileList()
    </#list>
</#if>
<#if jsMethods??>
      this.loadChkStr2Arr()
</#if>
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
<#if jsMethods??>
          this.loadChkArr2Str()
</#if>
          let data = {...this.temp}
          if (this.dialogType === 'update') {
            request({url: '${controllerMapping}/update', method: 'post', data}).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({url: '${controllerMapping}/add', method: 'post', data}).then(response => {
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
          const data = this.tableSelectRows.map(r => r.${entityKeyName})
          request({url: '${controllerMapping}/delete', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData), sorts: JSON.stringify(this.sortData)}
      downloadUtil.download('${controllerMapping}/export/excel', params, '${table.comment!}.xlsx')
    },
    // 导入Excel之前，显示loading
    beforeImportUpload(file) {
      this.isImportLoading = true
    },
    // 导入Excel成功
    importExcelSuccess(response) {
      this.isImportLoading = false
      if (response.message === "Success") {
        this.$message({type: 'success', message: '导入成功！'})
        this.dialogImportVisible = false
        this.loadTableList()
      } else {
        this.$alert(response.message, "提示",
            {confirmButtonText: "确定", dangerouslyUseHTMLString: true, customClass: 'width800'});
      }
    },
    // 导入Excel失败，取消loading状态
    importExcelError() {
      this.isImportLoading = false
    },
    // 下载模板
    downloadExcelTemplate() {
      downloadUtil.download('${controllerMapping}/import/downloadTemplate', {}, '${table.comment!}-导入模板.xlsx')
    },
<#if jsMethods??>
    ${jsMethods}
</#if>
  }
}
</script>
<#if templateCss??>
<style>
    ${templateCss}
</style>
</#if>