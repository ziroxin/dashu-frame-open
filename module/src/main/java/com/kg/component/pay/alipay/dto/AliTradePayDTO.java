package com.kg.component.pay.alipay.dto;

import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 支付宝支付：交易信息 - 实体DTO
 *
 * @author ziro
 * @date 2023-04-09 22:29:37
 */
@Getter
@Setter
@ApiModel(description = "支付宝支付：交易信息 - 实体DTO")
public class AliTradePayDTO implements BaseDTO {
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
     * 付款金额，单位：分
     */
    @ApiModelProperty(value = "付款金额，单位：分", required = true)
    private Integer totalFee;
    /**
     * 附加信息
     */
    @ApiModelProperty(value = "附加信息", required = false)
    private String attach;
}
