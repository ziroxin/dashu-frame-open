<template>
  <div class="app-container">
    <!-- 交易 - 支付demo-管理按钮 -->
    <div style="margin-bottom: 20px;">
      <el-select v-model="searchData.payType" size="small" style="width: 100px;margin-right: 10px;"
                 class="filter-item" placeholder="支付方式">
        <el-option label="全部" value=""></el-option>
        <el-option label="微信支付" value="0"></el-option>
        <el-option label="支付宝支付" value="1"></el-option>
      </el-select>
      <el-select v-model="searchData.tradeStatus" size="small" style="width: 100px;margin-right: 10px;"
                 class="filter-item" placeholder="支付状态">
        <el-option label="全部" value=""></el-option>
        <el-option label="未支付" value="0"></el-option>
        <el-option label="已支付" value="1"></el-option>
      </el-select>
      <el-date-picker v-model="searchData.paySuccessTime" size="small" style="width: 150px;margin-right: 10px;"
                      type="datetime" class="filter-item" placeholder="支付成功时间"/>
      <el-button v-waves class="filter-item" type="primary" size="small"
                 icon="el-icon-search" @click="loadTableList">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" size="small"
                 icon="el-icon-refresh" @click="resetTableList">显示全部
      </el-button>
      <div style="float: right;">
        <el-button v-waves type="primary" icon="el-icon-plus" @click="openAdd" size="small">
          打开支付
        </el-button>
        <el-button v-waves type="danger" icon="el-icon-delete" @click="deleteByIds(null)" size="small"
                   v-permission="'trade-busTrade-delete'">删除
        </el-button>
      </div>
    </div>
    <!-- 交易 - 支付demo-列表 -->
    <el-table :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="订单ID" prop="tradeId" align="center" show-overflow-tooltip/>
      <el-table-column label="关联商品ID" prop="productId" align="center"/>
      <el-table-column label="支付方式" prop="payType" align="center">
        <template v-slot="scope">
          <span v-if="scope.row.payType===0" style="color: #00a226;">微信支付</span>
          <span v-if="scope.row.payType===1" style="color: #00afff;">支付宝支付</span>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" prop="tradeStatus" align="center">
        <template v-slot="scope">
          <span v-if="scope.row.tradeStatus === 1" style="color: #00a226;">已支付</span>
          <span v-else>未支付</span>
        </template>
      </el-table-column>
      <el-table-column label="支付成功时间" prop="paySuccessTime" align="center"/>
      <el-table-column label="总金额(分)" prop="totalFee" align="center"/>
      <el-table-column label="商户订单号" prop="outTradeNo" align="center" show-overflow-tooltip/>
      <el-table-column label="支付反馈结果json" prop="resultJson" align="center">
        <template v-slot="scope">
          <el-popover placement="top-start" title="支付反馈结果JSON"
                      width="500" trigger="hover" :content="scope.row.resultJson">
            <div slot="reference"
                 style="display: -webkit-box;-webkit-line-clamp: 2;-webkit-box-orient: vertical;overflow: hidden;">
              {{ scope.row.resultJson }}
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button v-permission="'trade-busTrade-delete'" style="color: #ff6d6d;"
                     type="text" size="small" @click="deleteByIds(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 交易 - 支付demo-分页 -->
    <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog title="支付窗口" :visible.sync="dialogFormVisible" width="600px"
               @closed="closePayDialog">
      <el-form>
        <el-form-item label="选择支付方式" prop="openPayType">
          <el-radio-group v-model="openPayType" @change="toPay">
            <el-radio-button :label="0">微信PC扫码</el-radio-button>
            <el-radio-button :label="1">微信移动支付(H5/JSAPI)</el-radio-button>
            <el-radio-button :label="2">支付宝PC</el-radio-button>
            <el-radio-button :label="3">支付宝移动端</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible=false">关闭</el-button>
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
      // 弹窗类型
      openPayType: '',
      // 弹窗显示隐藏
      dialogFormVisible: false,
      // 支付数据
      payData: {
        // 商品id（关联待支付的商品信息）
        productId: 'xxxx-xxxx-xx-x',
        // 商品名称（支付时显示）
        productName: '测试商品',
        // 支付金额（单位：分）
        totalFee: 1
      },
    }
  },
  created() {
    this.loadTableList()
  },
  methods: {
    // 打开支付
    openAdd() {
      this.openPayType = ''
      this.dialogFormVisible = true
    },
    toPay() {
      window.sessionStorage.setItem('payData', JSON.stringify(this.payData))
      if (this.openPayType === 0) {
        this.$router.replace('/demo/trade/WechatPcPay')
      } else if (this.openPayType === 1) {
        // 判断是否微信客户端
        if (/MicroMessenger/.test(navigator.userAgent)) {
          this.$router.replace('/demo/trade/WechatJsapiPay')
        } else {
          this.$router.replace('/demo/trade/WechatH5Pay')
        }
      }
    },
    // 显示全部
    resetTableList() {
      this.searchData = {}
      this.loadTableList()
    },
    // 加载表格
    loadTableList() {
      const params = {...this.pager, params: JSON.stringify(this.searchData)};
      request({
        url: '/trade/busTrade/list', method: 'get', params
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
          const data = this.tableSelectRows.map(r => r.tradeId)
          request({url: '/trade/busTrade/delete', method: 'delete', data})
            .then(response => {
              this.$message({type: 'success', message: '删除成功！'})
              this.loadTableList()
            })
        })
      }
    },
    // 关闭
    closePayDialog() {
      this.loadTableList()
      // 清理定时任务
      for (let i = 0; i < 10000; i++) {
        clearInterval(i)
      }
    }
  }
}
</script>
