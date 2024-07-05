package com.kg.module.message.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息统计DTO
 *
 * @author ziro
 * @date 2024/6/19 10:15
 */
@Getter
@Setter
public class MessageCountsDTO {
    /**
     * 消息总数
     */
    private long count;
    /**
     * 未读消息数
     */
    private long unreadCount;
    /**
     * 各模块未读数Json字符串
     * 例如：｛key:'permissionName',value:未读数｝
     */
    private String permissionUnreadJson;
}
