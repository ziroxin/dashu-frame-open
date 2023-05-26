<template>
  <div style="margin: 20px;">
    <h1>支付宝PC扫码支付</h1>
    <el-form ref="dataForm" label-position="right" label-width="100px">
      <el-form-item label="支付金额" prop="totalFee">
        <el-input-number v-model="payData.totalFee" placeholder="请输入支付金额"/>
        <span style="margin-left: 20px;color: red;">(单位：分)</span>
      </el-form-item>
      <el-form-item label="PC扫码支付">
        <img v-if="tradePayQRCodeUrl" :src="tradePayQRCodeUrl">
        <div v-else style="font-size: 10px;color: red;">付款码加载中...</div>
      </el-form-item>
    </el-form>
    <div style="margin: 20px 100px;">
      <el-button type="primary" @click="clearBack">返回</el-button>
    </div>
  </div>
</template>

<script>
import QRCode from 'qrcode'

let intervalIndex

export default {
  name: 'AlipayScanPay',
  data() {
    return {
      // 支付二维码
      tradePayQRCodeUrl: '',
      // 支付数据
      payData: {
        // 商品id（关联待支付的商品信息）
        productId: 'xxxx-xxxx-xx-x',
        // 商品名称（支付时显示）
        productName: '测试商品',
        // 支付金额（单位：分）
        totalFee: 1,
        // 附加内容（在查询API和支付通知中原样返回，最长127个字符）
        attach: ''
      }
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      // 清空二维码
      this.tradePayQRCodeUrl = ''
      // 支付信息
      this.payData = {...JSON.parse(window.sessionStorage.getItem('payData'))}
      // 调用支付宝支付
      let data = {...this.payData}
      this.$request({url: '/pay/alipay/scanPay', method: 'post', data})
        .then((response) => {
          // 二维码url，转成二维码图片
          QRCode.toDataURL(response.data.qrCodeUrl).then(url => {
            this.tradePayQRCodeUrl = url
          })
          // 更新支付宝支付状态
          this.alipayUpdateStatus(response.data.tradeId)
        })
    },
    alipayUpdateStatus(tradeId) {
      let params = {tradeId: tradeId}
      intervalIndex = setInterval(() => {
        this.$request({url: '/pay/alipay/getPayResult', method: 'get', params}).then((response) => {
          if (response.data.tradeStatus === 1) {
            this.$message({type: 'success', message: '您已支付成功!', duration: 5000, showClose: true})
            this.clearBack()
          }
        })
      }, 2000)
    },
    clearBack() {
      clearInterval(intervalIndex)
      this.$router.replace('/demo/trade')
    }
  }
}
</script>
