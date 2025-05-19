package com.kg.core.security.wrapper;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kg.component.utils.MyRSAUtils;
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
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求参数全局解密方法
 * <p>
 * 说明：
 * 在请求处理过程中，我们想要拦截 RequestBody 中的数据，且不影响后续的请求。
 * 但是，由于 RequestBody 通常是一次性读取的输入流，一旦读取过，输入流就会关闭，无法再次读取。
 * 因此，我们需要将输入流的内容读出，解密操作完毕后，创建一个新的请求，把解密数据写入，继续请求。
 * <p>
 *
 * @author ziro
 * @date 2025/5/14 15:47
 */
public class DecryptHttpServletWrapper extends HttpServletRequestWrapper {
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

    public DecryptHttpServletWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 读取body，并解密，然后放入requestBody中，备用
        setRequestBody(new String(IOUtils.toByteArray(request.getInputStream()), CHARSET_UTF8));
    }

    /**
     * 处理body参数的方法
     */
    private void setRequestBody(String body) {
        if (!StringUtils.hasText(body)) {
            return;
        }
        System.out.println("=================111-body:" + body);
        if (JSONUtil.isTypeJSONObject(body)) {
            JSONObject bodyJson = JSONUtil.parseObj(body);
            if (bodyJson.containsKey("encryptDataArray")) {
                // 数组类型body，例如删除接口
                requestBody = decryptString(bodyJson.getStr("encryptDataArray"));
            } else {
                // 其他json类型body，例如新增、修改接口
                bodyJson.keySet().forEach(key -> {
                    bodyJson.set(key, decrypt(bodyJson.getStr(key)));
                });
                requestBody = JSONUtil.toJsonStr(bodyJson);
            }
        } else {
            requestBody = decryptString(body);
        }
    }

    /**
     * 统一解密方法1：字符串
     */
    private String decryptString(String value) {
        if (StringUtils.hasText(value)) {
            try {
                return URLDecoder.decode(MyRSAUtils.decryptPrivate(value), CHARSET_UTF8);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("URLDecoder解码失败:" + e.getMessage());
            }
        }
        return value;
    }

    /**
     * 统一解密方法2：不同类型分别处理（数组、JSON、普通字符串）
     */
    private Object decrypt(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String valueNew = decryptString(value);
        if (valueNew.startsWith("object_")) {
            String valueAfter = valueNew.substring("object_".length());
            if (JSONUtil.isTypeJSONArray(valueAfter)) {
                return JSONUtil.parseArray(valueAfter);
            }
            if (JSONUtil.isTypeJSONObject(valueAfter)) {
                return JSONUtil.parseObj(valueAfter);
            }
        } else if (valueNew.startsWith("string_")) {
            return valueNew.substring("string_".length());
        }
        return valueNew;
    }

    /**
     * 传参方式 2：queryString参数
     */
    @Override
    public String getQueryString() {
        System.out.println("=================222-queryString:" + super.getQueryString());
        return decryptString(super.getQueryString());
    }

    /**
     * 传参方式 3：params参数
     */
    @Override
    public String getParameter(String name) {
        System.out.println("=================333-name:" + name);
        return decryptString(super.getParameter(name));
    }

    /**
     * 传参方式 4：多个params参数
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        System.out.println("=================444-name:" + name);
        System.out.println("=================444-values:" + JSONUtil.toJsonStr(values));
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                values[i] = decryptString(values[i]);
            }
        }
        System.out.println("=================444-values-after:" + JSONUtil.toJsonStr(values));
        return values;
    }

    /**
     * 传参方式 5：多个params参数
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        System.out.println("=================555-parameterMap:" + JSONUtil.toJsonStr(parameterMap));
        Map<String, String[]> map = new LinkedHashMap<>();
        if (parameterMap == null) {
            return super.getParameterMap();
        }
        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            if (values != null && values.length > 0) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = decryptString(values[i]);
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

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (originalInputStream == null) {
            if (StringUtils.hasText(requestBody)) {
                originalInputStream = new RequestCatchingServletInputStream(requestBody.getBytes(CHARSET_UTF8));
            } else {
                originalInputStream = new RequestCatchingServletInputStream(new byte[0]);
            }
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