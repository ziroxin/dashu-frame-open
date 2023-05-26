package com.kg.component.pay.wechat.service;

import com.kg.component.pay.wechat.dto.WxTradePayDTO;
import com.kg.component.pay.wechat.dto.WxTradeResutDTO;
import com.kg.core.exception.BaseException;

/**
 * 微信支付服务
 *
 * @author ziro
 * @date 2023/5/22 17:06
 */
public interface WxPayService {
    /**
     * 调取微信支付
     *
     * @param wxTradePayDTO 交易信息
     * @param tradeType   支付类型
     */
    WxTradeResutDTO getPayInfo(WxTradePayDTO wxTradePayDTO, String tradeType) throws BaseException;

    /**
     * 微信支付通知
     *
     * @param result 支付结果
     */
    String payNotify(String result);

    /**
     * 查询支付结果
     *
     * @param wxTradeResutDTO 交易信息
     */
    WxTradeResutDTO getPayResult(WxTradeResutDTO wxTradeResutDTO);
}
