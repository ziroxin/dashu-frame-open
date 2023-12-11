package com.kg.component.wechat.applet;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序相关配置
 *
 * @author ziro
 * @date 2023-12-11 15:20
 */
@Configuration
@Getter
@Setter
public class WechatAppletConfig {
    /**
     * 微信小程序appid
     */
    @Value("${com.kg.weixin.applet.appId}")
    private String appId;
    /**
     * 微信小程序secret
     */
    @Value("${com.kg.weixin.applet.secret}")
    private String secret;
    /**
     * 微信小程序：auth.getAccessToken地址
     */
    @Value("${com.kg.weixin.applet.accessTokenUrl:https://api.weixin.qq.com/cgi-bin/token}")
    private String accessTokenUrl;
    /**
     * 微信小程序：jscode-url地址
     */
    @Value("${com.kg.weixin.applet.jsCodeUrl:https://api.weixin.qq.com/sns/jscode2session}")
    private String jsCodeUrl;
    /**
     * 微信小程序：获取二维码
     */
    @Value("${com.kg.weixin.applet.qrCodeUrl:https://api.weixin.qq.com/wxa/getwxacodeunlimit}")
    private String QRCodeUrl;

}
