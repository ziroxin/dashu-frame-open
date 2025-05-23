package com.kg.component.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 *
 * @author ziro
 * @date 2025/4/11 16:03
 */
public class IpUtils {
    /**
     * 获取客户端IP
     * <p>
     * 取最后一个非unknown的IP，有可能有多个IP，一般最后一个IP才是真实的IP
     */
    public static String getClientIP(HttpServletRequest request) {
        String[] ipList = getClientIPs(request);
        if (ipList != null && ipList.length > 0) {
            return ipList[ipList.length - 1];
        }
        return null;
    }

    /**
     * 获取客户端IP数组
     */
    public static String[] getClientIPs(HttpServletRequest request) {
        String[] headers = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        return getClientIPByHeader(request, headers);
    }

    /**
     * 获取客户端IP数组
     */
    private static String[] getClientIPByHeader(HttpServletRequest request, String... headerNames) {
        String ipStr;
        for (String header : headerNames) {
            ipStr = request.getHeader(header);
            if (StringUtils.hasText(ipStr) && !"unknown".equalsIgnoreCase(ipStr)) {
                return getMultistageReverseProxyIp(ipStr);
            }
        }
        ipStr = request.getRemoteAddr();
        return getMultistageReverseProxyIp(ipStr);
    }

    /**
     * 从多级反向代理中 非unknown IP地址数组
     */
    private static String[] getMultistageReverseProxyIp(String ipStr) {
        // 多级反向代理检测
        if (StringUtils.hasText(ipStr)) {
            return ipStr.trim().split(",");
        }
        return null;
    }
}
