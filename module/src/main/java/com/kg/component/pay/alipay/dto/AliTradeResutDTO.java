package com.kg.component.pay.alipay.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ziro
 * @date 2023/5/18 18:01
 */
@Getter
@Setter
@Builder
@ApiModel(description = "支付宝支付：交易信息 - 实体DTO")
public class AliTradeResutDTO {

    @ApiModelProperty(value = "交易ID", required = false)
    private String tradeId;

    @ApiModelProperty(value = "支付状态0未支付1已支付", required = false)
    private Integer tradeStatus;

    @ApiModelProperty(value = "二维码URL", required = false)
    private String qrCodeUrl;
}
