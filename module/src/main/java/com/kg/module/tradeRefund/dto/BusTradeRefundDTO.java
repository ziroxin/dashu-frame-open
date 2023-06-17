package com.kg.module.tradeRefund.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 退款 - 支付demo
 * </p>
 *
 * @author ziro
 * @since 2023-06-14
 */
@Getter
@Setter
@ApiModel(value = "BusTradeRefundDTO", description = "退款 - 支付demo")
public class BusTradeRefundDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("退款ID")
    private String refundId;

    @ApiModelProperty("订单ID")
    private String tradeId;

    @ApiModelProperty("商户退款单号")
    private String outRefundNo;

    @ApiModelProperty("序号（下标$n）")
    private Integer refundIndex;

    @ApiModelProperty("退款原因")
    private String refundDesc;

    @ApiModelProperty("退款状态0退款中1退款成功2退款异常")
    private Integer refundStatus;

    @ApiModelProperty("退款成功时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime refundSuccessTime;

    @ApiModelProperty("退款金额，单位：分")
    private Integer refundFee;

    @ApiModelProperty("退款反馈结果json")
    private String refundResultJson;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
