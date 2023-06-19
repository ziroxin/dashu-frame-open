package com.kg.component.pay.wechat.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.model.OrderQueryModel;
import com.ijpay.wxpay.model.RefundModel;
import com.ijpay.wxpay.model.RefundQueryModel;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import com.kg.component.pay.wechat.config.WxPayConfig;
import com.kg.component.pay.wechat.dto.WxTradePayDTO;
import com.kg.component.pay.wechat.dto.WxTradeRefundDTO;
import com.kg.component.pay.wechat.dto.WxTradeResutDTO;
import com.kg.core.exception.BaseException;
import com.kg.core.xss.XssFormatUtil;
import com.kg.module.trade.constant.TradeConstant;
import com.kg.module.trade.entity.BusTrade;
import com.kg.module.trade.service.BusTradeService;
import com.kg.module.tradeRefund.entity.BusTradeRefund;
import com.kg.module.tradeRefund.service.BusTradeRefundService;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * V2 版微信支付工具
 *
 * @author ziro
 * @date 2023/6/14 10:23
 */
public class WxPayV2ServiceUtils {
    /**
     * 统一支付v2版本
     */
    public static WxTradeResutDTO toV2Pay(WxPayConfig wxPayConfig, BusTradeService tradeService,
                                          WxTradePayDTO wxTradePayDTO, String tradeType, BusTrade busTrade)
            throws BaseException {
        // 统一支付
        Map<String, String> params = UnifiedOrderModel.builder()
                .appid(wxPayConfig.getAppId())//公众账号ID
                .mch_id(wxPayConfig.getMchId())//商户号
                .nonce_str(WxPayKit.generateStr())//随机字符串
                .body(wxTradePayDTO.getProductName())//商品描述（用户支付时显示）
                .product_id(wxTradePayDTO.getProductId())//商品id（trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。）
                .attach(wxTradePayDTO.getAttach())//附加内容（在查询API和支付通知中原样返回）
                .out_trade_no(busTrade.getOutTradeNo())//商户订单号
                .total_fee(busTrade.getTotalFee() + "")//金额：单位 分
                .spbill_create_ip(wxTradePayDTO.getSpbillCreateIp())//终端IP
                .notify_url(wxPayConfig.getNotifyUrl())//通知地址（回调）
                .trade_type(tradeType)//交易类型
                .openid(wxTradePayDTO.getOpenId())//openid，tradeType=JSAPI方式必传
                .build()
                .createSign(wxPayConfig.getPartnerKey(), SignType.HMACSHA256);

        String xmlResult = WxPayApi.pushOrder(false, params);
        // 以下字段在 return_code 和 result_code 都为 SUCCESS 的时候代表支付成功
        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
        if (WxPayKit.codeIsOk(result.get("return_code"))) {
            if (WxPayKit.codeIsOk(result.get("result_code"))) {
                tradeService.save(busTrade);

                //生成预付订单success
                WxTradeResutDTO wxTradeResutDTO = new WxTradeResutDTO();
                wxTradeResutDTO.setTradeId(busTrade.getTradeId());
                // 不同类型支付，返回不同数据
                if (tradeType.equals(TradeType.NATIVE.getTradeType())) {
                    wxTradeResutDTO.setQrCodeUrl(result.get("code_url"));
                } else if (tradeType.equals(TradeType.MWEB.getTradeType())) {
                    wxTradeResutDTO.setH5Url(result.get("mweb_url"));
                } else if (tradeType.equals(TradeType.JSAPI.getTradeType())) {
                    Map<String, String> packageParams =
                            WxPayKit.prepayIdCreateSign(result.get("prepay_id"),
                                    wxPayConfig.getAppId(),
                                    wxPayConfig.getPartnerKey(), SignType.HMACSHA256);
                    wxTradeResutDTO.setPayInfo(JSONUtil.toJsonStr(packageParams));
                }
                return wxTradeResutDTO;
            }
        }
        throw new BaseException(StringUtils.hasText(result.get("err_code_des")) ? result.get("err_code_des") : "未知错误");
    }

