package com.kg.core.zuserlock.excels;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 用户锁定
 *
 * @author ziro
 * @date 2023-01-05
 */
@Getter
@Setter
public class ZUserLockExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 用户锁定id
     */
    private String lockId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 锁定原因
     */
    private String lockReason;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}