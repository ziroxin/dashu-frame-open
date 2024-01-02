package com.kg.module.userTheme.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 用户主题配置
 *
 * @author ziro
 * @date 2023-11-04
 */
@Getter
@Setter
public class ZUserThemeExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 用户主题配置id
     */
    private String themeId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 主题内容json
     */
    private String themeJson;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}