<template>
  <div class="app-container">
    <!-- 应用信息表-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.clientId" size="small" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入应用ID查询"/>
      <el-input v-model="searchData.webServerRedirectUri" size="small" style="width: 190px;margin-right: 10px;"
                class="filter-item" placeholder="请输入应用回调地址查询"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">显示全部
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'oauth2.client-oauthClientDetails-add'">新增
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'oauth2.client-oauthClientDetails-delete'">删除
        </el-button>
      </div>
    </div>
    <!-- 应用信息表-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="应用ID" prop="clientId" align="center"/>
      <el-table-column label="授权模式" prop="authorizedGrantTypes" align="center" show-overflow-tooltip/>
      <el-table-column label="应用回调地址" prop="webServerRedirectUri" align="center" show-overflow-tooltip/>
      <el-table-column label="access_token有效期（秒）" prop="accessTokenValidity" align="center" width="120px"/>
      <el-table-column label="refresh_token有效期（秒）" prop="refreshTokenValidity" align="center" width="120px"/>
      <el-table-column label="自动授权" prop="autoapprove" align="center" width="100px">
        <template v-slot="scope">
          <el-tag type="primary" v-if="scope.row.autoapprove==='false'">手动授权</el-tag>
          <el-tag type="success" v-else-if="scope.row.autoapprove==='true'">自动授权</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="130" align="center">
        <template v-slot="scope">
          <el-button type="text" style="color: #13ce66;"
                     size="small" @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'oauth2.client-oauthClientDetails-update'"
                     type="text" size="small" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'oauth2.client-oauthClientDetails-delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
          <el-button type="text"
                     size="small" @click="testOauth(scope.row)">测试
          </el-button>
          <el-button v-permission="'oauth2.client-oauthClientDetails-resetSecret'" style="color: #ff6d6d;"
                     type="text" size="small" @click="resetSecret(scope.row)">重置Secret
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 应用信息表-分页 -->
    <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="resetTemp" width="660px">
      <!-- 重置Secret -->
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="150px"
               v-if="dialogType === 'resetSecret'">
        <el-form-item label-width="0px">
          <div style="font-size: 16px;font-weight: bold;text-align: center;color: #dd1f29;">
            请妥善保管好应用Secret，不要泄露；不可找回，若丢失只能重置！
          </div>
        </el-form-item>
        <el-form-item label="应用Secret" prop="clientSecret"
                      :rules="[{required: true, message: '应用Secret不能为空'}]">
          <el-input v-model="temp.clientSecret" type="text" placeholder="请输入应用Secret（点击下方按钮可随机生成）"/>
          <el-button type="primary" size="mini" @click="generateRandomPassword(16)">随机生成Secret</el-button>
          <el-button type="success" size="mini" v-clipboard:copy="temp.clientSecret">复制当前Secret</el-button>
        </el-form-item>
      </el-form>
      <!-- 添加、修改、查看 -->
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="150px"
               v-else :disabled="dialogType==='view'">
        <!-- 应用ID -->
        <el-form-item label="应用ID" prop="clientId" v-if="dialogType === 'add'"
                      :rules="[{required: true, message: '应用ID不能为空'}]">
          <el-input v-model="temp.clientId" type="text"
                    maxlength="256" placeholder="请输入应用ID（注意：应用ID添加后不能修改）"/>
        </el-form-item>
        <el-form-item label="应用ID" v-if="dialogType === 'update'">
          <span style="color: #dd1f29;font-weight: bold;font-size: 20px;">{{ temp.clientId }}</span>
          <span style="color: #2C7EEA;margin-left: 10px;">(不能修改)</span>
        </el-form-item>
        <!-- 应用Secret -->
        <el-form-item label="应用Secret" prop="clientSecret" v-if="dialogType === 'add'"
                      :rules="[{required: true, message: '应用Secret不能为空'}]">
          <el-input v-model="temp.clientSecret" type="text" placeholder="请输入应用Secret（点击下方按钮可随机生成）"/>
          <div style="color: #dd1f29;">请妥善保管好应用Secret，不要泄露；不可找回，若丢失只能重置！</div>
          <el-button type="primary" size="mini" @click="generateRandomPassword(16)">随机生成Secret</el-button>
          <el-button type="success" size="mini" v-clipboard:copy="temp.clientSecret">复制当前Secret</el-button>
        </el-form-item>
        <!-- 回调地址 -->
        <el-form-item label="回调地址" prop="webServerRedirectUri"
                      :rules="[{required: true, message: '应用回调地址不能为空'}]">
          <el-input v-model="temp.webServerRedirectUri" type="text" placeholder="请输入应用回调地址"/>
        </el-form-item>
        <!-- 授权模式 -->
        <el-form-item label="授权模式" prop="authorizedGrantTypesArr"
                      :rules="[{required: true, message: '授权模式不能为空'}]">
          <el-checkbox-group v-model="temp.authorizedGrantTypesArr" size="small">
            <el-checkbox label="authorization_code" name="type" border>授权码模式</el-checkbox>
            <el-checkbox label="refresh_token" name="type" border>刷新令牌</el-checkbox>
            <el-checkbox label="password" name="type" border>密码模式</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="access_token有效期" prop="accessTokenValidity"
                      :rules="[{type: 'number', message: '必须为数字'},{required: true, message: 'access_token有效期不能为空'}]">
          <el-input-number v-model.number="temp.accessTokenValidity" size="small"
                           placeholder="请输入access_token有效期，单位秒"/>
          <span style="color: #2C7EEA;margin-left: 10px;">秒</span>
        </el-form-item>
        <el-form-item label="refresh_token有效期" prop="refreshTokenValidity"
                      :rules="[{type: 'number', message: '必须为数字'},{required: true, message: 'refresh_token有效期不能为空'}]">
          <el-input-number v-model.number="temp.refreshTokenValidity" size="small"
                           placeholder="请输入refresh_token有效期，单位秒"/>
          <span style="color: #2C7EEA;margin-left: 10px;">秒</span>
        </el-form-item>
        <el-form-item label="是否自动授权" prop="autoapprove"
                      :rules="[{required: true, message: '是否自动授权不能为空'}]">
          <el-radio-group v-model="temp.autoapprove" size="small">
            <el-radio-button :label="true">自动授权</el-radio-button>
            <el-radio-button :label="false">用户手动确认</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'

