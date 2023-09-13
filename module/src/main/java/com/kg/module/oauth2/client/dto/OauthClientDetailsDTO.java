package com.kg.module.oauth2.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * Oauth2客户端信息表
 * </p>
 *
 * @author ziro
 * @since 2023-09-12
 */
@Getter
@Setter
@ApiModel(value = "OauthClientDetailsDTO", description = "Oauth2客户端信息表")
public class OauthClientDetailsDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户端id")
    private String clientId;

    @ApiModelProperty("资源ids")
    private String resourceIds;

    @ApiModelProperty("客户端Secret")
    private String clientSecret;

    @ApiModelProperty("权限范围(read,write)")
    private String scope;

    @ApiModelProperty("授权模式(authorization_code,password,refresh_token)")
    private String authorizedGrantTypes;

    @ApiModelProperty("客户端回调地址")
    private String webServerRedirectUri;

    @ApiModelProperty("授权用户/角色")
    private String authorities;

    @ApiModelProperty("access_token有效期，单位秒")
    private Integer accessTokenValidity;

    @ApiModelProperty("refresh_token有效期，单位秒")
    private Integer refreshTokenValidity;

    @ApiModelProperty("备注信息，必须是JSON格式")
    private String additionalInformation;

    @ApiModelProperty("是否自动授权(true自动授权;false需要用户手动确认)")
    private String autoapprove;
}
