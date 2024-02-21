package com.kg.component.captcha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码相关属性 - 实体
 *
 * @author ziro
 * @date 2022-12-27 10:15:43
 */
@Getter
@Setter
@Configuration
public class CaptchaConfig {
    /**
     * 验证码类型 默认：ARITHMETIC - 算数
     */
    @Value("${com.kg.captcha.type:ARITHMETIC}")
    private CaptchaTypeEnum captchaType;
    /**
     * 验证码有效期（单位分钟） 默认：10分钟
     */
    @Value("${com.kg.captcha.expiration:10}")
    private Long expiration;
    /**
     * 验证码内容长度 默认：4
     */
    @Value("${com.kg.captcha.length:4}")
    private int length;
    /**
     * 验证码宽度 默认：120
     */
    @Value("${com.kg.captcha.width:120}")
    private int width;
    /**
     * 验证码高度 默认：36
     */
    @Value("${com.kg.captcha.height:36}")
    private int height;
    /**
     * 字体 默认：Default
     */
    @Value("${com.kg.captcha.font-family:Default}")
    private String fontFamily;
    /**
     * 字体大小 默认：20
     */
    @Value("${com.kg.captcha.font-size:20}")
    private int fontSize;
    /**
     * 字体样式（0正常、1加粗、2斜体） 默认：Font.PLAIN=0
     */
    @Value("${com.kg.captcha.font-style:0}")
    private int fontStyle;
    /**
     * 背景色（设置为空时，代表透明图，无背景色）
     */
    @Value("${com.kg.captcha.bg-color}")
    private String bgColor;
}
