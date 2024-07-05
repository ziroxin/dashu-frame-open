package com.kg.module.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
@TableName("demo_news")
@ApiModel(value = "News对象", description = "新闻表-测试")
public class News implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("新闻id")
    @TableId(value = "news_id", type = IdType.ASSIGN_UUID)
    private String newsId;

    @ApiModelProperty("新闻标题")
    @TableField("news_title")
    private String newsTitle;

    @ApiModelProperty("新闻内容")
    @TableField("news_content")
    private String newsContent;

    @ApiModelProperty("顺序")
    @TableField("order_index")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty("消息id")
    @TableField(exist = false)
    private String msgId;
}
