package com.kg.core.filter;

import com.kg.component.jwt.JwtUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.security.util.SecurityIgnoreUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

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
    @Autowired
    private RedisUtils redisUtils;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // ============ 1. 是否忽略列表（在忽略列表中时，不抛异常） ============
        boolean isIgnore = SecurityIgnoreUtils.matcher(request.getServletPath());
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
        Object userId = null;
        boolean tokenFlag = true;
        try {
            userId = JwtUtils.parseToken(jwtToken);
            if (ObjectUtils.isEmpty(userId)) {
                tokenFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            tokenFlag = false;
        }
        if (!tokenFlag) {
            // token解析出错，且不在忽略列表，返回，抛异常
            if (!isIgnore) {
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
            }
        } else {
            // ============ 4. 加载当前登录用户信息 ============
            boolean userFlag = true;
            // 从redis中读取用户信息
            SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity) redisUtils.get(LoginConstant.LOGIN_INFO_REDIS_PRE + userId);
            if (ObjectUtils.isEmpty(userDetailEntity)) {
                userFlag = false;
            } else {
                try {
                    // 存入SecurityContextHolder
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetailEntity, null, userDetailEntity.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    userFlag = false;
                }
            }
            if (!isIgnore && !userFlag) {
                // user加载出错，且不在忽略列表，返回，抛异常
                throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
            }
        }

        // ============ 5. 继续请求动作 ============
        filterChain.doFilter(request, response);
    }
}
