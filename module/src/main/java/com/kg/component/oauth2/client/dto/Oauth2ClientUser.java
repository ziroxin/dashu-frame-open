package com.kg.component.oauth2.client.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Oauth2 Client 用户信息
 *
 * @author ziro
 * @date 2023/9/5 16:17
 */
@Getter
@Setter
public class Oauth2ClientUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户唯一ID")
    private String openId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("性别(0未知1男2女)")
    private String sex;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("手机号")
    private String phone;
}