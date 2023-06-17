package com.kg.module.tradeRefund.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 退款 - 支付demo
 *
 * @author ziro
 * @date 2023-06-14
 */
@Getter
@Setter
public class BusTradeRefundExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 退款ID
     */
    private String refundId;

    /**
     * 订单ID
     */
    private String tradeId;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 退款原因
     */
    private String refundDesc;

    /**
     * 退款状态0退款中1退款成功2退款异常
     */
    private Integer refundStatus;

    /**
     * 退款成功时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime refundSuccessTime;

    /**
     * 退款金额，单位：分
     */
    private Integer refundFee;

    /**
     * 退款反馈结果json
     */
    private String refundResultJson;

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