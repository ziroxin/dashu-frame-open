package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库列信息 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("数据库列信息")
public class DbColumnInfoDTO {

    @ApiModelProperty("列名")
    private String columnName;

    @ApiModelProperty("数据类型")
    private String dataType;

    @ApiModelProperty("列类型（完整定义）")
    private String columnType;

    @ApiModelProperty("是否可为空")
    private Boolean nullable;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("是否主键")
    private Boolean primaryKey;

    @ApiModelProperty("是否自增")
    private Boolean autoIncrement;

    @ApiModelProperty("列注释")
    private String columnComment;

    @ApiModelProperty("字符集")
    private String characterSet;

    @ApiModelProperty("排序规则")
    private String collation;
}
