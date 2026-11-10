package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 写 SQL 执行结果 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("写SQL执行结果")
public class DbWriteResultDTO {

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("实际影响行数")
    private Integer affectedRows;

    @ApiModelProperty("提示信息")
    private String message;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("执行耗时（毫秒）")
    private Long executeTime;
}
