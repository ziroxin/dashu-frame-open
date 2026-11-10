package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 写 SQL 预览请求 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("写SQL预览请求")
public class DbWritePreviewDTO {

    @NotBlank(message = "SQL不能为空")
    @ApiModelProperty(value = "写SQL（INSERT/UPDATE/DELETE，单条）", required = true)
    private String sql;

    @ApiModelProperty("数据库名称（可选，默认使用配置的库）")
    private String databaseName;

    @ApiModelProperty("原始行备份最大返回行数，默认200")
    private Integer maxBackupRows = 200;
}
