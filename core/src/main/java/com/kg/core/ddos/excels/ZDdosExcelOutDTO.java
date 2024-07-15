package com.kg.core.ddos.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - ddos用户请求记录
 *
 * @author ziro
 * @date 2024-07-15
 */
@Getter
@Setter
public class ZDdosExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * DDoS_id
     */
    private String ddosId;

    /**
     * 用户ID
     */
    private String userIp;

    /**
     * 请求次数
     */
    private Integer requestCount;

    /**
     * 限制配置（例如：5分钟内限制100次）
     */
    private String limitJson;

    /**
     * 用户id（有则保存）
     */
    private String userId;

    /**
     * 记录时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}