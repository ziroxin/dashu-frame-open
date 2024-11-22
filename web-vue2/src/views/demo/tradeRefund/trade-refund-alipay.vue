<template>
  <div v-if="currentTradeInfo" v-loading.fullscreen.lock="isLoading">
    <!-- 退款 - 支付demo-管理按钮 -->
    <div class="filter-container" style="margin-bottom: 20px;">
      支付宝退款：
      <el-tag type="success">总金额：{{ currentTradeInfo.totalFee }} 分</el-tag>
      <el-tag type="danger">已退款金额：{{ currentTradeInfo.refundTotalFee || 0 }} 分</el-tag>
      <el-button v-if="currentTradeInfo.totalFee > (currentTradeInfo.refundTotalFee || 0)"
                 v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small"
                 v-permission="'tradeRefund-busTradeRefund-add'">退款
      </el-button>
      <el-button v-else type="info" size="small">已全部退款</el-button>
    </div>
    <!-- 退款 - 支付demo-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="商户退款单号" prop="outRefundNo" align="center"/>
      <el-table-column label="退款原因" prop="refundDesc" align="center"/>
      <el-table-column label="退款状态" align="center">
        <template v-slot="scope">
          <el-tag v-if="scope.row.refundStatus===0" type="info">退款中</el-tag>
          <el-tag v-else-if="scope.row.refundStatus===1" type="success">退款成功</el-tag>
          <el-tag v-else type="danger">退款异常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="退款成功时间" prop="refundSuccessTime" align="center"/>
      <el-table-column label="退款金额，单位：分" prop="refundFee" align="center"/>
      <el-table-column label="退款反馈结果json" prop="refundResultJson" align="center">
        <template v-slot="scope">
          <el-popover placement="top-start" title="支付反馈结果JSON"
                      width="500" trigger="hover" :content="scope.row.refundResultJson">
            <div slot="reference"
                 style="display: -webkit-box;-webkit-line-clamp: 2;-webkit-box-orient: vertical;overflow: hidden;">
              {{ scope.row.refundResultJson }}
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button v-permission="'tradeRefund-busTradeRefund-update'" style="color: #00afff;"
                     v-if="scope.row.refundStatus!==1"
                     type="text" size="small" @click="updateRefundStatus(scope.row)">更新状态
          </el-button>
          <el-button type="text" style="color: #13ce66;"
                     size="small" @click="openView(scope.row)">详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 退款 - 支付demo-分页 -->
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" :close-on-click-modal="dialogType !== 'view' ? false : true"
               :visible.sync="dialogFormVisible" @close="closeDialog" width="600px" :modal="false">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="100px">
        <el-form-item label="退款金额" prop="refundFee"
                      :rules="[{required: true, message: '退款金额不能为空'}, {type: 'number', message: '必须为数字'}]">
          <el-input-number v-model.number="temp.refundFee" placeholder="请输入退款金额，单位：分"/>
        </el-form-item>
        <el-form-item label="退款原因" prop="refundDesc">
          <el-input v-model="temp.refundDesc" placeholder="请输入退款原因"/>
        </el-form-item>

        <el-form-item label="退款ID" prop="refundId" v-show="dialogType === 'view'">
          <el-input v-model="temp.refundId"/>
        </el-form-item>
        <el-form-item label="订单ID" prop="tradeId" v-show="dialogType === 'view'">
          <el-input v-model="temp.tradeId"/>
        </el-form-item>
        <el-form-item label="商户退款单号" prop="outRefundNo" v-show="dialogType === 'view'">
          <el-input v-model="temp.outRefundNo"/>
        </el-form-item>
        <el-form-item label="退款状态" prop="refundStatus" v-show="dialogType === 'view'">
          <el-tag type="primary" v-if="temp.refundStatus===0">退款中</el-tag>
          <el-tag type="success" v-else-if="temp.refundStatus===1">退款成功</el-tag>
          <el-tag type="danger" v-else>退款异常</el-tag>
        </el-form-item>
        <el-form-item label="退款成功时间" prop="refundSuccessTime" v-show="dialogType === 'view'">
          <el-date-picker v-model="temp.refundSuccessTime" type="datetime"/>
        </el-form-item>
        <el-form-item label="退款反馈结果json" prop="refundResultJson" v-show="dialogType === 'view'">
          <el-input v-model="temp.refundResultJson" type="textarea"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-waves type="primary" v-if="dialogType!=='view'" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
  <div v-else>暂无数据</div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'

