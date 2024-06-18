<template>
  <div class="app-container">
    <div class="smsInfo">
      短信模块Demo（说明：
      1.目前集成了阿里云短信，后续可再集成其他平台，集成时需要再开发；
      2.发送短信模板，在【
      <el-link href="/#/system/dictType" :underline="false" icon="el-icon-coin">数据字典</el-link>
      】模块维护
      ）
    </div>
    <!-- 短信 - demo-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.smsChannel" size="small" style="width: 120px;margin-right: 10px;"
                class="filter-item" placeholder="发送渠道"/>
      <el-input v-model="searchData.smsPhones" size="small" style="width: 120px;margin-right: 10px;"
                class="filter-item" placeholder="手机号"/>
      <el-select v-model="searchData.status" size="small" style="width: 120px;margin-right: 10px;"
                 class="filter-item" placeholder="发送状态" clearable>
        <el-option label="发送成功" value="1"/>
        <el-option label="发送失败" value="0"/>
      </el-select>
      <el-date-picker v-model="searchData.createTime" size="small" style="width: 140px;margin-right: 10px;"
                      type="date" class="filter-item" placeholder="发送时间"
                      format="yyyy-MM-dd" value-format="yyyy-MM-dd"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">显示全部
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                   v-permission="'sms-demoSms-add'">发短信
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'sms-demoSms-delete'">删除
        </el-button>
      </div>
    </div>
    <!-- 短信 - demo-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange"
              v-loading="isLoading">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="发送渠道" prop="smsChannel" align="center"/>
      <el-table-column label="手机号" prop="smsPhones" align="center"/>
      <el-table-column label="发送短信内容json" prop="sendJson" align="center" show-overflow-tooltip/>
      <el-table-column label="发送状态" prop="status" align="center">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === '0'" type="danger">发送失败</el-tag>
          <el-tag v-if="scope.row.status === '1'" type="success">发送成功</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="返回结果json" prop="resultJson" align="center" show-overflow-tooltip/>
      <el-table-column label="发送时间" prop="createTime" align="center"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button v-permission="'sms-demoSms-delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 短信 - demo-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="resetTemp" width="600px" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px" :disabled="dialogType==='view'">
        <el-form-item label="手机号" prop="smsPhones"
                      :rules="[{required: true, message: '手机号不能为空'}]">
          <el-input v-model="temp.smsPhones" type="textarea"
                    maxlength="12000" show-word-limit :autosize="{minRows: 3}"
                    placeholder="请输入手机号(例如：18900000001,18900000002)"/>
          <el-tag type="success">支持群发，多个手机号以英文逗号隔开，最多1000个</el-tag>
        </el-form-item>
        <el-form-item label="发送渠道" prop="smsChannel"
                      :rules="[]">
          <el-select v-model="temp.smsChannel" placeholder="请选择发送渠道">
            <el-option label="阿里云短信" value="阿里云短信"/>
          </el-select>
        </el-form-item>
        <el-form-item label="选择模板" v-if="temp.smsChannel==='阿里云短信'">
          <el-select v-model="temp.smsTemplate" placeholder="请选择模板">
            <el-option v-for="item in dict.dict.aliyun_sms_template" :key="item.value"
                       :value="item.value" :label="item.label"/>
          </el-select>
          <div v-if="temp.smsTemplate" class="smsTemplate">
            模板内容：{{ temp.smsTemplate }}
          </div>
        </el-form-item>
        <el-form-item label="短信内容" v-if="temp.smsTemplate">
          <template v-for="field in smsTemplateFields">
            <el-input v-model="temp.sendJson[field]" placeholder="请输入">
              <template slot="prepend">{{ field }}</template>
            </el-input>
          </template>
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
  dicts: ['aliyun_sms_template'],
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
      titleMap: {add: '添加短信 - demo', update: '修改短信 - demo', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      isLoading: false,
      dialogIndex: 0,
      smsTemplateFields: [],// 短信模板中的字段
    }
  },
  watch: {
    'temp.smsTemplate'(newVal) {
      if (newVal) {
        this.smsTemplateFields = this.extractFieldNames(newVal);
      }
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
        url: '/sms/demoSms/list', method: 'get', params
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
      this.temp = {orderIndex: 0, sendJson: {}}
      this.dialogIndex++
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
    // 添加/修改，保存事件
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          let data = {...this.temp, sendJson: JSON.stringify(this.temp.sendJson)}
          console.log(data)
          request({
            url: '/sms/demoSms/add', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '短信发送成功！'})
            this.loadTableList()
            this.dialogFormVisible = false
          }).catch((err) => {
            this.loadTableList()
            this.dialogFormVisible = false
          })
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
          const data = this.tableSelectRows.map(r => r.smsId)
          request({
            url: '/sms/demoSms/delete', method: 'delete', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 提取字段
    extractFieldNames(str) {
      const regex = /\${(.*?)}/g;
      const matches = str.match(regex);
      if (matches) {
        return matches.map(match => match.substring(2, match.length - 1));
      } else {
        return [];
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.smsInfo {
  margin-bottom: 10px;
  line-height: 30px;
  font-size: 12px;
  color: #D7000F;
  border: 1px solid rgba(215, 0, 15, 0.1);
  border-radius: 5px;
  text-align: center;
  cursor: pointer;

  &:hover {
    border: 1px solid rgba(215, 0, 15, 0.5);
  }
}

.smsTemplate {
  font-size: 12px;
  background-color: #ecf5ff;
  padding: 0 10px;
  line-height: 30px;
  color: #409eff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  box-sizing: border-box;
  margin-top: 5px;
}
</style>
