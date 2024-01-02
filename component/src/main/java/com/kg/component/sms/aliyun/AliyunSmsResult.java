package com.kg.component.sms.aliyun;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 阿里云短信发送结果
 *
 * @author ziro
 * @date 2023/12/13 16:42
 */
@Getter
@Setter
public class AliyunSmsResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否发送成功
     */
    private boolean success;

    /**
     * 发送失败原因
     */
    private String errorMessage;

    /**
     * 发送结果JSON
     * 示例：{"Code": "OK","Message": "OK","BizId": "******","RequestId": "******"}
     */
    private String resultJson;

    /**
     * 短信签名
     */
    private String sign;

}
