package com.kg.component.wechat.applet;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序登录api：通过js_code获取用户openid
 *
 * @author ziro
 * @date 2023-12-11 15:20
 * @see:https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
 */
@Component
public class WechatAppletLoginUtils {
    @Resource
    private WechatAppletConfig appletConfig;

    /**
     * 小程序获取微信用户openid
     *
     * @param js_code 小程序获取的用户code
     * @return 微信用户openid
     * @see:https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
     */
    public String getOpenidByJsCode(String js_code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", appletConfig.getAppId());
        paramMap.put("secret", appletConfig.getSecret());
        paramMap.put("js_code", js_code);
        paramMap.put("grant_type", "authorization_code");
        // 请求微信
        String result = HttpUtil.get(appletConfig.getJsCodeUrl(), paramMap);
        JSONObject obj = JSONUtil.parseObj(result);
        String openId = obj.getStr("openid");
        if (StringUtils.hasText(openId)) {
            // 请求成功
            return openId;
        }
        return null;
    }


}
