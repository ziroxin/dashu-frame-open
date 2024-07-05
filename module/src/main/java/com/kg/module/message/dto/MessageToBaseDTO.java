package com.kg.module.message.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 消息发送基类
 *
 * @author ziro
 * @date 2024/7/4 15:37
 */
@Getter
@Setter
public class MessageToBaseDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息标题")
    private String msgTitle;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("跳转路由，用于点击消息跳转到指定页面")
    private String msgRouter;

    @ApiModelProperty("所属模块/菜单，用于菜单显示未读消息数")
    private String permissionName;

    @ApiModelProperty("关联id(在模块内标记已读可用)")
    private String joinId;

    @ApiModelProperty("发送至类型:user=用户;org=组织机构;role=角色")
    private String toType;

    @ApiModelProperty("发送至IDs")
    private List<String> toIds;
}
