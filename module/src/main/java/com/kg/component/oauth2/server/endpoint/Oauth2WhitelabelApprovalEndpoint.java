package com.kg.component.oauth2.server.endpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 授权码模式 - 用户确认授权页面
 *
 * @author ziro
 * @date 2023/9/5 17:55
 */
@Controller
public class Oauth2WhitelabelApprovalEndpoint {

    /**
     * 授权码模式，用户确认授权页面
     * 说明：
     * 1. 当应用(oauth2 client)字段autoapprove=false时，用户登录后，需要用户主动【同意授权】，允许第三方应用获取自己的信息
     * 2. 当字段autoapprove=true时，会跳过这一步，直接带code回调应用
     */
    @RequestMapping("/oauth2/approval")
    public ModelAndView oauth2Approval(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put("client_id", request.getAttribute("client_id"));
        model.put("scopes", request.getAttribute("scopes"));
        return new ModelAndView("oauth2/confirm_access", model);
    }
}
