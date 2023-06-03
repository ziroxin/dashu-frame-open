package com.kg.core.filter;

import com.kg.component.jwt.JwtUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.security.util.SecurityIgnoreUtils;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证用户登录状态
 * 过滤器，使用JwtToken验证
 *
 * @author ziro
 * @date 2022/5/4 22:41
 */
@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtils redisUtils;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // ============ 1. 是否忽略列表（在忽略列表中时，不抛异常） ============
        boolean isIgnore = SecurityIgnoreUtils.matcher(request.getServletPath());
        if (isIgnore) {
            // 忽略列表，也可能需要加载当前登录用户信息，所以继续执行；但遇到异常，无需抛出异常
            doIgnoreFilter(request, response, filterChain);
        } else {
            // 非忽略列表，正常逻辑判断
            // ============ 2. 获取token ============
            String jwtToken = request.getHeader(LoginConstant.LOGIN_JWT_TOKEN_KEY);// header中的token
            if (!StringUtils.hasText(jwtToken)) {
                jwtToken = request.getParameter(LoginConstant.LOGIN_JWT_TOKEN_KEY);// 参数中的token
                if (!StringUtils.hasText(jwtToken)) {
                    // 无token放行（后边security会判断权限）
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            // ============ 3. 解析token ============
            Object userId;
            try {
                userId = JwtUtils.parseToken(jwtToken);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
            }
            if (ObjectUtils.isEmpty(userId)) {
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
            }
            // ============ 4. 判断是否单例登录 ============
            System.out.println("IS_USER_LOGIN_ONLY_ONE:::" + LoginConstant.IS_USER_LOGIN_ONLY_ONE);
            if (LoginConstant.IS_USER_LOGIN_ONLY_ONE) {
                // 登录
                if (!redisUtils.hasKey(LoginConstant.LAST_LOGIN_TOKEN_PRE + userId)) {
                    // 无缓存的token，抛出token无效异常
                    throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
                }
                Object lastToken = redisUtils.get(LoginConstant.LAST_LOGIN_TOKEN_PRE + userId);
                if (lastToken == null || !jwtToken.equals(lastToken.toString())) {
                    // 无缓存token or 请求的token != 缓存的token，抛出token无效异常
                    throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
                }
            }
            // ============ 5. 加载当前登录用户信息 ============
            // 从redis中读取用户信息
            SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity) redisUtils.get(CacheConstant.LOGIN_INFO_REDIS_PRE + userId);
            if (ObjectUtils.isEmpty(userDetailEntity)) {
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
            } else {
                try {
                    // 存入SecurityContextHolder
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetailEntity, null, userDetailEntity.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
                }
            }

            // ============ 6. 继续请求动作 ============
            filterChain.doFilter(request, response);
        }
    }

    @SneakyThrows
    private void doIgnoreFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // ============ 2. 获取token ============
        String jwtToken = request.getHeader(LoginConstant.LOGIN_JWT_TOKEN_KEY);// header中的token
        if (!StringUtils.hasText(jwtToken)) {
            jwtToken = request.getParameter(LoginConstant.LOGIN_JWT_TOKEN_KEY);// 参数中的token
            if (!StringUtils.hasText(jwtToken)) {
                // 无token放行
                filterChain.doFilter(request, response);
                return;
            }
        }
        // ============ 3. 解析token ============
        Object userId = null;
        try {
            userId = JwtUtils.parseToken(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(userId)) {
            // ============ 5. 加载当前登录用户信息 ============
            // 从redis中读取用户信息
            SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity) redisUtils.get(CacheConstant.LOGIN_INFO_REDIS_PRE + userId);
            if (!ObjectUtils.isEmpty(userDetailEntity)) {
                try {
                    // 存入SecurityContextHolder
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetailEntity, null, userDetailEntity.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // ============ 6. 继续请求动作 ============
        filterChain.doFilter(request, response);
    }
}
