package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据库表信息 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("数据库表信息")
public class DbTableInfoDTO {

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("表注释")
    private String tableComment;

    @ApiModelProperty("表类型（TABLE/VIEW）")
    private String tableType;

    @ApiModelProperty("引擎类型")
    private String engine;

    @ApiModelProperty("行数估计")
    private Long tableRows;

    @ApiModelProperty("列信息")
    private List<DbColumnInfoDTO> columns;

    @ApiModelProperty("索引信息")
    private List<DbIndexInfoDTO> indexes;
}
