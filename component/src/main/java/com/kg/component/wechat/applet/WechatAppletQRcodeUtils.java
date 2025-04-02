package com.kg.component.wechat.applet;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.file.utils.UploadFileUtils;
import com.kg.component.redis.RedisUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序扫描二维码跳转指定页面
 *
 * @author ziro
 * @date 2023-12-11 15:20
 * @see:https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/qr-code/getUnlimitedQRCode.html
 */
@Component
public class WechatAppletQRcodeUtils {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private WechatAppletConfig appletConfig;

    /**
     * 微信小程序二维码（扫码进入小程序首页page/index/index）
     *
     * @param scene 扫描二维码带的参数（最大32个可见字符，只支持数字，大小写英文以及部分特殊字符）
     * @return 二维码图片保存地址
     */
    public FileDTO getQRCode(String scene) {
        return getQRCode(scene, null, null);
    }

    /**
     * 微信小程序二维码（扫码进入特定页面）
     *
     * @param scene 扫描二维码带的参数（最大32个可见字符，只支持数字，大小写英文以及部分特殊字符）
     * @param page  跳转的小程序页面
     * @return 二维码图片保存地址
     */
    public FileDTO getQRCode(String scene, String page) {
        return getQRCode(scene, page, null);
    }

    /**
     * 微信小程序二维码（扫码进入小程序首页）：特定环境版本
     *
     * @param scene       扫描二维码带的参数（最大32个可见字符，只支持数字，大小写英文以及部分特殊字符）
     * @param env_version 环境版本（正式版为"release"，体验版为"trial"，开发版为"develop"）
     * @return 二维码图片保存地址
     */
    public FileDTO getQRCodeEnvVersion(String scene, String env_version) {
        return getQRCode(scene, null, env_version);
    }

    /**
     * 微信小程序二维码（扫码进入特定页面）：特定环境版本
     *
     * @param scene       扫描二维码带的参数（最大32个可见字符，只支持数字，大小写英文以及部分特殊字符）
     * @param page        跳转的小程序页面
     * @param env_version 环境版本（正式版为"release"，体验版为"trial"，开发版为"develop"）
     * @return 二维码图片保存地址
     */
    public FileDTO getQRCodeEnvVersion(String scene, String page, String env_version) {
        return getQRCode(scene, page, env_version);
    }


    /**
     * 微信小程序二维码（扫码进入特定页面）
     *
     * @param scene       必填：扫描二维码带的参数（最大32个可见字符，只支持数字，大小写英文以及部分特殊字符）
     * @param page        选填：跳转的小程序页面，默认是主页pages/index/index，详见文档
     * @param env_version 选填：环境版本，默认正式版（正式版为"release"，体验版为"trial"，开发版为"develop"）
     * @return 二维码图片保存地址
     * @see:https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/qr-code/getUnlimitedQRCode.html
     */
    private FileDTO getQRCode(String scene, String page, String env_version) {
        String accessToken = getAccessToken();
        JSONObject bodyObj = new JSONObject();
        // 参数（最大32个可见字符，只支持数字）
        bodyObj.set("scene", scene);
        // 环境版本（正式版为"release"，体验版为"trial"，开发版为"develop"）
        bodyObj.set("env_version", StringUtils.hasText(env_version) ? env_version : "release");
        // 跳转到点餐页（根路径前不要填加 /，如果不填写这个字段，默认跳主页面）
        bodyObj.set("page", StringUtils.hasText(page) ? page : "pages/index/index");
        byte[] buffer = HttpUtil.createPost(appletConfig.getQRCodeUrl() + "?access_token=" + accessToken)
                .body(bodyObj.toString()).execute().bodyBytes();
        // 保存二维码（base64转图片）
        return UploadFileUtils.bufferToFile(buffer, "wechat/applet/qrcode", "jpg");
    }

    /**
     * 微信小程序AccessToken，暂存redis的key（有效期内不用重新获取）
     */
    private final String APPLET_ACCESS_TOKEN_REDIS_KEY = "wechat_applet_access_token";

    /**
     * 获取小程序access_token
     */
    private String getAccessToken() {
        try {
            if (redisUtils.hasKey(APPLET_ACCESS_TOKEN_REDIS_KEY)) {
                return redisUtils.get(APPLET_ACCESS_TOKEN_REDIS_KEY).toString();
            }
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appid", appletConfig.getAppId());
            paramMap.put("secret", appletConfig.getSecret());
            paramMap.put("grant_type", "client_credential");
            // 请求微信
            String result = HttpUtil.get(appletConfig.getAccessTokenUrl(), paramMap);
            JSONObject obj = JSONUtil.parseObj(result, true);
            String access_token = obj.getStr("access_token");
            if (StringUtils.hasText(access_token)) {
                // 把access_token填入redis，有效期：实际有效期-100s
                redisUtils.set(APPLET_ACCESS_TOKEN_REDIS_KEY, access_token, (obj.getLong("expires_in") - 100));
                return access_token;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
