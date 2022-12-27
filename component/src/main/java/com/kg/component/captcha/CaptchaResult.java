package com.kg.component.captcha;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 获取验证码 - 结果实体
 *
 * @author ziro
 * @date 2022-12-27 11:29:16
 */
@Getter
@Setter
public class CaptchaResult {
    /**
     * 验证码唯一id
     */
    private String codeUuid;
    /**
     * 验证码值
     */
    private String codeValue;
    /**
     * 验证码base64字符串
     */
    private String codeBaseImage;
    /**
     * 有效期至
     */
    private Date codeExpire;
}
