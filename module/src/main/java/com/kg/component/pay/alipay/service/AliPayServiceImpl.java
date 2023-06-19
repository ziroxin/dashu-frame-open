package com.kg.component.pay.alipay.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.core.kit.WxPayKit;
import com.kg.component.pay.alipay.config.AliPayConfig;
import com.kg.component.pay.alipay.dto.AliTradePayDTO;
import com.kg.component.pay.alipay.dto.AliTradeRefundDTO;
import com.kg.component.pay.alipay.dto.AliTradeResutDTO;
import com.kg.component.utils.GuidUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.module.trade.constant.TradeConstant;
import com.kg.module.trade.entity.BusTrade;
import com.kg.module.trade.service.BusTradeService;
import com.kg.module.tradeRefund.entity.BusTradeRefund;
import com.kg.module.tradeRefund.service.BusTradeRefundService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ziro
 * @date 2023/5/24 14:57
 */
@Service
public class AliPayServiceImpl implements AliPayService {

    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private BusTradeService tradeService;
    @Resource
    private BusTradeRefundService refundService;

    @Override
    public void toPcPay(HttpServletResponse response, AliTradePayDTO tradePayDTO) {
        saveTradeToPay(response, tradePayDTO, "pc");
    }

    @Override
    public void toWapPay(HttpServletResponse response, AliTradePayDTO tradePayDTO) {
        saveTradeToPay(response, tradePayDTO, "wap");
    }

