package com.kg.module.sms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
@TableName("demo_sms")
@ApiModel(value = "DemoSms对象", description = "短信 - demo")
public class DemoSms implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("发送短信id")
    @TableId(value = "sms_id", type = IdType.ASSIGN_UUID)
    private String smsId;

    @ApiModelProperty("发送渠道（例如：阿里云短信）")
    @TableField("sms_channel")
    private String smsChannel;

    @ApiModelProperty("手机号（多个以英文逗号隔开，最多支持1000个）")
    @TableField("sms_phones")
    private String smsPhones;

    @ApiModelProperty("发送短信内容json")
    @TableField("send_json")
    private String sendJson;

    @ApiModelProperty("发送状态（0发送失败1发送成功）")
    @TableField("status")
    private String status;

    @ApiModelProperty("返回结果json")
    @TableField("result_json")
    private String resultJson;

    @ApiModelProperty("发送时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
