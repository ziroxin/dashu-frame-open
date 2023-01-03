package com.kg.core.zcaptcha.service.impl;

import com.kg.component.captcha.CaptchaResult;
import com.kg.component.captcha.CaptchaUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ziro
 * @date 2023-01-03 10:28:09
 */
@Service
public class ZCaptchaServiceImpl implements ZCaptchaService {
    @Resource
    private CaptchaUtils captchaUtils;
    @Resource
    private RedisUtils redisUtils;
    // 存入Redis前缀
    private String CAPTCHA_PREFIX = "captcha_redis_pre_";

    @Override
    public CaptchaResult getCaptcha() {
        CaptchaResult captcha = captchaUtils.getCaptcha();
        // 写入redis
        redisUtils.set(CAPTCHA_PREFIX + captcha.getCodeUuid(), captcha.getCodeValue(), captcha.getCodeExpire());
        // 不返回结果
        captcha.setCodeValue("");
        return captcha;
    }

    /**
     * 检验验证码是否正确
     *
     * @param codeUuid  验证码唯一id
     * @param codeValue 用户输入的验证码值
     * @return 是否正确
     */
    @Override
    public boolean checkCaptcha(String codeUuid, String codeValue) {
        if (redisUtils.hasKey(CAPTCHA_PREFIX + codeUuid)) {
            String value = redisUtils.get(CAPTCHA_PREFIX + codeUuid).toString();
            if (value.equals(codeValue)) {
                return true;
            }
        }
        return false;
    }
}
