package com.kg.component.sms.aliyun;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * 阿里云信息配置
 *
 * @author ziro
 * @date 2023/12/13 14:27
 */
@Configuration
@Getter
@Setter
public class AliyunConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 阿里云账号accessKeyId
     *
     * @see:https://help.aliyun.com/zh/sdk/developer-reference/manage-access-credentials
     */
    @Value("${com.kg.sms.aliyun.accessKeyId}")
    private String accessKeyId;

    /**
     * 阿里云账号accessKeySecret
     */
    @Value("${com.kg.sms.aliyun.accessKeySecret}")
    private String accessKeySecret;

    /**
     * Endpoint
     *
     * @see:https://api.aliyun.com/product/Dysmsapi
     */
    @Value("${com.kg.sms.aliyun.endpoint:dysmsapi.aliyuncs.com}")
    private String endpoint;

    /**
     * 短信签名
     */
    @Value("${com.kg.sms.aliyun.sign}")
    private String sign;
}
