package com.kg.module.trade.constant;

/**
 * @author ziro
 * @date 2023/5/18 18:12
 */
public class TradeConstant {
    // 支付状态 - 成功
    public static final Integer TRADE_SUCCESS = 1;
    // 支付状态 - 失败
    public static final Integer TRADE_FAIL = 0;

    // 支付状态 - 成功 - 微信支付成功返回值
    public static final String TRADE_SUCCESS_WECHAT = "SUCCESS";

    // 支付类型 - 0 - 微信支付
    public static final Integer PAY_TYPE_WECHAT = 0;
    // 支付类型 - 1 - 支付宝
    public static final Integer PAY_TYPE_ALIPAY = 1;

    // 退款状态 - 退款中
    public static final Integer REFUNDING = 0;
    // 退款状态 - 退款成功
    public static final Integer REFUND_SUCCESS = 1;
    // 退款状态 - 退款失败
    public static final Integer REFUND_FAIL = 2;
}
