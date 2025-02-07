package com.kg.core.zlogin.controller;

import com.kg.component.jwt.JwtUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.base.controller.BaseController;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;
import com.kg.core.zlogin.service.ZLoginService;
import com.kg.core.zuser.entity.ZUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录
 *
 * @author ziro
 * @date 2022/5/2 9:46
 */
@Api(tags = "ZLoginController", value = "用户登录", description = "用户登录控制器")
@RestController
@Validated
@RequestMapping("login")
public class ZLoginController implements BaseController {

    @Resource
    private ZLoginService zLoginService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ZCaptchaService captchaService;

    @ApiOperation(value = "/login/login", notes = "登录接口", httpMethod = "POST")
    @PostMapping("login")
    public LoginSuccessDTO login(@RequestBody LoginFormDTO loginForm) throws BaseException {
        // 验证码校验
        captchaService.checkCaptchaByConfig(loginForm.getCodeUuid(), loginForm.getYzm());
        // 登录
        return zLoginService.login(loginForm);
    }

    @ApiOperation(value = "/login/logout", notes = "退出接口", httpMethod = "POST")
    @GetMapping("logout")
    public void logout(HttpServletRequest request) {
        String jwtToken = request.getHeader(LoginConstant.LOGIN_JWT_TOKEN_KEY);// header中的token
        if (!StringUtils.hasText(jwtToken)) {
            jwtToken = request.getParameter(LoginConstant.LOGIN_JWT_TOKEN_KEY);// 参数中的token
        }
        zLoginService.logout(jwtToken);
    }


    @ApiOperation(value = "/login/refresh/token", notes = "刷新登录用户Token", httpMethod = "POST")
    @PostMapping("refresh/token")
    @NoRepeatSubmit
    public LoginSuccessDTO refreshToken() {
        ZUser user = CurrentUserUtils.getCurrentUser();
        // 生成JwtToken
        LoginSuccessDTO loginSuccessDTO = new LoginSuccessDTO();
        loginSuccessDTO.setAccessToken(JwtUtils.createToken(user.getUserId()));
        // JwtToken有效期
        loginSuccessDTO.setAccessTokenValidTime(TimeUtils.now().addMinute(LoginConstant.LOGIN_JWT_TOKEN_EXPIRY).toDate());
        // 缓存用户登录的最新token（用于判断和处理单例登录）
        redisUtils.set(LoginConstant.LAST_LOGIN_TOKEN_PRE + user.getUserId(), loginSuccessDTO.getAccessToken(), LoginConstant.LOGIN_JWT_TOKEN_EXPIRY * 60L);
        // 延长redis中，用户有效期
        redisUtils.setExpire(CacheConstant.LOGIN_INFO_REDIS_PRE + user.getUserId(), LoginConstant.LOGIN_JWT_TOKEN_EXPIRY * 60L);
        return loginSuccessDTO;
    }
}
