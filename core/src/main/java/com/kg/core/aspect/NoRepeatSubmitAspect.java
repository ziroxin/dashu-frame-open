package com.kg.core.aspect;

import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 防止重复提交AOP
 *
 * @author ziro
 * @date 2023-01-07 11:08:15
 */
@Aspect
@Component
public class NoRepeatSubmitAspect {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 切点
     */
    @Around("@annotation(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 获取token
        String token = request.getHeader(LoginConstant.LOGIN_JWT_TOKEN_KEY);// header中的token
        if (!StringUtils.hasText(token)) {
            token = request.getParameter(LoginConstant.LOGIN_JWT_TOKEN_KEY);// 参数中的token
        }
        // 获取请求地址
        String path = request.getServletPath();
        String submitKey = token + path;
        if (redisUtils.hasKey(submitKey)) {
            // 重复提交
            throw new BaseException("您的操作过于频繁，请稍等一会！！！");
        } else {
            Object result = null;
            try {
                // 首次提交，锁定
                redisUtils.set(submitKey, GuidUtils.getUuid(), noRepeatSubmit.lockSecond());
                result = joinPoint.proceed();
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(e.getMessage());
            } finally {
                // 解锁
                redisUtils.delete(submitKey);
            }
            return result;
        }
    }
}
