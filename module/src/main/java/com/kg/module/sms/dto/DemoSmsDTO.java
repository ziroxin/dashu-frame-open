package com.kg.module.sms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 短信 - demo
 * </p>
 *
 * @author ziro
 * @since 2023-12-14
 */
@Getter
@Setter
@ApiModel(value = "DemoSmsDTO", description = "短信 - demo")
public class DemoSmsDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("发送短信id")
    private String smsId;

    @ApiModelProperty("发送渠道（例如：阿里云短信）")
    private String smsChannel;

    @ApiModelProperty("手机号（多个以英文逗号隔开，最多支持1000个）")
    private String smsPhones;

    @ApiModelProperty("发送短信内容json")
    private String sendJson;

    @ApiModelProperty("发送状态（0发送失败1发送成功）")
    private String status;

    @ApiModelProperty("返回结果json")
    private String resultJson;

    @ApiModelProperty("发送时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("短信模板")
    private String smsTemplate;
}
