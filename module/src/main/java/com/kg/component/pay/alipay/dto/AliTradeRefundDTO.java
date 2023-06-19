package com.kg.component.pay.alipay.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 支付宝支付：退款信息 - 实体DTO
 *
 * @author ziro
 * @date 2023/6/14 16:28
 */
@Getter
@Setter
@ApiModel(description = "支付宝支付：退款信息 - 实体DTO")
public class AliTradeRefundDTO {

    @ApiModelProperty(value = "退款ID", required = false)
    private String refundId;

    @ApiModelProperty(value = "交易ID", required = false)
    private String tradeId;

    @ApiModelProperty(value = "退款金额", required = false)
    private Integer refundFee;

    @ApiModelProperty(value = "退款原因", required = false)
    private String refundDesc;

    @ApiModelProperty(value = "退款状态0退款中1退款成功2退款异常", required = false)
    private Integer refundStatus;

}
