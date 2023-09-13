package com.kg.module.oauth2.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import java.io.Serializable;
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
@TableName("oauth_client_details")
@ApiModel(value = "OauthClientDetails对象", description = "Oauth2客户端信息表")
public class OauthClientDetails implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户端id")
    @TableId(value = "client_id", type = IdType.ASSIGN_UUID)
    private String clientId;

    @ApiModelProperty("资源ids")
    @TableField("resource_ids")
    private String resourceIds;

    @ApiModelProperty("客户端Secret")
    @TableField("client_secret")
    private String clientSecret;

    @ApiModelProperty("权限范围(read,write)")
    @TableField("scope")
    private String scope;

    @ApiModelProperty("授权模式(authorization_code,password,refresh_token)")
    @TableField("authorized_grant_types")
    private String authorizedGrantTypes;

    @ApiModelProperty("客户端回调地址")
    @TableField("web_server_redirect_uri")
    private String webServerRedirectUri;

    @ApiModelProperty("授权用户/角色")
    @TableField("authorities")
    private String authorities;

    @ApiModelProperty("access_token有效期，单位秒")
    @TableField("access_token_validity")
    private Integer accessTokenValidity;

    @ApiModelProperty("refresh_token有效期，单位秒")
    @TableField("refresh_token_validity")
    private Integer refreshTokenValidity;

    @ApiModelProperty("备注信息，必须是JSON格式")
    @TableField("additional_information")
    private String additionalInformation;

    @ApiModelProperty("是否自动授权(true自动授权;false需要用户手动确认)")
    @TableField("autoapprove")
    private String autoapprove;
}
