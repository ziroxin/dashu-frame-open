package com.kg.component.captcha;

/**
 * 验证码类型
 *
 * @author ziro
 * @date 2022-12-27 10:11:11
 */
public enum CaptchaTypeEnum {
    /**
     * 算数
     */
    ARITHMETIC,
    /**
     * 中文
     */
    CHINESE,
    /**
     * 中文闪图
     */
    CHINESE_GIF,
    /**
     * 闪图
     */
    GIF,
    /**
     * 常规
     */
    SPEC
}
