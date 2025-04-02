package com.kg.component.pay.wechat.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.RequestMethodEnum;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.enums.WxDomainEnum;
import com.ijpay.wxpay.enums.v3.BasePayApiEnum;
import com.ijpay.wxpay.model.v3.*;
import com.kg.component.pay.wechat.config.WxPayConfig;
import com.kg.component.pay.wechat.dto.WxTradePayDTO;
import com.kg.core.exception.BaseException;
import com.kg.module.trade.entity.BusTrade;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付v3证书版工具
 *
 * @author ziro
 * @date 2023/5/23 8:50
 */
public class WxPayV3Utils {

    /**
     * Native支付
     */
    public static JSONObject nativePay(WxPayConfig wxPayConfig, WxTradePayDTO wxTradePayDTO, BusTrade busTrade)
            throws BaseException {
        try {
            UnifiedOrderModel unifiedOrderModel =
                    WxPayV3Utils.buildPayData(wxPayConfig, wxTradePayDTO, busTrade.getOutTradeNo());
            System.out.println(getSerialNumber(wxPayConfig));
            // 请求支付信息
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    BasePayApiEnum.NATIVE_PAY.toString(),
                    wxPayConfig.getMchId(),
                    getSerialNumber(wxPayConfig),
                    null,
                    wxPayConfig.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            // 根据证书序列号，查询对应的证书来验证签名结果
            // boolean verifySignature = WxPayKit.verifySignature(response, wxPayConfig.getCertPath());
            return JSONUtil.parseObj(response.getBody(), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * H5支付
     */
    public static JSONObject h5Pay(WxPayConfig wxPayConfig, WxTradePayDTO wxTradePayDTO, BusTrade busTrade)
            throws BaseException {
        try {
            UnifiedOrderModel unifiedOrderModel =
                    WxPayV3Utils.buildPayData(wxPayConfig, wxTradePayDTO, busTrade.getOutTradeNo());
            //H5必传：
            unifiedOrderModel.setScene_info(new SceneInfo()
                    .setPayer_client_ip(wxTradePayDTO.getSpbillCreateIp())//终端IP
                    .setH5_info((new H5Info().setType("Wap")))
            );
            // 请求支付信息
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    BasePayApiEnum.H5_PAY.toString(),
                    wxPayConfig.getMchId(),
                    getSerialNumber(wxPayConfig),
                    null,
                    wxPayConfig.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            // 根据证书序列号，查询对应的证书来验证签名结果
            // boolean verifySignature = WxPayKit.verifySignature(response, wxPayConfig.getCertPath());
            return JSONUtil.parseObj(response.getBody(), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * JSAPI支付
     */
    public static String jsApiPay(WxPayConfig wxPayConfig, WxTradePayDTO wxTradePayDTO, BusTrade busTrade)
            throws BaseException {
        try {
            UnifiedOrderModel unifiedOrderModel =
                    WxPayV3Utils.buildPayData(wxPayConfig, wxTradePayDTO, busTrade.getOutTradeNo());
            //JSAPI必传：openid
            unifiedOrderModel.setPayer(new Payer().setOpenid(wxTradePayDTO.getOpenId()));
            // 请求支付信息
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    BasePayApiEnum.JS_API_PAY.toString(),
                    wxPayConfig.getMchId(),
                    getSerialNumber(wxPayConfig),
                    null,
                    wxPayConfig.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel)
            );
            // 根据证书序列号，查询对应的证书来验证签名结果
            // boolean verifySignature = WxPayKit.verifySignature(response, wxPayConfig.getCertPath());
            JSONObject result = JSONUtil.parseObj(response.getBody(), true);
            if (result.containsKey("prepay_id")) {
                Map<String, String> map = WxPayKit.jsApiCreateSign(wxPayConfig.getAppId(),
                        result.getStr("prepay_id"), wxPayConfig.getKeyPath());
                return JSONUtil.toJsonStr(map);
            }
            throw new BaseException(result.getStr("message"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }


    public static String queryOrder(WxPayConfig wxPayConfig, String outTradeNo) throws BaseException {
        try {
            Map<String, String> params = new HashMap<>(1);
            params.put("mchid", wxPayConfig.getMchId());
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    WxDomainEnum.CHINA.toString(),
                    String.format(BasePayApiEnum.ORDER_QUERY_BY_OUT_TRADE_NO.toString(), outTradeNo),
                    wxPayConfig.getMchId(),
                    getSerialNumber(wxPayConfig),
                    null,
                    wxPayConfig.getKeyPath(),
                    params
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 组装支付数据
     * UnifiedOrderModel
     */
    public static UnifiedOrderModel buildPayData(WxPayConfig wxPayConfig, WxTradePayDTO wxTradePayDTO,
                                                 String outTradeNo) throws BaseException {
        return UnifiedOrderModel.builder()
                .appid(wxPayConfig.getAppId())//公众账号ID
                .mchid(wxPayConfig.getMchId())//商户号
                .description(wxTradePayDTO.getProductName())//商品描述(用户支付时显示)
                .attach(wxTradePayDTO.getAttach())//附加内容(在查询API和支付通知中原样返回)
                .out_trade_no(outTradeNo)//商户订单号
                .amount(new Amount().setTotal(wxTradePayDTO.getTotalFee()))//金额(单位:分)
                .notify_url(wxPayConfig.getNotifyUrl())
                .build();
    }

    /**
     * 获取商户证书序列号
     */
    public static String getSerialNumber(WxPayConfig wxPayConfig) throws BaseException {
        // 获取证书序列号
        X509Certificate certificate = PayKit.getCertificate(wxPayConfig.getCertPath());
        if (null != certificate) {
            // 提前两天检查证书是否有效
            boolean isValid = PayKit.checkCertificateIsValid(certificate, wxPayConfig.getMchId(), -2);
            if (isValid) {
                return certificate.getSerialNumber().toString(16).toUpperCase();
            } else {
                throw new BaseException("证书已过期！");
            }
        }
        throw new BaseException("获取商户证书序列号失败！");
    }
}
