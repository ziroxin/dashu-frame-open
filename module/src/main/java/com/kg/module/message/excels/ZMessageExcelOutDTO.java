package com.kg.module.message.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 消息中心
 *
 * @author ziro
 * @date 2024-06-18
 */
@Getter
@Setter
public class ZMessageExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 消息中心id
     */
    private String msgId;

    /**
     * 消息标题
     */
    private String msgTitle;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 跳转路由
     */
    private String msgRouter;

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
     * 所属模块/菜单（可为空）
     */
    private String permissionName;

    /**
     * 消息创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}