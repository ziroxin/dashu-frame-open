<template>
  <el-table :data="list" style="width: 100%;padding-top: 15px;border-radius: 5px;">
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

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {success: 'success', pending: 'danger'}
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
        {order_no: 1, price: 10500, status: 'success'},
        {order_no: 2, price: 24560, status: 'pending'},
        {order_no: 3, price: 34520, status: 'pending'},
        {order_no: 3, price: 34530, status: 'pending'},
        {order_no: 5, price: 55350, status: 'success'},
        {order_no: 7, price: 75320, status: 'success'},
        {order_no: 7, price: 72340, status: 'success'},
        {order_no: 10, price: 104530, status: 'pending'}
      ]
    }
  }
}
</script>