export default {
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
      titleMap: {add: '添加应用信息表', update: '修改应用信息表', view: '查看详情', resetSecret: '重置应用Secret'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      isLoading: false,
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  methods: {
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
      this.isLoading = true
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/oauth2.client/oauthClientDetails/list', method: 'get', params
      }).then((response) => {
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
      this.temp = {
        authorizedGrantTypesArr: ['authorization_code', 'refresh_token'],
        accessTokenValidity: 3600,
        refreshTokenValidity: 3600,
        autoapprove: true
      }
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
        let updateData = this.tableSelectRows[0]
        updateData.authorizedGrantTypesArr = updateData.authorizedGrantTypes.split(',')
        this.temp = Object.assign({}, updateData)
        this.dialogType = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      }
    },
    // 打开查看窗口
    openView(row) {
      let viewData = row
      viewData.authorizedGrantTypesArr = viewData.authorizedGrantTypes.split(',')
      this.temp = Object.assign({}, viewData)
      this.dialogType = 'view'
      this.dialogFormVisible = true
    },
    // 重置应用Secret
    resetSecret(row) {
      let viewData = row
      viewData.authorizedGrantTypesArr = viewData.authorizedGrantTypes.split(',')
      this.temp = Object.assign({}, viewData)
      // 重新生成密码
      this.generateRandomPassword(16)
      this.dialogType = 'resetSecret'
      this.dialogFormVisible = true
    },
    // 添加/修改，保存事件
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          let data = {...this.temp, scope: 'read,write'}
          data.authorizedGrantTypes = this.temp.authorizedGrantTypesArr.join(',')
          if (this.dialogType === 'update' || this.dialogType === 'resetSecret') {
            request({
              url: '/oauth2.client/oauthClientDetails/update', method: 'put', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            request({
              url: '/oauth2.client/oauthClientDetails/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.clientId)
          request({
            url: '/oauth2.client/oauthClientDetails/delete', method: 'delete', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 生成随机密码
    generateRandomPassword(length) {
      const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#-_=+'
      let password = '';
      for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * charset.length);
        password += charset.charAt(randomIndex);
      }
      this.temp = {...this.temp, clientSecret: password}
    },
    // 测试认证
    testOauth(row) {
      window.open(this.$baseServer + '/oauth/authorize?response_type=code' +
          '&client_id=' + row.clientId +
          '&redirect_uri=' + encodeURI(row.webServerRedirectUri) +
          '&state=my_state')
    },
  }
}
</script>
