package com.kg.module.oauth2.client.excels;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - Oauth2客户端信息表
 *
 * @author ziro
 * @date 2023-09-12
 */
@Getter
@Setter
public class OauthClientDetailsExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 资源ids
     */
    private String resourceIds;

    /**
     * 客户端Secret
     */
    private String clientSecret;

    /**
     * 权限范围(read,write)
     */
    private String scope;

    /**
     * 授权模式(authorization_code,password,refresh_token)
     */
    private String authorizedGrantTypes;

    /**
     * 客户端回调地址
     */
    private String webServerRedirectUri;

    /**
     * 授权用户/角色
     */
    private String authorities;

    /**
     * access_token有效期，单位秒
     */
    private Integer accessTokenValidity;

    /**
     * refresh_token有效期，单位秒
     */
    private Integer refreshTokenValidity;

    /**
     * 备注信息，必须是JSON格式
     */
    private String additionalInformation;

    /**
     * 是否自动授权(true自动授权;false需要用户手动确认)
     */
    private String autoapprove;

}