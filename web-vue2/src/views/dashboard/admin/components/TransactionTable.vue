<template>
  <el-table :data="list" style="width: 100%;padding-top: 15px;">
    <el-table-column label="订单编号" min-width="200">
      <template slot-scope="scope">
        {{ scope.row.order_no | orderNoFilter }}
      </template>
    </el-table-column>
    <el-table-column label="价格" width="195" align="center">
      <template slot-scope="scope">
        ¥{{ scope.row.price | toThousandFilter }}
      </template>
    </el-table-column>
    <el-table-column label="状态" width="100" align="center">
      <template slot-scope="{row}">
        <el-tag :type="row.status | statusFilter">
          {{ row.status }}
        </el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import {transactionList} from '@/api/remote-search'

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        success: 'success',
        pending: 'danger'
      }
      return statusMap[status]
    },
    orderNoFilter(str) {
      return 'order - ' + str
    }
  },
  data() {
    return {
      list: null
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.list = [
        {order_no: 1, price: 10, status: 'success'},
        {order_no: 2, price: 20, status: 'pending'},
        {order_no: 3, price: 30, status: 'pending'},
        {order_no: 3, price: 30, status: 'pending'},
        {order_no: 5, price: 50, status: 'success'},
        {order_no: 7, price: 70, status: 'success'},
        {order_no: 7, price: 70, status: 'success'},
        {order_no: 10, price: 100, status: 'pending'},
      ]
    }
  }
}
</script>
