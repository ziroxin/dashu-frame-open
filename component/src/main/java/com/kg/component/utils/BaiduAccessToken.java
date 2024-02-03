package com.kg.component.utils;

import cn.hutool.json.JSONObject;
import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import com.kg.component.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yrt
 * @date 2022/7/26 9:08
 */
@Component
public class BaiduAccessToken {

    @Resource
    private RedisUtils redisUtils;

    // 百度API Host
    @Value("${com.kg.baidu.host}")
    private String BAIDU_API_HOST;
    // 百度API Key
    @Value("${com.kg.baidu.client-id}")
    private String CLIENT_ID;
    // 百度Secret Key
    @Value("${com.kg.baidu.client-secret}")
    private String CLIENT_SECRET;
    // 百度grant_type
    @Value("${com.kg.baidu.grant-type}")
    private String GRANT_TYPE;
    // 百度access_token的redis前缀
    private final String ACCESS_TOKEN_REDIS_KEY = "baidu_access_token@";


    // 获取AccessToken
    public String GetAccessToken() {
        try {
            if (redisUtils.hasKey(ACCESS_TOKEN_REDIS_KEY)) {
                return redisUtils.get(ACCESS_TOKEN_REDIS_KEY).toString();
            }

            ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.POST, BAIDU_API_HOST);
            // 设置header参数
            request.addHeaderParameter("Content-Type", "application/json;charset=UTF-8");
            // 设置query参数
            request.addQueryParameter("client_id", CLIENT_ID);
            request.addQueryParameter("client_secret", CLIENT_SECRET);
            request.addQueryParameter("grant_type", GRANT_TYPE);

            ApiExplorerClient client = new ApiExplorerClient();
            ApiExplorerResponse response = client.sendRequest(request);
            JSONObject jsonObj = new JSONObject(response.getResult());
            String accessToken = jsonObj.get("access_token").toString();
            Long expiresIn = jsonObj.getLong("expires_in");
            redisUtils.set(ACCESS_TOKEN_REDIS_KEY, accessToken, expiresIn - 1000);
            // 返回结果格式为Json字符串
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
