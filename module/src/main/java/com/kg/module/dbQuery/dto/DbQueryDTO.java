package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * SQL 查询请求 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("SQL查询请求")
public class DbQueryDTO {

    @NotBlank(message = "SQL不能为空")
    @ApiModelProperty(value = "SQL语句", required = true)
    private String sql;

    @ApiModelProperty(value = "数据库名称（可选，默认使用主库）")
    private String databaseName;

    @ApiModelProperty(value = "最大返回行数，默认1000")
    private Integer maxRows = 1000;

    @ApiModelProperty(value = "查询超时时间（秒），默认30")
    private Integer timeout = 30;
}
