package com.kg.module.message.entity;

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

    @ApiModelProperty("所属模块/菜单（可为空）")
    @TableField("permission_name")
    private String permissionName;

    @ApiModelProperty("关联id(在模块内标记已读可用)")
    @TableField("join_id")
    private String joinId;

    @ApiModelProperty("消息创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
