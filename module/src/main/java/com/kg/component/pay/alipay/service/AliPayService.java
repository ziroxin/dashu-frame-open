package com.kg.component.pay.alipay.service;

import com.kg.component.pay.alipay.dto.AliTradePayDTO;
import com.kg.component.pay.alipay.dto.AliTradeResutDTO;
import com.kg.core.exception.BaseException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author ziro
 * @date 2023/5/24 14:56
 */
public interface AliPayService {
    void toPcPay(HttpServletResponse response, AliTradePayDTO tradePayDTO);

    void toWapPay(HttpServletResponse response, AliTradePayDTO tradePayDTO);

    /**
     * 获取支付二维码信息
     *
     * @param tradePayDTO 支付参数
     */
    AliTradeResutDTO scanPay(AliTradePayDTO tradePayDTO) throws BaseException;

    /**
     * 支付通知
     *
     * @param params 参数
     */
    String payNotify(Map<String, String> params);

    /**
     * 查询支付结果
     *
     * @param tradeResutDTO 交易信息
     */
    AliTradeResutDTO getPayResult(AliTradeResutDTO tradeResutDTO);
}
