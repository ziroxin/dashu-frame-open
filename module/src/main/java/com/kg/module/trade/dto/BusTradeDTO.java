package com.kg.module.trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * <p>
 * 交易 - 支付demo
 * </p>
 *
 * @author ziro
 * @since 2023-05-16
 */
@Getter
@Setter
@ApiModel(value = "BusTradeDTO", description = "交易 - 支付demo")
public class BusTradeDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    private String tradeId;

    @ApiModelProperty("商品ID（外链到需要付款的商品或其他信息）")
    private String productId;

    @ApiModelProperty("用户id（z_user表）")
    private String userId;

    @ApiModelProperty("支付方式0微信1支付宝")
    private Integer payType;

    @ApiModelProperty("支付用户微信/支付宝id")
    private String tradeOpenId;

    @ApiModelProperty("支付状态0未支付1已支付")
    private Integer tradeStatus;

    @ApiModelProperty("支付成功时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime paySuccessTime;

    @ApiModelProperty("总金额，单位：分")
    private Integer totalFee;

    @ApiModelProperty("商户订单号（微信32支付宝64内）")
    private String outTradeNo;

    @ApiModelProperty("附加内容（在查询API和支付通知中原样返回）")
    private String attach;

    @ApiModelProperty("支付反馈结果json")
    private String resultJson;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