    /**
     * V2版支付通知
     */
    public static String payNotify(String result, WxPayConfig wxPayConfig, BusTradeService tradeService) {
        Map<String, String> resultMap = WxPayKit.xmlToMap(XssFormatUtil.toHtml(result));
        // 以下字段在 return_code 和 result_code 都为 SUCCESS 的时候代表支付成功
        if (WxPayKit.codeIsOk(resultMap.get("return_code"))) {
            if (WxPayKit.codeIsOk(resultMap.get("result_code"))) {
                JSONObject resultObj = JSONUtil.parseObj(resultMap);
                List<BusTrade> list = tradeService.lambdaQuery().eq(BusTrade::getOutTradeNo, resultObj.getStr("out_trade_no")).list();
                BusTrade busTrade = list != null && list.size() > 0 ? list.get(0) : new BusTrade();
                // 支付成功，且金额正确
                if (busTrade.getTotalFee().toString().equals(resultObj.getStr("total_fee"))) {
                    // 保存订单信息
                    busTrade.setTradeOpenId(resultObj.getStr("openid"));
                    busTrade.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                    busTrade.setPaySuccessTime(LocalDateTime.now());
                    busTrade.setResultJson(resultMap.toString());
                    busTrade.setUpdateTime(LocalDateTime.now());
                    tradeService.updateById(busTrade);
                    // 给微信返回成功，不再继续回调
                    Map<String, String> prepayParams = new HashMap<>(1);
                    prepayParams.put("return_code", "SUCCESS");
                    return WxPayKit.toXml(prepayParams);
                }
            }
        }
        return null;
    }

    /**
     * V2版支付结果查询
     */
    public static WxTradeResutDTO orderQuery(WxPayConfig wxPayConfig, BusTradeService tradeService, BusTrade b, WxTradeResutDTO wxTradeResutDTO) {
        Map<String, String> params = OrderQueryModel.builder()
                .appid(wxPayConfig.getAppId())//公众账号ID
                .mch_id(wxPayConfig.getMchId())//商户号
                //.transaction_id(transactionId)//二选一：微信订单号（微信回调时返回的）
                .out_trade_no(b.getOutTradeNo())//二选一：商户订单号（自己生成的）
                .nonce_str(WxPayKit.generateStr())
                .build()
                .createSign(wxPayConfig.getPartnerKey(), SignType.MD5);
        String xmlResult = WxPayApi.orderQuery(params);
        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
        System.out.println(JSONUtil.toJsonStr(result));
        // 在 return_code 和 result_code 都为 SUCCESS 的时候有返回
        if (WxPayKit.codeIsOk(result.get("return_code"))) {
            if (WxPayKit.codeIsOk(result.get("result_code"))) {
                // 支付成功，且金额正确
                JSONObject resultObj = JSONUtil.parseObj(result);
                if (TradeConstant.TRADE_SUCCESS_WECHAT.equals(resultObj.getStr("trade_state"))
                        && b.getTotalFee().toString().equals(resultObj.getStr("total_fee"))) {
                    // 保存订单信息
                    b.setTradeOpenId(resultObj.getStr("openid"));
                    b.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                    b.setPaySuccessTime(LocalDateTime.now());
                    b.setResultJson(result.toString());
                    b.setUpdateTime(LocalDateTime.now());
                    tradeService.updateById(b);
                    // 返回支付成功
                    wxTradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                    return wxTradeResutDTO;
                }
            }
        }
        wxTradeResutDTO.setTradeStatus(TradeConstant.TRADE_FAIL);
        return wxTradeResutDTO;
    }

