package com.kg.component.oss.aliyun.dto;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 客户端直传，上传凭证配置 DTO
 *
 * @author ziro
 * @date 2024/7/16 16:11
 */
@Getter
@Setter
public class OssClientUploadDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 以下字段，对应前端需要传递的参数名
     */
    private String OSSAccessKeyId;
    private String policy;
    private String Signature;
    /** oss中的路径+文件名 */
    private String key;
    private String host;
    private String expire;
    // 回调配置 @See:https://help.aliyun.com/zh/oss/use-cases/overview-20?spm=a2c4g.11186623.0.0.2f4f5febxZBpOm
    private String callback;
    // 回调时，自定义参数
    private JSONObject callbackVar;
}
