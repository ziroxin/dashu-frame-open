package com.kg.core.zlogin.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.common.primitives.Ints;
import com.kg.component.jwt.JwtUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;
import com.kg.core.zlogin.service.ZLoginService;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import com.kg.core.zuserlock.entity.ZUserLock;
import com.kg.core.zuserlock.service.ZUserLockService;
import com.kg.core.zuserpassword.entity.ZUserPassword;
import com.kg.core.zuserpassword.service.ZUserPasswordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * @author ziro
 * @date 2022/5/2 22:14
 */
@Service
public class ZLoginServiceImpl implements ZLoginService {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ZSafetyService safetyService;
    @Resource
    private ZUserPasswordService passwordService;
    @Resource
    private ZUserLockService lockService;

    @Resource
    private ZCaptchaService captchaService;
    @Value("${com.kg.login.isYzm}")
    private boolean IS_YZM;

    @Override
    public LoginSuccessDTO login(LoginFormDTO loginForm) throws BaseException {
        // 验证码
        if (IS_YZM) {
            if (!StringUtils.hasText(loginForm.getYzm())) {
                throw new BaseException("请输入验证码！");
            }
            if (!captchaService.checkCaptcha(loginForm.getCodeUuid(), loginForm.getYzm())) {
                throw new BaseException("验证码错误！请检查");
            }
        }
        // 判断用户是否已锁定
        ZUserLock userLock = lockService.isLocking(loginForm.getUserName());
        if (userLock != null) {
            // 锁定抛出锁定原因
            throw new BaseException(userLock.getLockReason());
        }

        // AuthenticationManager 进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginForm.getUserName(), loginForm.getPassword());
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            // 用户禁用
            BaseErrorCode errorCode = BaseErrorCode.getByCode(Ints.tryParse(e.getMessage()));
            if (errorCode != null && errorCode == BaseErrorCode.LOGIN_ERROR_USER_DISABLED) {
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_USER_DISABLED);
            }
            // 用户名异常、密码错误异常，都会捕获，触发用户锁定
            String lockInfo = lockService.loginError(loginForm.getUserName());
            if (StringUtils.hasText(lockInfo)) {
                throw new BaseException(lockInfo);
            } else {
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG);
            }
        }
        // 获取登录成功的userId
        SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity) authenticate.getPrincipal();
        String userId = userDetailEntity.getZUser().getUserId();
        // 生成JwtToken
        LoginSuccessDTO loginSuccessDTO = new LoginSuccessDTO();
        loginSuccessDTO.setAccessToken(JwtUtils.createToken(userId));
        // JwtToken有效期
        Calendar calendar = Calendar.getInstance();
        calendar.getInstance().add(Calendar.MINUTE, LoginConstant.LOGIN_JWT_TOKEN_EXPIRY);
        loginSuccessDTO.setAccessTokenValidTime(calendar.getTime());
        // 把用户信息存入redis
        redisUtils.set(LoginConstant.LOGIN_INFO_REDIS_PRE + userId, userDetailEntity,
                LoginConstant.LOGIN_JWT_TOKEN_EXPIRY * 60L);
        loginSuccessDTO.setSuccessMsg("登录成功！");
        // 检查默认密码
        ZSafety safety = safetyService.getSafety();
        if (safety.getDefaultPassword().equals(loginForm.getPassword())) {
            loginSuccessDTO.setDefaultPassword(true);// 是默认密码
        }
        // 检查密码是否失效（=0时不过期，否则过期，单位天）
        if (safety.getValidTime() > 0) {
            ZUserPassword pwd = passwordService.getById(userId);
            long betweenDay = LocalDateTimeUtil.between(pwd.getEditPasswordTime(), LocalDateTime.now(), ChronoUnit.DAYS);
            if (betweenDay > safety.getValidTime()) {
                loginSuccessDTO.setInvalidPassword(true);// 密码已失效
            }
        }
        // 重置密码错误次数
        redisUtils.delete(LoginConstant.LOGIN_ERROR_COUNT_REDIS_PRE + loginForm.getUserName());
        return loginSuccessDTO;
    }

    @Override
    public void logout() {
        // 从SecurityContextHolder中获取用户信息
        SecurityUserDetailEntity userDetailEntity =
                (SecurityUserDetailEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetailEntity.getZUser().getUserId();
        // 清空redis中的登录信息
        redisUtils.delete(LoginConstant.LOGIN_INFO_REDIS_PRE + userId);
        // 清空上下文
        SecurityContextHolder.clearContext();
    }
}
