package com.kg.component.pay.wechat.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.AesUtil;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.model.OrderQueryModel;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import com.kg.component.pay.wechat.config.WxPayConfig;
import com.kg.component.pay.wechat.dto.TradePayDTO;
import com.kg.component.pay.wechat.dto.TradeResutDTO;
import com.kg.component.pay.wechat.utils.WxPayV3Utils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.xss.XssFormatUtil;
import com.kg.module.trade.constant.TradeConstant;
import com.kg.module.trade.entity.BusTrade;
import com.kg.module.trade.service.BusTradeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信支付服务
 *
 * @author ziro
 * @date 2023/5/22 17:07
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private WxPayConfig wxPayConfig;
    @Resource
    private BusTradeService tradeService;

    @Override
    public TradeResutDTO getPayInfo(TradePayDTO tradePayDTO, String tradeType) throws BaseException {
        // 统一支付：组装订单信息
        BusTrade busTrade = new BusTrade();
        busTrade.setTradeId(GuidUtils.getUuid());
        busTrade.setProductId(tradePayDTO.getProductId());
        busTrade.setUserId(StringUtils.hasText(tradePayDTO.getUserId())
                ? tradePayDTO.getUserId() : CurrentUserUtils.getCurrentUser().getUserId());
        busTrade.setPayType(0);
        busTrade.setTradeStatus(TradeConstant.TRADE_FAIL);
        busTrade.setTotalFee(tradePayDTO.getTotalFee());
        busTrade.setOutTradeNo(WxPayKit.generateStr());
        busTrade.setAttach(tradePayDTO.getAttach());
        busTrade.setCreateTime(LocalDateTime.now());

        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版支付
            return toV3Pay(tradePayDTO, tradeType, busTrade);
        } else {
            // V2版支付
            return toV2Pay(tradePayDTO, tradeType, busTrade);
        }
    }

    @Override
    public String payNotify(String result) {
        if (wxPayConfig.getKeyVersion().equals("v3")) {
            System.out.println("=========================");
            System.out.println(result);
            // V3版支付通知
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
        } else {
            // V2版支付通知
            Map<String, String> resultMap = WxPayKit.xmlToMap(XssFormatUtil.toHtml(result));
            // 以下字段在 return_code 和 result_code 都为 SUCCESS 的时候代表支付成功
            if (WxPayKit.codeIsOk(resultMap.get("return_code"))) {
                if (WxPayKit.codeIsOk(resultMap.get("result_code"))) {
                    System.out.println("=====================result==========payNotify=========");
                    System.out.println(resultMap);
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
        }
        return null;
    }

    @Override
    public TradeResutDTO getPayResult(TradeResutDTO tradeResutDTO) {
        BusTrade b = tradeService.lambdaQuery()
                .eq(BusTrade::getTradeId, tradeResutDTO.getTradeId()).one();
        if (b != null && TradeConstant.TRADE_SUCCESS.intValue() == b.getTradeStatus().intValue()) {
            // 已经支付成功
            tradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
            return tradeResutDTO;
        }

        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版支付结果查询
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
                    tradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                    return tradeResutDTO;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tradeResutDTO.setTradeStatus(TradeConstant.TRADE_FAIL);
            return tradeResutDTO;
        } else {
            // V2版支付结果查询
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
                        tradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                        return tradeResutDTO;
                    }
                }
            }
            tradeResutDTO.setTradeStatus(TradeConstant.TRADE_FAIL);
            return tradeResutDTO;
        }
    }

    /**
     * 统一支付v3版本
     */
    private TradeResutDTO toV3Pay(TradePayDTO tradePayDTO, String tradeType, BusTrade busTrade)
            throws BaseException {
        TradeResutDTO tradeResutDTO = new TradeResutDTO();
        tradeResutDTO.setTradeId(busTrade.getTradeId());

        if (tradeType.equals(TradeType.NATIVE.getTradeType())) {
            JSONObject result = WxPayV3Utils.nativePay(wxPayConfig, tradePayDTO, busTrade);
            if (result != null && result.containsKey("code_url")) {
                tradeService.save(busTrade);
                // PC支付二维码
                tradeResutDTO.setQrCodeUrl(result.getStr("code_url"));
                return tradeResutDTO;
            }
        } else if (tradeType.equals(TradeType.MWEB.getTradeType())) {
            JSONObject result = WxPayV3Utils.h5Pay(wxPayConfig, tradePayDTO, busTrade);
            if (result != null && result.containsKey("h5_url")) {
                tradeService.save(busTrade);
                // H5支付链接
                tradeResutDTO.setH5Url(result.getStr("h5_url"));
                return tradeResutDTO;
            }
        } else if (tradeType.equals(TradeType.JSAPI.getTradeType())) {
            String payInfo = WxPayV3Utils.jsApiPay(wxPayConfig, tradePayDTO, busTrade);
            if (StringUtils.hasText(payInfo)) {
                tradeService.save(busTrade);
                // JSAPI支付信息
                tradeResutDTO.setPayInfo(payInfo);
                return tradeResutDTO;
            }
        }
        throw new BaseException("未知错误");
    }

    /**
     * 统一支付v2版本
     */
    private TradeResutDTO toV2Pay(TradePayDTO tradePayDTO, String tradeType, BusTrade busTrade)
            throws BaseException {
        // 统一支付
        Map<String, String> params = UnifiedOrderModel.builder()
                .appid(wxPayConfig.getAppId())//公众账号ID
                .mch_id(wxPayConfig.getMchId())//商户号
                .nonce_str(WxPayKit.generateStr())//随机字符串
                .body(tradePayDTO.getProductName())//商品描述（用户支付时显示）
                .product_id(tradePayDTO.getProductId())//商品id（trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。）
                .attach(tradePayDTO.getAttach())//附加内容（在查询API和支付通知中原样返回）
                .out_trade_no(busTrade.getOutTradeNo())//商户订单号
                .total_fee(busTrade.getTotalFee() + "")//金额：单位 分
                .spbill_create_ip(tradePayDTO.getSpbillCreateIp())//终端IP
                .notify_url(wxPayConfig.getDomain().concat("/pay/wechat/payNotify"))//通知地址（回调）
                .trade_type(tradeType)//交易类型
                .openid(tradePayDTO.getOpenId())//openid，tradeType=JSAPI方式必传
                .build()
                .createSign(wxPayConfig.getPartnerKey(), SignType.HMACSHA256);

        String xmlResult = WxPayApi.pushOrder(false, params);
        // 以下字段在 return_code 和 result_code 都为 SUCCESS 的时候代表支付成功
        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
        if (WxPayKit.codeIsOk(result.get("return_code"))) {
            if (WxPayKit.codeIsOk(result.get("result_code"))) {
                tradeService.save(busTrade);

                //生成预付订单success
                TradeResutDTO tradeResutDTO = new TradeResutDTO();
                tradeResutDTO.setTradeId(busTrade.getTradeId());
                // 不同类型支付，返回不同数据
                if (tradeType.equals(TradeType.NATIVE.getTradeType())) {
                    tradeResutDTO.setQrCodeUrl(result.get("code_url"));
                } else if (tradeType.equals(TradeType.MWEB.getTradeType())) {
                    tradeResutDTO.setH5Url(result.get("mweb_url"));
                } else if (tradeType.equals(TradeType.JSAPI.getTradeType())) {
                    Map<String, String> packageParams =
                            WxPayKit.prepayIdCreateSign(result.get("prepay_id"),
                                    wxPayConfig.getAppId(),
                                    wxPayConfig.getPartnerKey(), SignType.HMACSHA256);
                    tradeResutDTO.setPayInfo(JSONUtil.toJsonStr(packageParams));
                }
                return tradeResutDTO;
            }
        }
        throw new BaseException(StringUtils.hasText(result.get("err_code_des")) ? result.get("err_code_des") : "未知错误");
    }
}
