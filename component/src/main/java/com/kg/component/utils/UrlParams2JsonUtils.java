package com.kg.component.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Url参数和Json字符串互转工具类
 *
 * @author ziro
 * @date 2024/7/17 16:46
 */
public class UrlParams2JsonUtils {
    /**
     * 将url参数转为json字符串
     *
     * @param urlParams url参数
     * @return {@link String }
     */
    public static JSONObject toJson(String urlParams) {
        JSONObject json = new JSONObject();
        String[] params = urlParams.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            if (kv.length >= 2) {
                String key = kv[0];
                String v = param.substring(key.length() + 1);
                // 仅在开头或结尾存在单引号或双引号时去除
                if ((v.startsWith("'") && v.endsWith("'")) || (v.startsWith("\"") && v.endsWith("\""))) {
                    v = v.substring(1, v.length() - 1);
                }
                json.put(key, v);
            }
        }
        return json;
    }

    /**
     * 将url参数转为json字符串
     *
     * @param urlParams url参数
     * @return {@link String }
     */
    public static JSONObject toJsonDecode(String urlParams) throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        String[] params = urlParams.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            if (kv.length >= 2) {
                String key = kv[0];
                String v = URLDecoder.decode(param.substring(key.length() + 1));
                // 仅在开头或结尾存在单引号或双引号时去除
                if ((v.startsWith("'") && v.endsWith("'")) || (v.startsWith("\"") && v.endsWith("\""))) {
                    v = v.substring(1, v.length() - 1);
                }
                json.put(key, v);
            }
        }
        return json;
    }

    /**
     * 将json字符串转为url参数（参数value会进行url编码）
     *
     * @param jsonStr json字符串
     * @return {@link String }
     */
    public static String toUrlParams(String jsonStr) throws UnsupportedEncodingException {
        JSONObject json = JSONUtil.parseObj(jsonStr, true);
        StringBuilder sb = new StringBuilder();
        for (String key : json.keySet()) {
            sb.append(key).append("=").append(URLEncoder.encode(json.getStr(key), "UTF-8")).append("&");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }
}