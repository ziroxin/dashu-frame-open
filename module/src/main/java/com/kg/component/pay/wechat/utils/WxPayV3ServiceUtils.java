package com.kg.component.pay.wechat.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.RequestMethodEnum;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.AesUtil;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.enums.WxDomainEnum;
import com.ijpay.wxpay.enums.v3.BasePayApiEnum;
import com.ijpay.wxpay.model.v3.RefundAmount;
import com.ijpay.wxpay.model.v3.RefundModel;
import com.kg.component.pay.wechat.config.WxPayConfig;
import com.kg.component.pay.wechat.dto.WxTradePayDTO;
import com.kg.component.pay.wechat.dto.WxTradeRefundDTO;
import com.kg.component.pay.wechat.dto.WxTradeResutDTO;
import com.kg.core.exception.BaseException;
import com.kg.module.trade.constant.TradeConstant;
import com.kg.module.trade.entity.BusTrade;
import com.kg.module.trade.service.BusTradeService;
import com.kg.module.tradeRefund.entity.BusTradeRefund;
import com.kg.module.tradeRefund.service.BusTradeRefundService;
import org.springframework.util.StringUtils;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * V3 版微信支付工具
 *
 * @author ziro
 * @date 2023/6/14 10:22
 */
public class WxPayV3ServiceUtils {
    /**
     * 统一支付v3版本
     */
    public static WxTradeResutDTO toV3Pay(WxPayConfig wxPayConfig, BusTradeService tradeService,
                                          WxTradePayDTO wxTradePayDTO, String tradeType, BusTrade busTrade)
            throws BaseException {
        WxTradeResutDTO wxTradeResutDTO = new WxTradeResutDTO();
        wxTradeResutDTO.setTradeId(busTrade.getTradeId());

        if (tradeType.equals(TradeType.NATIVE.getTradeType())) {
            JSONObject result = WxPayV3Utils.nativePay(wxPayConfig, wxTradePayDTO, busTrade);
            if (result != null && result.containsKey("code_url")) {
                tradeService.save(busTrade);
                // PC支付二维码
                wxTradeResutDTO.setQrCodeUrl(result.getStr("code_url"));
                return wxTradeResutDTO;
            }
        } else if (tradeType.equals(TradeType.MWEB.getTradeType())) {
            JSONObject result = WxPayV3Utils.h5Pay(wxPayConfig, wxTradePayDTO, busTrade);
            if (result != null && result.containsKey("h5_url")) {
                tradeService.save(busTrade);
                // H5支付链接
                wxTradeResutDTO.setH5Url(result.getStr("h5_url"));
                return wxTradeResutDTO;
            }
        } else if (tradeType.equals(TradeType.JSAPI.getTradeType())) {
            String payInfo = WxPayV3Utils.jsApiPay(wxPayConfig, wxTradePayDTO, busTrade);
            if (StringUtils.hasText(payInfo)) {
                tradeService.save(busTrade);
                // JSAPI支付信息
                wxTradeResutDTO.setPayInfo(payInfo);
                return wxTradeResutDTO;
            }
        }
        throw new BaseException("未知错误");
    }

