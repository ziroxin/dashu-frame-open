package com.kg.module.message.dto;

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
 * 消息中心
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@Getter
@Setter
@ApiModel(value = "ZMessageDTO", description = "消息中心")
public class ZMessageDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息中心id")
    private String msgId;

    @ApiModelProperty("消息标题")
    private String msgTitle;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("跳转路由")
    private String msgRouter;

    @ApiModelProperty("所属模块/菜单（可为空）")
    private String permissionName;

    @ApiModelProperty("消息创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 从子表查出 */
    @ApiModelProperty("消息状态（0未读1已读）")
    private String msgStatus;

    /** 从子表查出 */
    @ApiModelProperty("已读时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readTime;
}
