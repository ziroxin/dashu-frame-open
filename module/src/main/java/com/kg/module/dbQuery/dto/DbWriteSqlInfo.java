package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 写 SQL 解析结果
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("写SQL解析结果")
public class DbWriteSqlInfo {

    @ApiModelProperty("语句类型：INSERT/UPDATE/DELETE")
    private String sqlType;

    @ApiModelProperty("目标表（全限定名，含反引号引用）")
    private String table;

    @ApiModelProperty("WHERE 子句（规范化后，仅 UPDATE/DELETE）")
    private String whereClause;

    @ApiModelProperty("统计受影响行数的 SQL（UPDATE/DELETE）")
    private String countSql;

    @ApiModelProperty("备份受影响原始行的 SQL（UPDATE/DELETE）")
    private String backupSql;

    @ApiModelProperty("原始 SQL")
    private String rawSql;
}
