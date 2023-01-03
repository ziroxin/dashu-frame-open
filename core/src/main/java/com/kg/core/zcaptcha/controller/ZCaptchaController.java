package com.kg.core.zcaptcha.controller;

import com.kg.component.captcha.CaptchaResult;
import com.kg.core.zcaptcha.service.ZCaptchaService;
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
    private ZCaptchaService captchaService;

    @ApiOperation(value = "captcha/get", notes = "查询验证码", httpMethod = "GET")
    @GetMapping("get")
    public CaptchaResult getCaptcha() {
        return captchaService.getCaptcha();
    }
}
