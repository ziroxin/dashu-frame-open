package com.kg.core.security.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * Security忽略名单工具类
 *
 * @author ziro
 * @date 2023-02-22 14:46:39
 */
public class SecurityIgnoreUtils {
    // 忽略名单
    private static List<String> SECURITY_IGNORE_URLS;

    private static void loadIgnoreUrl() {
        // 加载忽略名单
        List<String> antMatchers = FileUtil.readLines("security.ignore", CharsetUtil.defaultCharset());
        SECURITY_IGNORE_URLS = antMatchers.stream().filter(url -> StringUtils.hasText(url) && !url.startsWith("#"))
                .collect(Collectors.toList());
    }

    /**
     * 判断当前URL，是否在忽略名单中
     *
     * @param url 待判断的url
     * @return 是否在忽略名单中
     */
    public static boolean matcher(String url) {
        if (SECURITY_IGNORE_URLS == null || SECURITY_IGNORE_URLS.size() <= 0) {
            loadIgnoreUrl();
        }
        if (SECURITY_IGNORE_URLS.contains(url)) {
            return true;
        } else {
            // 判断 **
            for (String permit : SECURITY_IGNORE_URLS) {
                if (permit.indexOf("**") >= 0) {
                    //正则表达式相匹配 ^表示以什么开头   .*  表示0-n个字符
                    if (compile("^" + permit.replace("**", ".*")).matcher(url).find()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
