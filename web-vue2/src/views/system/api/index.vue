<template>
  <div class="app-container">
    <el-row>
      <el-col :span="9">
        <!-- 资源表格 -->
        <div style="margin-bottom: 5px;">
          <el-button @click="toggleTableOprate">全部{{ isExpand ? '收起' : '展开' }}</el-button>
        </div>
        <div class="grid-content bg-purple">
          <el-table ref="permissionTable" v-loading="listLoading" :default-expand-all="isExpand"
                    :height="this.$windowHeight-170" style="width: 96%;"
                    border :data="tableData" row-key="permissionId"
                    highlight-current-row :tree-props="{children: 'children'}"
          >
            <el-table-column label="名称">
              <template v-slot="{row}">
                <el-tag v-if="row.permissionType === '0'" disable-transitions>路由</el-tag>
                <el-tag v-if="row.permissionType === '1'" disable-transitions type="warning">按钮</el-tag>
                <el-tag v-if="row.permissionType === '2'" disable-transitions type="success">外链</el-tag>
                <el-tag v-if="row.permissionType === '3'" disable-transitions type="danger">其他</el-tag>
                {{ row.permissionTitle }}{{ row.permissionRouter ? '(' + row.permissionRouter + ')' : '' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70px" align="center">
              <template v-slot="{row}">
                <el-button type="text" size="small" @click.native.prevent="setMyApi(row.permissionId)">
                  设置API
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="15" style="padding-left: 10px;border-left: 1px solid #dedede;">
        <!--        API列表-->
        <div class="grid-content bg-purple-light">
          <div style="margin-bottom: 5px;">
            <el-button type="primary" :disabled="isSaveBtn" @click="savePermissionApi()">保存关联API</el-button>
            <el-button type="primary" @click="openGroupDialog()">设置分组</el-button>
            <el-button type="danger" @click="scanApi()">自动扫描API（增量）</el-button>
            <el-button type="info" @click="clearApi()">清除无效API</el-button>
          </div>
          <div :style="'height:' + ( this.$windowHeight - 170 ) + 'px;overflow-y: auto;'">
            <el-collapse v-model="activeNames" style="padding-top: 5px;">
              <el-collapse-item v-for="group2 in tableData2" :key="group2.apiGroupId"
                                :name="group2.apiGroupId"
              >
                <template slot="title">
                  <div class="collapse-title">
                    分组：{{ group2.groupName }}
                    <el-button v-if="group2.apiGroupId!='no_group_api'" type="danger"
                               icon="el-icon-delete" size="mini" circle
                               @click.stop="deleteGroup(group2.apiGroupId)"
                    />
                  </div>
                </template>
                <el-checkbox-group v-model="selectPermissionApiList" style="line-height: 50px;">
                  <template v-for="cls in group2.apiClass">
                    <el-divider :key="cls.className">{{ cls.className }}</el-divider>
                    <template v-for="api2 in cls.apiList">
                      <el-tooltip :key="'tip'+api2.apiId" placement="left">
                        <div slot="content" :key="'tipcontent'+api2.apiId" style="line-height: 30px;">
                          请求地址：{{ api2.apiRequestUrl }}
                          <br>请求方式：{{ api2.apiRequestMethod }}
                          <br>描述：{{ api2.apiDescription }}
                        </div>
                        <el-checkbox :key="api2.apiId" :label="api2.apiId" border style="margin-left: 0px!important;">
                          {{ api2.apiName }}
                        </el-checkbox>
                      </el-tooltip>
                    </template>
                  </template>
                </el-checkbox-group>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog :visible.sync="groupDialogShow">
      <el-form ref="groupDataForm" :model="temp" :rules="rules" label-position="right" label-width="100px"
               style="width: 500px; margin-left: 50px;"
      >
        <el-form-item label="">
          <el-radio v-model="isNewGroup" label="0" @change="isNewGroup='0';temp={};">创建新分组</el-radio>
          <el-radio v-model="isNewGroup" label="1" @change="isNewGroup='1'">加入已有分组</el-radio>
        </el-form-item>
        <el-form-item label="分组名称：" prop="groupName" v-if="isNewGroup==='0'">
          <el-input v-model="temp.groupName" placeholder="请输入分组名称"/>
        </el-form-item>
        <el-form-item label="选择分组：" prop="apiGroupId" v-else>
          <el-select v-model="temp.apiGroupId" placeholder="请选择分组" @change="groupSelectChange">
            <el-option v-for="item in groupList" :key="item.apiGroupId"
                       :label="item.groupName" :value="item.apiGroupId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分组顺序：" prop="groupOrder" v-if="isNewGroup==='0'">
          <el-input-number v-model="temp.groupOrder"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="saveGroupInfo">保存</el-button>
        <el-button @click="groupDialogShow=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {clearApi, deleteApiGroup, getApiList, getApiListByPermissionId, permissionTreeList, saveApiGroup, savePermissionApi, scanApi} from '@/api/api'
import request from '@/utils/request'

export default {
  data() {
    return {
      isExpand: true,
      currentPermissionId: '',
      selectPermissionApiList: [],
      activeNames: [],
      tableData: [],
      tableData2: [],
      listLoading: true,
      listLoading2: true,
      isSaveBtn: true,
      // 分组数据
      groupDialogShow: false,
      isNewGroup: '0',
      temp: {},
      rules: {
        groupName: [{required: true, message: '分组名称必填', trigger: 'blur'}],
        groupOrder: [{required: true, message: '分组顺序必填'}],
      },
      groupList: [],
    }
  },
  created() {
    this.getPermissionTreeList()
    this.getApiList();
  },
  methods: {
    toggleTableOprate() {
      if (this.isExpand) {
        this.isExpand = false
      } else {
        this.isExpand = true
      }
      this.toggleRowExpansionAll(this.tableData, this.isExpand)
    },
    toggleRowExpansionAll(data, isExpansion) {
      data.forEach((item) => {
        this.$refs.permissionTable.toggleRowExpansion(item, isExpansion);
        if (item.children !== undefined && item.children !== null) {
          this.toggleRowExpansionAll(item.children, isExpansion);
        }
      });
    },
    // 左侧菜单列表
    async getPermissionTreeList() {
      this.listLoading = true
      const {data} = await permissionTreeList()
      this.tableData = data
      this.listLoading = false
    },
    // 右侧api列表
    async getApiList() {
      this.listLoading2 = true
      const {data} = await getApiList()
      this.activeNames = [];
      for (const group1 of data) {
        this.activeNames.push(group1.apiGroupId);
      }
      this.tableData2 = data
      this.listLoading2 = false
    },
    // 扫描后台api列表
    async scanApi() {
      const {code} = await scanApi()
      if (code === '200') {
        this.$notify({
          title: '扫描成功',
          message: '扫描所有api已完成，并存入数据库中',
          type: 'success'
        });
      }
      // 刷新api列表
      this.getApiList()
    },
    // 清除无效的API
    clearApi() {
      this.$confirm('确定要清除无效的Api吗?', '提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        clearApi().then(response => {
          const {code} = response
          if (code === '200') {
            this.$message({type: 'success', message: '清除无效的API成功！'})
            this.getApiList()
          } else {
            this.$message({type: 'error', message: '操作失败！'})
          }
        })
      })
    },
    // 设置api
    setMyApi(permissionId) {
      getApiListByPermissionId({'permissionId': permissionId})
        .then((response) => {
          const {data} = response
          this.selectPermissionApiList = data ? [...new Set(data)] : []
          this.currentPermissionId = permissionId
          this.isSaveBtn = false
        })
    },
    // 保存关联API
    savePermissionApi() {
      if (this.selectPermissionApiList.length <= 0) {
        this.$message({type: 'error', message: '请至少选择一个API才能保存'})
        return
      }
      savePermissionApi({'permissionId': this.currentPermissionId, 'apiIds': this.selectPermissionApiList})
        .then((response) => {
          const {code} = response
          if (code === '200') {
            this.$notify({
              title: '保存成功',
              message: '保存关联API成功！',
              type: 'success'
            })
          }
        })
    },
    // 保存分组
    openGroupDialog() {
      if (this.selectPermissionApiList.length <= 0) {
        this.$message({message: '至少选择一个API接口！', type: 'error'})
        return;
      }
      // 加载分组下拉框
      request({url: '/api/group/list', method: 'get'})
        .then((response) => {
          this.groupList = response.data
        })
      // 打开窗口
      this.groupDialogShow = true
    },
    groupSelectChange(val) {
      this.groupList.forEach(g => {
        if (g.apiGroupId === val) {
          this.temp.groupOrder = g.groupOrder
          this.temp.groupName = g.groupName
        }
      })
    },
    saveGroupInfo() {
      if (this.isNewGroup === '1') {
        if (this.temp.apiGroupId == undefined) {
          this.$message({type: 'error', message: '请选择分组'})
          return
        }
      }
      this.$refs.groupDataForm.validate(valid => {
        if (valid) {
          let data = {...this.temp};
          data.apiIds = this.selectPermissionApiList
          saveApiGroup(data).then((response) => {
            this.groupDialogShow = false
            this.$notify({title: '保存成功', message: '分组信息保存成功！', type: 'success'})
            this.getApiList()
          })
        }
      });
    },
    // 删除分组
    deleteGroup(apiGroupId) {
      this.$confirm('确定要删除该分组吗?', '提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        deleteApiGroup({'apiGroupId': apiGroupId})
          .then((response) => {
            const {code} = response
            if (code === '200') {
              this.$notify({
                title: '删除成功',
                message: '删除API分组成功！',
                type: 'success'
              });
              this.getApiList()
            }
          })
      })
    }
  }
}
</script>
<style>
.collapse-title {
  color: red;
  font-weight: bold;
  text-align: center;
  width: 100%;
}

.el-collapse-item__header.is-active {
  color: #828282;
  background-color: #FFFFFF !important;
  border-radius: 20px;
  border-top: 5px solid #0a76a4;
}

.el-collapse-item__header.is-active .collapse-title {
  color: red;
}

.el-collapse-item__header {
  background-color: #0a76a4 !important;
  border-radius: 20px;
  color: white;
}

.el-collapse-item__header .collapse-title {
  color: white;
}

.el-collapse-item__wrap {
  margin: 10px auto;
  border-bottom: 2px solid #0a76a4;
  border-radius: 20px;
}
</style>
