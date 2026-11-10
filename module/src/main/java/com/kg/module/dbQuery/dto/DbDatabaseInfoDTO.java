package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库信息 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("数据库信息")
public class DbDatabaseInfoDTO {

    @ApiModelProperty("数据库名")
    private String databaseName;

    @ApiModelProperty("字符集")
    private String charset;

    @ApiModelProperty("排序规则")
    private String collation;

    @ApiModelProperty("表数量")
    private Integer tableCount;
}
