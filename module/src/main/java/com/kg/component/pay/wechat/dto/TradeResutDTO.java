package com.kg.component.pay.wechat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ziro
 * @date 2023/5/18 18:01
 */
@Getter
@Setter
@ApiModel(description = "微信支付：交易信息 - 实体DTO")
public class TradeResutDTO {
    /**
     * 交易ID
     */
    @ApiModelProperty(value = "交易ID", required = false)
    private String tradeId;

    /**
     * 支付状态0未支付1已支付
     */
    @ApiModelProperty(value = "支付状态0未支付1已支付", required = false)
    private Integer tradeStatus;

    /**
     * Native方式支付的：二维码URL
     */
    @ApiModelProperty(value = "二维码URL", required = false)
    private String qrCodeUrl;

    /**
     * H5方式支付的：跳转支付页
     */
    @ApiModelProperty(value = "二维码URL", required = false)
    private String h5Url;

    /**
     * Jsapi方式支付的：微信支付信息
     */
    @ApiModelProperty(value = "微信支付信息", required = false)
    private String payInfo;
}
