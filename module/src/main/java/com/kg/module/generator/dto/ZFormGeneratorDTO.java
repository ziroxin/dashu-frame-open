package com.kg.module.generator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * <p>
 * 代码生成器表单
 * </p>
 *
 * @author ziro
 * @since 2023-11-22
 */
@Getter
@Setter
@ApiModel(value = "ZFormGeneratorDTO", description = "代码生成器表单")
public class ZFormGeneratorDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("表单名称")
    private String formName;

    @ApiModelProperty("表单内容json格式")
    private String formJson;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("表描述")
    private String tableDecription;

    @ApiModelProperty("pom模块名")
    private String basePackage;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("生成包名")
    private String tablePackage;

    @ApiModelProperty("前端路径")
    private String viewPath;

    @ApiModelProperty("代码生成状态（0未生成1已生成）")
    private String status;

    @ApiModelProperty("显示顺序")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