    /**
     * 获取支付二维码信息
     *
     * @param tradePayDTO 支付参数
     */
    @Override
    public AliTradeResutDTO scanPay(AliTradePayDTO tradePayDTO) throws BaseException {
        // 保存订单
        BusTrade busTrade = new BusTrade();
        busTrade.setTradeId(GuidUtils.getUuid());
        busTrade.setProductId(tradePayDTO.getProductId());
        busTrade.setUserId(StringUtils.hasText(tradePayDTO.getUserId())
                ? tradePayDTO.getUserId() : CurrentUserUtils.getCurrentUser().getUserId());
        busTrade.setPayType(TradeConstant.PAY_TYPE_ALIPAY);
        busTrade.setTradeStatus(TradeConstant.TRADE_FAIL);
        busTrade.setTotalFee(tradePayDTO.getTotalFee());
        busTrade.setOutTradeNo(GuidUtils.getUuid32());
        busTrade.setAttach(tradePayDTO.getAttach());
        busTrade.setCreateTime(LocalDateTime.now());
        tradeService.save(busTrade);

        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        // 商户订单号
        model.setOutTradeNo(busTrade.getOutTradeNo());
        // 商品名称（支付时显示的名称）
        model.setSubject(tradePayDTO.getProductName());
        // 支付金额（计算成单位元，保留2位小数）
        BigDecimal totalFee = new BigDecimal(tradePayDTO.getTotalFee());
        BigDecimal totalAmount = totalFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        model.setTotalAmount(totalAmount.toString());
        // 附加信息（通知时原样返回）
        model.setBody(tradePayDTO.getAttach());

        try {
            AlipayTradePrecreateResponse response = AliPayApi.tradePrecreatePayToResponse(model, aliPayConfig.getNotifyUrl());
            return AliTradeResutDTO.builder()
                    .tradeId(busTrade.getTradeId())
                    .qrCodeUrl(response.getQrCode())
                    .build();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public String payNotify(Map<String, String> params) {
        try {
            boolean verifyResult = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAliPayPublicKey(), "UTF-8", "RSA2");
            if (verifyResult && StringUtils.hasText(params.get("trade_status"))) {
                if (params.get("trade_status").equals("TRADE_SUCCESS")) {
                    // 支付成功
                    List<BusTrade> list = tradeService.lambdaQuery().eq(BusTrade::getOutTradeNo, params.get("out_trade_no")).list();
                    BusTrade busTrade = list != null && list.size() > 0 ? list.get(0) : new BusTrade();
                    // 支付成功，且金额正确
                    if (busTrade.getTotalFee().longValue() ==
                            new BigDecimal(params.get("total_amount")).multiply(new BigDecimal(100)).longValue()) {
                        // 保存订单信息
                        busTrade.setTradeOpenId(params.get("open_id"));
                        busTrade.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                        busTrade.setPaySuccessTime(LocalDateTime.now());
                        busTrade.setResultJson(JSONUtil.toJsonStr(params));
                        busTrade.setUpdateTime(LocalDateTime.now());
                        tradeService.updateById(busTrade);
                        // 给支付宝返回成功，不再继续回调
                        return "success";
                    }
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "failure";
    }

    @Override
    public AliTradeResutDTO getPayResult(AliTradeResutDTO tradeResutDTO) {
        BusTrade b = tradeService.lambdaQuery()
                .eq(BusTrade::getTradeId, tradeResutDTO.getTradeId()).one();
        if (b != null && TradeConstant.TRADE_SUCCESS.intValue() == b.getTradeStatus().intValue()) {
            // 已经支付成功
            tradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
            return tradeResutDTO;
        }

        try {
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(b.getOutTradeNo());
            AlipayTradeQueryResponse response = AliPayApi.tradeQueryToResponse(model);

            if (StringUtils.hasText(response.getTradeStatus()) && response.getTradeStatus().equals("TRADE_SUCCESS")) {
                // 支付成功
                // 支付成功，且金额正确
                if (b.getTotalFee().longValue() ==
                        new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal(100)).longValue()) {
                    // 保存订单信息
                    b.setTradeOpenId(response.getOpenId());
                    b.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                    b.setPaySuccessTime(LocalDateTime.now());
                    b.setResultJson(JSONUtil.toJsonStr(response));
                    b.setUpdateTime(LocalDateTime.now());
                    tradeService.updateById(b);
                    // 给支付宝返回成功，不再继续回调
                    tradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
                    return tradeResutDTO;
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        tradeResutDTO.setTradeStatus(TradeConstant.TRADE_FAIL);
        return tradeResutDTO;
    }

    @Override
    public AliTradeRefundDTO refund(AliTradeRefundDTO tradeRefundDTO) throws BaseException {
        // 是否有退款中的订单
        Long count = refundService.lambdaQuery()
                .eq(BusTradeRefund::getTradeId, tradeRefundDTO.getTradeId())
                .eq(BusTradeRefund::getRefundStatus, 0).count();
        if (count > 0) {
            throw new BaseException("有" + count + "笔订单正在退款中，为防止重复退款，请稍后再试！");
        }
        // 校验退款金额
        BusTrade busTrade = tradeService.lambdaQuery().eq(BusTrade::getTradeId, tradeRefundDTO.getTradeId()).one();
        Integer refundTotalFee = busTrade.getRefundTotalFee() == null ? 0 : busTrade.getRefundTotalFee();
        if (busTrade.getTotalFee() < (refundTotalFee + tradeRefundDTO.getRefundFee())) {
            throw new BaseException("退款金额合计，不能大于支付总金额!");
        }
        // 生成退款信息
        BusTradeRefund refund = new BusTradeRefund();
        refund.setRefundId(GuidUtils.getUuid());
        refund.setTradeId(tradeRefundDTO.getTradeId());
        refund.setOutRefundNo(WxPayKit.generateStr());
        refund.setRefundFee(tradeRefundDTO.getRefundFee());
        refund.setRefundDesc(tradeRefundDTO.getRefundDesc());
        refund.setRefundIndex(refundService.lambdaQuery()
                .eq(BusTradeRefund::getTradeId, tradeRefundDTO.getTradeId())
                .count().intValue());
        try {
            // 退款
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(busTrade.getOutTradeNo());
            // 退款金额（计算成单位元，保留两位小数）
            BigDecimal refundFee = new BigDecimal(refund.getRefundFee());
            BigDecimal refundAmount = refundFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            model.setRefundAmount(refundAmount.toString());
            model.setRefundReason(refund.getRefundDesc());
            model.setOutRequestNo(refund.getOutRefundNo());
            AlipayTradeRefundResponse response = AliPayApi.tradeRefundToResponse(model);
            if (response.isSuccess()) {
                // 保存退款信息
                refund.setRefundStatus(TradeConstant.REFUNDING);
                refund.setCreateTime(LocalDateTime.now());
                refundService.save(refund);
                // 申请退款预订单成功（状态：退款中）
                tradeRefundDTO.setRefundId(refund.getRefundId());
                tradeRefundDTO.setTradeId(refund.getTradeId());
                tradeRefundDTO.setRefundStatus(TradeConstant.REFUNDING);
                return tradeRefundDTO;
            } else {
                // 申请失败
                throw new BaseException(response.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public AliTradeRefundDTO queryRefund(AliTradeRefundDTO tradeRefundDTO) throws BaseException {
        // 查询退款记录
        BusTradeRefund refund = refundService.getById(tradeRefundDTO.getRefundId());
        // 退款记录不存在
        if (refund == null) {
            throw new BaseException("退款信息不存在，请刷新重试！");
        }
        // 退款状态已更新，直接返回
        if (refund.getRefundStatus().intValue() != 0) {
            return JSONUtil.toBean(JSONUtil.parseObj(refund), AliTradeRefundDTO.class);
        }
        try {
            // 查询退款结果
            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            BusTrade busTrade = tradeService.getById(refund.getTradeId());
            model.setOutTradeNo(busTrade.getOutTradeNo());
            model.setOutRequestNo(refund.getOutRefundNo());
            model.setQueryOptions(Arrays.asList("gmt_refund_pay"));// 要求返回请求成功时间
            AlipayTradeFastpayRefundQueryResponse response = AliPayApi.tradeRefundQueryToResponse(model);
            if (StringUtils.hasText(response.getRefundStatus()) && response.getRefundStatus().equals("REFUND_SUCCESS")) {
                // 退款成功
                refund.setRefundStatus(TradeConstant.REFUND_SUCCESS);
                // 格式：2014-11-27 15:45:57
                String format = DateUtil.format(response.getGmtRefundPay(), "yyyy-MM-dd HH:mm:ss");
                refund.setRefundSuccessTime(LocalDateTime.parse(format,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                refund.setRefundResultJson(JSONUtil.toJsonStr(response));
                refund.setUpdateTime(LocalDateTime.now());
                refundService.updateById(refund);
                // 更新主表退款总金额
                Integer refundTotalFee = busTrade.getRefundTotalFee() == null ? 0 : busTrade.getRefundTotalFee();
                busTrade.setRefundTotalFee(refundTotalFee + refund.getRefundFee());
                tradeService.updateById(busTrade);
                // 返回数据
                tradeRefundDTO.setRefundStatus(TradeConstant.REFUND_SUCCESS);
                return tradeRefundDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
        tradeRefundDTO.setRefundStatus(refund.getRefundStatus());
        return tradeRefundDTO;
    }

    private void saveTradeToPay(HttpServletResponse response, AliTradePayDTO tradePayDTO, String type) {
        // 保存订单
        BusTrade busTrade = new BusTrade();
        busTrade.setTradeId(GuidUtils.getUuid());
        busTrade.setProductId(tradePayDTO.getProductId());
        busTrade.setUserId(StringUtils.hasText(tradePayDTO.getUserId())
                ? tradePayDTO.getUserId() : CurrentUserUtils.getCurrentUser().getUserId());
        busTrade.setPayType(TradeConstant.PAY_TYPE_ALIPAY);
        busTrade.setTradeStatus(TradeConstant.TRADE_FAIL);
        busTrade.setTotalFee(tradePayDTO.getTotalFee());
        busTrade.setOutTradeNo(GuidUtils.getUuid32());
        busTrade.setAttach(tradePayDTO.getAttach());
        busTrade.setCreateTime(LocalDateTime.now());
        tradeService.save(busTrade);

        try {
            if ("pc".equals(type)) {
                // 跳转支付宝支付
                AlipayTradePagePayModel model = new AlipayTradePagePayModel();
                // 商户订单号
                model.setOutTradeNo(busTrade.getOutTradeNo());
                // 商品名称（支付时显示的名称）
                model.setSubject(tradePayDTO.getProductName());
                // 支付金额（计算成单位元，保留2位小数）
                BigDecimal totalFee = new BigDecimal(tradePayDTO.getTotalFee());
                BigDecimal totalAmount = totalFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                model.setTotalAmount(totalAmount.toString());
                // 附加信息（通知时原样返回）
                model.setBody(tradePayDTO.getAttach());
                AliPayApi.tradePage(response, model,
                        aliPayConfig.getNotifyUrl(), aliPayConfig.getReturnUrl());
            } else if ("wap".equals(type)) {
                // 跳转支付宝支付
                AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
                // 商户订单号
                model.setOutTradeNo(busTrade.getOutTradeNo());
                // 商品名称（支付时显示的名称）
                model.setSubject(tradePayDTO.getProductName());
                // 支付金额（计算成单位元，保留2位小数）
                BigDecimal totalFee = new BigDecimal(tradePayDTO.getTotalFee());
                BigDecimal totalAmount = totalFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                model.setTotalAmount(totalAmount.toString());
                // 附加信息（通知时原样返回）
                model.setBody(tradePayDTO.getAttach());
                AliPayApi.wapPay(response, model,
                        aliPayConfig.getNotifyUrl(), aliPayConfig.getReturnUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
