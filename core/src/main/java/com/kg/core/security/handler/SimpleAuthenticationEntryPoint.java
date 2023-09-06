package com.kg.core.security.handler;

import com.kg.component.utils.ResponseWriteUtils;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.web.ResponseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security 认证失败拦截器
 *
 * @author ziro
 * @date 2022/5/5 22:44
 */
@Component
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Security 认证失败拦截器（token失效 或 未传token）
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        if (request.getRequestURL().indexOf("/oauth/authorize") >= 0) {
            // Oauth2 授权码模式，未登录状态，则 forward 跳转：统一认证中心登录页
            System.out.println(authException.getMessage());
            request.getRequestDispatcher(request.getContextPath() + "/oauth2/login.html").
                    forward(request, response);
        } else {
            // 其他接口，未登录状态，抛出自定义异常
            String result = ResponseResult.builder()
                    .code("" + BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID.getCode())
                    .message(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID.getInfo()).toString();
            ResponseWriteUtils.writeJson200(response, result);
        }
    }
}
