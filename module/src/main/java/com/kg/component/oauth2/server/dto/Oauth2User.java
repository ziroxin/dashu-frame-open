package com.kg.component.oauth2.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Oauth2 用户信息
 *
 * @author ziro
 * @date 2023/9/1 16:17
 */
@Getter
@Setter
@ApiModel(description = "Oauth2当前登录用户基本信息")
public class Oauth2User implements Serializable {
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
