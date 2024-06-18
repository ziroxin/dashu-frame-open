package com.kg.module.message.entity;

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
 * 消息中心
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@Getter
@Setter
@TableName("z_message")
@ApiModel(value = "ZMessage对象", description = "消息中心")
public class ZMessage implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息中心id")
    @TableId(value = "msg_id", type = IdType.ASSIGN_UUID)
    private String msgId;

    @ApiModelProperty("消息标题")
    @TableField("msg_title")
    private String msgTitle;

    @ApiModelProperty("消息内容")
    @TableField("msg_content")
    private String msgContent;

    @ApiModelProperty("跳转路由")
    @TableField("msg_router")
    private String msgRouter;

    @ApiModelProperty("消息状态（0未读1已读）")
    @TableField("msg_status")
    private String msgStatus;

    @ApiModelProperty("已读时间")
    @TableField("read_time")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readTime;

    @ApiModelProperty("消息类型（0私信1公开）")
    @TableField("msg_type")
    private String msgType;

    @ApiModelProperty("所属模块/菜单（可为空）")
    @TableField("permission_name")
    private String permissionName;

    @ApiModelProperty("消息创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
