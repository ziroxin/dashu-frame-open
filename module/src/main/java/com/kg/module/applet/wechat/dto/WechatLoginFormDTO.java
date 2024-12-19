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
    /** 用户名 */
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;
    /** 密码 */
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    /** 是否加密传输 */
    @ApiModelProperty(value = "是否加密传输参数", required = true)
    private Boolean isEncrypt;
    /** 微信登录凭证（code） */
    @ApiModelProperty(value = "微信登录凭证（code）", required = true)
    private String code;
    /** 验证码 */
    private String yzm;
    /** 验证码uuid */
    private String codeUuid;
}
