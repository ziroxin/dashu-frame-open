package com.kg.core.retgister.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 注册表单DTO
 *
 * @author ziro
 * @date 2024/5/27 11:17
 */
@Getter
@Setter
@ApiModel(description = "注册表单DTO", discriminator = "注册表单DTO")
public class RegisterFormDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户所在部门ID")
    private String orgId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("性别(0未知1男2女)")
    private String sex;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("简介")
    private String introduce;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("验证码")
    private String yzm;

    @ApiModelProperty("验证码uuid")
    private String codeUuid;

    @ApiModelProperty("是否加密传输")
    private Boolean isEncrypt;
}
