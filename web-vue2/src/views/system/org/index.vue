<template>
  <div class="app-container">
    <!-- 组织机构表-管理按钮 -->
    <div style="margin-bottom: 20px;">
      <el-input v-model="searchData.orgName" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入用户名/姓名查询"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="loadTableList">查询</el-button>
      <el-button v-waves class="filter-item" type="info" icon="el-icon-refresh" @click="resetTableList">显示全部</el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd"
                   v-permission="'zorg-zOrganization-add'">新增
        </el-button>
        <el-button v-waves type="info" icon="el-icon-edit" @click="openUpdate"
                   v-permission="'zorg-zOrganization-update'">修改
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds"
                   v-permission="'zorg-zOrganization-delete'">删除
        </el-button>
      </div>
    </div>
    <!-- 组织机构表-列表 -->
    <el-table :data="tableData" @selection-change="handleTableSelectChange"
              :tree-props="{children: 'children'}"
              row-key="orgId" default-expand-all border stripe>
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="组织机构名称" prop="orgName" min-width="30%"/>
      <el-table-column label="备注" prop="remarks" align="center" min-width="40%"/>
      <el-table-column label="层级" prop="orgLevel" align="center" width="100"/>
      <el-table-column label="顺序" prop="orderIndex" align="center" width="100"/>
    </el-table>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px"
               style="width: 500px; margin-left: 50px;" :disabled="dialogType=='view'">
        <el-form-item label="组织机构名称" prop="orgName"
                      :rules="[]">
          <el-input v-model="temp.orgName" placeholder="请输入组织机构名称"/>
        </el-form-item>
        <el-form-item label="父级ID" prop="orgParentId"
                      :rules="[]">
          <select-tree v-model="temp.orgParentId" empty-text="请选择" empty-value="-1"
                       :props="{children: 'children', label: 'label'}"
                       :data="treeSelectData" style="width: 400px;"></select-tree>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="temp.orderIndex" :min="0"/>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="temp.remarks" placeholder="请输入备注" type="textarea"
                    :autosize="{minRows: 2, maxRows: 5}" maxlength="300"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!='view'" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'
import SelectTree from "@/components/SelectTree";

export default {
  components: {SelectTree},
  directives: {waves},
  data() {
    return {
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加组织机构表', update: '修改组织机构表', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      // 下拉树-组织机构
      treeSelectData: []
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  methods: {
    // 显示全部
    resetTableList() {
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {params: JSON.stringify(this.searchData)};
      request({
        url: '/zorg/zOrganization/tree', method: 'get', params
      }).then((response) => {
        this.tableData = response.data
      })
    },
    // 加载下拉选择框组织机构树
    loadOrgTreeForSelect(parentId) {
      let params = parentId == undefined ? {} : {parentId: parentId}
      request({
        url: '/zorg/zOrganization/treeForSelect', method: 'get', params
      }).then((response) => {
        this.treeSelectData = response.data
      })
    },
    // 监听选中行
    handleTableSelectChange(rows) {
      this.tableSelectRows = rows
    },
    // 清空表单temp数据
    resetTemp() {
      this.temp = {orderIndex: 0}
    },
    // 打开添加窗口
    openAdd() {
      this.resetTemp()
      if (this.tableSelectRows.length === 1) {
        this.temp.orgParentId = this.tableSelectRows[0].orgId
      }
      this.dialogFormVisible = true
      this.dialogType = 'add'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        this.loadOrgTreeForSelect()
      })
    },
    // 打开修改窗口
    openUpdate() {
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
          this.loadOrgTreeForSelect()
        })
      }
    },
    // 打开查看窗口
    openView(row) {
      // 修改弹窗
      this.temp = Object.assign({}, row)
      this.dialogType = 'view'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        for (const $elElement of this.$refs['dataForm'].$el) {
          $elElement.placeholder = '';
        }
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 添加/修改，保存事件
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var data = this.temp;
          if (this.dialogType === 'update') {
            request({
              url: '/zorg/zOrganization/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/zorg/zOrganization/add', method: 'post', data
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
    deleteByIds() {
      if (this.tableSelectRows.length <= 0) {
        this.$message({message: '请选择一条数据删除！', type: 'warning'})
      } else {
        this.$confirm('确定要删除吗?', '删除提醒', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 执行删除
          const data = this.tableSelectRows.map(r => r.orgId)
          request({
            url: '/zorg/zOrganization/delete', method: 'post', data
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
        url: '/zorg/zOrganization/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '组织机构表.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    },
  }
}
</script>
