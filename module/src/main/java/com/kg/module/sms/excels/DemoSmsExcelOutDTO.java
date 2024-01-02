package com.kg.module.sms.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 短信 - demo
 *
 * @author ziro
 * @date 2023-12-14
 */
@Getter
@Setter
public class DemoSmsExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 发送短信id
     */
    private String smsId;

    /**
     * 发送渠道（例如：阿里云短信）
     */
    private String smsChannel;

    /**
     * 手机号（多个以英文逗号隔开，最多支持1000个）
     */
    private String smsPhones;

    /**
     * 发送短信内容json
     */
    private String sendJson;

    /**
     * 发送状态（0发送失败1发送成功）
     */
    private String status;

    /**
     * 返回结果json
     */
    private String resultJson;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}