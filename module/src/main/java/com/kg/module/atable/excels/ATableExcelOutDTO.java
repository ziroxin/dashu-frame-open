package com.kg.module.atable.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 我的表a_table
 *
 * @author ziro
 * @date 2025-03-18
 */
@Getter
@Setter
public class ATableExcelOutDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    /** 姓名 */
    private String testName;

    /** ImageAvatar */
    private String field118;

    /** ImageOne */
    private String field119;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}