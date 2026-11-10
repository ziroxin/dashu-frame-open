package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 写 SQL 预览结果 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("写SQL预览结果")
public class DbWritePreviewResultDTO {

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("语句类型：INSERT/UPDATE/DELETE")
    private String sqlType;

    @ApiModelProperty("目标表（全限定名）")
    private String table;

    @ApiModelProperty("预览受影响行数（UPDATE/DELETE；INSERT 为 null）")
    private Integer affectedRows;

    @ApiModelProperty("原始行备份 - 列名")
    private List<String> columns;

    @ApiModelProperty("原始行备份 - 类型")
    private List<String> columnTypes;

    @ApiModelProperty("原始行备份 - 数据（最多 maxBackupRows 行）")
    private List<Map<String, Object>> rows;

    @ApiModelProperty("二次确认用一次性token")
    private String token;

    @ApiModelProperty("token有效期（秒）")
    private Long expireSeconds;

    @ApiModelProperty("提示信息")
    private String message;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("执行耗时（毫秒）")
    private Long executeTime;
}
