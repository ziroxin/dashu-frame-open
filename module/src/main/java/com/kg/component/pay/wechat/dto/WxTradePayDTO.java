package com.kg.component.pay.wechat.dto;

import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信支付：交易信息 - 实体DTO
 *
 * @author ziro
 * @date 2023-04-09 22:29:37
 */
@Getter
@Setter
@ApiModel(description = "微信支付：交易信息 - 实体DTO")
public class WxTradePayDTO implements BaseDTO {
    /**
     * 商品ID
     */
    @ApiModelProperty(value = "商品ID", required = true)
    private String productId;
    /**
     * 商品名称（用户支付时显示）
     */
    @ApiModelProperty(value = "商品名称（用户支付时显示）", required = false)
    private String productName;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", required = false)
    private String userId;
    /**
     * 附加信息
     */
    @ApiModelProperty(value = "附加内容（在查询API和支付通知中原样返回，最长127个字符）", required = false)
    private String attach;
    /**
     * 付款金额，单位：分
     */
    @ApiModelProperty(value = "付款金额，单位：分", required = true)
    private Integer totalFee;
    /**
     * Jsapi方式，前端获取openId
     */
    @ApiModelProperty(value = "Jsapi方式，前端获取openId", required = false)
    private String openId;
    /**
     * 终端IP
     */
    @ApiModelProperty(value = "终端IP", required = false)
    private String spbillCreateIp;
}
