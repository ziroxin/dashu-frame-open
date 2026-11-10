package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * SQL 查询结果 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("SQL查询结果")
public class DbQueryResultDTO {

    @ApiModelProperty("列名列表")
    private List<String> columns;

    @ApiModelProperty("数据类型列表")
    private List<String> columnTypes;

    @ApiModelProperty("数据行")
    private List<Map<String, Object>> rows;

    @ApiModelProperty("总行数")
    private Integer totalRows;

    @ApiModelProperty("是否被截断（超过最大行数）")
    private Boolean truncated;

    @ApiModelProperty("执行耗时（毫秒）")
    private Long executeTime;

    @ApiModelProperty("影响的行数（用于UPDATE/DELETE/INSERT）")
    private Integer affectedRows;

    @ApiModelProperty("错误信息（如果有）")
    private String errorMessage;

    @ApiModelProperty("是否成功")
    private Boolean success;
}
