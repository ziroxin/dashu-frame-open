package com.kg.core.zcaptcha.service;

import com.kg.component.captcha.CaptchaResult;
import com.kg.core.exception.BaseException;

/**
 * @author ziro
 * @date 2023-01-03 10:27:49
 */
public interface ZCaptchaService {
    /**
     * 获取验证码
     */
    CaptchaResult getCaptcha();

    /**
     * 检验验证码是否正确
     *
     * @param codeUuid  验证码唯一id
     * @param codeValue 用户输入的验证码值
     * @return 是否正确
     */
    boolean checkCaptcha(String codeUuid, String codeValue);

    /**
     * 根据配置检查验证码是否正确
     *
     * @param codeUuid 验证码唯一id
     * @param yzm      用户输入的验证码值
     */
    void checkCaptchaByConfig(String codeUuid, String yzm) throws BaseException;
}
