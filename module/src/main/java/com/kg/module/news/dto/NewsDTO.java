package com.kg.module.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 新闻表-测试
 * </p>
 *
 * @author ziro
 * @since 2023-02-17
 */
@Getter
@Setter
@ApiModel(value = "NewsDTO", description = "新闻表-测试")
public class NewsDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("新闻id")
    private String newsId;

    @ApiModelProperty("新闻标题")
    private String newsTitle;

    @ApiModelProperty("新闻内容")
    private String newsContent;

    @ApiModelProperty("顺序")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