export default {
  name: 'trade-refund-alipay',
  directives: {waves},
  props: {
    // 当前订单信息
    currentTradeInfo: {type: Object, default: {}},
    // 刷新数据
    refundRefreshIndex: {type: Number, default: 0}
  },
  data() {
    return {
      // 是否加载中
      isLoading: false,
      // 分页数据
      pager: {page: 1, limit: 10, totalCount: 0},
      // 表格
      tableData: [],
      // 查询表单数据
      searchData: {},
      // 选中行
      tableSelectRows: [],
      // 弹窗标题
      titleMap: {add: '添加退款 - 支付demo', update: '修改退款 - 支付demo', view: '查看详情'},
      // 添加/修改模式（add/update）
      dialogType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 表单临时数据
      temp: {},
      // 退款状态定时任务
      intervalIndex: null,
    }
  },
  created() {
    this.loadTableList()
    this.resetTemp()
  },
  watch: {
    refundRefreshIndex(val) {
      this.loadTableList()
    }
  },
  beforeDestroy() {
    clearInterval(this.intervalIndex)
  },
  methods: {
    // 更新退款状态
    updateRefundStatus(row) {
      // 查询退款状态
      this.isLoading = true
      const params = {refundId: row.refundId}
      this.$request({url: '/pay/alipay/queryRefund', method: 'get', params})
        .then((resp) => {
          if (resp.data.refundStatus === 1) {
            this.$message({type: 'success', message: '退款成功'})
            this.currentTradeInfo.refundTotalFee = this.currentTradeInfo.refundTotalFee + row.refundFee
            this.dialogFormVisible = false
          } else if (resp.data.refundStatus === 2) {
            this.$message({type: 'error', message: '退款异常'})
            this.dialogFormVisible = false
          }
          this.loadTableList()
          this.isLoading = false
        })
    },
    // 查询按钮
    searchBtnHandle() {
      this.pager.page = 1
      this.loadTableList()
    },
    // 显示全部
    resetTableList() {
      this.pager.page = 1
      this.searchData = this.$options.data().searchData
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      this.searchData = {...this.searchData, tradeId: this.currentTradeInfo.tradeId}
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/tradeRefund/busTradeRefund/list', method: 'get', params
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
      this.temp = {}
    },
    // 关闭弹窗事件
    closeDialog() {
      this.isLoading = false
      clearInterval(this.intervalIndex)
      this.loadTableList()
      this.resetTemp()
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
          this.currentTradeInfo.refundTotalFee = this.currentTradeInfo.refundTotalFee || 0
          if (this.currentTradeInfo.totalFee < (this.currentTradeInfo.refundTotalFee + this.temp.refundFee)) {
            this.$message({message: '退款金额合计，不能大于支付总金额!', type: 'error'})
            return
          }
          this.temp.tradeId = this.currentTradeInfo.tradeId
          var data = {...this.temp}
          // 提交退款
          request({url: '/pay/alipay/refund', method: 'post', data})
            .then(response => {
              if (response.data.refundStatus === 0) {
                this.isLoading = true
                this.intervalIndex = setInterval(() => {
                  // 查询退款状态
                  const params = {refundId: response.data.refundId}
                  this.$request({url: '/pay/alipay/queryRefund', method: 'get', params})
                    .then((resp) => {
                      if (resp.data.refundStatus === 1) {
                        this.$message({type: 'success', message: '退款成功'})
                        this.currentTradeInfo.refundTotalFee = this.currentTradeInfo.refundTotalFee + this.temp.refundFee
                        this.dialogFormVisible = false
                      } else if (resp.data.refundStatus === 2) {
                        this.$message({type: 'error', message: '退款异常'})
                        this.dialogFormVisible = false
                      }
                    })
                }, 1500)
              }
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
          const data = this.tableSelectRows.map(r => r.refundId)
          request({
            url: '/tradeRefund/busTradeRefund/delete', method: 'post', data
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
        url: '/tradeRefund/busTradeRefund/export/excel', method: 'get', params
      }).then(response => {
        // 创建a标签
        const link = document.createElement('a');
        // 组装下载地址
        link.href = this.$baseServer + response.data;
        // 修改文件名
        link.setAttribute('download', '退款 - 支付demo.xlsx');
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
<style lang="scss" scoped>
.filter-container {
  span {
    margin-right: 10px;
  }
}
</style>
