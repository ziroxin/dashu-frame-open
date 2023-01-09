package com.kg.core.zlog.excels;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 操作日志表
 *
 * @author ziro
 * @date 2023-01-07
 */
@Getter
@Setter
public class ZOperateLogExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 操作日志id
     */
    private String logId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 操作人用户名
     */
    private String userName;

    /**
     * 执行方法名称
     */
    private String logMethod;

    /**
     * 执行方法描述
     */
    private String logMsg;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 请求路径
     */
    private String actionUrl;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}