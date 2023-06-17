package com.kg.module.trade.excels;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 导出Excel实体 - 交易 - 支付demo
 *
 * @author ziro
 * @date 2023-05-16
 */
@Getter
@Setter
public class BusTradeExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 订单ID
     */
    private String tradeId;

    /**
     * 商品ID（外链到需要付款的商品或其他信息）
     */
    private String productId;

    /**
     * 用户id（z_user表）
     */
    private String userId;

    /**
     * 支付方式0微信1支付宝
     */
    private Boolean payType;

    /**
     * 支付用户微信/支付宝id
     */
    private String tradeOpenId;

    /**
     * 支付状态0未支付1已支付
     */
    private Integer tradeStatus;

    /**
     * 支付成功时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime paySuccessTime;

    /**
     * 总金额，单位：分
     */
    private Integer totalFee;

    /**
     * 商户订单号（微信32支付宝64内）
     */
    private String outTradeNo;

    /**
     * 支付反馈结果json
     */
    private String resultJson;

    /**
     * 已退款总金额，单位：分
     */
    private String refundTotalFee;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}