    /**
     * V3版支付通知
     */
    public static String payNotify(String result, WxPayConfig wxPayConfig, BusTradeService tradeService) {
        JSONObject resultJson = JSONUtil.parseObj(result);// 密文数据
        if ("TRANSACTION.SUCCESS".equals(resultJson.getStr("event_type"))
                && "encrypt-resource".equals(resultJson.getStr("resource_type"))) {
            // 解密
            AesUtil aesUtil = new AesUtil(wxPayConfig.getPartnerKeyV3().getBytes());
            JSONObject resource = resultJson.getJSONObject("resource");
            try {
                // 密文数据
                String data = aesUtil.decryptToString(resource.getStr("associated_data").getBytes(),
                        resource.getStr("nonce").getBytes(),
                        resource.getStr("ciphertext"));
                JSONObject dataObj = JSONUtil.parseObj(data);
                if (WxPayKit.codeIsOk(dataObj.getStr("trade_state"))) {
                    List<BusTrade> list = tradeService.lambdaQuery().eq(BusTrade::getOutTradeNo, dataObj.getStr("out_trade_no")).list();
                    BusTrade busTrade = list != null && list.size() > 0 ? list.get(0) : new BusTrade();
                    // 支付成功，且金额正确
                    if (busTrade.getTotalFee().toString().equals(dataObj.getJSONObject("amount").getStr("total"))) {
                        // 保存订单信息
                        busTrade.setTradeOpenId(dataObj.getJSONObject("payer").getStr("openid"));
                        busTrade.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                        busTrade.setPaySuccessTime(LocalDateTime.now());
                        busTrade.setResultJson(dataObj.toString());
                        busTrade.setUpdateTime(LocalDateTime.now());
                        tradeService.updateById(busTrade);
                        // 给微信返回成功，不再继续回调
                        Map<String, String> prepayParams = new HashMap<>(2);
                        prepayParams.put("code", "SUCCESS");
                        prepayParams.put("message", "SUCCESS");
                        return JSONUtil.toJsonStr(prepayParams);
                    }
                }
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * V3版支付结果查询
     */
    public static WxTradeResutDTO orderQuery(WxPayConfig wxPayConfig, BusTradeService tradeService,
                                             BusTrade b, WxTradeResutDTO wxTradeResutDTO) {
        try {
            String result = WxPayV3Utils.queryOrder(wxPayConfig, b.getOutTradeNo());
            JSONObject resultJson = JSONUtil.parseObj(result);
            if (WxPayKit.codeIsOk(resultJson.getStr("trade_state"))
                    && b.getTotalFee().toString().equals(resultJson.getJSONObject("amount").getStr("total"))) {
                // 保存订单信息
                b.setTradeOpenId(resultJson.getJSONObject("payer").getStr("openid"));
                b.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                b.setPaySuccessTime(LocalDateTime.now());
                b.setResultJson(result);
                b.setUpdateTime(LocalDateTime.now());
                tradeService.updateById(b);
                // 返回支付成功
                wxTradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                return wxTradeResutDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wxTradeResutDTO.setTradeStatus(TradeConstant.TRADE_FAIL);
        return wxTradeResutDTO;
    }

    public static WxTradeRefundDTO refund(WxPayConfig wxPayConfig, BusTradeRefundService refundService,
                                          BusTrade busTrade, BusTradeRefund refund,
                                          WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        try {
            RefundModel refundModel = new RefundModel()
                    .setOut_trade_no(busTrade.getOutTradeNo())
                    .setOut_refund_no(refund.getOutRefundNo())
                    .setReason(refund.getRefundDesc())//退款原因
                    // 退款结果通知地址，非必填（本项目采用主动查询，所有暂不用配置该地址）
                    //.setNotify_url()
                    .setAmount(new RefundAmount().setRefund(refund.getRefundFee())
                            .setTotal(busTrade.getTotalFee()).setCurrency("CNY"));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    BasePayApiEnum.REFUND.toString(),
                    wxPayConfig.getMchId(),
                    WxPayV3Utils.getSerialNumber(wxPayConfig),
                    null,
                    wxPayConfig.getKeyPath(),
                    JSONUtil.toJsonStr(refundModel)
            );
            // 根据证书序列号，查询对应的证书来验证签名结果
            // boolean verifySignature = WxPayKit.verifySignature(response, wxPayConfig.getCertPath());
            JSONObject result = JSONUtil.parseObj(response.getBody());
            if ("SUCCESS".equals(result.getStr("status")) || "PROCESSING".equals(result.getStr("status"))) {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
        throw new BaseException("未知错误");
    }

    public static WxTradeRefundDTO queryRefund(WxPayConfig wxPayConfig, BusTradeRefundService refundService,
                                               BusTradeService busTradeService, BusTradeRefund refund,
                                               WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        try {
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    WxDomainEnum.CHINA.toString(),
                    String.format(BasePayApiEnum.REFUND_QUERY_BY_OUT_REFUND_NO.toString(), refund.getOutRefundNo()),
                    wxPayConfig.getMchId(),
                    WxPayV3Utils.getSerialNumber(wxPayConfig),
                    null,
                    wxPayConfig.getKeyPath(),
                    ""
            );
            // 根据证书序列号，查询对应的证书来验证签名结果
            // boolean verifySignature = WxPayKit.verifySignature(response, wxPayConfig.getCertPath());
            JSONObject result = JSONUtil.parseObj(response.getBody());
            // 退款状态（下标_0，用out_refund_no查询，下标始始为0）
            if ("SUCCESS".equals(result.getStr("status"))) {
                // 更新退款信息
                refund.setRefundStatus(TradeConstant.REFUND_SUCCESS);
                // 格式：2020-12-01T16:18:12+08:00
                refund.setRefundSuccessTime(LocalDateTime.parse(result.getStr("success_time"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                refund.setRefundResultJson(JSONUtil.toJsonStr(result));
                refundService.updateById(refund);
                // 更新主表退款总金额
                BusTrade busTrade = busTradeService.getById(refund.getTradeId());
                Integer refundTotalFee = busTrade.getRefundTotalFee() == null ? 0 : busTrade.getRefundTotalFee();
                busTrade.setRefundTotalFee(refundTotalFee + refund.getRefundFee());
                busTradeService.updateById(busTrade);
                // 返回数据
                wxTradeRefundDTO.setRefundStatus(TradeConstant.REFUND_SUCCESS);
                return wxTradeRefundDTO;
            } else if ("ABNORMAL".equals(result.getStr("status")) ||
                    "CLOSED".equals(result.getStr("status"))) {
                // 退款异常 || 退款关闭
                refund.setRefundStatus(TradeConstant.REFUND_FAIL);
                refund.setRefundResultJson(JSONUtil.toJsonStr(result));
                refundService.updateById(refund);

                wxTradeRefundDTO.setRefundStatus(TradeConstant.REFUND_FAIL);
                return wxTradeRefundDTO;
            }
            wxTradeRefundDTO.setRefundStatus(refund.getRefundStatus());
            return wxTradeRefundDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }
}
