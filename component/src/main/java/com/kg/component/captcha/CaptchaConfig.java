package com.kg.component.captcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置验证码工具类
 *
 * @author ziro
 * @date 2022-12-27 10:28:48
 */
@Configuration
public class CaptchaConfig {
    private CaptchaEntity config = new CaptchaEntity();

    @Value("${com.kg.captcha.type:ARITHMETIC}")
    public void setCaptchaType(CaptchaTypeEnum type) {
        config.setCaptchaType(type);
    }

    @Value("${com.kg.captcha.expiration:10}")
    public void setExpiration(Long expiration) {
        config.setExpiration(expiration);
    }

    @Value("${com.kg.captcha.length:4}")
    public void setLength(int length) {
        config.setLength(length);
    }

    @Value("${com.kg.captcha.width:120}")
    public void setWidth(int width) {
        config.setWidth(width);
    }

    @Value("${com.kg.captcha.height:36}")
    public void setHeight(int height) {
        config.setHeight(height);
    }

    @Value("${com.kg.captcha.font-family:Default}")
    public void setFontFamily(String fontFamily) {
        config.setFontFamily(fontFamily);
    }

    @Value("${com.kg.captcha.font-size:20}")
    public void setFontSize(int fontSize) {
        config.setFontSize(fontSize);
    }

    @Value("${com.kg.captcha.font-style:0}")
    public void setFontStyle(int fontStyle) {
        config.setFontStyle(fontStyle);
    }

    /**
     * 初始化，验证码工具类
     */
    @Bean
    public CaptchaUtils CaptchaInitialization() {
        CaptchaUtils captchaUtils = new CaptchaUtils();
        captchaUtils.setCaptchaEntity(config);
        return captchaUtils;
    }

}
