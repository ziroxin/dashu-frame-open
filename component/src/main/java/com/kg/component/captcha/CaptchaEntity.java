package com.kg.component.captcha;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 验证码相关属性 - 实体
 *
 * @author ziro
 * @date 2022-12-27 10:15:43
 */
@Getter
@Setter
public class CaptchaEntity {
    /**
     * 验证码类型 默认：ARITHMETIC - 算数
     */
    private CaptchaTypeEnum captchaType = CaptchaTypeEnum.ARITHMETIC;
    /**
     * 验证码有效期（单位分钟） 默认：10分钟
     */
    private Long expiration = 10L;
    /**
     * 验证码内容长度 默认：4
     */
    private int length = 4;
    /**
     * 验证码宽度 默认：120
     */
    private int width = 120;
    /**
     * 验证码高度 默认：36
     */
    private int height = 36;
    /**
     * 字体 默认：Default
     */
    private String fontFamily = "Default";
    /**
     * 字体大小 默认：20
     */
    private int fontSize = 20;
    /**
     * 字体样式（0正常、1加粗、2斜体） 默认：Font.PLAIN=0
     */
    private int fontStyle = Font.PLAIN;

}
