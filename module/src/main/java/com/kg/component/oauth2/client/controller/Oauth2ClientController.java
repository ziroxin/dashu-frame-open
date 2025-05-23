package com.kg.component.oauth2.client.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kg.component.jwt.JwtUtils;
import com.kg.component.oauth2.client.dto.Oauth2ClientProperties;
import com.kg.component.oauth2.client.dto.Oauth2ClientUser;
import com.kg.component.oauth2.client.dto.Oauth2UserBindDTO;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.entity.SecurityUserDetailEntity;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;
import com.kg.core.zlogin.service.ZLoginService;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import com.kg.module.oauth2.user.entity.OauthClientUser;
import com.kg.module.oauth2.user.service.OauthClientUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private OauthClientUserService userBindService;
    @Resource
    private ZLoginService loginService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private Oauth2ClientProperties client;
    @Resource
    private ZCaptchaService captchaService;

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
                    JSONObject resultObj = JSONUtil.parseObj(result, true);
                    String accessToken = resultObj.getJSONObject("data").getStr("access_token");
                    if (StringUtils.hasText(accessToken)) {
                        //2. 使用access_token换取用户信息
                        paramMap = new HashMap<>(1);
                        paramMap.put("access_token", accessToken);
                        String result2 = HttpUtil.post(client.getOauthServerUri() + "oauth/resources/userInfo?", paramMap);
                        if (StringUtils.hasText(result2) && result2.indexOf("openId") >= 0) {
                            // 授权成功，获得用户信息
                            JSONObject result2Obj = JSONUtil.parseObj(result2, true);
                            Oauth2ClientUser oauthUser = JSONUtil.toBean(result2Obj.getJSONObject("data"), Oauth2ClientUser.class);

                            // ====================== 自己写登录逻辑 =======================
                            OauthClientUser bindUser = userBindService.lambdaQuery()
                                    .eq(OauthClientUser::getOpenId, oauthUser.getOpenId())
                                    .last("limit 1").one();
                            if (bindUser != null) {
                                ZUserRoleSaveDTO user = userService.getUserById(bindUser.getUserId());
                                if (user != null) {
                                    // 已绑定用户，跳转至登录成功界面
                                    LoginFormDTO loginForm = new LoginFormDTO();
                                    loginForm.setUserName(user.getUserName());
                                    loginForm.setPassword(user.getPassword());
                                    String oauthLoginInfoId = GuidUtils.getUuid32();
                                    redisUtils.set(oauthLoginInfoId, loginService.login(loginForm));// 登录
                                    response.sendRedirect(client.getSuccessRouter() + "?loginId=" + oauthLoginInfoId);
                                }
                            }
                            // 未绑定用户，或绑定的用户不存在，跳转至绑定界面
                            String uuid = GuidUtils.getUuid32();
                            redisUtils.set(uuid, oauthUser.getOpenId());
                            response.sendRedirect(client.getUserBindRouter() + "?loginId=" + uuid);
                        }
                    }
                }
            }
        } catch (BaseException ex) {
            // 自定义异常，抛出异常，并跳转至登录失败界面，显示失败原因
            ex.printStackTrace();
            response.sendRedirect(client.getErrorRouter() + "?err=统一认证出现异常，失败原因：" + ex.getMessage());
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

    /**
     * Oauth2 用户绑定
     */
    @PostMapping("/login/userBind")
    @ResponseBody
    public LoginSuccessDTO bind(@RequestBody Oauth2UserBindDTO bindUser) {
        try {
            if (StringUtils.hasText(bindUser.getLoginId()) && redisUtils.hasKey(bindUser.getLoginId())) {
                // 验证验证码
                captchaService.checkCaptchaByConfig(bindUser.getCodeUuid(), bindUser.getYzm());
                // 尝试登录
                LoginFormDTO loginForm = JSONUtil.toBean(JSONUtil.parseObj(bindUser, true), LoginFormDTO.class);
                LoginSuccessDTO loginSuccess = loginService.login(loginForm);
                // 登录成功，开始绑定
                Object userId;
                try {
                    userId = JwtUtils.parseToken(loginSuccess.getAccessToken());
                    if (ObjectUtils.isEmpty(userId)) {
                        throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);
                }
                SecurityUserDetailEntity userDetailEntity = (SecurityUserDetailEntity)
                        redisUtils.get(CacheConstant.LOGIN_INFO_REDIS_PRE + userId);
                if (ObjectUtils.isEmpty(userDetailEntity)) {
                    throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
                }
                OauthClientUser entity = new OauthClientUser();
                entity.setUserId(userDetailEntity.getZUser().getUserId());
                entity.setOpenId(redisUtils.get(bindUser.getLoginId()).toString());
                userBindService.save(entity);
                // 绑定成功，返回登录成功信息
                return loginSuccess;
            }
        } catch (BaseException ex) {
            ex.printStackTrace();
            // 自定义异常处理
            LoginSuccessDTO errorResult = new LoginSuccessDTO();
            errorResult.setSuccessMsg("error|绑定用户失败，失败原因：" + ex.getMessage());
            return errorResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginSuccessDTO errorResult = new LoginSuccessDTO();
        errorResult.setSuccessMsg("error|绑定用户失败，未知异常，请重试！");
        return errorResult;
    }


    /**
     * Oauth2 用户解绑
     */
    @GetMapping("/login/userUnbind")
    @ResponseBody
    public void userUnbind() throws BaseException {
        ZUser currentUser = CurrentUserUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
        }
        // 解绑
        userBindService.lambdaUpdate()
                .eq(OauthClientUser::getUserId, currentUser.getUserId())
                .remove();
    }

    /**
     * 获取 oauth2 服务端 authorization 地址
     */
    @GetMapping("/login/getOauthAuthorizationUrl")
    @ResponseBody
    public String getOauthAuthorizationUrl(String state) throws UnsupportedEncodingException {
        return client.getOauthServerUri() + "oauth/authorize" +
                "?client_id=" + client.getClientId() +
                "&redirect_uri=" + URLEncoder.encode(client.getRedirectUri(), "UTF-8") +
                "&response_type=code" +
                "&state=" + state;
    }

}
