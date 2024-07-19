package com.kg.component.oss.aliyun.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Oss 基础配置
 *
 * @author ziro
 * @date 2024/7/16 16:11
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "com.kg.oss.aliyun")
public class OssBaseConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** accessId */
    private String accessKeyId;
    /** 密钥 */
    private String accessKeySecret;
    /** Endpoint */
    private String endpoint;
    /** oss存储桶 */
    private String bucket;
    /** 上传域名，格式：https://bucketname.endpoint */
    private String host;
    /** 单次上传凭证的有效期，单位：秒 */
    private long uploadTokenExpire;
    /** 回调地址url */
    private String callbackUrl;
    /** 访问域名 */
    private String domain;
    /** 临时访问凭证有效期，单位：秒 */
    private int stsExpire;
}
