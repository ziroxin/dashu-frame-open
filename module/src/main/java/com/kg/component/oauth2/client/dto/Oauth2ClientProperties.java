package com.kg.component.oauth2.client.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Oauth2 客户端信息
 *
 * @author ziro
 * @date 2023/9/7 15:53
 */
@Component
@Getter
@Setter
public class Oauth2ClientProperties {
    /**
     * client_id
     */
    @Value("${com.kg.oauth2.client.clientId}")
    private String clientId;
    /**
     * client_secret
     */
    @Value("${com.kg.oauth2.client.clientSecret}")
    private String clientSecret;
    /**
     * redirect_uri
     */
    @Value("${com.kg.oauth2.client.redirectUri}")
    private String redirectUri;
    /**
     * 统一认证服务端地址
     */
    @Value("${com.kg.oauth2.client.oauthServerUri}")
    private String oauthServerUri;
    /**
     * 客户端，错误页
     */
    @Value("${com.kg.oauth2.client.errorRouter}")
    private String errorRouter;
    /**
     * 客户端，登录成功页
     */
    @Value("${com.kg.oauth2.client.successRouter}")
    private String successRouter;
    /**
     * 客户端，用户绑定页
     */
    @Value("${com.kg.oauth2.client.userBindRouter}")
    private String userBindRouter;
}
