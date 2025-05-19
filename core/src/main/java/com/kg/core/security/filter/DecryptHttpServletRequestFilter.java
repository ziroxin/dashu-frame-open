package com.kg.core.security.filter;

import com.kg.core.security.wrapper.DecryptHttpServletWrapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求参数（data和params）加密/解密过滤
 *
 * @author ziro
 * @date 2025-05-14 15:23:57
 */
@Component
public class DecryptHttpServletRequestFilter extends OncePerRequestFilter {

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // 判断是否加密
        String isEncrypt = request.getHeader("isRequestEncrypt");
        if (StringUtils.hasText(isEncrypt) && "true".equalsIgnoreCase(isEncrypt)) {
            // 已加密，创建新的请求wrapper，在wrapper中解密参数
            filterChain.doFilter(new DecryptHttpServletWrapper(request), response);
        } else {
            // 未加密，继续执行
            filterChain.doFilter(request, response);
        }
    }
}
