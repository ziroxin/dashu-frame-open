package com.kg.core.aspect;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.IpUtils;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zlog.entity.ZOperateLog;
import com.kg.core.zlog.service.ZOperateLogService;
import com.kg.core.zuser.entity.ZUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 *
 * @author ziro
 * @date 2023-01-07 17:14:37
 */
@Aspect
@Component
public class AutoOperateLogAspect {
    @Resource
    private ZOperateLogService operateLogService;

    /**
     * 操作日志 - 切点
     */
    @Around("@annotation(autoOperateLog)")
    public Object around(ProceedingJoinPoint joinPoint, AutoOperateLog autoOperateLog) throws Throwable {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            ZOperateLog log = new ZOperateLog();
            log.setLogId(GuidUtils.getUuid());
            // 1. 操作接口信息
            log.setLogMethod(autoOperateLog.logMethod());
            log.setLogMsg(autoOperateLog.logMsg());
            log.setActionUrl(request.getRequestURI());
            // 2. 操作用户信息
            ZUser currentUser = CurrentUserUtils.getCurrentUser();
            if (null != currentUser) {
                log.setUserId(currentUser.getUserId());
                log.setUserName(currentUser.getUserName());
            }
            // 3. 操作IP
            log.setIp(IpUtils.getClientIP(request));
            // 4. body参数
            JSONObject content = new JSONObject();
            boolean hasFile = false;// 判断传参数中是否有上传文件，如果有，则不记录body参数
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof MultipartRequest) {
                    // 包含上传文件，不处理body参数
                    hasFile = true;
                    break;
                }
            }
            if (hasFile) {
                content.set("body", "参数中包含文件，不记录body");
            } else {
                content.set("body", JSONUtil.toJsonStr(joinPoint.getArgs()));
            }
            // 5. params参数
            content.set("params", JSONUtil.toJsonStr(request.getParameterMap()));
            log.setContent(content.toString());
            log.setCreateTime(LocalDateTime.now());
            operateLogService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return joinPoint.proceed();
        }
    }
}
