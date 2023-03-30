package com.kg.core.xss;


import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.util.regex.Pattern.compile;

/**
 * Xss过滤信息
 *
 * @author ziro
 * @date 2023-01-28 14:42:18
 */
@Component
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String thisUrl = removeCtx(request.getRequestURI(), request.getContextPath());
        boolean isIgnore = false;
        if (StringUtils.hasText(thisUrl) && XssConstant.XSS_INGORE_URL_LIST.length > 0) {
            for (String permit : XssConstant.XSS_INGORE_URL_LIST) {
                if (permit.indexOf("**") >= 0) {
                    // 正则判断
                    if (compile("^" + permit.replace("**", ".*")).matcher(thisUrl).find()) {
                        isIgnore = true;
                        break;
                    }
                } else {
                    if (permit.trim().equals(thisUrl)) {
                        isIgnore = true;
                        break;
                    }
                }
            }
        }
        String contentType = request.getHeader("Content-Type");
        if (isIgnore || (contentType != null && contentType.toLowerCase().contains("multipart/form-data"))) {
            // 忽略 or 文件上传
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // xss过滤
            filterChain.doFilter(new XssHttpServletRequestWrapper(request), servletResponse);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    /**
     * 去掉当前路径上下文
     */
    private static String removeCtx(String url, String contextPath) {
        url = url.trim();
        if (!StringUtils.hasText(contextPath)) {
            return url;
        }
        if (!StringUtils.hasText(url)) {
            return "";
        }
        if (url.startsWith(contextPath)) {
            url = url.replaceFirst(contextPath, "");
        }
        return url;
    }
}
