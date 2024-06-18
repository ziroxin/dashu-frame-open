package com.kg.module.messageTo.dto;

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
 * 消息发送至
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@Getter
@Setter
@ApiModel(value = "ZMessageToDTO", description = "消息发送至")
public class ZMessageToDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息发送至ID")
    private String toId;

    @ApiModelProperty("消息ID")
    private String msgId;

    @ApiModelProperty("收信用户ID")
    private String toUserId;

    @ApiModelProperty("发信用户ID")
    private String sendUserId;

    @ApiModelProperty("消息状态（0未读1已读）")
    private String msgStatus;

    @ApiModelProperty("已读时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readTime;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
