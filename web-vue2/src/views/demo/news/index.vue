<template>
  <div class="app-container">
    <!-- 新闻表-测试-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.newsTitle" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入新闻标题查询" size="small"
      />
      <el-input v-model="searchData.newsContent" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入新闻内容查询" size="small"
      />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search"
                 size="small" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" icon="el-icon-refresh"
                 size="small" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves v-permission="'news-news-add'" type="primary" icon="el-icon-plus"
                   @click="openAdd" size="small">新增
        </el-button>
        <el-button v-waves v-permission="'news-news-update'" type="info" icon="el-icon-edit"
                   @click="openUpdate(null)" size="small">修改
        </el-button>
        <el-button v-waves v-permission="'news-news-delete'" type="danger" icon="el-icon-delete"
                   @click="deleteByIds(null)" size="small">删除
        </el-button>
        <el-button v-waves v-permission="'news-news-exportExcel'" type="success" icon="el-icon-printer"
                   @click="exportExcel" size="small">导出Excel
        </el-button>
      </div>
    </div>
    <!-- 新闻表-测试-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="新闻标题" prop="newsTitle" align="center"/>
      <el-table-column label="顺序" prop="orderIndex" align="center"/>
      <el-table-column label="添加时间" prop="createTime" align="center"/>
      <el-table-column label="修改时间" prop="updateTime" align="center"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" size="mini" style="color: #4dd219;"
                     @click="openView(scope.row)">详情</el-button>
          <el-button v-permission="'news-news-update'" size="mini"
                     type="text" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'news-news-delete'" size="mini" style="color: #f56c6c;"
                     type="text" @click="deleteByIds(scope.row)">删除
          </el-button>
          <br/>
          <el-button size="mini" v-if="scope.row.msgId" style="color: red;"
                     type="text" @click="messageRead(scope.row)">[标记已读]
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新闻表-测试-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible" width="900px"
               :close-on-click-modal="dialogType !== 'view' ? false : true"
               @close="resetTemp" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="50px" :disabled="dialogType==='view'">
        <el-form-item label-width="0px" prop="newsTitle"
                      :rules="[{required: true, message: '新闻标题不能为空'}]">
          <el-input v-model="temp.newsTitle" maxlength="30" :show-word-limit="true"
                    placeholder="请输入新闻标题"/>
        </el-form-item>
        <el-form-item label-width="0px" prop="newsContent"
                      :rules="[{required: true, message: '新闻内容不能为空'}]">
          <my-wang-editor ref="myEditor" v-model="temp.newsContent" height="300px"
                          placeholder="请输入新闻内容"/>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="顺序" prop="orderIndex"
                          :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
              <el-input-number v-model="temp.orderIndex" :min="0"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- 选择消息发送的用户 -->
            <el-form-item label="" prop="messageSend" label-width="50px">
              <el-select v-model="messageSendData.type" placeholder="请选择消息发送类型">
                <el-option label="选择用户" value="user"></el-option>
                <el-option label="选择组织机构" value="org"></el-option>
                <el-option label="选择角色" value="role"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <message-send v-model="messageSendData.ids" label-width="80px"
                          :scope="messageSendData.scope" :type="messageSendData.type"/>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="dialogType !== 'view'" v-waves type="primary" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import MyWangEditor from '@/components/MyWangEditor';
import MessageSend from "@/components/MessageSend/index.vue";

export default {
  components: {MessageSend, MyWangEditor},
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
      titleMap: {add: '添加新闻表-测试', update: '修改新闻表-测试', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      dialogIndex: 0,
      messageSendData: {
        ids: [],// 根据用户选择自动获取
        type: 'user',// 发送用户类型：user=用户；org=组织机构；role=角色（不根据scope查询）
        scope: 'all',// all=全部；children=下级；selfAndChildren=本机构及下级
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
    // 重置
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/news/news/list', method: 'get', params
      }).then((response) => {
        const {data} = response
        this.pager.totalCount = data.total
        this.tableData = data.records
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
      this.temp = {orderIndex: 0}
      this.messageSendData.ids = []
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
          let data = {...this.temp};
          if (this.dialogType === 'update') {
            request({
              url: '/news/news/update', method: 'post', data
            }).then(response => {
              this.$message({type: 'success', message: '修改成功！'})
              this.loadTableList()
              this.dialogFormVisible = false
            })
          } else {
            if (this.messageSendData.ids.length > 0) {
              // 有选择消息，则填充消息实体字段
              data = {
                ...data,
                msgTitle: '发表了新闻《' + data.newsTitle + '》',
                msgContent: '发表了新闻《' + data.newsTitle + '》',
                msgRouter: '/demo/news',// 菜单管理-修改-菜单地址
                permissionName: 'news-news',// 菜单管理-修改-菜单标记
                toType: this.messageSendData.type,
                toIds: this.messageSendData.ids
              }
            }
            // 添加信息时，增加通知用户字段
            request({
              url: '/news/news/add', method: 'post', data
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
          const data = this.tableSelectRows.map(r => r.newsId)
          request({
            url: '/news/news/delete', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '删除成功！'})
            this.loadTableList()
          })
        })
      }
    },
    // 标记消息已读
    messageRead(row){
      const params = {msgId: row.msgId}
      this.$request({
        url: '/message/zMessage/read', method: 'get', params
      }).then((response) => {
        this.$store.dispatch('message/refreshMessageCount');
        this.loadTableList()
      })
    },
    // 导出Excel文件
    exportExcel() {
      const params = {params: JSON.stringify(this.searchData)};
      request({
        url: '/news/news/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '新闻表-测试.xlsx');
        // 开始下载
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
      })
    }
  }
}
</script>
