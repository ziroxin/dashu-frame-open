package com.kg.module.tradeRefund.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
@TableName("bus_trade_refund")
@ApiModel(value = "BusTradeRefund对象", description = "退款 - 支付demo")
public class BusTradeRefund implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("退款ID")
    @TableId(value = "refund_id", type = IdType.ASSIGN_UUID)
    private String refundId;

    @ApiModelProperty("订单ID")
    @TableField("trade_id")
    private String tradeId;

    @ApiModelProperty("商户退款单号")
    @TableField("out_refund_no")
    private String outRefundNo;

    @ApiModelProperty("序号（下标$n）")
    @TableField("refund_index")
    private Integer refundIndex;

    @ApiModelProperty("退款原因")
    @TableField("refund_desc")
    private String refundDesc;

    @ApiModelProperty("退款状态0退款中1退款成功2退款异常")
    @TableField("refund_status")
    private Integer refundStatus;

    @ApiModelProperty("退款成功时间")
    @TableField("refund_success_time")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime refundSuccessTime;

    @ApiModelProperty("退款金额，单位：分")
    @TableField("refund_fee")
    private Integer refundFee;

    @ApiModelProperty("退款反馈结果json")
    @TableField("refund_result_json")
    private String refundResultJson;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
