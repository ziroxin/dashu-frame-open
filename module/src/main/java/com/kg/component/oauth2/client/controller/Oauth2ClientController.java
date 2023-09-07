package com.kg.component.oauth2.client.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kg.component.oauth2.client.dto.Oauth2ClientProperties;
import com.kg.component.oauth2.client.dto.Oauth2ClientUser;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;
import com.kg.core.zlogin.service.ZLoginService;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.service.IZUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author ziro
 * @date 2023/9/5 16:17
 */
@Controller
@RequestMapping("/oauth2/client")
public class Oauth2ClientController {
    @Resource
    private IZUserService userService;
    @Resource
    private ZLoginService loginService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private Oauth2ClientProperties client;

    /**
     * Oauth2 Client(客户端)回调地址
     *
     * @param code  授权码模式的code
     * @param state 用户自定义的state，回调时原样返回
     */
    @RequestMapping("/login/code")
    public void loginCode(String code, String state, HttpServletResponse response) throws IOException {
        try {
            // 用户自定义的state，回调时原样返回，需要时使用
            System.out.println(state);
            // 1. 使用code换取access_token(code只能使用1次，使用后失效)
            if (StringUtils.hasText(code)) {
                HashMap<String, Object> paramMap = new HashMap<>(5);
                paramMap.put("client_id", client.getClientId());
                paramMap.put("client_secret", client.getClientSecret());
                paramMap.put("redirect_uri", client.getRedirectUri());
                paramMap.put("grant_type", "authorization_code");
                paramMap.put("code", code);
                String result = HttpUtil.post(client.getOauthServerUri() + "oauth/token?", paramMap);
                if (StringUtils.hasText(result) && result.indexOf("access_token") >= 0) {
                    JSONObject resultObj = JSONUtil.parseObj(result);
                    String accessToken = resultObj.getJSONObject("data").getStr("access_token");
                    if (StringUtils.hasText(accessToken)) {
                        //2. 使用access_token换取用户信息
                        paramMap = new HashMap<>(1);
                        paramMap.put("access_token", accessToken);
                        String result2 = HttpUtil.post(client.getOauthServerUri() + "oauth/resources/userInfo?", paramMap);
                        if (StringUtils.hasText(result2) && result2.indexOf("openId") >= 0) {
                            // 授权成功，获得用户信息
                            JSONObject result2Obj = JSONUtil.parseObj(result2);
                            Oauth2ClientUser oauthUser = JSONUtil.toBean(result2Obj.getJSONObject("data"), Oauth2ClientUser.class);

                            // ========== 自己写登录逻辑，跳转至登录成功界面 ==========
                            ZUserRoleSaveDTO user = userService.getUserById(oauthUser.getOpenId());
                            LoginFormDTO loginForm = new LoginFormDTO();
                            loginForm.setUserName(user.getUserName());
                            loginForm.setPassword(user.getPassword());
                            String oauthLoginInfoId = GuidUtils.getUuid32();
                            redisUtils.set(oauthLoginInfoId, loginService.login(loginForm));
                            response.sendRedirect(client.getSuccessRouter() + "?loginId=" + oauthLoginInfoId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 失败，跳转至登录失败界面
        response.sendRedirect(client.getErrorRouter());
    }

    /**
     * 登录成功数据校验
     *
     * @param loginId oauth登录id
     */
    @GetMapping("/login/check")
    @ResponseBody
    public LoginSuccessDTO loginCheck(String loginId, HttpServletResponse response) throws IOException {
        try {
            if (StringUtils.hasText(loginId)) {
                if (redisUtils.hasKey(loginId)) {
                    return (LoginSuccessDTO) redisUtils.get(loginId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出错
        LoginSuccessDTO errorResult = new LoginSuccessDTO();
        errorResult.setSuccessMsg("error|授权意外失败了！请重新登录");
        return errorResult;
    }

}
