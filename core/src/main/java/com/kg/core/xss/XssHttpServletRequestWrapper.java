package com.kg.core.xss;

import cn.hutool.json.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Xss处理
 *
 * @author ziro
 * @date 2023-01-28 16:48:51
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 存储body参数
     */
    private String requestBody;
    /**
     * 存储原始请求数据
     */
    private ServletInputStream originalInputStream;
    /**
     * IO流
     */
    private BufferedReader reader;
    /**
     * 字符集格式
     */
    private final String CHARSET_UTF8 = "UTF-8";

    public XssHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 取出body数据，备用
        setRequestBody(new String(IOUtils.toByteArray(request.getInputStream()), CHARSET_UTF8));
    }

    /**
     * 传参方式 1：header参数
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (StringUtils.hasText(value)) {
            return XssFormatUtil.cleanHtml(value);
        }
        return value;
    }

    /**
     * 传参方式 2：queryString参数
     */
    @Override
    public String getQueryString() {
        return StringUtils.hasText(super.getQueryString()) ? XssFormatUtil.cleanHtml(super.getQueryString()) : "";
    }

    /**
     * 传参方式 3：params参数
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (StringUtils.hasText(value)) {
            return XssFormatUtil.cleanHtml(value);
        }
        return value;
    }

    /**
     * 传参方式 4：多个params参数
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return values;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = XssFormatUtil.cleanHtml(values[i]);
        }
        return values;
    }

    /**
     * 传参方式 5：多个params参数
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameterMap = super.getParameterMap();
        if (parameterMap == null) {
            return super.getParameterMap();
        }
        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            if (values != null && values.length > 0) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = XssFormatUtil.cleanHtml(values[i]);
                }
            }
            map.put(key, values);
        }
        return map;
    }

    /**
     * 传参方式 6：body参数
     */
    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(getInputStream(), CHARSET_UTF8));
        }
        return reader;
    }

    /**
     * 存储body数据，备用
     */
    private void setRequestBody(String body) {
        if (!StringUtils.hasText(body)) {
            // 为空时，直接返回
            return;
        }
        if (JSONUtil.isTypeJSONObject(body)) {
            Map<String, Object> bodyMap = JSONUtil.toBean(body, Map.class);
            Map<String, Object> result = new HashMap<>(bodyMap.size());
            for (String key : bodyMap.keySet()) {
                Object val = bodyMap.get(key);
                if (bodyMap.get(key) instanceof String) {
                    result.put(key, XssFormatUtil.cleanHtml(val.toString()));
                } else {
                    result.put(key, val);
                }
            }
            requestBody = JSONUtil.toJsonStr(result);
        } else {
            requestBody = XssFormatUtil.cleanHtml(body);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (originalInputStream == null) {
            originalInputStream = new RequestCatchingServletInputStream(requestBody.getBytes(CHARSET_UTF8));
        }
        return originalInputStream;
    }

    /**
     * 缓存输入流
     */
    private class RequestCatchingServletInputStream extends ServletInputStream {
        private ByteArrayInputStream inputStream;

        public RequestCatchingServletInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
