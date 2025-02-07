package com.kg.module.applet.wechat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 小程序-登录表单DTO
 *
 * @author ziro
 * @date 2024/12/13 9:34
 */
@Getter
@Setter
@ApiModel(description = "小程序-登录表单DTO")
public class WechatLoginFormDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "是否加密传输参数", required = true)
    private Boolean isEncrypt;

    @ApiModelProperty(value = "验证码", required = false)
    private String yzm;

    @ApiModelProperty(value = "验证码UUID", required = false)
    private String codeUuid;

    @ApiModelProperty(value = "微信登录凭证（code）", required = false)
    private String code;

    @ApiModelProperty(value = "是否绑定微信", required = false)
    private Boolean bindWechat;
}
