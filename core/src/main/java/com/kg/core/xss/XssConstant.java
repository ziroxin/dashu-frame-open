package com.kg.core.xss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Xss 相关常量
 *
 * @author ziro
 * @date 2023-01-28 16:40:33
 */
public class XssConstant {
    // 忽略的Url
    public static String[] XSS_INGORE_URL_LIST;

    static {
        // 读取忽略列表
        List<String> antMatchers = FileUtil.readLines("xss.ignore", CharsetUtil.defaultCharset());
        XssConstant.XSS_INGORE_URL_LIST = antMatchers.stream()
                .filter(url -> StringUtils.hasText(url) && !url.startsWith("#"))
                .collect(Collectors.toList()).toArray(new String[]{});
    }
}
