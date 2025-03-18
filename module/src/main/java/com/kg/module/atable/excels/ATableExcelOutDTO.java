package com.kg.module.atable.excels;

import java.io.Serializable;
import java.math.BigDecimal;
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

    /** 手机号 */
    private String mobile;

    /** 顺序 */
    private Integer orderIndex;

    /** 测试单行文本 */
    private String testText;

    /** 富文本框 */
    private String testEditor;

    /** 测试decimal */
    private BigDecimal testDecimal;

    /** ImageOne */
    private String testImg;

    /** ImageAvatar */
    private String testAvatar;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}