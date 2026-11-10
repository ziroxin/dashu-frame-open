package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 写 SQL 执行请求 DTO（二次确认）
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("写SQL执行请求")
public class DbWriteExecuteDTO {

    @NotBlank(message = "预览token不能为空")
    @ApiModelProperty(value = "预览返回的一次性token", required = true)
    private String token;

    @NotBlank(message = "SQL不能为空")
    @ApiModelProperty(value = "与预览一致的写SQL", required = true)
    private String sql;

    @ApiModelProperty("数据库名称（须与预览一致）")
    private String databaseName;
}
