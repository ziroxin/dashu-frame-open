package com.kg.component.pay.alipay.config;

import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付配置
 *
 * @author ziro
 * @date 2023/5/24 14:58
 */
@Component
//@PropertySource("classpath:/application.yml") // 默认配置文件可不写
@ConfigurationProperties(prefix = "com.kg.alipay")
@Getter
@Setter
public class AliPayConfig {
    private String appId;
    private String privateKey;
    private String aliPayPublicKey;
    private String notifyUrl;
    private String returnUrl;

    @Bean
    public AliPayApiConfig getAliPayApiConfig() {
        // 初始化支付宝配置
        AliPayApiConfig aliPayApiConfig;
        try {
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(appId);
        } catch (Exception e) {
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(appId)
                    .setPrivateKey(privateKey)
                    .setAliPayPublicKey(aliPayPublicKey)
                    .setServiceUrl("https://openapi.alipay.com/gateway.do")
                    .setCharset("UTF-8")
                    .setSignType("RSA2")
                    .build();
            AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
        }
        return aliPayApiConfig;
    }
}
