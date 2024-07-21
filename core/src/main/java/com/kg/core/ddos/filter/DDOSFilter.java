package com.kg.core.ddos.filter;


import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.ResponseWriteUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.core.ddos.entity.ZDdos;
import com.kg.core.ddos.service.ZDdosService;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.web.ResponseResult;
import com.kg.core.zuser.entity.ZUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 防止DDoS攻击的Filter
 *
 * @author ziro
 * @date 2024/7/15 15:31
 */
@Component
@ConditionalOnProperty(name = "com.kg.ddos.enable", havingValue = "true")
public class DDOSFilter implements Filter {
    @Value("${com.kg.ddos.time}")
    private int limitMinites;
    @Value("${com.kg.ddos.limit}")
    private int limitCount;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ZDdosService ddosService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 获取客户端ip
        String clientIP = getClientIP(httpRequest);
        // 查询ip请求次数
        String key = "ddos:" + clientIP;
        List<String> requestTimeList = new ArrayList<>();
        requestTimeList.add(String.valueOf(TimeUtils.now().toTimestamp()));
        if (redisUtils.hasKey(key)) {
            requestTimeList.addAll((List<String>) redisUtils.get(key));
            // 移除 limitMinites 分钟前的请求
            requestTimeList = requestTimeList.stream()
                    .filter(r -> Long.parseLong(r) > TimeUtils.now().toTimestamp() - limitMinites * 60 * 1000)
                    .collect(Collectors.toList());
        }
        // 写入请求次数
        redisUtils.set(key, requestTimeList, limitMinites * 60l);
        // 判断请求次数是否超过限制
        if (requestTimeList.size() > limitCount) {
            // 记录日志
            ZDdos entity = new ZDdos();
            entity.setDdosId(GuidUtils.getUuid());
            entity.setUserIp(clientIP);
            entity.setRequestCount(requestTimeList.size());
            entity.setLimitJson("{" + limitMinites + "分钟内限制" + limitCount + "次}");
            ZUser currentUser = CurrentUserUtils.getCurrentUser();
            if (currentUser != null && StringUtils.hasText(currentUser.getUserId())) {
                entity.setUserId(currentUser.getUserId());
            }
            entity.setCreateTime(LocalDateTime.now());
            ddosService.save(entity);
            // 响应
            ResponseResult<Object> result = ResponseResult.builder()
                    .code(HttpStatus.TOO_MANY_REQUESTS.value() + "")
                    .message("您的请求次数过多，请稍后再试。")
                    .build();
            ResponseWriteUtils.writeJson200(httpResponse, result.toString());
            return;
        }
        // 执行请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    /**
     * 获取客户端ip
     *
     * @param request 请求
     * @return {@link String }
     */
    private String getClientIP(HttpServletRequest request) {
        String xffHeader = request.getHeader("X-Forwarded-For");
        if (xffHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xffHeader.split(",")[0];
        }
    }
}

