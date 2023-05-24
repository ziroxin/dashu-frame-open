<template>
  <div style="margin: 20px;">
    <h1>JSAPI支付</h1>
    <el-form ref="dataForm" label-position="right" label-width="100px">
      <el-form-item label="支付金额" prop="totalFee">
        <el-input-number v-model="payData.totalFee" placeholder="请输入支付金额"/>
        <span style="margin-left: 20px;color: red;">(单位：分)</span>
      </el-form-item>
    </el-form>
    <div style="margin: 20px 100px;">
      <el-button type="primary" @click="getPay">立即支付</el-button>
      <el-button type="primary" @click="clearBack">返回</el-button>
    </div>
  </div>
</template>

<script>
let intervalIndex
export default {
  name: 'WechatJsapiPay',
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
    }
  },
  methods: {
    getPay() {
      // 支付信息
      this.payData = {...JSON.parse(window.sessionStorage.getItem('payData'))}
      // 调用微信支付（微信客户端内）
      // 注意：
      //     1. 只能在微信客户端内完成；
      //     2. 且用户必须先（商户号关联的公众号）授权，需要先获取用户的openid
      // 商户后台设置回调域名：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=7_3

      // let data = {...this.payData, openId: 'o_IQK5AvUyFUouVr5byhnXocxKU4'}
      let data = {...this.payData, openId: 'oA1uP6Q6ByzfrUpXr_N4dPuXmuQk'}
      this.$request({url: '/pay/wechat/getPayJsapi', method: 'post', data})
        .then((response) => {
          let data = JSON.parse(response.data.payInfo);
          if (typeof WeixinJSBridge === 'undefined') {
            if (document.addEventListener) {
              document.addEventListener('WeixinJSBridgeReady',
                this.onBridgeReady(data), false);
            } else if (document.attachEvent) {
              document.attachEvent('WeixinJSBridgeReady',
                this.onBridgeReady(data));
              document.attachEvent('onWeixinJSBridgeReady',
                this.onBridgeReady(data));
            }
          } else {
            this.onBridgeReady(data);
          }
          // 更新微信支付状态
          this.wechatPayUpdateStatus(response.data.tradeId)
        })
    },
    // jsapi微信内支付结果
    onBridgeReady(json) {
      alert(json)
      WeixinJSBridge.invoke('getBrandWCPayRequest', json, function (res) {
        // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
        if (res.err_msg == 'get_brand_wcpay_request:ok') {
          this.$message({type: 'success', message: '支付成功!'})
          this.clearBack()
        } else {
          this.$message({type: 'error', message: '支付失败!'})
        }
      });
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
