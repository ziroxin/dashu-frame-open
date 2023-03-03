package com.kg.core.security.handler;

import com.kg.component.utils.ResponseWriteUtils;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.web.ResponseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败拦截器
 *
 * @author ziro
 * @date 2022/5/5 22:44
 */
@Component
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 认证异常（无token或者token无效）
        String result = ResponseResult.builder()
                .code("" + BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID.getCode())
                .message(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID.getInfo()).toString();
        ResponseWriteUtils.writeJson200(response, result);
    }
}
