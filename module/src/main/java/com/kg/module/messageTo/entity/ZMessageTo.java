package com.kg.module.messageTo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("z_message_to")
@ApiModel(value = "ZMessageTo对象", description = "消息发送至")
public class ZMessageTo implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息发送至ID")
    @TableId(value = "to_id", type = IdType.ASSIGN_UUID)
    private String toId;

    @ApiModelProperty("消息ID")
    @TableField("msg_id")
    private String msgId;

    @ApiModelProperty("收信用户ID")
    @TableField("to_user_id")
    private String toUserId;

    @ApiModelProperty("发信用户ID")
    @TableField("send_user_id")
    private String sendUserId;

    @ApiModelProperty("消息状态（0未读1已读）")
    @TableField("msg_status")
    private String msgStatus;

    @ApiModelProperty("已读时间")
    @TableField("read_time")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readTime;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