    /**
     * V2版退款
     */
    public static WxTradeRefundDTO refund(WxPayConfig wxPayConfig, BusTradeRefundService refundService,
                                          BusTrade busTrade, BusTradeRefund refund,
                                          WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        try {
            Map<String, String> params = RefundModel.builder()
                    .appid(wxPayConfig.getAppId())
                    .mch_id(wxPayConfig.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .out_trade_no(busTrade.getOutTradeNo())
                    .out_refund_no(refund.getOutRefundNo())
                    .total_fee(busTrade.getTotalFee() + "")
                    .refund_fee(refund.getRefundFee() + "")
                    .refund_desc(refund.getRefundDesc())//退款原因
                    // 退款结果通知地址，非必填（本项目采用主动查询，所有暂不用配置该地址）
                    //.notify_url()
                    .build()
                    .createSign(wxPayConfig.getPartnerKey(), SignType.MD5);
            InputStream certFileInputStream = PayKit.getCertFileInputStream(wxPayConfig.getCertP12Path());
            // 调用微信退款接口
            String xmlResult = WxPayApi.orderRefundByProtocol(false, params,
                    // 注意：最后一个参数protocal，不能用默认的TLSv1（有安全隐患，微信不支持），所以传null自动处理
                    certFileInputStream, wxPayConfig.getMchId(), null);

            // 以下字段在 return_code 和 result_code 都为 SUCCESS 的时候代表支付成功
            Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
            if (WxPayKit.codeIsOk(result.get("return_code"))) {
                if (WxPayKit.codeIsOk(result.get("result_code"))) {
                    // 保存退款信息
                    refund.setRefundStatus(TradeConstant.REFUNDING);
                    refund.setCreateTime(LocalDateTime.now());
                    refundService.save(refund);
                    // 申请退款预订单成功（状态：退款中）
                    wxTradeRefundDTO.setRefundId(refund.getRefundId());
                    wxTradeRefundDTO.setTradeId(refund.getTradeId());
                    wxTradeRefundDTO.setRefundStatus(TradeConstant.REFUNDING);
                    return wxTradeRefundDTO;
                }
            } else {
                throw new BaseException(result.get("return_msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
        throw new BaseException("未知错误");
    }

    /**
     * V2版查询退款信息
     */
    public static WxTradeRefundDTO queryRefund(WxPayConfig wxPayConfig, BusTradeRefundService refundService,
                                               BusTradeService busTradeService, BusTradeRefund refund,
                                               WxTradeRefundDTO wxTradeRefundDTO) {
        Map<String, String> params = RefundQueryModel.builder()
                .appid(wxPayConfig.getAppId())
                .mch_id(wxPayConfig.getMchId())
                .nonce_str(WxPayKit.generateStr())
                .out_refund_no(refund.getOutRefundNo())
                .build()
                .createSign(wxPayConfig.getPartnerKey(), SignType.MD5);

        String xmlResult = WxPayApi.orderRefundQuery(false, params);
        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
        if (WxPayKit.codeIsOk(result.get("return_code"))) {
            if (WxPayKit.codeIsOk(result.get("result_code"))) {
                // 退款状态（下标_0，用out_refund_no查询，下标始始为0）
                if ("SUCCESS".equals(result.get("refund_status_0"))) {
                    // 更新退款信息
                    refund.setRefundStatus(TradeConstant.REFUND_SUCCESS);
                    // 格式：2016-07-25 15:26:26
                    refund.setRefundSuccessTime(LocalDateTime.parse(result.get("refund_success_time_0"),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    refund.setRefundResultJson(JSONUtil.toJsonStr(result));
                    refund.setUpdateTime(LocalDateTime.now());
                    refundService.updateById(refund);
                    // 更新主表退款总金额
                    BusTrade busTrade = busTradeService.getById(refund.getTradeId());
                    Integer refundTotalFee = busTrade.getRefundTotalFee() == null ? 0 : busTrade.getRefundTotalFee();
                    busTrade.setRefundTotalFee(refundTotalFee + refund.getRefundFee());
                    busTradeService.updateById(busTrade);
                    // 返回数据
                    wxTradeRefundDTO.setRefundStatus(TradeConstant.REFUND_SUCCESS);
                    return wxTradeRefundDTO;
                } else if ("CHANGE".equals(result.get("refund_status_0")) ||
                        "REFUNDCLOSE".equals(result.get("refund_status_0"))) {
                    // 退款异常 || 退款关闭
                    refund.setRefundStatus(TradeConstant.REFUND_FAIL);
                    refund.setRefundResultJson(JSONUtil.toJsonStr(result));
                    refundService.updateById(refund);

                    wxTradeRefundDTO.setRefundStatus(TradeConstant.REFUND_FAIL);
                    return wxTradeRefundDTO;
                }
            }
        }
        wxTradeRefundDTO.setRefundStatus(refund.getRefundStatus());
        return wxTradeRefundDTO;
    }
}
