<template>
  <div v-if="!tradeId" style="margin: 20px;">
    <h1>H5（非微信客户端）支付</h1>
    <el-form ref="dataForm" label-position="right" label-width="100px">
      <el-form-item label="支付金额" prop="totalFee">
        <el-input-number v-model="payData.totalFee" placeholder="请输入支付金额"/>
        <span style="margin-left: 20px;color: red;">(单位：分)</span>
      </el-form-item>
    </el-form>
    <div style="margin: 20px 100px;">
      <el-button type="primary" @click="payRedirect">立即支付</el-button>
      <el-button type="primary" @click="clearBack">返回</el-button>
    </div>
  </div>
  <div v-else>
    正在验证支付结果，请稍后...
    （这里可以弹窗，让用户确认，是否支付成功）
    <br/>示例：
    <img src="https://pay.weixin.qq.com/wiki/doc/api/img/chapter15_4_5.jpg"/>
  </div>
</template>

<script>

let intervalIndex
export default {
  name: 'WechatH5Pay',
  data() {
    return {
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
      },
      // 支付跳转链接
      payRedirectUrl: '',
      // 订单id
      tradeId: this.$route.query.tradeId
    }
  },
  mounted() {
    if (this.tradeId) {
      console.log(this.tradeId)

      this.wechatPayUpdateStatus(this.tradeIdtradeId)
    } else {
      this.init()
    }
  },
  methods: {
    init() {
      // 支付信息
      this.payData = {...JSON.parse(window.sessionStorage.getItem('payData'))}
      // 调用微信支付
      let data = {...this.payData}
      this.$request({url: '/pay/wechat/getPayH5', method: 'post', data})
        .then((response) => {
          //https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx231804107332511b4813f57ef7095d0000&package=376201905
          //https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx20161110163838f231619da20804912345&package=1037687096&redirect_url=https%3A%2F%2Fwww.wechatpay.com.cn
          this.payRedirectUrl = response.data.h5Url + "&redirect_url=" + encodeURI(location.href + '?tradeId=' + response.data.tradeId)
        })
    },
    payRedirect() {
      location.href = this.payRedirectUrl
    },
    wechatPayUpdateStatus(tradeId) {
      let params = {tradeId: tradeId}
      intervalIndex = setInterval(() => {
        this.$request({url: '/pay/wechat/getPayResult', method: 'get', params}).then((response) => {
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
