package com.kg.core.zcaptcha.service.impl;

import com.kg.component.captcha.CaptchaResult;
import com.kg.component.captcha.CaptchaUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    @Value("${com.kg.login.is-yzm}")
    private boolean IS_YZM;
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
            // 验证码用完一次，就销毁
            redisUtils.delete(CAPTCHA_PREFIX + codeUuid);
            if (value.equals(codeValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据配置检查验证码是否正确
     *
     * @param codeUuid 验证码唯一id
     * @param yzm      用户输入的验证码值
     */
    @Override
    public void checkCaptchaByConfig(String codeUuid, String yzm) throws BaseException {
        if (IS_YZM) {
            if (!StringUtils.hasText(yzm)) {
                throw new BaseException("请输入验证码！");
            }
            if (!checkCaptcha(codeUuid, yzm)) {
                throw new BaseException("验证码错误！请检查");
            }
        }
    }
}
