<template>
  <div class="app-container mb-0!">
    <el-row>
      <el-col :span="9">
        <!-- 资源表格 -->
        <div class="mb-10px">
          <base-button type="primary" plain :icon="isExpand?'el-icon-arrow-up':'el-icon-arrow-down'"
                       @click="toggleTableOprate">全部{{ isExpand ? '收起' : '展开' }}
          </base-button>
        </div>
        <div class="overflow-x-hidden overflow-y-scroll h-[calc(var(--app-content-height)-67px)]">
          <el-table ref="permissionTable" v-loading="listLoading" :default-expand-all="isExpand"
                    border :data="tableData" row-key="permissionId" highlight-current-row
                    :tree-props="{children: 'children',checkStrictly: true}">
            <el-table-column label="名称" show-overflow-tooltip>
              <template #default="{row}">
                <el-tag v-if="row.permissionType==='0'" disable-transitions class="p-[0_2px]! h-18px!"
                        size="small">路由
                </el-tag>
                <el-tag v-if="row.permissionType==='1'" disable-transitions class="p-[0_2px]! h-18px!"
                        type="warning" size="small">按钮
                </el-tag>
                <el-tag v-if="row.permissionType==='3'" disable-transitions class="p-[0_2px]! h-18px!"
                        type="success" size="small">其他
                </el-tag>
                <span class="ml-2px text-12px">
                  {{ row.permissionTitle }}{{ row.permissionRouter ? '(' + row.permissionRouter + ')' : '' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="75px" align="center">
              <template #default="{row}">
                <base-button link type="primary" size="small" @click="setMyApi(row.permissionId)">设置API
                </base-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="15" class="pl-10px b-l-1px b-l-solid b-l-#dedede" v-loading="listLoading2">
        <!-- API列表 -->
        <div>
          <div class="mb-10px">
            <base-button type="primary" :disabled="isSaveBtn" icon="el-icon-check"
                         @click="savePermissionApi()">保存关联API
            </base-button>
            <div class="float-right">
              <base-button type="primary" @click="openGroupDialog()">设置分组</base-button>
              <base-button type="danger" @click="scanApi()" icon="el-icon-refresh">自动扫描API（增量）</base-button>
              <base-button type="info" @click="clearApi()" icon="el-icon-close">清除无效API</base-button>
            </div>
          </div>
          <div class="overflow-x-hidden overflow-y-scroll h-[calc(var(--app-content-height)-67px)]">
            <el-collapse v-model="activeNames" class="p-[5px_10px_5px_0]!">
              <el-collapse-item v-for="group2 in tableData2" :key="group2.apiGroupId" :name="group2.apiGroupId">
                <template #title>
                  <div class="collapse-title">
                    <div>分组：{{ group2.groupName }}</div>
                    <el-tooltip v-if="group2.apiGroupId!=='no_group_api'" content="点击删除分组" placement="right">
                      <my-icon @click="deleteGroup(group2.apiGroupId)"
                               icon="el-icon-delete" class="color-#D7000F text-16px ml-10px"/>
                    </el-tooltip>
                  </div>
                </template>
                <el-checkbox-group v-model="selectPermissionApiList" class="lh-50px">
                  <template v-for="cls in group2.apiClass" :key="cls.className">
                    <el-divider>
                      <p class="class-name-p">{{ cls.className.split('@')[0] }}</p>
                      <p v-if="cls.className.split('@').length > 1" class="class-name-p controller">
                        {{ cls.className.split('@')[1] }}</p>
                    </el-divider>
                    <template v-for="api2 in cls.apiList" :key="'tip'+api2.apiId">
                      <el-tooltip placement="left">
                        <template #content>
                          <div :key="'tipcontent'+api2.apiId" class="lh-30px">
                            请求地址：{{ api2.apiRequestUrl }}
                            <br/>请求方式：{{ api2.apiRequestMethod }}
                            <br/>描述：{{ api2.apiDescription }}
                          </div>
                        </template>
                        <el-checkbox ref="apiCheckboxList" :key="api2.apiId" :value="api2.apiId"
                                     border class="m-[5px_10px_5px_0px]! h-42px! font-normal! color-#333!">
                          <span class="text-12px!">{{ api2.apiName }}<br/>{{ api2.apiRequestUrl }}</span>
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
    <el-dialog v-model="groupDialogShow">
      <el-form ref="groupDataForm" :model="temp" :rules="rules" label-position="right" label-width="100px"
               class="w-500px ml-50px">
        <el-form-item label="">
          <el-radio v-model="isNewGroup" value="0" @change="isNewGroup='0';temp={};">创建新分组</el-radio>
          <el-radio v-model="isNewGroup" value="1" @change="isNewGroup='1'">加入已有分组</el-radio>
        </el-form-item>
        <el-form-item v-if="isNewGroup==='0'" label="分组名称：" prop="groupName">
          <el-input v-model="temp.groupName" placeholder="请输入分组名称"/>
        </el-form-item>
        <el-form-item v-else label="选择分组：" prop="apiGroupId">
          <el-select v-model="temp.apiGroupId" placeholder="请选择分组" @change="groupSelectChange">
            <el-option v-for="item in groupList" :key="item.apiGroupId"
                       :label="item.groupName" :value="item.apiGroupId"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="isNewGroup==='0'" label="分组顺序：" prop="groupOrder">
          <el-input-number v-model="temp.groupOrder"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button type="primary" @click="saveGroupInfo">保存</base-button>
          <base-button @click="groupDialogShow=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys'

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
        groupOrder: [{required: true, message: '分组顺序必填'}]
      },
      groupList: [],
      contentHeight: localStorage.getItem(storageKeys.l_contentHeight)
    }
  },
  created() {
    this.getPermissionTreeList()
    this.getApiList()
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
        this.$refs.permissionTable.toggleRowExpansion(item, isExpansion)
        if (item.children !== undefined && item.children !== null) {
          this.toggleRowExpansionAll(item.children, isExpansion)
        }
      })
    },
    // 左侧菜单列表
    getPermissionTreeList() {
      this.listLoading = true
      request({url: '/permission/tree/list', method: 'get'}).then(response => {
        const {data} = response
        this.tableData = data
        this.listLoading = false
      })
    },
    // 右侧api列表
    getApiList() {
      this.listLoading2 = true
      request({url: '/api/listGroupApi', method: 'get'}).then(response => {
        const {data} = response
        this.activeNames = data.map(item => item.apiGroupId) || []
        this.tableData2 = data
        this.listLoading2 = false
      }).catch(error => {
        console.log('加载api列表失败', error)
        this.listLoading2 = false
      })
    },
    // 扫描后台api列表
    scanApi() {
      this.listLoading2 = true
      request({url: 'api/saveScanApi', method: 'get'}).then(response => {
        this.$notify({title: '扫描成功', message: '扫描所有api已完成，并存入数据库中', type: 'success'})
        // 刷新api列表
        this.getApiList()
      })
    },
    // 清除无效的API
    clearApi() {
      this.$confirm('确定要清除无效的Api吗?', '提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.listLoading2 = true
        request({url: 'api/clearApi', method: 'get'}).then(response => {
          this.$message({type: 'success', message: '清除无效的API成功！'})
          this.getApiList()
        })
      })
    },
    // 设置api
    setMyApi(permissionId) {
      this.listLoading2 = true
      const params = {permissionId: permissionId}
      request({url: 'permission/api/getApiListByPermissionId', method: 'get', params}).then((response) => {
        const {data} = response
        this.selectPermissionApiList = data ? [...new Set(data)] : []
        this.currentPermissionId = permissionId
        this.isSaveBtn = false
        this.listLoading2 = false
        const toApiId = data && data.length > 0 ? data[0] : this.tableData2[0].apiClass[0].apiList[0].apiId || ''
        const chkBox = this.$refs.apiCheckboxList.find(ref => ref.value === toApiId)
        if (chkBox) {
          chkBox.$el.scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'})
        }
      })
    },
    // 保存关联API
    savePermissionApi() {
      if (this.selectPermissionApiList.length <= 0) {
        this.$message({type: 'error', message: '请至少选择一个API才能保存'})
        return
      }
      this.listLoading2 = true
      const data = {permissionId: this.currentPermissionId, apiIds: this.selectPermissionApiList}
      request({url: 'permission/api/savePermissionApi', method: 'post', data}).then((response) => {
        this.$notify({title: '保存成功', message: '保存关联API成功！', type: 'success'})
        this.listLoading2 = false
      })
    },
    // 保存分组
    openGroupDialog() {
      if (this.selectPermissionApiList.length <= 0) {
        this.$message({message: '至少选择一个API接口！', type: 'error'})
        return
      }
      // 加载分组下拉框
      request({url: '/api/group/list', method: 'get'}).then((response) => {
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
        if (this.temp.apiGroupId === undefined) {
          this.$message({type: 'error', message: '请选择分组'})
          return
        }
      }
      this.$refs.groupDataForm.validate(valid => {
        if (valid) {
          const data = {...this.temp, apiIds: this.selectPermissionApiList}
          request({url: 'api/group/add', method: 'post', data}).then((response) => {
            this.groupDialogShow = false
            this.$notify({title: '保存成功', message: '分组信息保存成功！', type: 'success'})
            this.selectPermissionApiList = []
            this.getApiList()
          })
        }
      })
    },
    // 删除分组
    deleteGroup(apiGroupId) {
      this.$confirm('确定要删除该分组吗?', '提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        const params = {apiGroupId: apiGroupId}
        request({url: 'api/group/delete', method: 'post', params}).then((response) => {
          this.$notify({title: '删除成功', message: '删除API分组成功！', type: 'success'})
          this.getApiList()
        })
      })
    }
  }
}
</script>

<style lang="less" scoped>
.app-container {
  :deep(.el-collapse-item) {
    .el-collapse-item__header {
      background-color: var(--el-color-primary-light-9) !important;
      border: 1px solid var(--el-color-primary);
      border-radius: 10px;
      margin: 5px;
      color: var(--el-color-primary) !important;
      &.is-active {
        color: #828282;
        background-color: #FFFFFF !important;
        border-radius: 10px 10px 0px 0px;
        border: 0px;
        border-top: 1px solid var(--el-color-primary);
      }
      .collapse-title {
        color: var(--el-color-primary);
        font-weight: bold;
        text-align: center;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
    .el-collapse-item__wrap {
      margin: 0 10px;
      padding: 5px 10px;
      border-radius: 10px;
      border: 1px dotted var(--el-color-primary-light-5);
    }
  }

  :deep(.class-name-p) {
    line-height: 20px;
    text-align: center;
    color: #4e5969;
    &.controller {
      color: #999999;
      font-size: 12px;
    }
  }
}
</style>
