package com.kg.module.dbQuery.dto;

import lombok.Data;

/**
 * 写操作预览快照（Redis 暂存，二次确认校验用）
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
public class DbWriteSnapshot {

    /**
     * 规范化后的原始 SQL（去分号、去堆叠）
     */
    private String sql;

    /**
     * 语句类型
     */
    private String sqlType;

    /**
     * 目标数据库
     */
    private String databaseName;

    /**
     * 预览受影响行数（INSERT 为 null）
     */
    private Integer affectedRows;

    /**
     * 发起预览的用户ID
     */
    private String userId;

    /**
     * SQL 摘要（MD5），执行时校验一致性
     */
    private String sqlHash;

    /**
     * 创建时间戳（毫秒）
     */
    private Long createTime;
}
