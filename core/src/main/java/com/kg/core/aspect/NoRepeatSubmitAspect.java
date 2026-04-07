package com.kg.core.aspect;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kg.component.utils.IpUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交 AOP
 * 注意：锁会在 lockSecond 秒后自动过期，如果业务执行时间超过锁过期时间，
 * 相同的请求可以在锁过期后再次提交。请根据业务场景合理设置 lockSecond。
 *
 * @author ziro
 * @date 2023-01-07 11:08:15
 */
@Aspect
@Component
public class NoRepeatSubmitAspect {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 在 ObjectMapper 配置中启用排序
    private final ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

    /**
     * 防止重复提交 - 切点
     */
    @Around("@annotation(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        // 1获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        // 2.1获取身份标志（组合token、ip等信息）
        String identity = resolveIdentity(request);
        // 2.2获取请求地址
        String path = request.getServletPath();
        // 2.3获取请求方法
        String method = request.getMethod();
        // 2.4获取参数信息，若配置了不校验参数，则忽略（默认：带参数校验）
        String paramsFingerprint = noRepeatSubmit.useParamFingerprint()
                ? generateParamsFingerprint(joinPoint.getArgs())
                : "ignore_params";
        // 3组装redis缓存key
        String submitKey = String.format("repeat_submit:%s:%s:%s:%s", identity, path, method, paramsFingerprint);
        // 4存入redis锁
        try {
            long lt = Math.max(noRepeatSubmit.lockSecond(), 1L);
            Boolean setResult = redisTemplate.opsForValue().setIfAbsent(submitKey, "1", lt, TimeUnit.SECONDS);
            if (!Boolean.TRUE.equals(setResult)) {
                throw new BaseException(String.format("操作过于频繁，请 %d 秒后再试", lt));
            }
            return joinPoint.proceed();
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            // Redis 异常时，放行请求，避免影响正常业务
            return joinPoint.proceed();
        }
    }

    /**
     * 生成请求参数的指纹
     * 用于区分不同参数的请求
     */
    private String generateParamsFingerprint(Object[] args) {
        if (args == null || args.length == 0) {
            return "no_params";
        }
        try {
            Map<String, Object> paramsMap = new LinkedHashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                // 文件上传等特殊场景，跳过
                if (arg == null || shouldIgnoreArg(arg)) {
                    continue;
                }
                // 基本类型，转字符串
                if (isPrimitiveType(arg)) {
                    paramsMap.put("arg_" + i, String.valueOf(arg));
                } else {
                    paramsMap.put("arg_" + i, arg);
                }
            }
            if (paramsMap.isEmpty()) {
                return "no_params";
            }
            String jsonStr = objectMapper.writeValueAsString(paramsMap);
            return DigestUtils.md5Hex(jsonStr);
        } catch (Exception e) {
            Map<String, Object> fallbackMap = new LinkedHashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                // 文件上传等特殊场景，跳过
                if (arg == null || shouldIgnoreArg(arg)) {
                    continue;
                }
                // 使用 identityHashCode 避免调用 toString() 可能泄露敏感信息
                fallbackMap.put("arg_" + i, arg.getClass().getName() + "@" + System.identityHashCode(arg));
            }
            if (fallbackMap.isEmpty()) {
                return "no_params";
            }
            return DigestUtils.md5Hex(String.valueOf(fallbackMap));
        }
    }

    /**
     * 获取身份标志
     * 若有token，则使用token作为身份标志
     * 否则使用ip+ua作为身份标志
     */
    private String resolveIdentity(HttpServletRequest request) {
        String token = request.getHeader(LoginConstant.LOGIN_JWT_TOKEN_KEY);
        if (!StringUtils.hasText(token)) {
            token = request.getParameter(LoginConstant.LOGIN_JWT_TOKEN_KEY);
        }
        if (StringUtils.hasText(token)) {
            // 对token做哈希，缩短长度且保护隐私
            return DigestUtils.md5Hex(token);
        }
        String ip = IpUtils.getClientIP(request);
        ip = StringUtils.hasText(ip) ? ip : "unknown_ip";
        String ua = request.getHeader("User-Agent");
        // UA可能很长，所以对UA也做哈希，缩短长度
        String uaHash = StringUtils.hasText(ua) ? DigestUtils.md5Hex(ua) : "unknown_ua";
        return DigestUtils.md5Hex(ip + "|" + uaHash);
    }

    /**
     * 文件上传等特殊场景，忽略校验
     */
    private boolean shouldIgnoreArg(Object arg) {
        if (arg == null) {
            return true;
        }
        return arg instanceof HttpServletRequest
                || arg instanceof ServletResponse
                || arg instanceof MultipartFile
                || arg instanceof MultipartFile[]
                || arg.getClass().getName().contains("InputStream");
    }

    /**
     * 判断是否为基本类型
     */
    private boolean isPrimitiveType(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz == Byte.class
                || clazz == Short.class;
    }
}