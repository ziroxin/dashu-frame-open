package com.kg.component.pay.wechat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信商户号配置
 *
 * @author ziro
 * @date 2023-04-10 08:23:38
 */
@Component
//@PropertySource("classpath:/application.yml") // 默认配置文件可不写
@ConfigurationProperties(prefix = "com.kg.weixin.pay")
@Getter
@Setter
public class WxPayConfig {
    /**
     * 微信绑定的公众号appId
     * 商户号只能绑定：已通过微信认证的服务号、政府或媒体类订阅号、小程序、企业微信、移动应用、网站应用
     */
    private String appId;
    /**
     * 商户号mchId
     */
    private String mchId;
    /**
     * 当前使用的证书版本v2/v3
     */
    private String keyVersion;
    /**
     * 商户号v2密钥
     */
    private String partnerKey;
    /**
     * 商户号v3密钥
     */
    private String partnerKeyV3;
    /**
     * 证书pem地址(v3时需要)
     */
    private String certPath;
    /**
     * 证书p12地址(v2退款时需要)
     */
    private String certP12Path;
    /**
     * 私钥pem地址(v3时需要)
     */
    private String keyPath;
    /**
     * 支付结果通知地址
     */
    private String notifyUrl;
}
