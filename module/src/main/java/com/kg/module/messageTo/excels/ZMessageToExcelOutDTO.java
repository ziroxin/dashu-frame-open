package com.kg.module.messageTo.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 消息发送至
 *
 * @author ziro
 * @date 2024-06-18
 */
@Getter
@Setter
public class ZMessageToExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 消息发送至ID
     */
    private String toId;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 收信用户ID
     */
    private String toUserId;

    /**
     * 发信用户ID
     */
    private String sendUserId;

    /**
     * 消息状态（0未读1已读）
     */
    private String msgStatus;

    /**
     * 已读时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}