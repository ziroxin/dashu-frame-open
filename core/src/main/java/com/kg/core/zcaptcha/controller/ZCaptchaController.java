package com.kg.core.zcaptcha.controller;

import com.kg.component.captcha.CaptchaResult;
import com.kg.component.captcha.CaptchaUtils;
import com.kg.component.redis.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码控制器
 *
 * @author ziro
 * @date 2022-12-27 14:02:45
 */
@Api(tags = "ZCaptchaController", value = "验证码控制器", description = "验证码控制器")
@RestController
@RequestMapping("captcha")
public class ZCaptchaController {
    @Resource
    private CaptchaUtils captchaUtils;
    @Resource
    private RedisUtils redisUtils;
    // 存入Redis前缀
    private String CAPTCHA_PREFIX = "captcha_redis_pre_";

    @ApiOperation(value = "captcha/get", notes = "查询验证码", httpMethod = "GET")
    @GetMapping("get")
    public CaptchaResult getCaptcha() {
        CaptchaResult captcha = captchaUtils.getCaptcha();
        // 写入redis
        redisUtils.set(CAPTCHA_PREFIX + captcha.getCodeUuid(), captcha.getCodeValue(), captcha.getCodeExpire());
        // 返回
        return captcha;
    }

    /**
     * 检验验证码是否正确
     *
     * @param codeUuid  验证码唯一id
     * @param codeValue 用户输入的验证码值
     * @return 是否正确
     */
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
