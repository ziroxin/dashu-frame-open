package com.kg.module.news.excels;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 新闻表-测试
 *
 * @author ziro
 * @date 2023-02-17
 */
@Getter
@Setter
public class NewsExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 新闻id
     */
    private String newsId;

    /**
     * 新闻标题
     */
    private String newsTitle;

    /**
     * 新闻内容
     */
    private String newsContent;

    /**
     * 顺序
     */
    private Integer orderIndex;

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