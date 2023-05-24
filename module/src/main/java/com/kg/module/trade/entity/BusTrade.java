package com.kg.module.trade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

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
@TableName("bus_trade")
@ApiModel(value = "BusTrade对象", description = "交易 - 支付demo")
public class BusTrade implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    @TableId(value = "trade_id", type = IdType.ASSIGN_UUID)
    private String tradeId;

    @ApiModelProperty("商品ID（外链到需要付款的商品或其他信息）")
    @TableField("product_id")
    private String productId;

    @ApiModelProperty("用户id（z_user表）")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("支付方式0微信1支付宝")
    @TableField("pay_type")
    private Integer payType;

    @ApiModelProperty("支付用户微信/支付宝id")
    @TableField("trade_open_id")
    private String tradeOpenId;

    @ApiModelProperty("支付状态0未支付1已支付")
    @TableField("trade_status")
    private Integer tradeStatus;

    @ApiModelProperty("支付成功时间")
    @TableField("pay_success_time")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime paySuccessTime;

    @ApiModelProperty("总金额，单位：分")
    @TableField("total_fee")
    private Integer totalFee;

    @ApiModelProperty("商户订单号（微信32支付宝64内）")
    @TableField("out_trade_no")
    private String outTradeNo;

    @ApiModelProperty("附加内容（在查询API和支付通知中原样返回）")
    @TableField("attach")
    private String attach;

    @ApiModelProperty("支付反馈结果json")
    @TableField("result_json")
    private String resultJson;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
